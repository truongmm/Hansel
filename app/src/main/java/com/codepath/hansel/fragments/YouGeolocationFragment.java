package com.codepath.hansel.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.hansel.models.GeoPoint;

import java.util.ArrayList;

public class YouGeolocationFragment extends GeolocationFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return super.onCreateView(inflater, parent, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void stubGeoPoints() {
        GeoPoint geoPoint1 = new GeoPoint("Melody", 100, 100, "5 mins ago");
        GeoPoint geoPoint2 = new GeoPoint("Melody", 101, 101, "6 mins ago");
        GeoPoint geoPoint3 = new GeoPoint("Melody", 102, 102, "7 mins ago");
        ArrayList<GeoPoint> stubbedGeopoints = new ArrayList<>();
        aGeoPoints.add(geoPoint1);
        aGeoPoints.add(geoPoint2);
        aGeoPoints.add(geoPoint3);
    }
}
