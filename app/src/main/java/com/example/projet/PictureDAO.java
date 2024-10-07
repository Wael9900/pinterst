package com.example.projet;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PictureDAO {
    private SQLiteDatabase db;
    private MyDBHelper dbHelper;

    public PictureDAO(Context context) {
        dbHelper = new MyDBHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insertURL(String url) {
        ContentValues values = new ContentValues();
        values.put(MyDBHelper.COLUMN_URL, url);
        long newRowId = db.insert(MyDBHelper.TABLE_NAME, null, values);
        if (newRowId == -1) {
            Log.e("PictureDAO", "Failed to insert URL: " + url);
        } else {
            Log.d("PictureDAO", "Inserted URL: " + url);
        }
    }

    public List<String> getAllURLs() {
        List<String> urls = new ArrayList<>();
        Cursor cursor = db.query(MyDBHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String url = cursor.getString(cursor.getColumnIndex(MyDBHelper.COLUMN_URL));
                urls.add(url);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return urls;
    }
}
