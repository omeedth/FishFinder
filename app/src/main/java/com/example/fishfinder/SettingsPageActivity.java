package com.example.fishfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsPageActivity extends AppCompatActivity {


    Button btnConfirmSettings;
    Switch swtSyncDataAuto;
    Switch swtAskPostPublic;
    Switch swtPostingVisibility;
    Switch swtAskSaveProfile;
    Switch swtSetDefault;

    SharedPreferences pref;

    boolean isOnSyncDataAuto;
    boolean isAskPostPublic;
    boolean isPublicPostVisible;
    boolean isAskSaveProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        //Init buttons switches etc.
        swtSetDefault = (Switch) findViewById(R.id.swtSetDefault); //switch to just reset everything back to default.
        swtAskSaveProfile = (Switch) findViewById(R.id.swtAskSaveProfile);
        swtPostingVisibility = (Switch) findViewById(R.id.swtPostingVisibility);
        swtAskPostPublic = (Switch) findViewById(R.id.swtAskPostPublic);
        swtSyncDataAuto = (Switch) findViewById(R.id.swtSyncDataAuto);
        btnConfirmSettings = (Button) findViewById(R.id.btnConfirmSettings);

        //1. Refer to the SharedPreference Object.
        pref = getSharedPreferences("UserSettings", MODE_PRIVATE);

        isOnSyncDataAuto = pref.getBoolean("SyncDataAuto", true); //get the value on sharedpreferences, defaults to sync as true
        swtSyncDataAuto.setChecked(isOnSyncDataAuto);//set it to that value we got from get preferences
        isAskPostPublic = pref.getBoolean("AskPostPublic", true); //set the user ask to post publically as true by default or fetch from local
        swtAskPostPublic.setChecked(isAskPostPublic); //set it on switch
        isPublicPostVisible = pref.getBoolean("PostingVisibility", true); //set main page posting visibility to be visible by default or fetch from local
        swtPostingVisibility.setChecked(isPublicPostVisible);
        isAskSaveProfile = pref.getBoolean("AskSaveProfile", true);
        swtAskSaveProfile.setChecked(isAskSaveProfile);



        //set the text of the switch appropriately
        if (isOnSyncDataAuto) {
            swtSyncDataAuto.setText("Yes");
        } else {
            swtSyncDataAuto.setText("No");
        }
        if (isAskPostPublic) {
            swtAskPostPublic.setText("Always");
        } else {
            swtAskPostPublic.setText("Never");
        }
        if (isPublicPostVisible) {
            swtPostingVisibility.setText("Visible");
        } else {
            swtPostingVisibility.setText("Not Visible");
        }
        if (isAskSaveProfile) {
            swtAskSaveProfile.setText("Always");
        } else {
            swtAskSaveProfile.setText("Never");
        }

        swtSetDefault.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = pref.edit(); //change this for the app
//                Toast.makeText(getBaseContext(), "Ask to Save to Profile is on", Toast.LENGTH_SHORT).show();
                editor.putBoolean("AskSaveProfile", true);
                isAskSaveProfile = true;
                swtAskSaveProfile.setText("Always");
//                Toast.makeText(getBaseContext(), "Public posting are visible", Toast.LENGTH_SHORT).show();
                editor.putBoolean("PostingVisibility", true);
                isPublicPostVisible = true;
                swtPostingVisibility.setText("Visible");
//                Toast.makeText(getBaseContext(), "Ask to Post to Community is on", Toast.LENGTH_SHORT).show();
                editor.putBoolean("AskPostPublic", true);
                isAskPostPublic = true;
                swtAskPostPublic.setText("Always");
                Toast.makeText(getBaseContext(), "Default is turned on! Just click submit to Apply changes.", Toast.LENGTH_SHORT).show();
                editor.putBoolean("SyncDataAuto", true);
                isOnSyncDataAuto = true;
                swtSyncDataAuto.setText("Yes");
                editor.apply();

            }
        });

        swtAskSaveProfile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = pref.edit(); //change this for the app
                if (isAskSaveProfile == true) {
                    //if it is check, make it automatically save once submitted
                    Toast.makeText(getBaseContext(), "Ask to Save to Profile is off", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("AskSaveProfile", false);
                    isAskSaveProfile = false;
                    swtAskSaveProfile.setText("Never");
                    editor.apply(); //finish applying the changes to sharedpreferences
                } else{
                    //turn it on, make it ask always to save to profile
                    Toast.makeText(getBaseContext(), "Ask to Save to Profile is on", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("AskSaveProfile", true);
                    isAskSaveProfile = true;
                    swtAskSaveProfile.setText("Always");
                    editor.apply();
                }
            }
        });

        swtPostingVisibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = pref.edit(); //change this for the app
                if (isPublicPostVisible == true) {
                    //if it is check we turn it off, so public posts are not visible
                    Toast.makeText(getBaseContext(), "Public postings are not visible", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("PostingVisibility", false);
                    isPublicPostVisible = false;
                    swtPostingVisibility.setText("Not Visible");
                    editor.apply(); //finish applying the changes to sharedpreferences
                } else{
                    //turn it on
                    Toast.makeText(getBaseContext(), "Public posting are visible", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("PostingVisibility", true);
                    isPublicPostVisible = true;
                    swtPostingVisibility.setText("Visible");
                    editor.apply();
                }
            }
        });


        swtAskPostPublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = pref.edit(); //change this for the app
                if (isAskPostPublic == true) {
                    //if it is check, make it automatically publish to community once submitted
                    Toast.makeText(getBaseContext(), "Ask to Post to Community is off", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("AskPostPublic", false);
                    isAskPostPublic = false;
                    swtAskPostPublic.setText("Never");
                    editor.apply(); //finish applying the changes to sharedpreferences
                } else{
                    //turn it on, ask the user if user does want to publish once submitted
                    Toast.makeText(getBaseContext(), "Ask to Post to Community is on", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("AskPostPublic", true);
                    isAskPostPublic = true;
                    swtAskPostPublic.setText("Always");
                    editor.apply();
                }
            }
    });
        swtSyncDataAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = pref.edit(); //change this for the app
                if (isOnSyncDataAuto == true) {
                    //if it is check or set to sync automatically i.e. is on, then we turn it off and show it as so, it is now only manual sync
                    Toast.makeText(getBaseContext(), "Sync is off", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("SyncDataAuto", false);
                    isOnSyncDataAuto = false;
                    swtSyncDataAuto.setText("No");
                    editor.apply(); //finish applying the changes to sharedpreferences
                } else{
                    //turn it on, now it is syncing automatically across entering a screen.
                    Toast.makeText(getBaseContext(), "Sync is on", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("SyncDataAuto", true);
                    isOnSyncDataAuto = true;
                    swtSyncDataAuto.setText("Yes");
                    editor.apply();
                }
            }
        });




        btnConfirmSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goBackWithMainActivity = new Intent(v.getContext(), MainPageActivity.class);
                startActivity(goBackWithMainActivity);
            }
        });

    }
}
