package com.example.recipepal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipepal.helpers.DatabaseHelper;

import java.util.Date;

public class RecipeActivity extends AppCompatActivity {
    static final String TAG = "RecipeActivityTag";
    DatabaseHelper databaseHelper;
    int recipeId;

    TextView recipeNameTextView;
    ListView ingredientsListView;
    ListView instructionListView;
    SimpleCursorAdapter ingredientsAdapter;
    SimpleCursorAdapter instructionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        databaseHelper = new DatabaseHelper(this);

        recipeNameTextView = findViewById(R.id.recipeNameTextView);
        ingredientsListView = findViewById(R.id.ingredientsListView);
        instructionListView = findViewById(R.id.instructionsListView);

        Intent intent = getIntent();
        if (intent != null) {
            recipeId = intent.getIntExtra("recipeId", -1);

            if (recipeId != -1) {
                setRecipeInfo();
            }
        }
    }

    public void setRecipeInfo() {
        Cursor cursor = databaseHelper.getRecipeByIdCursor(recipeId);
        if (cursor.getColumnCount() > 0 && cursor.moveToPosition(0)) {
            recipeNameTextView.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
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

                TextView instructionContextTextView = view.findViewById(R.id.instructionContextTextView);

                if (cursor3.moveToPosition(position)) {
                    instructionContextTextView.setText(cursor3.getString(cursor3.getColumnIndex(DatabaseHelper.CONTENT)));
                }

                return view;
            }
        };
        instructionListView.setAdapter(instructionsAdapter);
    }

    public void startRecipeButtonOnClick(View view) {
        Toast.makeText(this, "TODO: start recipe", Toast.LENGTH_SHORT).show();
    }

    public void addIngredientButtonOnClick(View view) {
        Toast.makeText(this, "TODO: add ingredient", Toast.LENGTH_SHORT).show();
    }

    public void addInstructionButtonOnClick(View view) {
        Toast.makeText(this, "TODO: add instruction", Toast.LENGTH_SHORT).show();
    }
}
