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
    private static final int DATABASE_VERSION = 7;

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

        String CREATE_REPORTS_TABLE = "CREATE TABLE review_reports ("
                + "reportId INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "reviewId INTEGER NOT NULL, "
                + "reporterId INTEGER NOT NULL, "
                + "reason TEXT NOT NULL, "
                + "explanation TEXT, "
                + "status TEXT NOT NULL DEFAULT 'PENDING', "
                + "FOREIGN KEY(reviewId) REFERENCES reviews(reviewId), "
                + "FOREIGN KEY(reporterId) REFERENCES users(userId))";
        db.execSQL(CREATE_REPORTS_TABLE);

        // Seed data
        db.execSQL("INSERT INTO orders DEFAULT VALUES");
        db.execSQL("INSERT INTO stalls (stallName, location, openingTime, closingTime, description, isOpen) VALUES ('Mangan Tamu Pu', '8th Floor | FEU Tech Canteen', '7:00 AM', '4:00 PM', 'Kapampangan dishes', 1)");
        db.execSQL("INSERT INTO stalls (stallName, location, openingTime, closingTime, description, isOpen) VALUES ('Sisig-Ok', '8th Floor | FEU Tech Canteen', '7:00 AM', '4:00 PM', 'Sizzling favorites', 1)");

        db.execSQL("INSERT INTO menu_items (stallId, foodName, description, category, price, isAvailable) VALUES (2, 'Pork Sisig', 'Crispy pork sisig with egg', 'Rice Meals', 8000, 1)");
        db.execSQL("INSERT INTO menu_items (stallId, foodName, description, category, price, isAvailable) VALUES (1, 'Carbonara', 'Creamy white sauce pasta', 'Pasta', 5000, 1)");
        db.execSQL("INSERT INTO users (name, email, password, role) VALUES ('Tammy', 'admin@cafetamgo.com', 'admin123', 'ADMIN')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS review_reports");
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

    public String getUserNameById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_NAME},
                COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null, null);
        String name = null;
        if (cursor != null && cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            cursor.close();
        }
        db.close();
        return name;
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

    public boolean addReview(int userId, Integer stallId, int menuItemId, int rating, String comment, String reviewDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("stallId", stallId);
        values.put("menuItemId", menuItemId);
        values.put("orderId", 1);
        values.put("rating", rating);
        values.put("comment", comment);
        values.put("reviewDate", reviewDate);
        values.put("isVisible", 1);

        long result = db.insert("reviews", null, values);
        db.close();
        return result != -1;
    }

    public List<String> getReviewsForProduct(int menuItemId) {
        List<String> reviewList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT r.*, u.name FROM reviews r JOIN users u ON r.userId = u.userId " +
                "WHERE r.menuItemId = ? AND r.isVisible = 1 ORDER BY r.reviewId DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(menuItemId)});

        if (cursor.moveToFirst()) {
            do {
                String userName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                int rating = cursor.getInt(cursor.getColumnIndexOrThrow("rating"));
                String comment = cursor.getString(cursor.getColumnIndexOrThrow("comment"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("reviewDate"));

                reviewList.add(userName + " - ★" + rating + "/5\n\"" + comment + "\"\n(" + date + ")");
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return reviewList;
    }

    public List<ProductReviewItem> getProductReviewItems(int menuItemId) {
        List<ProductReviewItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT r.reviewId, r.userId, u.name AS reviewerName, r.rating, r.comment, r.reviewDate " +
                "FROM reviews r JOIN users u ON r.userId = u.userId " +
                "WHERE r.menuItemId = ? AND r.isVisible = 1 " +
                "ORDER BY r.reviewId DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(menuItemId)});
        if (cursor.moveToFirst()) {
            do {
                list.add(new ProductReviewItem(
                        cursor.getInt(cursor.getColumnIndexOrThrow("reviewId")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("userId")),
                        cursor.getString(cursor.getColumnIndexOrThrow("reviewerName")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("rating")),
                        cursor.getString(cursor.getColumnIndexOrThrow("comment")),
                        cursor.getString(cursor.getColumnIndexOrThrow("reviewDate"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public boolean reportReview(int reviewId, int reporterId, String reason, String explanation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("reviewId", reviewId);
        values.put("reporterId", reporterId > 0 ? reporterId : 1);
        values.put("reason", reason);
        values.put("explanation", explanation);
        values.put("status", "PENDING");

        long result = db.insert("review_reports", null, values);
        db.close();
        return result != -1;
    }

    public boolean addStall(String name, String location, String open, String close, String desc, int isOpen) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("stallName", name);
        cv.put("location", location);
        cv.put("openingTime", open);
        cv.put("closingTime", close);
        cv.put("description", desc);
        cv.put("isOpen", isOpen);
        long res = db.insert("stalls", null, cv);
        db.close();
        return res != -1;
    }

    public boolean updateStall(int id, String name, String location, String open, String close, String desc, int isOpen) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("stallName", name);
        cv.put("location", location);
        cv.put("openingTime", open);
        cv.put("closingTime", close);
        cv.put("description", desc);
        cv.put("isOpen", isOpen);
        int res = db.update("stalls", cv, "stallId = ?", new String[]{String.valueOf(id)});
        db.close();
        return res > 0;
    }

    public boolean addMenuItem(int stallId, String name, String desc, String category, int priceInCentavos, int isAvailable) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("stallId", stallId);
        cv.put("foodName", name);
        cv.put("description", desc);
        cv.put("category", category);
        cv.put("price", priceInCentavos);
        cv.put("isAvailable", isAvailable);
        long res = db.insert("menu_items", null, cv);
        db.close();
        return res != -1;
    }

    public boolean updateMenuItem(int menuItemId, String name, String desc, String category, int priceInCentavos, int isAvailable) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("foodName", name);
        cv.put("description", desc);
        cv.put("category", category);
        cv.put("price", priceInCentavos);
        cv.put("isAvailable", isAvailable);
        int res = db.update("menu_items", cv, "menuItemId = ?", new String[]{String.valueOf(menuItemId)});
        db.close();
        return res > 0;
    }

    public boolean deleteMenuItem(int menuItemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete("menu_items", "menuItemId = ?", new String[]{String.valueOf(menuItemId)});
        db.close();
        return res > 0;
    }

    public List<ModerationReview> getReportedReviews() {
        List<ModerationReview> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT r.reviewId, r.menuItemId, r.rating, r.comment, r.isVisible, " +
                "u.name AS reviewerName, m.foodName, s.stallName, " +
                "COUNT(rep.reportId) AS reportCount, " +
                "GROUP_CONCAT(DISTINCT rep.reason) AS reasons, " +
                "GROUP_CONCAT(rep.explanation, ' | ') AS explanations " +
                "FROM reviews r " +
                "JOIN review_reports rep ON r.reviewId = rep.reviewId " +
                "JOIN users u ON r.userId = u.userId " +
                "JOIN menu_items m ON r.menuItemId = m.menuItemId " +
                "JOIN stalls s ON m.stallId = s.stallId " +
                "WHERE r.isVisible = 1 AND rep.status = 'PENDING' " +
                "GROUP BY r.reviewId " +
                "ORDER BY r.reviewId DESC";

        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                list.add(new ModerationReview(
                        c.getInt(c.getColumnIndexOrThrow("reviewId")),
                        c.getInt(c.getColumnIndexOrThrow("menuItemId")),
                        c.getString(c.getColumnIndexOrThrow("foodName")),
                        c.getString(c.getColumnIndexOrThrow("stallName")),
                        c.getString(c.getColumnIndexOrThrow("reviewerName")),
                        c.getInt(c.getColumnIndexOrThrow("rating")),
                        c.getString(c.getColumnIndexOrThrow("comment")),
                        c.getInt(c.getColumnIndexOrThrow("isVisible")) == 1,
                        c.getInt(c.getColumnIndexOrThrow("reportCount")),
                        c.getString(c.getColumnIndexOrThrow("reasons")),
                        c.getString(c.getColumnIndexOrThrow("explanations"))
                ));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    public List<ModerationReview> getHiddenReviews() {
        List<ModerationReview> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT r.reviewId, r.menuItemId, r.rating, r.comment, r.isVisible, " +
                "u.name AS reviewerName, m.foodName, s.stallName, " +
                "COUNT(rep.reportId) AS reportCount, " +
                "GROUP_CONCAT(DISTINCT rep.reason) AS reasons, " +
                "GROUP_CONCAT(rep.explanation, ' | ') AS explanations " +
                "FROM reviews r " +
                "LEFT JOIN review_reports rep ON r.reviewId = rep.reviewId " +
                "JOIN users u ON r.userId = u.userId " +
                "JOIN menu_items m ON r.menuItemId = m.menuItemId " +
                "JOIN stalls s ON m.stallId = s.stallId " +
                "WHERE r.isVisible = 0 " +
                "GROUP BY r.reviewId " +
                "ORDER BY r.reviewId DESC";

        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                list.add(new ModerationReview(
                        c.getInt(c.getColumnIndexOrThrow("reviewId")),
                        c.getInt(c.getColumnIndexOrThrow("menuItemId")),
                        c.getString(c.getColumnIndexOrThrow("foodName")),
                        c.getString(c.getColumnIndexOrThrow("stallName")),
                        c.getString(c.getColumnIndexOrThrow("reviewerName")),
                        c.getInt(c.getColumnIndexOrThrow("rating")),
                        c.getString(c.getColumnIndexOrThrow("comment")),
                        false,
                        c.getInt(c.getColumnIndexOrThrow("reportCount")),
                        c.getString(c.getColumnIndexOrThrow("reasons")),
                        c.getString(c.getColumnIndexOrThrow("explanations"))
                ));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    public boolean hideReview(int reviewId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues revValues = new ContentValues();
        revValues.put("isVisible", 0);
        int updated = db.update("reviews", revValues, "reviewId = ?", new String[]{String.valueOf(reviewId)});

        ContentValues repValues = new ContentValues();
        repValues.put("status", "RESOLVED");
        db.update("review_reports", repValues, "reviewId = ?", new String[]{String.valueOf(reviewId)});
        db.close();
        return updated > 0;
    }

    public boolean dismissReportsForReview(int reviewId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues repValues = new ContentValues();
        repValues.put("status", "DISMISSED");
        int updated = db.update("review_reports", repValues, "reviewId = ?", new String[]{String.valueOf(reviewId)});
        db.close();
        return updated > 0;
    }

    public boolean restoreReview(int reviewId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues revValues = new ContentValues();
        revValues.put("isVisible", 1);
        int updated = db.update("reviews", revValues, "reviewId = ?", new String[]{String.valueOf(reviewId)});
        db.close();
        return updated > 0;
    }

    public int getStallCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM stalls", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        db.close();
        return count;
    }

    public int getProductCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM menu_items", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        db.close();
        return count;
    }

    public int getPendingReportCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(DISTINCT r.reviewId) FROM reviews r " +
                        "JOIN review_reports rep ON r.reviewId = rep.reviewId " +
                        "WHERE r.isVisible = 1 AND rep.status = 'PENDING'",
                null
        );
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        db.close();
        return count;
    }
}