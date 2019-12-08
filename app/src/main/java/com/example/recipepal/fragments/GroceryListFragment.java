package com.example.recipepal.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.recipepal.R;
import com.example.recipepal.helpers.DatabaseHelper;

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
        View view = inflater.inflate(R.layout.fragment_grocerylist, container, false);

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
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        databaseHelper = new DatabaseHelper(context);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // menuInflater.inflate(R.menu.grocery_list_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public void onResume() {
        super.onResume();

        createGroceryListListView(getContext());

        Log.d(TAG, "onResume: ");
    }
}