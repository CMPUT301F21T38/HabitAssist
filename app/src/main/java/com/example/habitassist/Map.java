package com.example.habitassist;


import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * This class implements the Map for Habit events and allows the user to mark down the location of their habit event
 */
public class Map {
    final private SupportMapFragment supportMapFragment;
    final private View mapViewWidget;

    Map(SupportMapFragment supportMapFragment, View mapViewWidget) {
        this.supportMapFragment = supportMapFragment;
        this.mapViewWidget = mapViewWidget;
        hideMapWidget();
    }

    //shows the user location
    public void showOnMap(LatLng point) {
        mapViewWidget.setVisibility(View.VISIBLE);
        MarkerOptions options = new MarkerOptions().position(point).title("Habit completed here");

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                googleMap.clear();
                googleMap.addMarker(options);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
            }
        });
    }

    public void showOnMap(String latlngString) {
        LatLng point = latlngFromString(latlngString);
        if (point != null) {
            showOnMap(point);
        } else {
            hideMapWidget();
        }
    }

    //sets the widget visibility to gone
    public void hideMapWidget() {
        mapViewWidget.setVisibility(View.GONE);
    }

    public static String latlngToString(LatLng point) {
        return point.latitude + ", " + point.longitude;
    }

    public static LatLng latlngFromString(String latlngString) {
        LatLng point = null;

        // Create a LatLng object from the string
        try {
            String[] latlng = latlngString.split(", ");
            point = new LatLng(Float.parseFloat(latlng[0]), Float.parseFloat(latlng[1]));
        }

        // No valid LatLng object can be made from string
        catch (Exception e) {
            e.printStackTrace();
        }

        return point;
    }
}
