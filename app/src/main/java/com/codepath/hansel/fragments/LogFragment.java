package com.codepath.hansel.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.hansel.R;
import com.codepath.hansel.adapters.LogAdapter;
import com.codepath.hansel.models.Pebble;
import com.codepath.hansel.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class LogFragment extends Fragment {
    protected SwipeRefreshLayout swipeContainer;
    protected DatabaseHelper dbHelper;
    protected LogAdapter aPebbles;
    private ListView lvPebbles;
    private ArrayList<Pebble> pebbles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log, container, false);
        setupViews(view);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DatabaseHelper.getInstance(getActivity());
        pebbles = new ArrayList<>();
        aPebbles = new LogAdapter(getActivity(), pebbles);
        loadPebbles();
    }

    public abstract void loadPebbles();

    public void setupViews(View view) {
        lvPebbles = (ListView) view.findViewById(R.id.lvPebbles);
        lvPebbles.setAdapter(aPebbles);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPebbles();
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }
}
