package com.example.fishfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SearchForFishActivity extends AppCompatActivity {

    //Components
    private EditText edtSearch;
    private TextView tvGoogleMap;
    private Button btnGoToFish;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_fish);

        edtSearch   = findViewById(R.id.edtSearch);
        tvGoogleMap = findViewById(R.id.tvGoogleMap);
        btnGoToFish = findViewById(R.id.btnGoToFish);

        btnGoToFish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // WE GO TO BAROMETER WEATHER....
                Intent goToFishInfoActivity = new Intent(v.getContext(), FishInfoActivity.class);
                startActivity(goToFishInfoActivity);
            }
        });
    }
}