package com.example.fishfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HandshakeCompletedListener;

public class MainActivity extends AppCompatActivity {

    //Components
    private EditText        edtSearch;
    private TextView        tvGoogleMap;
    private Button          btnGoToFish;
    private final String    APIBase = "https://nas.er.usgs.gov/api/v2/occurrence/search?";

    private String apiResult = "";

    ExecutorService service = Executors.newFixedThreadPool(1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtSearch   = findViewById(R.id.edtSearch);
        tvGoogleMap = findViewById(R.id.tvGoogleMap);
        btnGoToFish = findViewById(R.id.btnGoToFish);


        btnGoToFish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = edtSearch.getText().toString();
                getCordinates(name);
            }
        });
    }

    private void getCordinates(String str) {
        String[] pieces = str.trim().split(",");

        //Some Default Values
        String genus     = "Lepomis";
        String species   = "cyanellus";
        String state     = "MA";

        if (pieces.length > 0) {
            String genus_ = pieces[0].trim();
            if (genus_.length() > 0){
                genus = pieces[0].trim();
            }
        }

        if (pieces.length > 1) {
            String species_ = pieces[1].trim();
            if (species_.length() > 0){
                species = pieces[1].trim();
            }
        }

        if (pieces.length > 2) {
            String state_ = pieces[2].trim();
            if (state_.length() >= 2){
                state = state_.substring(0, 2).toUpperCase();
            }
        }

        String urlString = String.format("%sgenus=%s&species=%s&state=%s", APIBase, genus, species, state);
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