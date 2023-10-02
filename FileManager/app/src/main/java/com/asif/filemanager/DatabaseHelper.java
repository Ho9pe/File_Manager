package com.asif.filemanager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final Context context;
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
        this.context = context;
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

    public void updateFile(String name, String path, String type, long size, long lastModified) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PATH, path);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_SIZE, size);
        values.put(COLUMN_LAST_MODIFIED, lastModified);

        // You should use the file's path or some other identifier to update the correct record
        int rowsAffected = db.update(TABLE_FILES, values, COLUMN_PATH + " = ?", new String[]{path});
        db.close();

        if (rowsAffected == 0) {
            // Handle the case where the file doesn't exist in the database and needs to be inserted
            insertFile(name, path, type, size, lastModified);
        }
    }

    public void deleteFile(String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_FILES, COLUMN_PATH + " = ?", new String[]{path});
        db.close();

        if (rowsDeleted == 0) {
            Toast.makeText(context, "File not found in the database.", Toast.LENGTH_SHORT).show();
        }
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
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(COLUMN_NAME);
            int pathColumnIndex = cursor.getColumnIndex(COLUMN_PATH);
            int typeColumnIndex = cursor.getColumnIndex(COLUMN_TYPE);
            int sizeColumnIndex = cursor.getColumnIndex(COLUMN_SIZE);
            int lastModifiedColumnIndex = cursor.getColumnIndex(COLUMN_LAST_MODIFIED);

            do {
                if (nameColumnIndex != -1 && pathColumnIndex != -1 && typeColumnIndex != -1 &&
                        sizeColumnIndex != -1 && lastModifiedColumnIndex != -1) {

                    String name = cursor.getString(nameColumnIndex);
                    String path = cursor.getString(pathColumnIndex);
                    String type = cursor.getString(typeColumnIndex);
                    long size = cursor.getLong(sizeColumnIndex);
                    long lastModified = cursor.getLong(lastModifiedColumnIndex);

                    FileModel fileModel = new FileModel(name, path, type, size, lastModified);
                    fileList.add(fileModel);
                } else {
                    Log.e("CursorError", "One or more columns are missing in the cursor.");
                }
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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

