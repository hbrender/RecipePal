//Source referenced: https://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/

package com.example.recipepal.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    static final String TAG = "DatabaseHelperTag";
    static final String DATABASE_NAME = "recipePalDB";
    static final int DATABASE_VERSION = 1;
    private SQLiteDatabase defaultDB = null;

    // Tables
    static final String TABLE_RECIPE = "Recipe";
    static final String TABLE_GROCERY_LIST = "GroceryList";
    static final String TABLE_INGREDIENTS = "Ingredient";
    static final String TABLE_INSTRUCTIONS = "Instructions";
    static final String TABLE_RECIPE_INGREDIENTS = "RecipeIngredients";

    // common column
    public static final String ID = "_id";
    public static final String NAME = "name";

    // columns for Ingredient tables
    public static final String AMOUNT = "amount"; // string, ex: 1 pound, 2 servings, 3, etc.

    // columns for GroceryList table
    public static final String INGREDIENT_ID = "ingredientId";
    public static final String CHECKED = "checked"; // checked vs unchecked

    // columns for Recipe table
    public static final String IMAGE_RESOURCE = "imageResource";
    public static final String TIME = "time";
    public static final String SERVINGS = "servings";
    public static final String FAVORITED = "favorited";

    // columns for Instructions table
    public static final String RECIPE_ID = "recipeId";
    public static final String STEP_NUM = "stepNum";
    public static final String CONTENT = "content";

    // Recipe table create statement
    private static final String CREATE_TABLE_RECIPE = "CREATE TABLE " + TABLE_RECIPE + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NAME + " TEXT, "
            + IMAGE_RESOURCE + " INTEGER, "
            + TIME + " TEXT, "
            + SERVINGS + " TEXT, "
            + FAVORITED + " INTEGER)";

    // Instruction table create statement
    private static final String CREATE_TABLE_INSTRUCTIONS = "CREATE TABLE " + TABLE_INSTRUCTIONS + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RECIPE_ID  + " INTEGER, "
            + STEP_NUM + " INTEGER, "
            + CONTENT + " TEXT, "
            + TIME + " TIME, "
            + IMAGE_RESOURCE + " INTEGER, "
            + "FOREIGN KEY(" + RECIPE_ID + ") REFERENCES " + TABLE_RECIPE + "(" + ID + "))";

    // Ingredient table create statement
    private static final String CREATE_TABLE_INGREDIENT = "CREATE TABLE " + TABLE_INGREDIENTS + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NAME + " TEXT,"
            + AMOUNT + " TEXT)";

    private static final String CREATE_TABLE_RECIPE_INGREDIENT = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RECIPE_ID + " INTEGER, "
            + INGREDIENT_ID + " INTEGER, "
            + " FOREIGN KEY(" + RECIPE_ID + ") REFERENCES " + RECIPE_ID + "(" + ID + "), "
            + " FOREIGN KEY(" + INGREDIENT_ID + ") REFERENCES " + TABLE_INGREDIENTS + "(" + ID + "))";

    // GroceryList create statement
    private static final String CREATE_TABLE_GROCERY_LIST = "CREATE TABLE " + TABLE_GROCERY_LIST + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CHECKED + " INTEGER, "
            + INGREDIENT_ID + " INTEGER,"
            + " FOREIGN KEY(" + INGREDIENT_ID + ") REFERENCES " + TABLE_INGREDIENTS + "(" + ID + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: " + CREATE_TABLE_RECIPE);
        db.execSQL(CREATE_TABLE_RECIPE);
        Log.d(TAG, "onCreate: " + CREATE_TABLE_INGREDIENT);
        db.execSQL(CREATE_TABLE_INGREDIENT);
        Log.d(TAG, "onCreate: " + CREATE_TABLE_INSTRUCTIONS);
        db.execSQL(CREATE_TABLE_INSTRUCTIONS);
        Log.d(TAG, "onCreate: " + CREATE_TABLE_RECIPE_INGREDIENT);
        db.execSQL(CREATE_TABLE_RECIPE_INGREDIENT);
        Log.d(TAG, "onCreate: " + CREATE_TABLE_GROCERY_LIST);
        db.execSQL(CREATE_TABLE_GROCERY_LIST);

        db.execSQL("INSERT INTO " + TABLE_INGREDIENTS + " VALUES(1, 'carrot', '3 large')");
        db.execSQL("INSERT INTO " + TABLE_INGREDIENTS + " VALUES(2, 'ice cream', '1 pint')");
        db.execSQL("INSERT INTO " + TABLE_INGREDIENTS + " VALUES(3, 'bread', '1 loaf')");
        db.execSQL("INSERT INTO " + TABLE_INGREDIENTS + " VALUES(4, 'apple', '2')");

        db.execSQL("INSERT INTO " + TABLE_GROCERY_LIST + " VALUES(1, 0, 1)");
        db.execSQL("INSERT INTO " + TABLE_GROCERY_LIST + " VALUES(2, 1, 2)");
        db.execSQL("INSERT INTO " + TABLE_GROCERY_LIST + " VALUES(3, 0, 3)");
        db.execSQL("INSERT INTO " + TABLE_GROCERY_LIST + " VALUES(4, 1, 4)");

        db.execSQL("INSERT INTO " + TABLE_RECIPE + " VALUES(1, 'The Worlds Easiest Cookies', null, '30 min', '16 cookies', 1)");
        db.execSQL("INSERT INTO " + TABLE_RECIPE + " VALUES(2, 'White Pizza with Pesto Parsely Drizzle', null, '40 min', '8 slices', 0)");

        db.execSQL("INSERT INTO " + TABLE_INSTRUCTIONS + " VALUES(1, 1, 1, "
                + "'Preheat the oven to 350°F. Line a cookie sheet or rimmed baking sheet with parchment paper.', "
                + "0, null)");
        db.execSQL("INSERT INTO " + TABLE_INSTRUCTIONS + " VALUES(2, 1, 2, "
                + "'In bowl, whisk together the almond flour, baking powder, and salt.', "
                + "0, null)");
        db.execSQL("INSERT INTO " + TABLE_INSTRUCTIONS + " VALUES(3, 1, 3, "
                + "'In same bowl, stir in maple syrup and vanilla and mix until a sticky dough holds together', "
                + "0, null)");
        db.execSQL("INSERT INTO " + TABLE_INSTRUCTIONS + " VALUES(4, 1, 4, "
                + "'Scoop 1 tbsp dough and roll it into a round ball and place on the baking sheet. Repeat with the rest of the dough, placing each ball about 2 inches apart.', "
                + "0, null)");
        db.execSQL("INSERT INTO " + TABLE_INSTRUCTIONS + " VALUES(5, 1, 5, "
                + "'Use your fingers to smush the cookies until they are about 1/2 inch tall.', "
                + "0, null)");
        db.execSQL("INSERT INTO " + TABLE_INSTRUCTIONS + " VALUES(6, 1, 6, "
                + "'Place the tray in the oven and bake for about 12 minutes, turning the tray 180° at the the halfway point. The cookies are ready when the edges are golden brown.', "
                + "'12:00:00', null)");

        db.execSQL("INSERT INTO " + TABLE_INGREDIENTS + " VALUES(5, 'almond flour', '2 cups')");
        db.execSQL("INSERT INTO " + TABLE_INGREDIENTS + " VALUES(6, 'baking soda', '1/2 tsp')");
        db.execSQL("INSERT INTO " + TABLE_INGREDIENTS + " VALUES(7, 'salt', '1/8 tsp')");
        db.execSQL("INSERT INTO " + TABLE_INGREDIENTS + " VALUES(8, 'maple syrup', '1/3 cup')");
        db.execSQL("INSERT INTO " + TABLE_INGREDIENTS + " VALUES(9, 'vanilla extract', '2 tsp')");

        db.execSQL("INSERT INTO " + TABLE_RECIPE_INGREDIENTS + " VALUES(1, 1, 5)");
        db.execSQL("INSERT INTO " + TABLE_RECIPE_INGREDIENTS + " VALUES(2, 1, 6)");
        db.execSQL("INSERT INTO " + TABLE_RECIPE_INGREDIENTS + " VALUES(3, 1, 7)");
        db.execSQL("INSERT INTO " + TABLE_RECIPE_INGREDIENTS + " VALUES(4, 1, 8)");
        db.execSQL("INSERT INTO " + TABLE_RECIPE_INGREDIENTS + " VALUES(5, 1, 9)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.defaultDB = db;
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROCERY_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSTRUCTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE);

        // create new tables
        onCreate(db);
    }

    /**
     * Get all grocery list items
     * @return cursor
     */
    public Cursor getAllGroceryListCursor() {
        String sqlSelect = "SELECT i.*, g." + CHECKED
                + " FROM " + TABLE_INGREDIENTS + " i, " + TABLE_GROCERY_LIST + " g"
                + " WHERE g." + INGREDIENT_ID + " = i." + ID;

        Log.d(TAG, "getAllGroceryListCursor: " + sqlSelect);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlSelect, null);

        return cursor;
    }

    /**
     * Get all recipes
     * @return cursor
     */
    public Cursor getAllRecipesCursor() {
        String sqlSelect = "SELECT * FROM " + TABLE_RECIPE;

        Log.d(TAG, "getAllRecipesCursor: " + sqlSelect);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlSelect, null);

        return cursor;
    }

    /**
     * Get a recipe given an id
     * @param recipeId
     * @return cursor
     */
    public Cursor getRecipeByIdCursor(int recipeId) {
        String sqlSelect = "SELECT * FROM " + TABLE_RECIPE + " WHERE " + ID + " = " + recipeId;

        Log.d(TAG, "getRecipeByIdCursor: " + sqlSelect);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlSelect, null);

        return cursor;
    }

    /**
     * Get all recipe ingredients for a specific recipe
     * @param recipeId
     * @return cursor
     */
    public Cursor getAllRecipeIngredientsByIdCursor(int recipeId) {
        String sqlSelect = "SELECT i.* FROM " + TABLE_INGREDIENTS + " i, " + TABLE_RECIPE_INGREDIENTS + " ri"
                + " WHERE i." + ID + " = ri." + INGREDIENT_ID
                + " AND ri." + RECIPE_ID + " = '" + recipeId + "'";

        Log.d(TAG, "getAllRecipeIngredientsByIdCursor: " + sqlSelect);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlSelect, null);

        return cursor;
    }

    /**
     * Get all recipe instructions for a specific recipe
     * @param recipeId
     * @return cursor
     */
    public Cursor getAllRecipeInstructionsByIdCursor(int recipeId) {
        String sqlSelect = "SELECT i.* FROM " + TABLE_INSTRUCTIONS + " i, " + TABLE_RECIPE + " r"
                + " WHERE i." + RECIPE_ID + " = r." + ID
                + " AND r." + ID + " = '" + recipeId + "'"
                + " ORDER BY " + STEP_NUM + "";

        Log.d(TAG, "getAllRecipeInstructionsByIdCursor: " + sqlSelect);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlSelect, null);

        return cursor;
    }

    /**
     * Update a recipe "favorited" status
     * @param recipeId
     * @param favorited
     */
    public void updateRecipeFavorite(int recipeId, int favorited) {
        String sqlUpdate = "UPDATE " + TABLE_RECIPE + " SET " + FAVORITED + " = "
                + favorited + " WHERE " + ID + " = " + recipeId;

        Log.d(TAG, "updateRecipeFavorite: " + sqlUpdate);

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sqlUpdate);
        db.close();
    }

    /**
     * Update a grocery list item "checked" status
     * @param groceryId
     * @param favorited
     */
    public void updateGroceryItemChecked(int groceryId, int favorited) {
        String sqlUpdate = "UPDATE " + TABLE_GROCERY_LIST + " SET " + CHECKED + " = "
                + favorited + " WHERE " + ID + " = " + groceryId;

        Log.d(TAG, "updateGroceryItemChecked: " + sqlUpdate);

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sqlUpdate);
        db.close();
    }

    /**
     * Update a recipe's information
     * @param recipeId
     */
    public void updateRecipe(int recipeId, String name, String time, String servings) {
        String sqlUpdate = "UPDATE " + TABLE_RECIPE + " SET "
                + NAME + " = '" + name + "', "
                + TIME + " = '" + time + "', "
                + SERVINGS + " = '" + servings
                + "' WHERE " + ID + " = " + recipeId;

        Log.d(TAG, "updateRecipe: " + sqlUpdate);

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sqlUpdate);
        db.close();
    }

    /**
     * Insert ingredient into the grocery list
     * @param ingredientId
     */
    public void insertGroceryListItem(int ingredientId) {
        String sqlInsert = "INSERT INTO " + TABLE_GROCERY_LIST + " VALUES(null, 0, "
                + ingredientId + ")";

        Log.d(TAG, "insertGroceryListItem: " + sqlInsert);

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sqlInsert);
        db.close();
    }

    /**
     * Insert an ingredient item
     * @param amount
     * @param name
     * @return
     */
    public long insertIngredientItem(String amount, String name) {
        ContentValues values = new ContentValues();
        values.put(AMOUNT, amount);
        values.put(NAME, name);

        SQLiteDatabase db = getWritableDatabase();
        long result = db.insert(TABLE_INGREDIENTS, null, values);
        db.close();

        return result;
    }

    /**
     * Insert an recipe item
     * @param name
     * @return
     */
    public long insertRecipeItem(String name) {
        ContentValues values = new ContentValues();
        values.put(NAME, name);

        SQLiteDatabase db = getWritableDatabase();
        long result = db.insert(TABLE_RECIPE, null, values);
        db.close();

        return result;
    }

    /**
     * Insert a recipe ingredient item
     * @param recipeId
     * @param ingredientId
     */
    public void insertRecipeIngredientItem(int recipeId, int ingredientId) {
        String sqlInsert = "INSERT INTO " + TABLE_RECIPE_INGREDIENTS + " VALUES(null, "
                + recipeId + ", " + ingredientId + ")";

        Log.d(TAG, "insertRecipeIngredientItem: " + sqlInsert);

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sqlInsert);
        db.close();
    }

    /**
     * insert a instruction item
     * @param recipeId
     * @param step
     * @param content
     * @param time
     * @param imageResource
     */
    public void insertInstructionItem(int recipeId, String step, String content, String time, int imageResource) {
        String sqlInsert = "INSERT INTO " + TABLE_INSTRUCTIONS + " VALUES(null, "
                + recipeId + ", '" + step + "', '" + content + "', '" + time + "', " + imageResource + ")";

        Log.d(TAG, "insertInstructionItem: " + sqlInsert);

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sqlInsert);
        db.close();
    }

    /**
     * Delete a recipe and its ingredients and instructions
     * @param recipeId the id matching the recipe to delete
     */
    public void deleteRecipeById(int recipeId) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_RECIPE_INGREDIENTS, RECIPE_ID + " = ?", new String[] { String.valueOf(recipeId)});
        db.delete(TABLE_INSTRUCTIONS, RECIPE_ID + " = ?", new String[] { String.valueOf(recipeId)});
        db.delete(TABLE_RECIPE, ID + " = ?", new String[] { String.valueOf(recipeId)});

        db.close();
    }

    /**
     * Delete a recipe's ingredient
     * @param recipeIngredientId the id matching the recipe ingredient to delete
     */
    public void deleteIngredientById(int recipeIngredientId) {
        String sqlDelete = "DELETE FROM " + TABLE_RECIPE_INGREDIENTS
                + " WHERE " + INGREDIENT_ID + " = " + recipeIngredientId;

        Log.d(TAG, "deleteIngredientById: " + sqlDelete);

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlDelete);
        db.close();
    }

    /**
     * Delete a recipe's instruction
     * @param instructionId the id matching the recipe instruction to delete
     */
    public void deleteInstructionById(int instructionId) {
        String sqlDelete = "DELETE FROM " + TABLE_INSTRUCTIONS
                + " WHERE " + ID + " = " + instructionId;

        Log.d(TAG, "deleteInstructionById: " + sqlDelete);

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlDelete);
        db.close();
    }
}
