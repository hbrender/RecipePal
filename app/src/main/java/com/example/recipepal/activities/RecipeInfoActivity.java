package com.example.recipepal.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipepal.R;
import com.example.recipepal.dialogs.AddIngredientDialog;
import com.example.recipepal.dialogs.AddInstructionDialog;
import com.example.recipepal.helpers.DatabaseHelper;
import com.example.recipepal.helpers.UIUtils;
import com.google.android.material.snackbar.Snackbar;

import java.io.FileNotFoundException;
import java.io.InputStream;

//https://www.youtube.com/watch?v=plQIpqBcdQE (referenced for custom circular button)
//https://stackoverflow.com/questions/6210895/listview-inside-scrollview-is-not-scrolling-on-android/6211286#6211286 (referenced for listview touch listener)
//https://stackoverflow.com/questions/6912237/how-to-return-to-default-style-on-edittext-if-i-apply-a-background (referenced for making edittext look like textview)
//https://stackoverflow.com/questions/16337063/how-to-change-the-default-disabled-edittexts-style (referenced for setting edittext disabled colors)
//https://stackoverflow.com/questions/38352148/get-image-from-the-gallery-and-show-in-imageview (referenced for adding photos from photo gallery)

public class RecipeInfoActivity extends AppCompatActivity implements AddIngredientDialog.AddIngredientDialogListener, AddInstructionDialog.AddInstructionDialogListener {
    static final String TAG = "RecipeActivityTag";
    static final int RESULT_LOAD_MAIN_RECIPE_IMG = 1;
    static final int RESULT_LOAD_INSTRUCTION_IMG = 2;
    static final int REQUEST_CODE_MAIN_RECIPE_GALLERY = 3;
    static final int REQUEST_CODE_INSTRUCTION_GALLERY = 4;

    DatabaseHelper databaseHelper;
    int recipeId;

    EditText recipeNameTextView;
    EditText totalTimeTextView;
    EditText servingsTextView;
    ListView ingredientsListView;
    ListView instructionListView;
    ImageView recipeImageView;
    Button addIngredientsButton;
    Button addInstructionsButton;
    Button startRecipeButton;
    Button addMainRecipePhotoButton;
    Button addInstructionPhotoButton;
    MenuItem editMenuItem;
    MenuItem saveMenuItem;
    SimpleCursorAdapter ingredientsAdapter;
    SimpleCursorAdapter instructionsAdapter;
    Drawable recipeNameOriginalDrawable;
    Drawable totalTimeOriginalDrawable;
    Drawable servingsOriginalDrawable;
    AddInstructionDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_info);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // home button
        //getSupportActionBar().setDisplayShowTitleEnabled(false); // no title

        databaseHelper = new DatabaseHelper(this);

        recipeNameTextView = findViewById(R.id.recipeNameTextView);
        totalTimeTextView = findViewById(R.id.totalTimeTextView);
        servingsTextView = findViewById(R.id.servingsTextView);
        ingredientsListView = findViewById(R.id.ingredientsListView);
        instructionListView = findViewById(R.id.instructionsListView);
        addIngredientsButton = findViewById(R.id.addIngredientsButton);
        addInstructionsButton = findViewById(R.id.addInstructionButton);
        startRecipeButton = findViewById(R.id.startRecipeButton);
        ingredientsListView = findViewById(R.id.ingredientsListView);
        instructionListView = findViewById(R.id.instructionsListView);
        recipeImageView = findViewById(R.id.recipeImageView);
        addMainRecipePhotoButton = findViewById(R.id.addMainRecipePhotoButton);
        addInstructionPhotoButton = findViewById(R.id.addInstructionPhotoButton);

        // get original background of edit texts
        recipeNameOriginalDrawable = recipeNameTextView.getBackground();
        totalTimeOriginalDrawable = totalTimeTextView.getBackground();
        servingsOriginalDrawable = servingsTextView.getBackground();

        Intent intent = getIntent();
        if (intent != null) {
            recipeId = intent.getIntExtra("recipeId", -1);

            if (recipeId != -1) {
                setRecipeInfo();
                disableEditing();
            } else {
                setMultiChoiceModeListeners();
            }
        }
    }

    /**
     * Sets multi choice mode listeners for ingredients list view and instructions list view
     */
    public void setMultiChoiceModeListeners() {
        ingredientsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        ingredientsListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                int numChecked = ingredientsListView.getCheckedItemCount();
                mode.setTitle(numChecked + " ingredients selected");
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = getMenuInflater();
                menuInflater.inflate(R.menu.cam_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.deleteMenuItem:
                        final long[] checkIds = ingredientsListView.getCheckedItemIds();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RecipeInfoActivity.this);
                        alertBuilder.setTitle(getString(R.string.delete_ingredient))
                                .setMessage(getString(R.string.delete_ingredient_message))
                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // delete all selected ingredients
                                        for (long id: checkIds) {
                                            databaseHelper.deleteIngredientById((int) id);
                                            setIngredientsListView(); // update list view
                                        }
                                    }
                                })
                                .setNegativeButton(R.string.no, null);
                        alertBuilder.show();

                        mode.finish(); // exit cam
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });

        instructionListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        instructionListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                int numChecked = instructionListView.getCheckedItemCount();
                mode.setTitle(numChecked + " instructions selected");
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = getMenuInflater();
                menuInflater.inflate(R.menu.cam_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.deleteMenuItem:
                        final long[] checkIds = instructionListView.getCheckedItemIds();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RecipeInfoActivity.this);
                        alertBuilder.setTitle(getString(R.string.delete_instruction))
                                .setMessage(getString(R.string.delete_instruction_message))
                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // delete all selected instructions
                                        for (long id: checkIds) {
                                            databaseHelper.deleteInstructionById((int) id);
                                            setInstructionListView(); // update list view
                                        }
                                    }
                                })
                                .setNegativeButton(R.string.no, null);
                        alertBuilder.show();

                        mode.finish(); // exit cam
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });
    }

    public void setRecipeInfo() {
        final Cursor cursor = databaseHelper.getRecipeByIdCursor(recipeId);
        if (cursor.getColumnCount() > 0 && cursor.moveToPosition(0)) {
            if(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)).length() != 0) {
                recipeNameTextView.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
            } else {
                recipeNameTextView.setText(null);
            }

            if (cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME)) != null &&
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME)).length() != 0) {
                totalTimeTextView.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME)));
            } else {
                totalTimeTextView.setText(null);
            }

            if (cursor.getString(cursor.getColumnIndex(DatabaseHelper.SERVINGS)) != null&&
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.SERVINGS)).length() != 0) {
                servingsTextView.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SERVINGS)));
            } else {
                servingsTextView.setText(null);
            }
        }

        setIngredientsListView();
        setInstructionListView();

        /*ingredientsListView.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });*/
    }

    public void setIngredientsListView() {
        final Cursor cursor = databaseHelper.getAllRecipeIngredientsByIdCursor(recipeId);

        // hide add grocery button if no ingredients
        if (cursor.getCount() == 0) {
            addIngredientsButton.setVisibility(View.INVISIBLE);
        }

        ingredientsAdapter = new SimpleCursorAdapter(
                this,
                R.layout.ingredient_list_row,
                cursor,
                new String[] {DatabaseHelper.NAME},
                new int[] {R.id.nameTextView},
                0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView amountTextView = view.findViewById(R.id.amountTextView);
                TextView nameTextView = view.findViewById(R.id.nameTextView);

                if (cursor.moveToPosition(position)) {
                    amountTextView.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.AMOUNT)));
                    nameTextView.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
                }

                return view;
            }
        };
        ingredientsListView.setAdapter(ingredientsAdapter);
        UIUtils.setListViewHeightBasedOnItems(ingredientsListView, this);

    }

    public void setInstructionListView() {
        final Cursor cursor = databaseHelper.getAllRecipeInstructionsByIdCursor(recipeId);
        instructionsAdapter = new SimpleCursorAdapter(
                this,
                R.layout.instruction_list_row,
                cursor,
                new String[] {DatabaseHelper.CONTENT},
                new int[] {R.id.instructionContextTextView},
                0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView stepTextView = view.findViewById(R.id.stepTextView);
                TextView instructionContextTextView = view.findViewById(R.id.instructionContextTextView);

                if (cursor.moveToPosition(position)) {
                    stepTextView.setText(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.STEP_NUM)) + ".");
                    instructionContextTextView.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CONTENT)));
                }

                return view;
            }
        };
        instructionListView.setAdapter(instructionsAdapter);

        UIUtils.setListViewHeightBasedOnItems(instructionListView, this);
    }

    public void disableEditing() {
        Log.d(TAG, "disableEditing: here");
        recipeNameTextView.setEnabled(false);
        totalTimeTextView.setEnabled(false);
        servingsTextView.setEnabled(false);

        // set background to look like text view
        recipeNameTextView.setBackgroundResource(android.R.color.transparent);
        totalTimeTextView.setBackgroundResource(android.R.color.transparent);
        servingsTextView.setBackgroundResource(android.R.color.transparent);

        Cursor cursor = databaseHelper.getAllRecipeIngredientsByIdCursor(recipeId);
        // hide add grocery button if no ingredients
        if (cursor.getCount() == 0) {
            addIngredientsButton.setVisibility(View.INVISIBLE);
        } else {
            addIngredientsButton.setText(getString(R.string.title_grocery_list));
        }

        addInstructionsButton.setVisibility(View.INVISIBLE);
        addMainRecipePhotoButton.setVisibility(View.GONE);
        startRecipeButton.setVisibility(View.VISIBLE);

        // no CAM for list view when recipe is not in edit mode
        ingredientsListView.clearChoices();
        ingredientsListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        instructionListView.clearChoices();
        instructionListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
    }

    public void enableEditing() {
        recipeNameTextView.setEnabled(true);
        totalTimeTextView.setEnabled(true);
        servingsTextView.setEnabled(true);

        // set background to look like edit text
        recipeNameTextView.setBackground(recipeNameOriginalDrawable);
        totalTimeTextView.setBackground(totalTimeOriginalDrawable);
        servingsTextView.setBackground(servingsOriginalDrawable);

        addIngredientsButton.setText("");
        addIngredientsButton.setVisibility(View.VISIBLE);
        addInstructionsButton.setVisibility(View.VISIBLE);
        if (recipeImageView.getDrawable() == null) {
            addMainRecipePhotoButton.setVisibility(View.VISIBLE);
        }
        startRecipeButton.setVisibility(View.GONE);

        // CAM for list views
        setMultiChoiceModeListeners();
    }

    public void saveRecipeInfo() {
        String name = recipeNameTextView.getText().toString();
        String time = totalTimeTextView.getText().toString();
        String servings = servingsTextView.getText().toString();

        databaseHelper.updateRecipe(recipeId, name, time, servings);
    }

    public void startRecipeButtonOnClick(View view) {
        Intent intent = new Intent(this, InteractiveRecipeActivity.class);

        intent.putExtra("ID", recipeId);

        startActivity(intent);
    }

    public void addIngredientButtonOnClick(View view) {
        if (addIngredientsButton.getText().toString().compareTo(getString(R.string.title_grocery_list)) == 0) {
            // get all recipe ingredients
            Cursor c = databaseHelper.getAllRecipeIngredientsByIdCursor(recipeId);

            // for each recipe ingredient add to the grocery list
            while(c.moveToNext()) {
                databaseHelper.insertGroceryListItem(c.getInt(c.getColumnIndex(DatabaseHelper.ID)));
            }

            GridLayout gridLayout = findViewById(R.id.gridLayout);
            Snackbar snackbar = Snackbar.make(gridLayout, getString(R.string.ingredients_added), Snackbar.LENGTH_LONG);
            snackbar.show();
        } else {
            AddIngredientDialog dialog = new AddIngredientDialog();
            dialog.show(getSupportFragmentManager(), "Add ingredient");
        }
    }

    public void addInstructionButtonOnClick(View view) {
        dialog = new AddInstructionDialog();
        dialog.show(getSupportFragmentManager(), "Add instruction");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.editMenuItem:
                enableEditing();
                editMenuItem.setVisible(false);
                saveMenuItem.setVisible(true);
                return true;
            case R.id.saveMenuItem:
                // check if recipe info can be saved with no errors
                saveRecipeInfo();
                disableEditing();
                editMenuItem.setVisible(true);
                saveMenuItem.setVisible(false);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.recipe_info_menu, menu);

        editMenuItem = menu.findItem(R.id.editMenuItem);
        saveMenuItem = menu.findItem(R.id.saveMenuItem);

        Log.d(TAG, "onCreateOptionsMenu: " + recipeId);
        if (recipeId == -1) {
            editMenuItem.setVisible(false);
            saveMenuItem.setVisible(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void applyTexts(String amount, String name) {
        long ingredientId = databaseHelper.insertIngredientItem(amount, name);

        if (ingredientId != -1) { // check if error occured
            databaseHelper.insertRecipeIngredientItem(recipeId, (int) ingredientId);
            setIngredientsListView();
        }
    }

    @Override
    public void applyTexts(String step, String content, String timer) {
        databaseHelper.insertInstructionItem(recipeId, step, content, timer, -1);
        setInstructionListView();
    }

    public void addMainRecipePhotoButtonOnClick(View view) {
        ActivityCompat.requestPermissions(RecipeInfoActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_MAIN_RECIPE_GALLERY);
    }

    public void addInstructionPhotoButtonOnClick(View view) {
        ActivityCompat.requestPermissions(RecipeInfoActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_INSTRUCTION_GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_MAIN_RECIPE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, RESULT_LOAD_MAIN_RECIPE_IMG);
            }
        }

        if (requestCode == REQUEST_CODE_INSTRUCTION_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, RESULT_LOAD_INSTRUCTION_IMG);
            } else {
                ActivityCompat.requestPermissions(RecipeInfoActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_INSTRUCTION_GALLERY);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == RESULT_LOAD_MAIN_RECIPE_IMG) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                recipeImageView.setImageBitmap(selectedImage);
                addMainRecipePhotoButton.setVisibility(View.GONE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (resultCode == RESULT_OK && requestCode == RESULT_LOAD_INSTRUCTION_IMG) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                dialog.sendImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
