package com.example.cafetamgo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cafetamgo.db";
    private static final int DATABASE_VERSION = 2; // Bumped version to recreate tables

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

        String CREATE_STALLS_TABLE = "CREATE TABLE stalls ("
                + "stallId INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "stallName TEXT NOT NULL, "
                + "location TEXT NOT NULL, "
                + "openingTime TEXT NOT NULL, "
                + "closingTime TEXT NOT NULL, "
                + "description TEXT, "
                + "isOpen INTEGER NOT NULL)";
        db.execSQL(CREATE_STALLS_TABLE);

        String CREATE_MENU_TABLE = "CREATE TABLE menu_items ("
                + "menuItemId INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "stallId INTEGER NOT NULL, "
                + "foodName TEXT NOT NULL, "
                + "description TEXT, "
                + "category TEXT NOT NULL, "
                + "price INTEGER NOT NULL, "
                + "imageUri TEXT, "
                + "isAvailable INTEGER NOT NULL, "
                + "FOREIGN KEY(stallId) REFERENCES stalls(stallId))";
        db.execSQL(CREATE_MENU_TABLE);

        String CREATE_ORDERS_TABLE = "CREATE TABLE orders ("
                + "orderId INTEGER PRIMARY KEY AUTOINCREMENT)";
        db.execSQL(CREATE_ORDERS_TABLE);

        String CREATE_REVIEWS_TABLE = "CREATE TABLE reviews ("
                + "reviewId INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "userId INTEGER NOT NULL, "
                + "stallId INTEGER, "
                + "menuItemId INTEGER NOT NULL, "
                + "orderId INTEGER NOT NULL, "
                + "rating INTEGER NOT NULL, "
                + "comment TEXT, "
                + "reviewDate TEXT NOT NULL, "
                + "isVisible INTEGER NOT NULL DEFAULT 1, "
                + "FOREIGN KEY(userId) REFERENCES users(userId), "
                + "FOREIGN KEY(stallId) REFERENCES stalls(stallId), "
                + "FOREIGN KEY(menuItemId) REFERENCES menu_items(menuItemId), "
                + "FOREIGN KEY(orderId) REFERENCES orders(orderId))";
        db.execSQL(CREATE_REVIEWS_TABLE);

        // Add this inside onCreate() right after creating the tables
        db.execSQL("INSERT INTO stalls (stallName, location, openingTime, closingTime, description, isOpen) VALUES ('Mangan Tamu Pu', '8th Floor | FEU Tech Canteen', '7:00 AM', '4:00 PM', 'Kapampangan dishes', 1)");
        db.execSQL("INSERT INTO stalls (stallName, location, openingTime, closingTime, description, isOpen) VALUES ('Sisig-Ok', '8th Floor | FEU Tech Canteen', '7:00 AM', '4:00 PM', 'Sizzling favorites', 1)");

        db.execSQL("INSERT INTO menu_items (stallId, foodName, description, category, price, isAvailable) VALUES (2, 'Pork Sisig', 'Crispy pork sisig with egg', 'Main Dish', 8000, 1)");
        db.execSQL("INSERT INTO menu_items (stallId, foodName, description, category, price, isAvailable) VALUES (1, 'Carbonara', 'Creamy white sauce pasta', 'Pasta', 5000, 1)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS reviews");
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS menu_items");
        db.execSQL("DROP TABLE IF EXISTS stalls");
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

    public List<Stall> getAllStalls() {
        List<Stall> stallList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT s.*, COALESCE(AVG(r.rating), 0.0) as avgRating, COUNT(r.reviewId) as revCount " +
                "FROM stalls s LEFT JOIN reviews r ON s.stallId = r.stallId AND r.isVisible = 1 " +
                "GROUP BY s.stallId";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Stall stall = new Stall(
                        cursor.getInt(cursor.getColumnIndexOrThrow("stallId")),
                        cursor.getString(cursor.getColumnIndexOrThrow("stallName")),
                        cursor.getString(cursor.getColumnIndexOrThrow("location")),
                        cursor.getString(cursor.getColumnIndexOrThrow("openingTime")),
                        cursor.getString(cursor.getColumnIndexOrThrow("closingTime")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("isOpen")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("avgRating")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("revCount"))
                );
                stallList.add(stall);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return stallList;
    }

    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> menuList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT m.*, s.stallName, COALESCE(AVG(r.rating), 0.0) as avgRating, COUNT(r.reviewId) as revCount " +
                "FROM menu_items m " +
                "JOIN stalls s ON m.stallId = s.stallId " +
                "LEFT JOIN reviews r ON m.menuItemId = r.menuItemId AND r.isVisible = 1 " +
                "GROUP BY m.menuItemId";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                MenuItem item = new MenuItem(
                        cursor.getInt(cursor.getColumnIndexOrThrow("menuItemId")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("stallId")),
                        cursor.getString(cursor.getColumnIndexOrThrow("foodName")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("price")),
                        cursor.getString(cursor.getColumnIndexOrThrow("imageUri")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("isAvailable")),
                        cursor.getString(cursor.getColumnIndexOrThrow("stallName")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("avgRating")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("revCount"))
                );
                menuList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return menuList;
    }
}