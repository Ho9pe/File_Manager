package com.asif.filemanager;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "file_manager.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_FILES = "files";
    private static final String COLUMN_ID = "file_id";
    private static final String COLUMN_NAME = "file_name";
    private static final String COLUMN_PATH = "file_path";
    private static final String COLUMN_TYPE = "file_type";
    private static final String COLUMN_SIZE = "file_size";
    private static final String COLUMN_LAST_MODIFIED = "last_modified";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_FILES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_PATH + " TEXT, "
                + COLUMN_TYPE + " TEXT, "
                + COLUMN_SIZE + " INTEGER, "
                + COLUMN_LAST_MODIFIED + " INTEGER "
                + ")";
        db.execSQL(createTableQuery);
    }
    public boolean isEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_FILES, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count == 0;
    }
    public void insertFile(String name, String path, String type, long size, long lastModified) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PATH, path);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_SIZE, size);
        values.put(COLUMN_LAST_MODIFIED, lastModified);
        db.insert(TABLE_FILES, null, values);
        db.close();
    }

    public ArrayList<FileModel> getAllFiles() {
        ArrayList<FileModel> fileList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                COLUMN_NAME,
                COLUMN_PATH,
                COLUMN_TYPE,
                COLUMN_SIZE,
                COLUMN_LAST_MODIFIED
        };
        Cursor cursor = db.query(
                TABLE_FILES,
                projection,
                null, // selection
                null, // selectionArgs
                null, // groupBy
                null, // having
                null  // orderBy
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(COLUMN_PATH));
                @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex(COLUMN_TYPE));
                @SuppressLint("Range") long size = cursor.getLong(cursor.getColumnIndex(COLUMN_SIZE));
                @SuppressLint("Range") long lastModified = cursor.getLong(cursor.getColumnIndex(COLUMN_LAST_MODIFIED));

                FileModel fileModel = new FileModel(name, path, type, size, lastModified);
                fileList.add(fileModel);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return fileList;
    }
    public static class FileModel {
        private final String name;
        private final String path;
        private final String type;
        private final long size;
        private final long lastModified;

        // Constructor, getters, setters, etc.
        public  FileModel(String name, String path, String type, long size, long lastModified) {
            this.name = name;
            this.path = path;
            this.type = type;
            this.size = size;
            this.lastModified = lastModified;

        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }

        public String getType() {
            return type;
        }

        public long getSize() {
            return size;
        }

        public long getLastModified() {
            return lastModified;
        }
    }











//    @SuppressLint("Range")
//    public long getLastInsertedFileDate() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        String query = "SELECT " + COLUMN_LAST_MODIFIED + " FROM " + TABLE_FILES +
//                " ORDER BY " + COLUMN_LAST_MODIFIED + " DESC LIMIT 1";
//        Cursor cursor = db.rawQuery(query, null);
//
//        long lastModifiedDate = 0;
//
//        if (cursor != null && cursor.moveToFirst()) {
//            lastModifiedDate = cursor.getLong(cursor.getColumnIndex(COLUMN_LAST_MODIFIED));
//            cursor.close();
//        }
//
//        return lastModifiedDate;
//    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

