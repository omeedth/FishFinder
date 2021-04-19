package com.example.fishfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.fishfinder.data.GeneralTest;
import com.example.fishfinder.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class ConfirmSavePublishActivity extends AppCompatActivity {

    Button btnConfirmSaveProfile;
    Button btnConfirmPublishCom;
    Button btnSaveFinish;

    //init for the intent data passed from the previous caught fish page
    String imgId;
    String latitude;
    String longitude;
    String title;
    String userId;
    String email;
    String fishname;
    String weight;
    String length;
    String species;
    String genus;
    String bait;
    String bodyshape;
    String usercomment;
    String username;

    GeneralTest toAdd;


    FirebaseDatabase firebase;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    private int profileSavesDatabaseSize;
    private int communitySaveDatabaseSize;

    SharedPreferences pref;
    private boolean isAskPostPublic;
    private boolean isAskSaveProfile;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_saves);

        btnConfirmPublishCom = (Button) findViewById(R.id.btnConfirmPublishCom);
        btnConfirmSaveProfile = (Button) findViewById(R.id.btnConfirmSaveProfile);
        btnSaveFinish = (Button) findViewById(R.id.btnSaveFinish);


        firebase = FirebaseDatabase.getInstance(); //get the root node point of the database, this is so we can get the references based on the root node to get the desired data references

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser(); //get the current user based on the auth
        toAdd = new GeneralTest();

        pref = getSharedPreferences("UserSettings", MODE_PRIVATE);
        isAskPostPublic = pref.getBoolean("AskPostPublic", true); //set the user ask to post publically as true by default or fetch from local
        isAskSaveProfile = pref.getBoolean("AskSaveProfile", true); //same thing but for save.


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                toAdd = new GeneralTest();

            } else {
                //grab all the data values from previous activity
                toAdd = (GeneralTest) extras.getSerializable("toAdd"); //get the object itself
            }
        } else {
//            newString= (String) savedInstanceState.getSerializable("STRING_I_NEED");
        }


        firebase.getReference("ProfileSaves").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    profileSavesDatabaseSize = (int) dataSnapshot.getChildrenCount(); //grab the count for the children.
                }
                else {
                    //if that database does not exist
                    profileSavesDatabaseSize = 0; //init the database size to be 0 so we can use it as an id.
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        firebase.getReference("CommunitySaves").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    communitySaveDatabaseSize = (int) dataSnapshot.getChildrenCount(); //grab the count for the children.
                }
                else {
                    //if that database does not exist
                    communitySaveDatabaseSize = 0; //init the database size to be 0 so we can use it as an id.
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //If from preferences the user, user does wants to not have it ask to save. Just publish automatically once we enter this screen.
        if (!isAskSaveProfile) {
            firebase.getReference("ProfileSaves").child(String.valueOf(profileSavesDatabaseSize)).setValue(toAdd); //store the fishinfos saved object to the saved to profile directory of our firebase
            btnConfirmSaveProfile.setEnabled(false); //make it so user cant just double submit this!!!
        }


        //If from preferences the user, user does wants to not have it ask to save. Just save automatically once we enter this screen.
        if (!isAskPostPublic) {
            firebase.getReference("CommunitySaves").child(String.valueOf(communitySaveDatabaseSize)).setValue(toAdd); //store the fishinfos saved object to the community accessible directory of our firebase
            btnConfirmPublishCom.setEnabled(false); //make it so user cant just double submit this!!!
        }





        // ** Add listeners to the buttons
        btnSaveFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goBackToMainPage = new Intent(v.getContext(), MainPageActivity.class);
                startActivity(goBackToMainPage);
            }
        });


        btnConfirmSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebase.getReference("ProfileSaves").child(String.valueOf(profileSavesDatabaseSize)).setValue(toAdd); //store the fishinfos saved object to the saved to profile directory of our firebase
                btnConfirmSaveProfile.setEnabled(false); //make it so user cant just double submit this!!!
            }
        });

        btnConfirmPublishCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebase.getReference("CommunitySaves").child(String.valueOf(communitySaveDatabaseSize)).setValue(toAdd); //store the fishinfos saved object to the community accessible directory of our firebase
                btnConfirmPublishCom.setEnabled(false); //make it so user cant just double submit this!!!
            }
        });

    }
}