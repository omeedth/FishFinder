package com.example.fishfinder;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class CaughtFishActivity extends AppCompatActivity {

    private EditText edtSaveFishName;
    private EditText edtSaveTitle;
    private ImageButton imgButtonTakePicture;
    private Button btnSubmitSave;

    private final int TAKE_PICTURE = 9990;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caught_fish);

        edtSaveTitle = (EditText) findViewById(R.id.edtSaveTitle);
        edtSaveFishName = (EditText) findViewById(R.id.edtSaveFishName);
        imgButtonTakePicture = (ImageButton) findViewById(R.id.imgButtonTakePicture);
        btnSubmitSave = (Button) findViewById(R.id.btnSubmitSave);


        imgButtonTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, TAKE_PICTURE);
            }
        });


        //save to database history in general, all submissions will be here regardless of whether the user wants to save to his own profile page or not. Or regardless to whether the person want to publish on community page
        btnSubmitSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToConfirmSavePublishActivity = new Intent(v.getContext(), ConfirmSavePublishActivity.class);

                startActivity(goToConfirmSavePublishActivity);
            }
        });


    }


}