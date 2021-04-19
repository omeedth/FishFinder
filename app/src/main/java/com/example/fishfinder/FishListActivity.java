package com.example.fishfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fishfinder.adapters.FishInfoAdapter;
import com.example.fishfinder.data.FishInfo;
import com.example.fishfinder.util.RestAPIUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FishListActivity extends AppCompatActivity {

    /* Variables */
    private String speciesEntered = "";     // Will save the string of the species you enter inside the EditText
    private String fishNameEntered = "";    // Will save the String of the Fish Name you enter inside the EditText
    private Context ctx;                    // Saves the Context of this Activity so that we may use it later within anonymous functions with no context
    private FishInfoAdapter fishInfoAdapter;

    // FishBase API Endpoints
    private final String    FishBaseAPIBase = "https://fishbase.ropensci.org/";
    private final String    FishBaseAPISpecies = "species?";

    // FishBase API search Queries
    private final String    FishBaseSpeciesSearch = "Species="; // Search based off species names
    private final String    FishBaseFBNameSearch = "FBname=";   // Search based off of common name (Must be very accurate)
    private final String    FishBaseLimit = "limit=";           // limit the amount of results returned

    // TODO: Use USGS NAS species endpoint in order to figure out all the searchable fish
    // Don't allow the user to search fish in FishBase that aren't in USGS NAS
    // USGS NAS API Endpoints
    private final String    USGS_NAS_API_SPECIES_ENDPOINT = "https://nas.er.usgs.gov/api/v2/species"; // We can use this by itself to get all species, or append "/{key}" where key is the species ID

    private final int DEFAULT_RECORD_COUNT = 10000; // Just a randomly chosen number as the default for how many records FishBase has
    private int TOTAL_FISHBASE_RECORDS;             // The actual total number of records FishBase Has

    /* Components */
    private EditText editTextSearchFish;
    private EditText editTextFishNameSearch;
    private ProgressBar progressBarFishSearch;
    private Button buttonSearchForFish;
    private Button buttonCantFindFish;
    private ListView listViewFishInfo;

    private final float DISABLED_FLOAT = 0.3f;
    private final int FINAL_BATCH_SIZE = 1000;

    // Only purpose is to run a function on a different thread, avoids thread locking on the UI
    ExecutorService service = Executors.newFixedThreadPool(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish_list);

//        Log.i("Info", "OnCreate!"); // DEBUGGING

        /* Initialize Variables */
        ctx = this.getBaseContext();
        fishInfoAdapter = new FishInfoAdapter(ctx, R.layout.list_view_fish_info, new ArrayList<FishInfo>()); // , service

        /* Initializing Components */
        listViewFishInfo = (ListView) findViewById(R.id.listViewFishInfo);
        progressBarFishSearch = (ProgressBar) findViewById(R.id.progressBarFishSearch);
        editTextFishNameSearch = (EditText) findViewById(R.id.editTextFishNameSearch);
        buttonSearchForFish = (Button) findViewById(R.id.buttonSearchForFish);
        buttonCantFindFish = (Button) findViewById(R.id.buttonCantFindFish);

        buttonCantFindFish.setEnabled(false);
        buttonCantFindFish.setAlpha(DISABLED_FLOAT); //initially it cant be used because no search option

        /* Pre Setup */
        listViewFishInfo.setAdapter(fishInfoAdapter); // Setting the ListAdapter which will populate the ListView
        progressBarFishSearch.setVisibility(View.GONE);

        // Figure out how many records are in the FishBase API
        // FishBase API has a "count" key when you query for species which is the number of records
        TOTAL_FISHBASE_RECORDS = DEFAULT_RECORD_COUNT;
        int limit = 1;
        try {
            // TODO: Consider making RestAPIUtil.get() return Future<String> so that we can wait for the result
            int recordCount = service.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    int result = -1;

                    String content = RestAPIUtil.get(FishBaseAPIBase + FishBaseAPISpecies + FishBaseLimit + limit);
                    JSONObject jsonObj = new JSONObject(content);
                    result = jsonObj.getInt("count");

                    return result;
                }
            }).get(3, TimeUnit.SECONDS);
            TOTAL_FISHBASE_RECORDS = recordCount;
            Log.i("Debug","Total FishBase Records: " + TOTAL_FISHBASE_RECORDS); // DEBUGGING
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            Log.e("Error",e.getLocalizedMessage());
        }

        /* Add Listeners */
        // OnClickListeners should be in the OnCreate, OnStart, or OnResume, because those are always called
        // https://stackoverflow.com/questions/43336253/why-is-the-setonclicklistener-has-to-be-inside-the-oncreate

        // Calls FishBase API to search for info on Fish based on Species
        buttonSearchForFish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //enable cant find fish option.
                buttonCantFindFish.setEnabled(true);
                buttonCantFindFish.setAlpha(1.0f);

                // TODO: Stop ExecutorService from running previous jobs when we add another job (Or switch activities)

                final String DEFAULT_SPECIES = "cyanellus"; // TODO: Change the default

                /* Reset ExecutorService */
                service.shutdownNow();
                service = Executors.newFixedThreadPool(1);
                fishInfoAdapter.setParentActivityService(service);

                /* Clear List Adapter */
                fishInfoAdapter.clear();

                /* Extract Text */
                fishNameEntered = editTextFishNameSearch.getText().toString().trim();

                /* Get Fish Info */
                if (speciesEntered.equals("")) speciesEntered = DEFAULT_SPECIES;

                // If fish Name is entered then search by Fish Name
                if (fishNameEntered.length() > 0) {

                    // TODO: get the number of records in FishBase by calling the API
                    int endPoint = TOTAL_FISHBASE_RECORDS;   // How many records will we try to search for (Max Value means going through all records) // 34571 HARDCODED TO MAX RESULTS RIGHT NOW
                    int offset = 0;         // Where we start searching from
                    int batchSize = FINAL_BATCH_SIZE;    // How many records we search per query

                    progressBarFishSearch.setProgress(0);
                    progressBarFishSearch.setMax(endPoint);
                    progressBarFishSearch.setVisibility(View.VISIBLE);

                    getFishInfoFromTo(offset, batchSize, endPoint, fishNameEntered);

                } else {
                    Toast.makeText(ctx, "Please Enter a value before searching!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // https://stackoverflow.com/questions/5551042/onitemclicklistener-not-working-in-listview (OnItemClickListener won't work if the Items contains focusable or clickable elements)
//        listViewFishInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}
//        });



        //This is button is for letting community handle everything. Since we cant find the fish using the api.
        buttonCantFindFish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If enabled check if there is a string passed into search for fish.
                fishNameEntered = editTextFishNameSearch.getText().toString().trim();
                if (! (fishNameEntered.length() > 0) ){
                    buttonCantFindFish.setEnabled(false);
                    buttonCantFindFish.setAlpha(DISABLED_FLOAT);
                    Toast.makeText(ctx, "Please Enter the fish you can't find", Toast.LENGTH_SHORT).show();
                    //make sure user enters a value to get to the map intent that relies on the community submissions
                } else {
                    //make a fishinfo record that only has the FBname as the common name.
                    FishInfo fishInfo = new FishInfo();
                    fishInfo.setFBname(fishNameEntered);
                    fishInfo.setBodyShapeI("");
                    fishInfo.setComments("");
                    fishInfo.setDangerous("");
                    fishInfo.setFresh(true);
                    fishInfo.setSaltwater(false);
                    fishInfo.setImage("");
                    fishInfo.setSpecies("");
                    fishInfo.setGenus("");
                    fishInfo.setLength(0.0);
                    fishInfo.setWeight(0.0);

                    try {
                        Intent goToSearchForFishActivity = new Intent(v.getContext(), SearchForFishActivity.class);
                        goToSearchForFishActivity.putExtra("fishInfo", fishInfo);
                        goToSearchForFishActivity.putExtra("isCantFind", true); //means we cant find it from the api.
                        //based on item add info to intent
                        startActivity(goToSearchForFishActivity);

                    } catch (Exception e) {
                        Log.e("Error", e.getLocalizedMessage());
                    }

                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
//        Log.i("Info", "OnStart!"); // DEBUGGING

        // Restart the ExecutorService if it is terminated
        if(service.isShutdown()) {
            Log.i("Info","Service is shutdown... Reinitializing Service!");
            service = Executors.newFixedThreadPool(1);
            fishInfoAdapter.setParentActivityService(service);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.i("Info", "OnResume!"); // DEBUGGING
    }

    @Override
    public void onBackPressed() {
        /* Stop Asynchronous Thread */
        service.shutdownNow();

        super.onBackPressed();
    }

    /**
     *
     * @param urlString - The URL the function will fetch data from
     */
    private void fetchFishBase(String urlString) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    /* Send GET Request to obtain data from URL, and parse as JSON */
                    String content = RestAPIUtil.get(urlString);
                    ArrayList<FishInfo> fishInfoList = parseFishInfo(content.toString());

                    // This will post a command to the main UI Thread
                    // This is necessary so that the code knows the variables for this class
                    // https://stackoverflow.com/questions/27737769/how-to-properly-use-a-handler
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            fillFishInfoAdapter(fishInfoList);

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
     * @return ArrayList<FishInfo> parsed from the String JSON
     */
    private ArrayList<FishInfo> parseFishInfo(String input){

        // Initialize List to fill with FishInfo Objects
        ArrayList<FishInfo> fishInfoList = new ArrayList<>();

        try {

            // Convert the input String into a JSON Object, and then go to the main results/data
            JSONObject job           = new JSONObject(input);
            JSONArray results        = job.getJSONArray("data");

            // Cycle through each entry and grab the desired data points
            for (int i=0; i< results.length(); i++){

                // Initialize FishInfo Object
                FishInfo fishInfo = new FishInfo();

                // Fill FishInfo Object based on JSON
                JSONObject element   = results.getJSONObject(i);
                if (element.getString("FBname").equalsIgnoreCase("null")) {
                    continue;
                }

                fishInfo.setSpecies(element.getString("Species"));
                fishInfo.setGenus(element.getString("Genus"));
                fishInfo.setFBname(element.getString("FBname"));
                fishInfo.setBodyShapeI(element.getString("BodyShapeI"));

                // In case the "Length" key is not parsable as a Double
                try {
                    fishInfo.setLength(element.getDouble("Length"));
                } catch (Exception e) {
                    fishInfo.setLength(null);
                }

                // In case the "Weight" key is not parsable as a Double
                try {
                    fishInfo.setWeight(element.getDouble("Weight"));
                } catch (Exception e) {
                    fishInfo.setWeight(null);
                }

                fishInfo.setImage(element.getString("image"));
                fishInfo.setDangerous(element.getString("Dangerous"));
                fishInfo.setComments(element.getString("Comments"));

                // 0: False (AKA NOT a fresh or salt water fish), -1: True (AKA IS a fresh or salt water fish) (This is info I assume after going over records of FishBase API)
                int TRUE = -1;
                fishInfo.setFresh(element.getInt("Fresh") == TRUE);
                fishInfo.setSaltwater(element.getInt("Saltwater") == TRUE);

                // Add FishInfo to FishInfoList
                fishInfoList.add(fishInfo);

            }

        } catch(Exception e){
            e.printStackTrace();
            System.out.println(e.getLocalizedMessage());
        }

        return fishInfoList;
    }

    /**
     *
     * @param species - The name of the species of fish you would like to search for
     */
    private void getFishInfo(String species) {

        /* Fetch API Call from Fishbase API */
        String completeAPICall = FishBaseAPIBase + FishBaseAPISpecies + FishBaseSpeciesSearch + species; // Combines the Base API strings with the query

        // DEBUGGING
        Log.i("Info", "API Call: " + completeAPICall);

        /* Fetch the results from the API */
        fetchFishBase(completeAPICall);

    }

    /**
     * Fills the CustomListAdapter for FishInfo
     * @param fishInfoList - the array of FishInfo objects to populate the ListView with
     */
    private void fillFishInfoAdapter(ArrayList<FishInfo> fishInfoList) {

        /* Fill The List View */
        fishInfoAdapter.clear();
        fishInfoAdapter.addAll(fishInfoList);
        fishInfoAdapter.notifyDataSetChanged();

    }

    /**
     *
     * @param startOffset - The offset where we start searching in the database for data
     * @param batchSize - How much data we look for between each search
     * @param endPoint  - Until when are we searching data in the database
     * @param nameRegex - The String by which we are filtering our data with
     * @return the filtered ArrayList<FishInfo>
     */
    private ArrayList<FishInfo> getFishInfoFromTo(int startOffset, int batchSize, int endPoint, String nameRegex) {
        assert startOffset <= endPoint;

        // Initialize list of FishInfo to fill
        ArrayList<FishInfo> fishinfoList = new ArrayList<>();
        final String lowerCaseNameRegex = nameRegex.toLowerCase();

        // Go through each result from the FishBase Species API Endpoint
        // After each loop offset until you grabbed all of the data or reached the endPoint
        int offset = startOffset;
        for (; offset < endPoint;) {

            // API Queries
            int limit = Math.min(batchSize, endPoint - offset);
            String limitQuery = "limit=" + limit;
            String offsetQuery = "offset=" + offset;
            String limitAndOffsetQuery = limitQuery + "&" + offsetQuery;
            String completeAPICall = FishBaseAPIBase + FishBaseAPISpecies + limitAndOffsetQuery;

            // DEBUGGING
//            Toast.makeText(ctx, ("Start: " + offset  + ", End: " + (offset + limit)), Toast.LENGTH_SHORT).show();

            // Fetch data from API and save into FishInfoList based off of Filter
            service.execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        /* Send GET Request to obtain data from URL, and parse as JSON */
                        String content = RestAPIUtil.get(completeAPICall);
                        ArrayList<FishInfo> fishInfoFromAPI = parseFishInfo(content);
                        ArrayList<FishInfo> filteredFishInfo = new ArrayList<>();

                        // Check to see if name contains the string, and add it to the list if so
                        for (FishInfo fishInfo : fishInfoFromAPI) {
//                                System.out.println("Fish Name: " + fishInfo.getFBname());
                            if (fishInfo.getFBname().toLowerCase().contains(lowerCaseNameRegex)) filteredFishInfo.add(fishInfo);
                        }

                        // This will post a command to the main UI Thread
                        // This is necessary so that the code knows the variables for this class
                        // https://stackoverflow.com/questions/27737769/how-to-properly-use-a-handler
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {

                                if (filteredFishInfo.size() > 0) {
//                                        Log.i("Info", "Fish Found: " + filteredFishInfo.toString());
                                    fishInfoAdapter.addAll(filteredFishInfo);
                                    fishInfoAdapter.notifyDataSetChanged();
                                    Log.i("Info", "Fish Found: " + fishInfoAdapter.getCount() + ", Adapter: " + fishInfoAdapter.toString());
                                }

                                progressBarFishSearch.incrementProgressBy(limit);

                                /* Remove Progress Bar if fully loaded */
                                if (progressBarFishSearch.getProgress() >= progressBarFishSearch.getMax()) {
                                    progressBarFishSearch.setVisibility(View.GONE);
                                }

                            }
                        });

                    } catch(IOException e) {
                        Log.e("Error", "Error fetching data from REST API asynchronously... Was possibly interrupted! (FishListActivity)");

                        // This will post a command to the main UI Thread
                        // This is necessary so that the code knows the variables for this class
                        // https://stackoverflow.com/questions/27737769/how-to-properly-use-a-handler
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                progressBarFishSearch.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });

            // Increment offset by limit (how many entries we searched for)
            offset += limit;

        }

        return fishinfoList;

    }

    /**
     * Shortened Version of getFishInfoFromTo(). Defaults to no endpoint AKA Integer.MAX_VALUE
     * @param startOffset - The offset where we start searching in the database for data
     * @param batchSize - How much data we look for between each search
     * @param nameRegex - The String by which we are filtering our data with
     * @return the filtered ArrayList<FishInfo>
     */
    private ArrayList<FishInfo> getFishInfoFromTo(int startOffset, int batchSize, String nameRegex) {
        return getFishInfoFromTo(startOffset, batchSize, Integer.MAX_VALUE, nameRegex);
    }

    /**
     * Shortened Version of getFishInfoFromTo().
     * 1. Defaults to no endpoint AKA Integer.MAX_VALUE
     * 2. Defaults startOffset to 0 AKA no offset
     * @param batchSize - How much data we look for between each search
     * @param nameRegex - The String by which we are filtering our data with
     * @return the filtered ArrayList<FishInfo>
     */
    private ArrayList<FishInfo> getFishInfoFromTo(int batchSize, String nameRegex) {
        return getFishInfoFromTo(0, batchSize, nameRegex);
    }

    /**
     * Shortened Version of getFishInfoFromTo().
     * 1. Defaults to no endpoint AKA Integer.MAX_VALUE
     * 2. Defaults startOffset to 0 AKA no offset
     * 3. Defaults batchSize to 10 (amount of records to get per GET Request)
     * @param nameRegex - The String by which we are filtering our data with
     * @return the filtered ArrayList<FishInfo>
     */
    private ArrayList<FishInfo> getFishInfoFromTo(String nameRegex) {
        return getFishInfoFromTo(10, nameRegex);
    }

}