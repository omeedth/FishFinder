package com.example.fishfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fishfinder.data.FishInfo;
import com.example.fishfinder.data.MapsClusterItem;
import com.example.fishfinder.data.MapsClusterItemRenderer;
import com.example.fishfinder.util.RestAPIUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.collections.MarkerManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchForFishActivity extends AppCompatActivity implements OnMapReadyCallback {

    /* Variables */
    private final String DEFAULT_SPECIES = "cyanellus"; // TODO: Change the default
    private final String DEFAULT_GENUS = "Lepomis";
    private String SPECIES;
    private String GENUS;
    private FishInfo fishInfo;
    private Context ctx;
    private LatLng lastAddedMarkerLocation;

    // Markers and Map
    private GoogleMap mMap;
    private MarkerManager markerManager;
    private ArrayList<MapsClusterItem> USGSAPIClusterItems;
    private ArrayList<MapsClusterItem> communityClusterItems;
    private ClusterManager<MapsClusterItem> USGSAPIClusterManager;
    private ClusterManager<MapsClusterItem> communityClusterManager;
    private MapsClusterItemRenderer USGSAPIClusterRenderer;
    private MapsClusterItemRenderer communityClusterRenderer;

    private CustomInfoWindowAdapter infoWindowAdapter;

    // Settings for the user
    private boolean showUSGSLocations = true;
    private boolean showCommunitySaves = true;

    // FireBase - Database Info
    FirebaseDatabase firebase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    //Components
    private TextView textViewSpecies;
    private EditText edtSearch;
    private EditText edtTextFilterByVet;

    private Switch switchUSGSAPI;
    private Switch switchCommunity;

    private Button btnGoToFish;
    private FrameLayout map_container;

    private TextView tvShowLat;
    private TextView tvShowLong;
    private Button btnCaughtFish;

    private ProgressBar progressBarLoadResults;

    private String LatitudeClicked;
    private String LongitudeClicked;

    boolean isCantFind;

    // TODO: Add other possible fish location APIs (Found Here: http://www.fishmap.org/technology.html)
    // 1. http://fishnet2.net/api/v1/apihelp.htm#params (Waiting for API Key)
    // 2. https://explorer.natureserve.org/api-docs/    (Haven't found latitude and longitude endpoint, How to pass data: https://stackoverflow.com/questions/52974330/spring-post-method-required-request-body-is-missing)
    // NAS API (Find coordinates of fish) - DOCUMENTATION: https://nas.er.usgs.gov/api/documentation.aspx
    private final String APIBase = "https://nas.er.usgs.gov/api/v2/occurrence/search?";  // TODO: Add Limit to add markers incrementally
    private final String genusQuery = "genus=";
    private final String speciesQuery = "species=";
    private final String spatialAccQuery = "spatialAcc=";
    private final String ACCURATE_SPATIAL_ACCURACY = "Accurate";
    private final String APPROXIMATE_SPATIAL_ACCURACY = "Approximate";
    private final String CENTROID_SPATIAL_ACCURACY = "Centroid";

    private String apiResult = "";
    private DecimalFormat locationDF = new DecimalFormat("#.#####");


    ExecutorService service = Executors.newFixedThreadPool(1);

    // TODO: Add textbox for users to decide radius of markers on the map
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_fish);

        /* Initialize Variables */
        ctx = this.getBaseContext();
        USGSAPIClusterItems = new ArrayList<>();
        communityClusterItems = new ArrayList<>();

        firebase = FirebaseDatabase.getInstance(); //get the root node point of the database, this is so we can get the references based on the root node to get the desired data references
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser(); //get the current user based on the auth

        infoWindowAdapter = new CustomInfoWindowAdapter();

        /* Get Bundle Info */
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                SPECIES = DEFAULT_SPECIES;
                GENUS = DEFAULT_GENUS;
                fishInfo = new FishInfo();
                isCantFind = false; //just came from found from apis.
                Toast.makeText(ctx, "Error No Fish Info Found!", Toast.LENGTH_SHORT).show();
            } else {
                fishInfo = (FishInfo) extras.getSerializable("fishInfo");
                isCantFind = extras.getBoolean("isCantFind");

                SPECIES = fishInfo.getSpecies();
                GENUS = fishInfo.getGenus();
            }
        }

        /* Initialize Components */
        textViewSpecies = findViewById(R.id.textViewSpecies);
        switchUSGSAPI = findViewById(R.id.switchUSGSAPI);
        switchCommunity = findViewById(R.id.switchCommunity);
        map_container = findViewById(R.id.map_container);
        tvShowLat = findViewById(R.id.tvShowLat);
        tvShowLong = findViewById(R.id.tvShowLong);
        btnCaughtFish = findViewById(R.id.btnCaughtFish);
        progressBarLoadResults = (ProgressBar) findViewById(R.id.progressBarLoadResults);
        edtTextFilterByVet = findViewById(R.id.editTextFilterByVet);
        edtTextFilterByVet.setText("");//just init to empty string first.

        /* Setup */
        // Display the Name of the fish you are searching locations for
        textViewSpecies.setText(fishInfo.getFBname());
        progressBarLoadResults.setVisibility(View.GONE);
        showUSGSLocations = switchUSGSAPI.isChecked();
        showCommunitySaves = switchCommunity.isChecked();

        // Adds the map Fragment inside the map_container
        addMapFragment();

        /* Listeners */

        /* Update Filters When Checked or Unchecked */
        switchUSGSAPI.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showUSGSLocations = isChecked;

                //If it is findable use the API.
                if (!isCantFind) {
                    if(showUSGSLocations) {
                        USGSAPIClusterManager.addItems(USGSAPIClusterItems);
                        USGSAPIClusterManager.getMarkerCollection().showAll();
                        USGSAPIClusterManager.cluster();
                    } else {
                        USGSAPIClusterManager.getMarkerCollection().hideAll();
                        USGSAPIClusterManager.clearItems();
                        USGSAPIClusterManager.cluster();
                    }
                }

            }
        });

        /* Update Filters When Checked or Unchecked */
        switchCommunity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showCommunitySaves = isChecked;

                if(showCommunitySaves) {
                    //reset it and make it go through the database again. To fetch the markers and check base on the filter.
                    communityClusterItems.clear();
                    communityClusterManager.clearItems();
                    addFirebaseCoordinates();

                    //Add the newly parsed through set of markers that was checked based on the filter value.
                    communityClusterManager.addItems(communityClusterItems);
                    communityClusterManager.getMarkerCollection().showAll();
                    communityClusterManager.cluster();
                } else {
                    communityClusterManager.getMarkerCollection().hideAll();
                    communityClusterManager.clearItems();
                    communityClusterManager.cluster();
                }
            }
        });

        btnCaughtFish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToCaughtFishActivity = new Intent(v.getContext(), CaughtFishActivity.class);
                goToCaughtFishActivity.putExtra("fishInfo", fishInfo);
                goToCaughtFishActivity.putExtra("latitude", LatitudeClicked);
                goToCaughtFishActivity.putExtra("longitude", LongitudeClicked);
                //TODO other fill ins like species name and etc... to putExtra

                startActivity(goToCaughtFishActivity);
            }
        });


    }

    @Override
    public void onBackPressed() {
        /* Stop Asynchronous Thread */
        service.shutdownNow();

        super.onBackPressed();
    }

    /**
     * Fetches data from the API Endpoint URL and set's the coordinates
     * @param urlString - The URL the function will fetch data from
     */
    private void fetch(String urlString) {
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

                        }
                    });

                } catch (Exception e) {
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
    private ArrayList<LatLng> parseCordinates(String input) {
        StringBuffer buffer = new StringBuffer();
        ArrayList<LatLng> latLngCoordinates = new ArrayList<>();

        try {
            JSONObject job = new JSONObject(input);
            JSONArray results = job.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject element = results.getJSONObject(i);

                // Surround in try-catch in case latitudes or longitudes are not parsable as Doubles
                try {
                    Double latitude = element.getDouble("decimalLatitude");
                    Double longitude = element.getDouble("decimalLongitude");

                    latLngCoordinates.add(new LatLng(latitude, longitude));
                    buffer.append(String.format("%.5f,                    %.5f\n", latitude, longitude));
                } catch (Exception e) {
                    Log.e("Error", e.getLocalizedMessage());
                }
            }
        } catch (Exception e) {
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

        progressBarLoadResults.setVisibility(View.VISIBLE);

        /* Change Map Options */
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setInfoWindowAdapter(infoWindowAdapter);

        /* Last Marker Location */
        lastAddedMarkerLocation = null;

        /* Setting up ClusterManager and MarkerManager - https://stackoverflow.com/questions/29936396/add-multiple-clustermanager-to-google-map */
        markerManager = new MarkerManager(mMap);
        USGSAPIClusterManager = new ClusterManager<>(ctx, mMap, markerManager);
        communityClusterManager = new ClusterManager<>(ctx, mMap, markerManager);

        mMap.setOnMarkerClickListener(markerManager);
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                USGSAPIClusterManager.onCameraIdle();
                communityClusterManager.onCameraIdle();
            }
        });

        /* Adding Render Object */
        USGSAPIClusterRenderer = new MapsClusterItemRenderer(ctx, mMap, USGSAPIClusterManager, Color.RED);
        USGSAPIClusterManager.setRenderer(USGSAPIClusterRenderer);

        communityClusterRenderer = new MapsClusterItemRenderer(ctx, mMap, communityClusterManager, Color.BLUE);
        communityClusterManager.setRenderer(communityClusterRenderer);

        if (!isCantFind) {
            setUpUSGSClusterer();
        }
        if (isCantFind) {
            progressBarLoadResults.setVisibility(View.GONE);
        }

        setUpCommunityClusterer();

        /* Save Latitude and Longitude when clicking on the map */
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                // TODO: Somehow the latitude and longitude appear to be switched
                String lat = String.valueOf(latLng.latitude);
                String lng = String.valueOf(latLng.longitude);

                Toast.makeText(SearchForFishActivity.this, "Location:" + latLng.latitude + latLng.longitude, Toast.LENGTH_SHORT).show();

                LatitudeClicked = lat; //update what we clicked so we can pass into next screen intent if that is the case final destination clicked
                LongitudeClicked = lng;

                String latFormatted = locationDF.format(latLng.latitude); //format the latitude value to 5 decimal places
                String lngFormatted = locationDF.format(latLng.longitude); //format the longitude value to 5 decimal places to display better in tv

                tvShowLat.setText(latFormatted); //show it in a textview what was clicked onto the screen
                tvShowLong.setText(lngFormatted); //keeps track of what marker was last clicked and lets user know this is where the fish was caught if he/she intends to submit based on this clicked location
            }
        });
//
//        // Setting up the marker click to show which one was clicked
//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//
//                // TODO: Somehow the latitude and longitude appear to be switched
//                String lat = String.valueOf(marker.getPosition().latitude);
//                String lng = String.valueOf(marker.getPosition().longitude);
//                Toast.makeText(SearchForFishActivity.this, "Location:" + lat + lng, Toast.LENGTH_SHORT).show();
//
//                LatitudeClicked = lat; //update what we clicked so we can pass into next screen intent if that is the case final destination clicked
//                LongitudeClicked = lng;
//
//                String latFormatted = locationDF.format(marker.getPosition().latitude); //format the latitude value to 5 decimal places
//                String lngFormatted = locationDF.format(marker.getPosition().longitude); //format the longitude value to 5 decimal places to display better in tv
//
//                tvShowLat.setText(latFormatted); //show it in a textview what was clicked onto the screen
//                tvShowLong.setText(lngFormatted); //keeps track of what marker was last clicked and lets user know this is where the fish was caught if he/she intends to submit based on this clicked location
//
//                infoWindowAdapter.getInfoWindow(marker);
//
//                return false;
//            }
//        });

        USGSAPIClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MapsClusterItem>() {
            @Override
            public boolean onClusterItemClick(MapsClusterItem item) {
                String lat = String.valueOf(item.getPosition().latitude);
                String lng = String.valueOf(item.getPosition().longitude);
                Toast.makeText(SearchForFishActivity.this, "Location:" + lat + lng, Toast.LENGTH_SHORT).show();

                LatitudeClicked = lat; //update what we clicked so we can pass into next screen intent if that is the case final destination clicked
                LongitudeClicked = lng;

                String latFormatted = locationDF.format(item.getPosition().latitude); //format the latitude value to 5 decimal places
                String lngFormatted = locationDF.format(item.getPosition().longitude); //format the longitude value to 5 decimal places to display better in tv

                tvShowLat.setText(latFormatted); //show it in a textview what was clicked onto the screen
                tvShowLong.setText(lngFormatted); //keeps track of what marker was last clicked and lets user know this is where the fish was caught if he/she intends to submit based on this clicked location

                return false;
            }
        });

        communityClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MapsClusterItem>() {
            @Override
            public boolean onClusterItemClick(MapsClusterItem item) {

                String lat = String.valueOf(item.getPosition().latitude);
                String lng = String.valueOf(item.getPosition().longitude);
                Toast.makeText(SearchForFishActivity.this, "Location:" + lat + lng, Toast.LENGTH_SHORT).show();

                LatitudeClicked = lat; //update what we clicked so we can pass into next screen intent if that is the case final destination clicked
                LongitudeClicked = lng;

                String latFormatted = locationDF.format(item.getPosition().latitude); //format the latitude value to 5 decimal places
                String lngFormatted = locationDF.format(item.getPosition().longitude); //format the longitude value to 5 decimal places to display better in tv

                tvShowLat.setText(latFormatted); //show it in a textview what was clicked onto the screen
                tvShowLong.setText(lngFormatted); //keeps track of what marker was last clicked and lets user know this is where the fish was caught if he/she intends to submit based on this clicked location

                return false;
            }
        });

    }

    private boolean isInRadius(LatLng coordinate, List<LatLng> coordinates, int radius) { // Radius in meters
        for (LatLng coord : coordinates) {
            if (SphericalUtil.computeDistanceBetween(coordinate, coord) < radius)
                return true;
        }
        return false;
    }

    /*----------------------*/

    // https://developers.google.com/maps/documentation/android-sdk/infowindows
    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

    }

    private void setUpUSGSClusterer() {
        addUSGSAPICoordinates();
    }

    private void setUpCommunityClusterer() {
        addFirebaseCoordinates();
    }

    private void addUSGSAPICoordinates() {

        /* Complete the API call to GET all LatLng's of where a fish can be caught */
        String spatialAcc = ACCURATE_SPATIAL_ACCURACY; // TODO: Decide which spatialAcc method to use (filter/settings)
        // TODO: Add search by "commonName" because there are multiple fish in a species
        String urlString = APIBase + genusQuery + GENUS + "&" + speciesQuery + SPECIES + "&" + spatialAccQuery + spatialAcc;   // API call that will get all locations this fish can be caught
        Log.i("Info", "URL: " + urlString); // DEBUGGING
        Log.i("Info", "<FishInfo> Species: " + fishInfo.getSpecies() + ", Genus: " + fishInfo.getGenus()); // DEBUGGING

        /* Reduce Marker Density */
        // NOTE: Unused - Radius exclusion
        ArrayList<LatLng> addedCoordinates = new ArrayList<>(); // Unused
        final int KILOMETER = 1000; // Unused

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

                                // NOTE: Unused - Radius exclusion
//                                if (!isInRadius(coordinates, addedCoordinates, KILOMETER * 250)) {
//                                    MarkerOptions marker = new MarkerOptions().position(coordinates).title("Marker " + i);
//                                    mMap.addMarker(marker);
//                                    addedCoordinates.add(coordinates);
//                                }

                                /* Add Map Cluster Items */
                                String title = "USGS API: " + i;
                                String snippet = "";
                                MapsClusterItem offsetItem = new MapsClusterItem(coordinates.latitude, coordinates.longitude, title, snippet);
                                USGSAPIClusterManager.addItem(offsetItem);
                                USGSAPIClusterItems.add(offsetItem);
                            }

                            /* Navigate to last Marker if one exists */
                            if (latLngCoordinates.size() > 0) {
                                lastAddedMarkerLocation = latLngCoordinates.get(latLngCoordinates.size() - 1);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastAddedMarkerLocation, 10));
                            }

                            progressBarLoadResults.setVisibility(View.GONE);

                        }
                    });

                } catch (Exception e){
                    Log.e("Error","Error loading the locations of the fish!");

                    // This will post a command to the main UI Thread
                    // This is necessary so that the code knows the variables for this class
                    // https://stackoverflow.com/questions/27737769/how-to-properly-use-a-handler
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            progressBarLoadResults.setVisibility(View.GONE);
                            Toast.makeText(ctx, "Error Loading Markers!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

    }

    private void addFirebaseCoordinates() {

        service.execute(new Runnable() {
            @Override
            public void run() {

                // TODO: Change where I have this if statement
                // Grab markers from firebase
                if (showCommunitySaves) {
                    // Show the locations from the community
//                    Log.i("Info", "Getting coordinates from Firebase Community..."); // DEBUGGING
                    DatabaseReference communitySavesReference = firebase.getReference().child("CommunitySaves");
                    communitySavesReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                //1. Check if this data's vet value
                                int threshHoldVetValue = 0;
                                if (edtTextFilterByVet.getText().toString().equals("")) {
                                    //1.1 If it is empty, means user did not input a threshhold value for vets.
                                    //give it a 0 threshold.
                                    threshHoldVetValue = 0;
                                } else {
                                    try {
                                        //Get the value from the text box. User submitted a value.
                                        threshHoldVetValue = Integer.parseInt(edtTextFilterByVet.getText().toString());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (data.child("vets").exists()) {
                                    //Field exists so we can compare the values
                                    if (! (threshHoldVetValue < Integer.parseInt(data.child("vets").getValue().toString())) ) {
                                        continue;
                                        //Just ignore this data value because it did not pass our user submitted threshhold.
                                        //Will not be added to clustermanager thus will not be shown.
                                    }
                                }


                                //2. Data is each child element in the "CommunitySaves" Database Reference now, Set the reference
                                String genus = "";
                                String species = "";
                                String fishFBname = "";

                                //2.1 Grab the data
                                try {
                                    genus = data.child("genus").getValue(String.class);
                                    species = data.child("species").getValue(String.class);
                                } catch (Exception e) {
                                    Log.e("Error", "Could not get genus or species");
                                }

                                if (data.child("fishname").exists()) {
                                    Toast.makeText(getBaseContext(), "Exists fishname" + data.child("fishname").getValue().toString().toLowerCase(), Toast.LENGTH_SHORT).show();
                                    fishFBname = data.child("fishname").getValue().toString().toLowerCase();
                                }

//                                Log.i("Info", "<COMMUNITY> Species: " + species + ", Genus: " + genus); // DEBUGGING

                                //3. Check if the fish we are on matches the fish posted in the database, if so do functions to grab the information. Making sure
                                // that there is a lat long value to display the marker off of and it is a double parsable value.
                                //Finally adding that marker with those values in clustermanager.


//                                if (species.equals("") || genus.equals("")) {
//                                    //do nothing
//                                    //this was an empty result so do nothng
//                                    continue;
//                                }

                                if (fishInfo.getGenus().equals(genus) && fishInfo.getSpecies().equals(species) && (!genus.equals("") && !species.equals(""))) {
                                    Log.i("Info", "Fish Match Found On Community!"); // DEBUGGING

                                    LatLng communityCoords = null;
                                    double latitude;
                                    double longitude;

                                    try {
                                        latitude = Double.parseDouble(data.child("latitude").getValue(String.class));
                                        longitude = Double.parseDouble(data.child("longitude").getValue(String.class));
                                        lastAddedMarkerLocation = new LatLng(latitude, longitude);
                                        communityCoords = new LatLng(latitude, longitude);
//                                        Log.i("Info", "Location: " + communityCoords); // DEBUGGING

                                        /* Add Markers */
//                                        MarkerOptions communityMarker = new MarkerOptions()
//                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
//                                                .alpha(1f)
//                                                .position(communityCoords)
//                                                .title("Community Catch!");
//                                        mMap.addMarker(communityMarker);

                                        /* Cluster Markers */
                                        String title = "Community Catch!";
                                        String snippet = "";
                                        MapsClusterItem offsetItem = new MapsClusterItem(latitude, longitude, title, snippet);
                                        communityClusterManager.addItem(offsetItem);
                                        communityClusterItems.add(offsetItem);

                                    } catch(Exception e) {
                                        Log.e("Error", "Could not get Latitude or Longitude! Latitude: " + data.child("latitude").getValue(String.class) + ", Longitude: " + data.child("longitude").getValue(String.class));
                                    }
                                }

                                //Check for matches in FBname for database check. A very soft general check.
                                else if (fishFBname.contains(fishInfo.getFBname().toLowerCase()) || fishInfo.getFBname().toLowerCase().contains(fishFBname)) {
                                    Log.i("Info", "Fish Match Found On Community!"); // DEBUGGING
                                    LatLng communityCoords = null;
                                    double latitude;
                                    double longitude;

                                    try {
                                        latitude = Double.parseDouble(data.child("latitude").getValue(String.class));
                                        longitude = Double.parseDouble(data.child("longitude").getValue(String.class));
                                        lastAddedMarkerLocation = new LatLng(latitude, longitude);
                                        communityCoords = new LatLng(latitude, longitude);

                                        /* Cluster Markers */
                                        String title = "Community Catch!";
                                        String snippet = "";
                                        MapsClusterItem offsetItem = new MapsClusterItem(latitude, longitude, title, snippet);
                                        communityClusterManager.addItem(offsetItem);
                                        communityClusterItems.add(offsetItem);
                                    } catch(Exception e) {
                                        Log.e("Error", "Could not get Latitude or Longitude! Latitude: " + data.child("latitude").getValue(String.class) + ", Longitude: " + data.child("longitude").getValue(String.class));
                                    }
                                }

                                else {
                                    //do nothing
                                    Toast.makeText(getBaseContext(), "Skipping ", Toast.LENGTH_SHORT).show();
//                                    System.out.println("Continueing skipping");
                                    continue;
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }
        });

    }

}