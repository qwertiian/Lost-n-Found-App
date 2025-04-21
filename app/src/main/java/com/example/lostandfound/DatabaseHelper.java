package com.example.lostandfound;

import static android.app.DownloadManager.COLUMN_ID;
import static android.app.DownloadManager.COLUMN_STATUS;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LostAndFound.db";
    private static final int DATABASE_VERSION = 1;

    // Users table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_USER_PHONE = "user_phone";
    private static final String COLUMN_IS_ADMIN = "is_admin";

    // Items table
    private static final String TABLE_ITEMS = "items";
    private static final String COLUMN_ITEM_ID = "item_id";
    private static final String COLUMN_ITEM_NAME = "item_name";
    private static final String COLUMN_ITEM_DESCRIPTION = "item_description";
    private static final String COLUMN_ITEM_DATE = "item_date";
    private static final String COLUMN_ITEM_LOCATION = "item_location";
    private static final String COLUMN_ITEM_IMAGE = "item_image";
    private static final String COLUMN_ITEM_STATUS = "item_status"; // "lost", "claimed", "returned"
    private static final String COLUMN_REPORTER_ID = "reporter_id";
    private static final String COLUMN_CLAIMER_ID = "claimer_id";
    private static final String COLUMN_CLAIM_PROOF = "claim_proof";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_NAME + " TEXT,"
                + COLUMN_USER_EMAIL + " TEXT,"
                + COLUMN_USER_PHONE + " TEXT,"
                + COLUMN_IS_ADMIN + " INTEGER DEFAULT 0)";

        // Create items table
        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
                + COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ITEM_NAME + " TEXT,"
                + COLUMN_ITEM_DESCRIPTION + " TEXT,"
                + COLUMN_ITEM_DATE + " TEXT,"
                + COLUMN_ITEM_LOCATION + " TEXT,"
                + COLUMN_ITEM_IMAGE + " TEXT,"
                + COLUMN_ITEM_STATUS + " TEXT DEFAULT 'lost',"
                + COLUMN_REPORTER_ID + " INTEGER,"
                + COLUMN_CLAIMER_ID + " INTEGER,"
                + COLUMN_CLAIM_PROOF + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_REPORTER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "),"
                + "FOREIGN KEY(" + COLUMN_CLAIMER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_ITEMS_TABLE);

        // Insert admin user (username: admin, password: admin123)
        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_USER_NAME, "admin");
        adminValues.put(COLUMN_USER_EMAIL, "admin@lostandfound.com");
        adminValues.put(COLUMN_USER_PHONE, "0000000000");
        adminValues.put(COLUMN_IS_ADMIN, 1);
        db.insert(TABLE_USERS, null, adminValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    // User methods
    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PHONE, user.getPhone());
        values.put(COLUMN_IS_ADMIN, user.isAdmin() ? 1 : 0);

        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    @SuppressLint("Range")
    public User getUser(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_USERS,
                    new String[]{COLUMN_USER_ID, COLUMN_USER_NAME, COLUMN_USER_EMAIL, COLUMN_USER_PHONE, COLUMN_IS_ADMIN},
                    COLUMN_USER_ID + "=?",
                    new String[]{String.valueOf(userId)}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                user = new User(
                        cursor.getLong(cursor.getColumnIndex(COLUMN_USER_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_USER_PHONE)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_IS_ADMIN)) == 1
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return user;
    }

    @SuppressLint("Range")
    public User authenticateAdmin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_USERS,
                    new String[]{COLUMN_USER_ID, COLUMN_USER_NAME, COLUMN_USER_EMAIL, COLUMN_USER_PHONE, COLUMN_IS_ADMIN},
                    COLUMN_USER_NAME + "=? AND " + COLUMN_IS_ADMIN + "=?",
                    new String[]{username, "1"},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                // Verify password (in real app, use proper password hashing)
                if (password.equals("admin123")) {
                    user = new User(
                            cursor.getLong(cursor.getColumnIndex(COLUMN_USER_ID)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_USER_PHONE)),
                            true
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return user;
    }

    public boolean updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PHONE, user.getPhone());

        int rowsAffected = db.update(TABLE_USERS, values,
                COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});

        db.close();
        return rowsAffected > 0;
    }

    @SuppressLint("Range")
    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_ITEMS,
                    null,
                    null, null, null, null,
                    COLUMN_ITEM_DATE + " DESC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Item item = cursorToItem(cursor);
                    itemList.add(item);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return itemList;
    }

    @SuppressLint("Range")
    public Item getItem(long itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Item item = null;
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_ITEMS,
                    null,
                    COLUMN_ITEM_ID + "=?",
                    new String[]{String.valueOf(itemId)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                item = cursorToItem(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return item;
    }

    public int updateItemStatus(long itemId, String status, long claimerId, String claimProof) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_STATUS, status);

        if (claimerId != -1) {
            values.put(COLUMN_CLAIMER_ID, claimerId);
        } else {
            values.putNull(COLUMN_CLAIMER_ID);
        }

        if (claimProof != null) {
            values.put(COLUMN_CLAIM_PROOF, claimProof);
        } else {
            values.putNull(COLUMN_CLAIM_PROOF);
        }

        int result = db.update(TABLE_ITEMS, values,
                COLUMN_ITEM_ID + " = ?",
                new String[]{String.valueOf(itemId)});

        db.close();
        return result;
    }

    @SuppressLint("Range")
    public List<Item> getItemsByStatus(String status) {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_ITEMS,
                    null,
                    COLUMN_ITEM_STATUS + " = ?",
                    new String[]{status},
                    null, null,
                    COLUMN_ITEM_DATE + " DESC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Item item = cursorToItem(cursor);
                    items.add(item);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return items;
    }

    // Replace the getPendingClaims method with this corrected version:
    public List<Item> getPendingClaims() {
        List<Item> claims = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_ITEMS +
                " WHERE " + COLUMN_ITEM_STATUS + " = 'claimed'";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Item item = cursorToItem(cursor);
                claims.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return claims;
    }

    // Also fix the approveClaim and rejectClaim methods:
    public boolean approveClaim(long itemId, long claimerId, String claimProof) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_STATUS, "returned");
        values.put(COLUMN_CLAIMER_ID, claimerId);
        values.put(COLUMN_CLAIM_PROOF, claimProof);

        int rowsAffected = db.update(TABLE_ITEMS, values,
                COLUMN_ITEM_ID + " = ?", new String[]{String.valueOf(itemId)});
        db.close();
        return rowsAffected > 0;
    }

    public boolean rejectClaim(long itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_STATUS, "lost");
        values.putNull(COLUMN_CLAIMER_ID);
        values.putNull(COLUMN_CLAIM_PROOF);

        int rowsAffected = db.update(TABLE_ITEMS, values,
                COLUMN_ITEM_ID + " = ?", new String[]{String.valueOf(itemId)});
        db.close();
        return rowsAffected > 0;
    }
    @SuppressLint("Range")
    private Item cursorToItem(Cursor cursor) {
        Item item = new Item();
        item.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ITEM_ID)));
        item.setName(cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_NAME)));
        item.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_DESCRIPTION)));
        item.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_DATE)));
        item.setLocation(cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_LOCATION)));
        item.setImage(cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_IMAGE)));
        item.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_STATUS)));
        item.setReporterId(cursor.getLong(cursor.getColumnIndex(COLUMN_REPORTER_ID)));

        // Handle nullable columns
        int claimerIdIndex = cursor.getColumnIndex(COLUMN_CLAIMER_ID);
        if (!cursor.isNull(claimerIdIndex)) {
            item.setClaimerId(cursor.getLong(claimerIdIndex));
        } else {
            item.setClaimerId(-1); // or whatever default value you prefer
        }

        int proofIndex = cursor.getColumnIndex(COLUMN_CLAIM_PROOF);
        if (!cursor.isNull(proofIndex)) {
            item.setClaimProof(cursor.getString(proofIndex));
        }

        return item;
    }

    public long addItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ITEM_NAME, item.getName());
            values.put(COLUMN_ITEM_DESCRIPTION, item.getDescription());
            values.put(COLUMN_ITEM_DATE, item.getDate());
            values.put(COLUMN_ITEM_LOCATION, item.getLocation());
            values.put(COLUMN_ITEM_IMAGE, item.getImage());
            values.put(COLUMN_ITEM_STATUS, item.getStatus());
            values.put(COLUMN_REPORTER_ID, item.getReporterId());

            id = db.insert(TABLE_ITEMS, null, values);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error adding item", e);
        } finally {
            db.close();
        }
        return id;
    }
}