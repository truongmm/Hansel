package com.codepath.hansel.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.hansel.models.User;

public class FriendsLogFragment extends LogFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return super.onCreateView(inflater, parent, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void loadPebbles() {
        aPebbles.clear();
        aPebbles.addAll(dbHelper.getPebblesWithoutUsers(new User[]{dbHelper.getUser(1)}));
    }
}
