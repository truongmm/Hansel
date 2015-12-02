package com.codepath.hansel.models;

import android.database.Cursor;

import com.codepath.hansel.utils.DatabaseHelper;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ryamada on 11/22/15.
 */
public class User {
    private long id;
    private String firstName;
    private String lastName;
    private ArrayList<Pebble> pebbles;

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
    public String toString() { return String.valueOf(id); }

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

    public ArrayList<Pebble> getPebbles() {
        return pebbles;
    }

    public void setPebbles(ArrayList<Pebble> pebbles) {
        this.pebbles = pebbles;
    }

    public Date getEarliestDate(){
        return pebbles.get(0).getDate();
    }

    public ArrayList<LatLng> getLatLngs(){
        ArrayList<LatLng> latLngs = new ArrayList<>();
        for (Pebble pebble : pebbles) {
            latLngs.add(pebble.getLatLng());
        }
        return latLngs;
    }

}
