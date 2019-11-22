//Source referenced: https://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/

package com.example.recipepal.helpers;

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
    public static final String CUISINE = "cuisine";

    // columns for Instructions table
    public static final String RECIPE_ID = "recipeId";
    public static final String STEP_NUM = "stepNum";
    public static final String CONTENT = "content";
    public static final String BOOL_TIMER_NEEDED = "boolTimerNeeded";

    // Recipe table create statement
    private static final String CREATE_TABLE_RECIPE = "CREATE TABLE " + TABLE_RECIPE + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NAME + " TEXT, "
            + IMAGE_RESOURCE + " INTEGER, "
            + TIME + " TEXT, "
            + CUISINE + " TEXT)";

    // Instruction table create statement
    private static final String CREATE_TABLE_INSTRUCTIONS = "CREATE TABLE " + TABLE_INSTRUCTIONS + "("
            + ID + " INTEGER, "
            + RECIPE_ID  + " INTEGER, "
            + STEP_NUM + " INTEGER, "
            + CONTENT + " TEXT, "
            + BOOL_TIMER_NEEDED + " INTEGER, "
            + TIME + " TIME, "
            + IMAGE_RESOURCE + " INTEGER, "
            + "PRIMARY KEY(" + ID + ", " + RECIPE_ID + ", " + STEP_NUM + "), "
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
            + ID + " INTEGER, "
            + CHECKED + " INTEGER, "
            + INGREDIENT_ID + " INTEGER,"
            + " PRIMARY KEY(" + ID + ", " + INGREDIENT_ID + "),"
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

    public Cursor getAllGroceryListCursor() {
        String sqlSelect = "SELECT i.*, g." + CHECKED
                + " FROM " + TABLE_INGREDIENTS + " i, " + TABLE_GROCERY_LIST + " g"
                + " WHERE g." + INGREDIENT_ID + " = i." + ID;

        Log.d(TAG, "getAllGroceryListCursor: " + sqlSelect);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlSelect, null);

        return cursor;
    }
}
