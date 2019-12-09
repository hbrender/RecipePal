package com.example.recipepal.fragments;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.recipepal.R;
import com.example.recipepal.activities.MainActivity;
import com.example.recipepal.dialogs.AddGroceryDialog;
import com.example.recipepal.dialogs.AddRecipeDialog;
import com.example.recipepal.helpers.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class GroceryListFragment extends Fragment {
    static final String TAG = "GroceryListFragmentTag";
    DatabaseHelper databaseHelper;
    ListView groceryListListView;
    SimpleCursorAdapter simpleCursorAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_grocerylist, container, false);

        groceryListListView = view.findViewById(R.id.groceryListListView);
        createGroceryListListView(getContext());

        return view;
    }

    /**
     * Creates layout for the grocery list view
     *
     * @param context the context
     */
    public void createGroceryListListView(final Context context) {
        // list view adapter
        final Cursor cursor = databaseHelper.getAllGroceryListCursor();
        simpleCursorAdapter = new SimpleCursorAdapter(
                context,
                R.layout.grocery_list_row,
                cursor,
                new String[]{DatabaseHelper.NAME},
                new int[]{R.id.nameTextView},
                0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                final CheckBox checkBox = view.findViewById(R.id.checkBox);
                TextView amountTextView = view.findViewById(R.id.amountTextView);
                TextView nameTextView = view.findViewById(R.id.nameTextView);

                // attach id to checkbox for updating
                int recipeId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ID));
                checkBox.setTag(recipeId);

                if (cursor.moveToPosition(position)) {
                    amountTextView.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.AMOUNT)));
                    nameTextView.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));

                    if (cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CHECKED)) == 0) {
                        checkBox.setChecked(false);
                    } else {
                        checkBox.setChecked(true);
                    }
                }

                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        // update if grocery item is checked or not
                        if (checkBox.isChecked()) {
                            databaseHelper.updateGroceryItemChecked((int) checkBox.getTag(), 1);
                        } else {
                            databaseHelper.updateGroceryItemChecked((int) checkBox.getTag(), 0);
                        }
                    }
                });

                return view;
            }
        };
        groceryListListView.setAdapter(simpleCursorAdapter);

        // set the listener for entering CAM, user long presses they can select multiple grocery items
        groceryListListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        groceryListListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                int numChecked = groceryListListView.getCheckedItemCount();
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
                        final long[] checkIds = groceryListListView.getCheckedItemIds();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                        alertBuilder.setTitle(getString(R.string.delete_grocery))
                                .setMessage(getString(R.string.delete_grocery_message))
                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // delete all selected recipes
                                        for (long id: checkIds) {
                                            databaseHelper.deleteGroceryById((int) id);
                                            createGroceryListListView(getContext()); // update list view
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        databaseHelper = new DatabaseHelper(context);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.grocery_list_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.addGroceryMenuItem:
                AddGroceryDialog dialog = new AddGroceryDialog(this);
                dialog.show(getActivity().getSupportFragmentManager(), "Add grocery");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        createGroceryListListView(getContext());
    }

    public void applyTexts(String name, String amount) {
        long newId = databaseHelper.insertIngredientItem(amount, name);
        int ingredientID = (int) newId;
        databaseHelper.insertGroceryListItem(ingredientID);
        createGroceryListListView(getContext());
    }
}