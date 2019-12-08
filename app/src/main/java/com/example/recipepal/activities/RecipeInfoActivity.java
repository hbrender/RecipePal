package com.example.recipepal.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipepal.R;
import com.example.recipepal.helpers.DatabaseHelper;
import com.example.recipepal.helpers.UIUtils;
import com.google.android.material.snackbar.Snackbar;

//https://www.youtube.com/watch?v=plQIpqBcdQE (referenced for custom circular button)
//https://stackoverflow.com/questions/6210895/listview-inside-scrollview-is-not-scrolling-on-android/6211286#6211286 (referenced for listview touch listener)
//https://stackoverflow.com/questions/6912237/how-to-return-to-default-style-on-edittext-if-i-apply-a-background (referenced for making edittext look like textview)
//https://stackoverflow.com/questions/16337063/how-to-change-the-default-disabled-edittexts-style (referenced for setting edittext disabled colors)

public class RecipeInfoActivity extends AppCompatActivity {
    static final String TAG = "RecipeActivityTag";
    DatabaseHelper databaseHelper;
    int recipeId;

    EditText recipeNameTextView;
    EditText totalTimeTextView;
    EditText servingsTextView;
    ListView ingredientsListView;
    ListView instructionListView;
    Button addIngredientsButton;
    Button addInstructionsButton;
    Button startRecipeButton;
    MenuItem editMenuItem;
    MenuItem saveMenuItem;
    SimpleCursorAdapter ingredientsAdapter;
    SimpleCursorAdapter instructionsAdapter;
    Drawable recipeNameOriginalDrawable;
    Drawable totalTimeOriginalDrawable;
    Drawable servingsOriginalDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_info);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // home button
        getSupportActionBar().setDisplayShowTitleEnabled(false); // no title

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

            }
        }

        setMultiChoiceModeListeners();
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
                        alertBuilder.setTitle(getString(R.string.delete_recipe))
                                .setMessage(getString(R.string.delete_recipe_message))
                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // delete all selected recipes
                                        for (long id: checkIds) {
                                            //databaseHelper.deleteRecipeById((int) id);
                                            //createRecipeListView(getContext()); // update list view
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
                        alertBuilder.setTitle(getString(R.string.delete_recipe))
                                .setMessage(getString(R.string.delete_recipe_message))
                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // delete all selected recipes
                                        for (long id: checkIds) {
                                            //databaseHelper.deleteRecipeById((int) id);
                                            //createRecipeListView(getContext()); // update list view
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
            recipeNameTextView.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
            totalTimeTextView.setText(" " + cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME)));
            servingsTextView.setText(" " + cursor.getString(cursor.getColumnIndex(DatabaseHelper.SERVINGS)));
        }

        final Cursor cursor2 = databaseHelper.getAllRecipeIngredientsByIdCursor(recipeId);
        ingredientsAdapter = new SimpleCursorAdapter(
                this,
                R.layout.ingredient_list_row,
                cursor2,
                new String[] {DatabaseHelper.NAME},
                new int[] {R.id.nameTextView},
                0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView amountTextView = view.findViewById(R.id.amountTextView);
                TextView nameTextView = view.findViewById(R.id.nameTextView);

                if (cursor2.moveToPosition(position)) {
                    amountTextView.setText(cursor2.getString(cursor2.getColumnIndex(DatabaseHelper.AMOUNT)));
                    nameTextView.setText(cursor2.getString(cursor2.getColumnIndex(DatabaseHelper.NAME)));
                }

                return view;
            }
        };
        ingredientsListView.setAdapter(ingredientsAdapter);

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

        final Cursor cursor3 = databaseHelper.getAllRecipeInstructionsByIdCursor(recipeId);
        instructionsAdapter = new SimpleCursorAdapter(
                this,
                R.layout.instruction_list_row,
                cursor3,
                new String[] {DatabaseHelper.CONTENT},
                new int[] {R.id.instructionContextTextView},
                0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView stepTextView = view.findViewById(R.id.stepTextView);
                TextView instructionContextTextView = view.findViewById(R.id.instructionContextTextView);

                if (cursor3.moveToPosition(position)) {
                    stepTextView.setText(cursor3.getInt(cursor3.getColumnIndex(DatabaseHelper.STEP_NUM)) + ".");
                    instructionContextTextView.setText(cursor3.getString(cursor3.getColumnIndex(DatabaseHelper.CONTENT)));
                }

                return view;
            }
        };
        instructionListView.setAdapter(instructionsAdapter);

        UIUtils.setListViewHeightBasedOnItems(ingredientsListView, this);
        UIUtils.setListViewHeightBasedOnItems(instructionListView, this);
    }

    public void disableEditing() {
        recipeNameTextView.setEnabled(false);
        totalTimeTextView.setEnabled(false);
        servingsTextView.setEnabled(false);

        // set background to look like text view
        recipeNameTextView.setBackgroundResource(android.R.color.transparent);
        totalTimeTextView.setBackgroundResource(android.R.color.transparent);
        servingsTextView.setBackgroundResource(android.R.color.transparent);

        addIngredientsButton.setText(getString(R.string.title_grocery_list));
        addInstructionsButton.setVisibility(View.INVISIBLE);
        startRecipeButton.setEnabled(true);
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
        addInstructionsButton.setVisibility(View.VISIBLE);
        startRecipeButton.setEnabled(false);
    }

    public void startRecipeButtonOnClick(View view) {
        Toast.makeText(this, "TODO: start recipe", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "add ingredient", Toast.LENGTH_SHORT).show();
        }
    }

    public void addIntructionButtonOnClick(View view) {
        Toast.makeText(this, "add instructions", Toast.LENGTH_SHORT).show();


    }

    public void addInstructionButtonOnClick(View view) {
        Toast.makeText(this, "TODO: add instruction", Toast.LENGTH_SHORT).show();
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
}
