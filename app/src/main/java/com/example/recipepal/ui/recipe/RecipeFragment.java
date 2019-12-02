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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.recipepal.R;
import com.example.recipepal.RecipeActivity;
import com.example.recipepal.helpers.DatabaseHelper;

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
                android.R.layout.simple_list_item_activated_1,
                cursor,
                new String[] {DatabaseHelper.NAME},
                new int[] {R.id.recipeNameTextView},
                0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView1 = view.findViewById(android.R.id.text1);

                if (cursor.moveToPosition(position)) {
                    textView1.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
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

            Intent intent = new Intent(getActivity(), RecipeActivity.class);
            intent.putExtra("recipeId", recipeId);
            startActivity(intent);
        }
    }
}