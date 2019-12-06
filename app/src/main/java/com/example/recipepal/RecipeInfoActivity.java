package com.example.recipepal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipepal.helpers.DatabaseHelper;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;

//https://www.youtube.com/watch?v=plQIpqBcdQE (referenced for custom circular button)
//https://stackoverflow.com/questions/6210895/listview-inside-scrollview-is-not-scrolling-on-android/6211286#6211286 (referenced for listview touch listener)

public class RecipeInfoActivity extends AppCompatActivity {
    static final String TAG = "RecipeActivityTag";
    DatabaseHelper databaseHelper;
    int recipeId;

    TextView recipeNameTextView;
    TextView totalTimeTextView;
    TextView servingsTextView;
    ListView ingredientsListView;
    ListView instructionListView;
    Button addIngredientsButton;
    SimpleCursorAdapter ingredientsAdapter;
    SimpleCursorAdapter instructionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHelper = new DatabaseHelper(this);

        recipeNameTextView = findViewById(R.id.recipeNameTextView);
        totalTimeTextView = findViewById(R.id.totalTimeTextView);
        servingsTextView = findViewById(R.id.servingsTextView);
        ingredientsListView = findViewById(R.id.ingredientsListView);
        instructionListView = findViewById(R.id.instructionsListView);
        addIngredientsButton = findViewById(R.id.addToGroceryListButton);

        Intent intent = getIntent();
        if (intent != null) {
            recipeId = intent.getIntExtra("recipeId", -1);

            if (recipeId != -1) {
                setRecipeInfo();
            }
        }
    }

    public void setRecipeInfo() {
        final Cursor cursor = databaseHelper.getRecipeByIdCursor(recipeId);
        if (cursor.getColumnCount() > 0 && cursor.moveToPosition(0)) {
            recipeNameTextView.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
            totalTimeTextView.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME)));
            servingsTextView.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SERVINGS)));
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

        ingredientsListView.setOnTouchListener(new ListView.OnTouchListener() {
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
        });

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
        }
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
