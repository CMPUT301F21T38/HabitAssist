package com.example.habitassist;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * This Class collects the users location if allowed and places a mark on the map
 * which is then used to mark down a location to be used
 * when marking down a habit event
 */
public class MapActivity extends AppCompatActivity {

    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    String latlngString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent intent1 = getIntent();
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices
                    .getFusedLocationProviderClient(this)
                    .getLastLocation().addOnSuccessListener((location) -> {
                        if (location != null) {
                            latlngString = location.getLatitude() + ", " + location.getLongitude();

                            // Load map into the map fragment on the screen
                            supportMapFragment.getMapAsync((googleMap) -> {
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                MarkerOptions options = new MarkerOptions().position(latLng).title("You are here");
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                                googleMap.addMarker(options);

                                // Set onClick listener to update the marker when clicked
                                googleMap.setOnMapClickListener((point) -> {
                                    googleMap.clear();
                                    MarkerOptions options1 = new MarkerOptions().position(point).title("Habit completed here");
                                    googleMap.addMarker(options1);
                                    latlngString = Map.latlngToString(point);
                                });
                            });
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            finish();
        }
    }

    //saves the location
    public void onClickSaveButton(View view) {
        if (latlngString == null) {
            latlngString = "";
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra("latlngString", latlngString);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    //implements the back button
    @Override
    public void onBackPressed() {
        if (latlngString == null) {
            latlngString = "";
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra("latlngString", latlngString);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}