package com.codepath.hansel.models;

import android.database.Cursor;

import com.codepath.hansel.utils.DatabaseHelper;

import java.sql.Timestamp;

/**
 * Created by ryamada on 11/22/15.
 */
public class User {
    private long id;
    private String firstName;
    private String lastName;
    private Timestamp createdAt;
    private Timestamp updatedAt;

//    public User(String firstName, String lastName, Timestamp createdAt, Timestamp updatedAt) {
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.createdAt = createdAt;
//        this.updatedAt = updatedAt;
//    }
    public User(){}

    public User(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static User fromDB(Cursor cursor) {
        User user = new User();

        try {
            user.id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_USER_ID));
            user.firstName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_USER_FIRST_NAME));
            user.lastName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_USER_LAST_NAME));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    public long getId(){
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName(){
        return firstName + " " + lastName;
    }
}
