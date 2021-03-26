package com.example.searchfforfish;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //Components
    private EditText edtSearch;
    private TextView tvGoogleMap;
    private Button   btnGoToFish;


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
                // WE GO TO BAROMETER WEATHER....


            }
        });
    }


}