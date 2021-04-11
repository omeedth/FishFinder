package com.example.fishfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.fishfinder.data.FishInfo;
import com.example.fishfinder.util.RestAPIUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchForFishActivity extends AppCompatActivity implements OnMapReadyCallback {

    /* Variables */
    private final String DEFAULT_SPECIES = "cyanellus"; // TODO: Change the default
    private final String DEFAULT_GENUS = "Lepomis";
    private String SPECIES;
    private String GENUS;
    private GoogleMap mMap;
    private FishInfo fishInfo;

    //Components
    private TextView        textViewSpecies;
    private EditText        edtSearch;
    private TextView        tvGoogleMap;
    private Button          btnGoToFish;
    private FrameLayout     map_container;

    // TODO: Add other possible fish location APIs (Found Here: http://www.fishmap.org/technology.html)
    // How to search for fish (https://www.hierarchystructure.com/animal-taxonomy-hierarchy/) Genus + Species is a unique combination
    // 1. http://fishnet2.net/api/v1/apihelp.htm#params (Waiting for API Key)
    // 2. https://explorer.natureserve.org/api-docs/    (Haven't found latitude and longitude endpoint, How to pass data: https://stackoverflow.com/questions/52974330/spring-post-method-required-request-body-is-missing)
    // NAS API (Find coordinates of fish) - DOCUMENTATION: https://nas.er.usgs.gov/api/documentation.aspx
    private final String    APIBase = "https://nas.er.usgs.gov/api/v2/occurrence/search?";
    private final String    genusQuery = "genus=";
    private final String    speciesQuery = "species=";
    private final String    spatialAccQuery = "spatialAcc=";
    private final String    ACCURATE_SPATIAL_ACCURACY = "Accurate";
    private final String    APPROXIMATE_SPATIAL_ACCURACY = "Approximate";
    private final String    CENTROID_SPATIAL_ACCURACY = "Centroid";

    private String apiResult = "";

    ExecutorService service = Executors.newFixedThreadPool(1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_fish);

        /* Get Bundle Info */
        // TODO: Make FishInfo Object Serializable, and send the whole Object over
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                SPECIES = DEFAULT_SPECIES;
                GENUS = DEFAULT_GENUS;
            } else {
//                Log.i("Info", "Found Species <" + extras.getString("species") + "> in Bundle\'s Extras!");
//                SPECIES = extras.getString("species");
                fishInfo = (FishInfo) extras.getSerializable("fishInfo");
                SPECIES = fishInfo.getSpecies();
                GENUS = fishInfo.getGenus();
            }
        } else {
//            fishInfo = (FishInfo) savedInstanceState.getSerializable("fishInfo");
        }

        /* Initialize Components */
        textViewSpecies = findViewById(R.id.textViewSpecies);
        edtSearch       = findViewById(R.id.edtSearch);
        tvGoogleMap     = findViewById(R.id.tvGoogleMap);
        btnGoToFish     = findViewById(R.id.btnGoToFish);
        map_container   = findViewById(R.id.map_container);

        /* Setup */
        // Display the Name of the fish you are searching locations for
        textViewSpecies.setText("Searching for: " + fishInfo.getFBname());

        // Adds the map Fragment inside the map_container
        addMapFragment();

        /* Listeners */
        btnGoToFish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Call NAS API (Finds Locations of Fish)
                String name = edtSearch.getText().toString();
                getCordinates(name);

                // Change Intent to Google Map Intent
//                Intent goToFishLocationActivity = new Intent(v.getContext(), FishLocationActivity.class);
//                startActivity(goToFishLocationActivity);

            }
        });
    }

    private void getCordinates(String str) {
//        String[] pieces = str.trim().split(",");

        //Some Default Values
        final String DEFAULT_STATE     = "MA"; // TODO: Get current State you're in

        /* Clean State Input */
        String state = FishInfoActivity.cleanSpeciesSearch(str);
        if (str.length() <= 1) state = DEFAULT_STATE;
        else {
            state = str.trim();
            if (state.length() >= 2){
                state = state.substring(0, 2).toUpperCase();
            }
        }

//        if (pieces.length > 0) {
//            String genus_ = pieces[0].trim();
//            if (genus_.length() > 0){
//                genus = pieces[0].trim();
//            }
//        }
//
//        if (pieces.length > 1) {
//            String species_ = pieces[1].trim();
//            if (species_.length() > 0){
//                species = pieces[1].trim();
//            }
//        }
//
//        if (pieces.length > 2) {
//            String state_ = pieces[2].trim();
//            if (state_.length() >= 2){
//                state = state_.substring(0, 2).toUpperCase();
//            }
//        }

        String urlString = String.format("%sgenus=%s&species=%s&state=%s", APIBase, GENUS, SPECIES, state);

        /* Debugging */
        Log.i("Debug", urlString);

        fetch(urlString);
//        parseCordinates(response);
    }

    private void longLatToMap(String[] cordinates){

    }

    /**
     * Fetches data from the API Endpoint URL and set's the coordinates
     * @param urlString - The URL the function will fetch data from
     */
    private void fetch(String urlString){
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    /* Send GET Request to obtain data from URL, and parse as JSON */
                    String content = RestAPIUtil.get(urlString);
                    ArrayList<LatLng> latLngCoordinates = parseCordinates(content.toString());

                    // This will post a command to the main UI Thread
                    // This is necessary so that the code knows the variables for this class
                    // https://stackoverflow.com/questions/27737769/how-to-properly-use-a-handler
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            /* Add Markers of Fish Locations */
                            for (int i = 0; i < latLngCoordinates.size(); i++) {
                                LatLng coordinates = latLngCoordinates.get(i);
                                mMap.addMarker(new MarkerOptions().position(coordinates).title("Marker " + i));
                            }

                            /* Navigate to last Marker if one exists */
                            if (latLngCoordinates.size() > 0) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngCoordinates.get(latLngCoordinates.size() - 1)));
                            }

//                            tvGoogleMap.setText(coordinates);
                        }
                    });

                } catch (Exception e){
                    e.printStackTrace();
                    System.out.println(e.getLocalizedMessage());
                }
            }
        });
    }

    /**
     *
     * @param input - String in JSON format
     * @return LatLng Object parsed from the String JSON
     */
    private ArrayList<LatLng> parseCordinates(String input){
        StringBuffer buffer = new StringBuffer();
        ArrayList<LatLng> latLngCoordinates = new ArrayList<>();

        try {
            JSONObject job           = new JSONObject(input);
            JSONArray results        = job.getJSONArray("results");

            for (int i=0; i< results.length(); i++){
                JSONObject element   = results.getJSONObject(i);

                // Surround in try-catch in case latitudes or longitudes are not parsable as Doubles
                try {
                    Double latitude      =  element.getDouble("decimalLatitude");
                    Double longitude     = element.getDouble("decimalLongitude");

                    latLngCoordinates.add(new LatLng(latitude, longitude));
                    buffer.append(String.format("%.5f,                    %.5f\n", latitude, longitude));
                } catch (Exception e) {
                    Log.e("Error", e.getLocalizedMessage());
                }
            }
        } catch(Exception e){
            e.printStackTrace();
            System.out.println(e.getLocalizedMessage());
        }

        return latLngCoordinates;
    }

    /* Google Map Methods */

    /**
     * This method adds map fragment to the container.
     */
    private void addMapFragment() {
        SupportMapFragment mMapFragment = SupportMapFragment.newInstance();
        mMapFragment.getMapAsync(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.map_container, mMapFragment)
                .commit();
    }

    /**
     * The method that runs when the map is ready to be displayed
     * @param googleMap - A GoogleMap Object
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /* Change Map Options */
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        /* Complete the API call to GET all LatLng's of where a fish can be caught */
        String spatialAcc = ACCURATE_SPATIAL_ACCURACY; // TODO: Decide which spatialAcc method to use (filter/settings)
        // TODO: Add search by "commonName" because there are multiple fish in a species
        String urlString = APIBase + genusQuery + GENUS + "&" + speciesQuery + SPECIES + "&" + spatialAccQuery + spatialAcc;   // API call that will get all locations this fish can be caught
        Log.i("Info", "URL: " + urlString);

        /* Add Markers of Fish Locations */
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    /* Send GET Request to obtain data from URL, and parse as JSON */
                    String content = RestAPIUtil.get(urlString);
                    ArrayList<LatLng> latLngCoordinates = parseCordinates(content);

                    // This will post a command to the main UI Thread
                    // This is necessary so that the code knows the variables for this class
                    // https://stackoverflow.com/questions/27737769/how-to-properly-use-a-handler
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            /* Add Markers of Fish Locations */
                            for (int i = 0; i < latLngCoordinates.size(); i++) {
                                LatLng coordinates = latLngCoordinates.get(i);
                                mMap.addMarker(new MarkerOptions().position(coordinates).title("Marker " + i));
                            }

                            /* Navigate to last Marker if one exists */
                            if (latLngCoordinates.size() > 0) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngCoordinates.get(latLngCoordinates.size() - 1)));
                            }

                        }
                    });

                } catch (Exception e){
                    e.printStackTrace();
                    System.out.println(e.getLocalizedMessage());
                }
            }
        });

    }

    /*----------------------*/
}