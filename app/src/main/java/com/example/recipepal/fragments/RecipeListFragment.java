package com.example.recipepal.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.recipepal.R;
import com.example.recipepal.activities.MainActivity;
import com.example.recipepal.activities.RecipeInfoActivity;
import com.example.recipepal.dialogs.AddInstructionDialog;
import com.example.recipepal.dialogs.AddRecipeDialog;
import com.example.recipepal.helpers.DatabaseHelper;
import com.example.recipepal.models.Recipe;

// https://syedasaraahmed.wordpress.com/2012/10/03/android-onitemclicklistener-not-responding-clickable-rowitem-of-custom-listview/ (referenced for listview with button component)
// https://stackoverflow.com/questions/3965484/custom-checkbox-image-android (referenced for custom checkbox)
// https://stackoverflow.com/questions/15941635/how-to-add-a-listener-for-checkboxes-in-an-adapter-view-android-arrayadapter (referenced for listener for checkbox in listview)
// https://droidmentor.com/how-to-use-fragment-specific-menu-in-android/ (reference to inflate menus for fragement)

public class RecipeListFragment extends Fragment {
    static final int REQUEST_CODE = 1;
    static final String TAG = "RecipeListFragment";
    DatabaseHelper databaseHelper;
    SimpleCursorAdapter simpleCursorAdapter;
    ListView recipeListView;
    int recipeId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        databaseHelper = new DatabaseHelper(getContext());
        recipeListView = root.findViewById(R.id.recipesListView);

        createRecipeListView(getContext());

        return root;
    }

    /**
     * Creates layout for the recipe list view
     * @param context the context
     */
    public void createRecipeListView(final Context context) {
        // list view adapter
        final Cursor cursor = databaseHelper.getAllRecipesCursor();
        simpleCursorAdapter = new SimpleCursorAdapter(
                context,
                R.layout.recipe_list_row,
                cursor,
                new String[] {DatabaseHelper.NAME},
                new int[] {R.id.recipeTitleTextView},
                0) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView recipeTitleTextView = view.findViewById(R.id.recipeTitleTextView);
                final CheckBox favoriteCheckBox = view.findViewById(R.id.favoriteCheckBox);

                if (cursor.moveToPosition(position)) {
                    recipeTitleTextView.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));

                    // attach id to checkbox for updating
                    int recipeId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ID));
                    favoriteCheckBox.setTag(recipeId);

                    if (cursor.getInt(cursor.getColumnIndex(DatabaseHelper.FAVORITED)) == 0) {
                        favoriteCheckBox.setChecked(false);
                    } else {
                        favoriteCheckBox.setChecked(true);
                    }
                }

                favoriteCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        // update if a recipe is favorited or not
                        if (favoriteCheckBox.isChecked()) {
                            databaseHelper.updateRecipeFavorite((int) favoriteCheckBox.getTag(), 1);
                        } else {
                            databaseHelper.updateRecipeFavorite((int) favoriteCheckBox.getTag(), 0);
                        }
                    }
                });

                return view;
            }
        };
        recipeListView.setAdapter(simpleCursorAdapter);

        // set the listener for entering CAM, user long presses they can select multiple recipes
        recipeListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        recipeListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                int numChecked = recipeListView.getCheckedItemCount();
                mode.setTitle(numChecked + " selected");
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = getActivity().getMenuInflater();
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
                        final long[] checkIds = recipeListView.getCheckedItemIds();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                        alertBuilder.setTitle(getString(R.string.delete_recipe))
                                .setMessage(getString(R.string.delete_recipe_message))
                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // delete all selected recipes
                                        for (long id: checkIds) {
                                            databaseHelper.deleteRecipeById((int) id);
                                            createRecipeListView(getContext()); // update list view
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

        RecipeItemClickListener listener = new RecipeItemClickListener();
        recipeListView.setOnItemClickListener(listener);
    }

    /**
     * Listener for click of a recipe item in the list view
     */
    public class RecipeItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);

            recipeId = cursor.getInt(0);

            startRecipeInfoActivity(recipeId);
        }
    }

    public void startRecipeInfoActivity(int recipeId) {
        Intent intent = new Intent(getActivity(), RecipeInfoActivity.class);
        intent.putExtra("recipeId", recipeId);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.recipe_list_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.addRecipeMenuItem:
                AddRecipeDialog dialog = new AddRecipeDialog(this);
                dialog.show(getActivity().getSupportFragmentManager(), "Add recipe");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void applyTexts(String name) {
        long newId = databaseHelper.insertRecipeItem(name);
        recipeId = (int) newId;
        createRecipeListView(getContext());
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Recipe recipe = (Recipe) data.getSerializableExtra("recipeObject");
            int position = data.getIntExtra("position", -1);

            if (position != -1) { // check for valid position (if existing recipe is being edited)
                // update existing recipe
                databaseHelper.updateRecipeById(note.getId(), note);
            } else {
                // add a new note to the database and update list view
                noteOpenHelper.insertNote(note);
            }
            mainActivityLayout.updateNotesListView();
        }
    }*/

    public int getRecipeId() {
        return recipeId;
    }

}
