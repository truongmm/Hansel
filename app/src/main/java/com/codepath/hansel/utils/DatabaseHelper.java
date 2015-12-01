package com.codepath.hansel.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.codepath.hansel.models.Pebble;
import com.codepath.hansel.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Info
    private static final String DATABASE_NAME = "hanselDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_PEBBLES = "pebbles";

    // Users Table Columns
    public static final String KEY_USER_ID = "id";
    public static final String KEY_USER_FIRST_NAME = "firstName";
    public static final String KEY_USER_LAST_NAME = "lastName";
    private static final String KEY_USER_CREATED_AT = "createdAt";
    private static final String KEY_USER_UPDATED_AT = "updatedAt";
    // https://stackoverflow.com/questions/14461851/how-to-have-an-automatic-timestamp-in-sqlite

    // Pebbles Table Columns
    public static final String KEY_PEBBLE_ID = "id";
    public static final String KEY_PEBBLE_USER_ID_FK = "userId";
    public static final String KEY_PEBBLE_TIMESTAMP = "timestamp";
    public static final String KEY_PEBBLE_LATITUDE = "latitude";
    public static final String KEY_PEBBLE_LONGITUDE = "longitude";
    private static final String KEY_PEBBLE_CREATED_AT = "createdAt";
    private static final String KEY_PEBBLE_UPDATED_AT = "updatedAt";
    // https://stackoverflow.com/questions/9701616/how-to-insert-double-and-float-values-to-sqlite

    private static DatabaseHelper instance;

    private static final String TAG = "DatabaseHelper";

    public static synchronized DatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
                "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY," +
                KEY_USER_FIRST_NAME + " TEXT," +
                KEY_USER_LAST_NAME + " TEXT," +
                KEY_USER_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                KEY_USER_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";

        String CREATE_PEBBLES_TABLE = "CREATE TABLE " + TABLE_PEBBLES +
                "(" +
                KEY_PEBBLE_ID + " INTEGER PRIMARY KEY," +
                KEY_PEBBLE_USER_ID_FK + " INTEGER REFERENCES " + TABLE_USERS + "," +
                KEY_PEBBLE_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                KEY_PEBBLE_LATITUDE + " REAL," +
                KEY_PEBBLE_LONGITUDE + " REAL," +
                KEY_PEBBLE_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                KEY_PEBBLE_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_PEBBLES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEBBLES);
            onCreate(db);
        }
    }

    public long addPebble(Pebble pebble) {
        SQLiteDatabase db = getWritableDatabase();
        long pebbleId = -1;
        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_PEBBLE_USER_ID_FK, pebble.getUser().getId());
            values.put(KEY_PEBBLE_LATITUDE, pebble.getLatitude());
            values.put(KEY_PEBBLE_LONGITUDE, pebble.getLongitude());
            values.put(KEY_PEBBLE_TIMESTAMP, dateTime);
            values.put(KEY_PEBBLE_CREATED_AT, dateTime);
            values.put(KEY_PEBBLE_UPDATED_AT, dateTime);

            pebbleId = db.insertOrThrow(TABLE_PEBBLES, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add pebble to database");
        } finally {
            db.endTransaction();
        }
        return pebbleId;
    }

    public long addPebble(Pebble pebble, Date date) {
        SQLiteDatabase db = getWritableDatabase();
        long pebbleId = -1;
        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_PEBBLE_USER_ID_FK, pebble.getUser().getId());
            values.put(KEY_PEBBLE_LATITUDE, pebble.getLatitude());
            values.put(KEY_PEBBLE_LONGITUDE, pebble.getLongitude());
            values.put(KEY_PEBBLE_TIMESTAMP, dateTime);
            values.put(KEY_PEBBLE_CREATED_AT, dateTime);
            values.put(KEY_PEBBLE_UPDATED_AT, dateTime);

            pebbleId = db.insertOrThrow(TABLE_PEBBLES, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add pebble to database");
        } finally {
            db.endTransaction();
        }
        return pebbleId;
    }

    public User getUser(long id) {
        SQLiteDatabase db = getReadableDatabase();
        User user = null;
        Cursor cursor = db.query(TABLE_USERS, new String[]{"*"}, KEY_USER_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null, "1");
        try {
            if (cursor.moveToFirst()) {
                user = User.fromDB(cursor);
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get pebbles from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return user;
    }

    public ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{"*"}, null, null, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    users.add(User.fromDB(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return users;
    }

    public long addOrUpdateUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        long userId = -1;
        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_FIRST_NAME, user.getFirstName());
            values.put(KEY_USER_LAST_NAME, user.getLastName());
            values.put(KEY_PEBBLE_CREATED_AT, dateTime);
            values.put(KEY_PEBBLE_UPDATED_AT, dateTime);

            int rows = db.update(TABLE_USERS, values, KEY_USER_ID + "= ?", new String[]{String.valueOf(user.getId())});

            if (rows == 1) {
                userId = user.getId();
            } else {
                userId = db.insertOrThrow(TABLE_USERS, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add or update user");
        } finally {
            db.endTransaction();
        }
        return userId;
    }

    public ArrayList<Pebble> getAllPebbles(){
        return getPebbles(null, null, null, false);
    }

    public ArrayList<Pebble> getAllPebbles(boolean desc) {
        return getPebbles(null, null, null, desc);
    }

    public ArrayList<Pebble> getPebblesForUsers(User[] whiteList, boolean desc) {
        return getPebbles(whiteList, null, null, desc);
    }

    public ArrayList<Pebble> getPebblesForUsersBeforeDate(User[] whiteList, Date date, boolean desc){
        return getPebbles(whiteList, null, date, desc);
    }

    public ArrayList<Pebble> getPebblesWithoutUsers(User[] blackList, boolean desc) {
        return getPebbles(null, blackList, null, desc);
    }

    public ArrayList<Pebble> getPebbles(User[] whiteList, User[] blackList, Date date, boolean desc) {
        ArrayList<Pebble> pebbles = new ArrayList<>();
        String query = "SELECT * FROM %s LEFT OUTER JOIN %s ON %s.%s = %s.%s WHERE 1 = 1";
        ArrayList<Object> params = new ArrayList<>();
        params.addAll(Arrays.asList(TABLE_PEBBLES, TABLE_USERS, TABLE_PEBBLES, KEY_PEBBLE_USER_ID_FK, TABLE_USERS, KEY_USER_ID));

        if (whiteList != null) {
            query += " AND %s.%s IN (%s)";
            params.addAll(Arrays.asList(TABLE_USERS, KEY_USER_ID, TextUtils.join(",", whiteList)));
        }

        if (blackList != null) {
            query += " AND NOT %s.%s IN (%s)";
            params.addAll(Arrays.asList(TABLE_USERS, KEY_USER_ID, TextUtils.join(",", blackList)));
        }

        if(date != null){
            query += " AND %s.%s < '%s'";
            params.addAll(Arrays.asList(TABLE_PEBBLES, KEY_PEBBLE_TIMESTAMP, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date)));
        }

        if (desc) {
            query += " ORDER BY timestamp DESC";
        }

        String PEBBLES_SELECT_QUERY = String.format(query, params.toArray());
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(PEBBLES_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    User user = User.fromDB(cursor);
                    Pebble pebble = Pebble.fromDB(cursor);
                    pebble.setUser(user);

                    pebbles.add(pebble);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get pebbles from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return pebbles;
    }

    public void deleteAllTables() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_PEBBLES, null, null);
            db.delete(TABLE_USERS, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all pebbles and users");
        } finally {
            db.endTransaction();
        }
    }
}
