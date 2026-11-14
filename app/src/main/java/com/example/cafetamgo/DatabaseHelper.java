package com.example.cafetamgo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cafetamgo.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "userId";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_EMAIL + " TEXT NOT NULL UNIQUE, "
                + COLUMN_PASSWORD + " TEXT NOT NULL, "
                + COLUMN_ROLE + " TEXT NOT NULL" + ")";
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_ROLE, user.getRole());

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public User login(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_PASSWORD, COLUMN_ROLE},
                COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{email, password},
                null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE))
            );
            cursor.close();
        }
        db.close();
        return user;
    }

    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID},
                COLUMN_EMAIL + " = ?",
                new String[]{email},
                null, null, null);

        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return exists;
    }
}