package com.example.fishfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainPageActivity extends AppCompatActivity {

    //Components
    private Button btnSearchFish;
    private TextView tvMainPage;
    private Button btnGoToProfile;
    private ListView lvCommunity;
    private Button btnLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        btnSearchFish = (Button) findViewById(R.id.btnSearchFish);
        tvMainPage = (TextView) findViewById(R.id.tvMainPage);
        btnGoToProfile = (Button) findViewById(R.id.btnGoToProfile);
        lvCommunity = (ListView) findViewById(R.id.lvCommunity);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        /* Listeners */
        btnSearchFish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent goToSearchForFishActivity = new Intent(v.getContext(), SearchForFishActivity.class);
//                startActivity(goToSearchForFishActivity);

                /* Go to FishInfoPage */
                Intent goToFishInfoActivity = new Intent(v.getContext(), FishInfoActivity.class);
                startActivity(goToFishInfoActivity);
            }
        });

        btnGoToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent goToProfile = new Intent(v.getContext(), UserProfileActivity.class);
                startActivity(goToProfile);

            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);   //get rid of default behavior.

        // Inflate the menu; this adds items to the action bar
        getMenuInflater().inflate(R.menu.simple_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.mnu_zero) { //replacing given menu code with new ones
            Toast.makeText(getBaseContext(), "Settings", Toast.LENGTH_LONG).show();
            Intent goToSettings = new Intent(getBaseContext(), SettingsPageActivity.class);
            startActivity(goToSettings);

        }

            return true;
        }
}