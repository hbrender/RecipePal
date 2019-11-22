package com.example.recipepal.ui.grocerylist;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
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
    DatabaseHelper databaseHelper;
    ListView groceryListListView;
    SimpleCursorAdapter simpleCursorAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grocerylist, container, false);
        groceryListListView = view.findViewById(R.id.groceryListListView);
        createGroceryListListView(getContext());

        return view;
    }

    /**
     * Creates layout for the note list view
     * @param context the context
     */
    public void createGroceryListListView(final Context context) {
        // list view adapter
        final Cursor cursor = databaseHelper.getAllGroceryListCursor();
        simpleCursorAdapter = new SimpleCursorAdapter(
                context,
                R.layout.grocery_list_row,
                cursor,
                new String[] {DatabaseHelper.NAME},
                new int[] {R.id.nameTextView},
                0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                CheckBox checkBox = view.findViewById(R.id.checkBox);
                TextView amountTextView = view.findViewById(R.id.amountTextView);
                TextView nameTextView = view.findViewById(R.id.nameTextView);

                if (cursor.moveToPosition(position)) {
                    amountTextView.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.AMOUNT)));
                    nameTextView.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));

                    if (cursor.getInt(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CHECKED))) == 0) {
                        checkBox.setChecked(false);
                    } else {
                        checkBox.setChecked(true);
                    }
                }

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
}