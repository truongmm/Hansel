package com.codepath.hansel.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.hansel.R;
import com.codepath.hansel.adapters.GeolocationAdapter;
import com.codepath.hansel.models.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public abstract class GeolocationFragment extends Fragment {

    private ListView lvGeoPoints;
    protected GeolocationAdapter aGeoPoints;
    private ArrayList<GeoPoint> geoPoints;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_geolocation, container, false);
        setupViews(view);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geoPoints = new ArrayList<>();
        aGeoPoints = new GeolocationAdapter(getActivity(), geoPoints);
        stubGeoPoints();
    }

    public abstract void stubGeoPoints();

    public void setupViews(View view) {
        lvGeoPoints = (ListView) view.findViewById(R.id.lvGeoPoints);
        lvGeoPoints.setAdapter(aGeoPoints);
    }

    public void addAll(List<GeoPoint> geopoints) {
        aGeoPoints.addAll(geopoints);
    }
}
