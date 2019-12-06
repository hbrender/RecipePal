package com.example.recipepal.ui.recipe;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.recipepal.R;
import com.example.recipepal.RecipeInfoActivity;
import com.example.recipepal.helpers.DatabaseHelper;

// https://syedasaraahmed.wordpress.com/2012/10/03/android-onitemclicklistener-not-responding-clickable-rowitem-of-custom-listview/
//

public class RecipeFragment extends Fragment {
    static final String TAG = "RecipeFragment";
    DatabaseHelper databaseHelper;
    SimpleCursorAdapter simpleCursorAdapter;
    ListView recipeListView;
    int recipeId;

    //private RecipeViewModel recipeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //recipeViewModel =
                ViewModelProviders.of(this).get(RecipeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_recipes, container, false);

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
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView recipeTitleTextView = view.findViewById(R.id.recipeTitleTextView);
                CheckBox favoriteCheckBox = view.findViewById(R.id.favoriteCheckBox);

                if (cursor.moveToPosition(position)) {
                    recipeTitleTextView.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));

                    if (cursor.getInt(cursor.getColumnIndex(DatabaseHelper.FAVORITED)) == 0) {
                        favoriteCheckBox.setChecked(false);
                    } else {
                        favoriteCheckBox.setChecked(true);
                    }
                }

                return view;
            }
        };
        recipeListView.setAdapter(simpleCursorAdapter);

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

            int recipeId = cursor.getInt(0);

            Intent intent = new Intent(getActivity(), RecipeInfoActivity.class);
            intent.putExtra("recipeId", recipeId);
            startActivity(intent);
        }
    }
}