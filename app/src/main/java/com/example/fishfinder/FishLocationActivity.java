package com.example.fishfinder;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

// More on Differences between FragmentActivity, Fragments, and Activities: https://stackoverflow.com/questions/10609268/what-is-the-difference-between-fragment-and-fragmentactivity
public class FishLocationActivity extends FragmentActivity implements OnMapReadyCallback { // Used to extends FragmentActivity (This is used for SDK versions older than HoneyComb)

    /* Variables */
    private GoogleMap mMap;
    private ArrayList<LatLng> fishLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        MapFragment mapFragment = (MapFragment) getFragmentManager()   // Originally: SupportMapFragment, and getSupportFragmentManager()
//                .findFragmentById(R.id.map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /* Initialize Variables (Except for googleMap) */
        fishLocations = new ArrayList<>();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /* Change Map Options */
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        /* Add Markers of Fish Locations */

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}