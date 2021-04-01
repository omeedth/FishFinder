package com.example.fishfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FishInfoActivity extends AppCompatActivity {

    /* Variables */
    private String speciesEntered = "";
    private Context ctx;

    // FishBase API
    private final String    FishBaseAPIBase        = "https://fishbase.ropensci.org/";
    private final String    FishBaseAPISpecies     = "species?";
    private final String    FishBaseSpeciesSearch  = "Species=";
    private final String    FishBaseFBNameSearch   = "FBname=";

    /* Components */
    private EditText editTextSearchFish;
    private Button buttonSearchForFish;
    private ListView listViewFishInfo;

    ExecutorService service = Executors.newFixedThreadPool(1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish_info);

        /* Initialize Variables */
        ctx = this.getBaseContext();

        /* Initializing Components */
        listViewFishInfo = (ListView) findViewById(R.id.listViewFishInfo);
        editTextSearchFish = (EditText) findViewById(R.id.editTextSearchFish);
        buttonSearchForFish = (Button) findViewById(R.id.buttonSearchForFish);

        /* Add Listener */

        buttonSearchForFish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String DEFAULT_SPECIES = "cyanellus";

                /* Extract Text */
                speciesEntered = cleanSpeciesSearch(editTextSearchFish.getText().toString());

                /* Get Fish Info */
                if (speciesEntered.equals("")) speciesEntered = DEFAULT_SPECIES;
                getFishInfo(speciesEntered);

            }
        });

        listViewFishInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FishInfo fishInfo = (FishInfo) parent.getItemAtPosition(position);

                Intent goToSearchForFishActivity = new Intent(view.getContext(), SearchForFishActivity.class);
                goToSearchForFishActivity.putExtra("species", fishInfo.getSpecies());

                //based on item add info to intent
                startActivity(goToSearchForFishActivity);
            }
        });

    }

    public static String cleanSpeciesSearch(String inputString) {

        final String DEFAULT_VALUE = "";

        /* Trim input String */
        inputString = inputString.trim();

        // Check if inputString has spaces in the words
        String[] tokens;
        if (inputString.length() > 0) {
            tokens = inputString.split("\\s+");
        } else {
            tokens = new String[1];
            tokens[0] = inputString;
        }


        /* Take the first word */
        String cleanString = tokens[0];

        /* Check if it is Number -> return DEFAULT VALUE */
        if (cleanString.contains(".*\\d.*")) {
            cleanString = DEFAULT_VALUE;
        }

        return cleanString;
    }

    private String fetchFishBase(String urlString) {
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

                    ArrayList<FishInfo> fishInfoList = parseFishInfo(content.toString());
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            /* Fill The List View */
                            FishInfoAdapter fishInfoAdapter = new FishInfoAdapter(ctx, R.layout.list_view_fish_info, fishInfoList);
                            listViewFishInfo.setAdapter(fishInfoAdapter);

                            System.out.println("FishInfo: " + fishInfoList);
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

    private ArrayList<FishInfo> parseFishInfo(String input){
        ArrayList<FishInfo> fishInfoList = new ArrayList<>();
        try {
            JSONObject job           = new JSONObject(input);
            JSONArray results        = job.getJSONArray("data");

            for (int i=0; i< results.length(); i++){

                // Initialize FishInfo Object
                FishInfo fishInfo = new FishInfo();

                // Fill FishInfo Object based on JSON
                JSONObject element   = results.getJSONObject(i);
                fishInfo.setSpecies(element.getString("Species"));
                fishInfo.setFBname(element.getString("FBname"));
                fishInfo.setBodyShapeI(element.getString("BodyShapeI"));

                try {
                    fishInfo.setLength(element.getDouble("Length"));
                } catch (Exception e) {
                    fishInfo.setLength(null);
                }

                try {
                    fishInfo.setWeight(element.getDouble("Weight"));
                } catch (Exception e) {
                    fishInfo.setWeight(null);
                }

                fishInfo.setImage(element.getString("image"));
                fishInfo.setDangerous(element.getString("Dangerous"));
                fishInfo.setComments(element.getString("Comments"));

                // 0: is a freshwater or saltwater fish, -1: NOT a freshwater or saltwater fish
                fishInfo.setFresh(element.getInt("Fresh") == 0);
                fishInfo.setSaltwater(element.getInt("Saltwater") == 0);

                // Add FishInfo to FishInfoList
                fishInfoList.add(fishInfo);

            }
        } catch(Exception e){
            e.printStackTrace();
            System.out.println(e.getLocalizedMessage());
        }
        return fishInfoList;
    }

    private void getFishInfo(String species) {

        /* Fetch API Call from Fishbase API */
        String completeAPICall = FishBaseAPIBase + FishBaseAPISpecies + FishBaseSpeciesSearch + species;
        System.out.println("API Call: " + completeAPICall);
        fetchFishBase(completeAPICall);

    }

}