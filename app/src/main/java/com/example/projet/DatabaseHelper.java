package com.example.projet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "image_metadata.db";
    public static final String TABLE_IMAGES = "images";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FILE_PATH = "file_path";

    // SQL statement to create the images table
    private static final String SQL_CREATE_TABLE_IMAGES =
            "CREATE TABLE " + TABLE_IMAGES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_FILE_PATH + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the images table
        db.execSQL(SQL_CREATE_TABLE_IMAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
    }
}
