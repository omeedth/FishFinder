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
import android.widget.TextView;

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

public class SearchForFishActivity extends AppCompatActivity {

    /* Variables */
    private final String DEFAULT_SPECIES = "cyanellus";
    private String SPECIES;

    //Components
    private TextView        textViewSpecies;
    private EditText        edtSearch;
    private TextView        tvGoogleMap;
    private Button          btnGoToFish;

    // NAS API (Find coordinates of fish)
    private final String    APIBase = "https://nas.er.usgs.gov/api/v2/occurrence/search?";
    private String apiResult = "";

    ExecutorService service = Executors.newFixedThreadPool(1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_fish);

        /* Get Bundle Info */
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                SPECIES = DEFAULT_SPECIES;
            } else {
                Log.i("Info", "Found Species <" + extras.getString("species") + "> in Bundle\'s Extras!");
                SPECIES = extras.getString("species");
            }
        } else {
//            newString= (String) savedInstanceState.getSerializable("STRING_I_NEED");
        }

        /* Initialize Components */
        textViewSpecies = findViewById(R.id.textViewSpecies);
        edtSearch       = findViewById(R.id.edtSearch);
        tvGoogleMap     = findViewById(R.id.tvGoogleMap);
        btnGoToFish     = findViewById(R.id.btnGoToFish);

        /* Setup */
        textViewSpecies.setText("Searching for: " + SPECIES);

        /* Listeners */
        btnGoToFish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Call NAS API (Finds Locations of Fish)
                String name = edtSearch.getText().toString();
                getCordinates(name);

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

        String urlString = String.format("%sspecies=%s&state=%s", APIBase, SPECIES, state);

        /* Debugging */
        Log.i("Debug", urlString);

        String response  = fetch(urlString);
        parseCordinates(response);
    }

    private void longLatToMap(String[] cordinates){

    }

    private String fetch(String urlString){
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String input;
                    URL url = new URL(urlString);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    //con.setRequestProperty("Content-Type", "application/json");
                    con.setConnectTimeout(5000);
                    //int status           = con.getResponseCode();
                    BufferedReader in      = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuffer content   = new StringBuffer();
                    while ((input = in.readLine()) != null){
                        content.append(input);
                    }
                    in.close();
                    con.disconnect();
                    String coordinates = parseCordinates(content.toString());
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            tvGoogleMap.setText(coordinates);
                        }
                    });
                } catch (Exception e){
                    e.printStackTrace();
                    System.out.println(e.getLocalizedMessage());
                }
            }
        });

        return "";
    }

    private String parseCordinates(String input){
        StringBuffer buffer = new StringBuffer();
        try {
            JSONObject job           = new JSONObject(input);
            JSONArray results        = job.getJSONArray("results");

            for (int i=0; i< results.length(); i++){
                JSONObject element   = results.getJSONObject(i);
                Double latitude      =  element.getDouble("decimalLatitude");
                Double longitude     = element.getDouble("decimalLongitude");
                buffer.append(String.format("%.5f,                    %.5f\n", latitude, longitude));

            }
        } catch(Exception e){
            e.printStackTrace();
            System.out.println(e.getLocalizedMessage());
        }
        return buffer.toString();
    }

}