package com.example.fishfinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fishfinder.data.FishInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainPageActivity extends AppCompatActivity {

    /* Components */
    private Button btnSearchFish;
    private TextView tvMainPage;
    private Button btnGoToProfile;
    private ListView lvCommunity;
    private Button btnLogout;

    /* Variables */
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        /* Retrieve Info from the Bundle */
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
//                user = (FirebaseUser) extras.getSerializable("user");
//                Log.d("Debugging", "User: " + user);
            } else {

            }
        }

        /* Initialize Variables */
        mAuth = FirebaseAuth.getInstance();

        // Go back to LoginPage if not signed in
        user = mAuth.getCurrentUser();
        if (user == null) {
            Intent goToLoginActivity = new Intent(this, LoginActivity.class);
            startActivity(goToLoginActivity);
        } else {
            Toast.makeText(this, "Welcome " + user.getEmail(),
                    Toast.LENGTH_LONG).show();
        }

        /* Initialize Components */
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

    }
}