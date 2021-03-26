package com.example.searchfforfish;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //Components

    private TextView tvFishInfo;
    private Button   btnCaughtFish;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvFishInfo = findViewById(R.id.tvFishInfo);
        btnCaughtFish = findViewById(R.id.btnCaughtFish);

        btnCaughtFish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // WE GO TO SAVE TO PROFILE PAGE....


            }
        });
    }


}