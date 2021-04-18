package com.example.fishfinder;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fishfinder.data.FishInfo;
import com.example.fishfinder.data.GeneralTest;


//USED FOR LISTVIEW SHOWING INFORMATION ON THE PUBLISHED FISH CAUGHTS
//The listview's way to show information from each of their row
public class ShowPublishedInfoActivity extends AppCompatActivity {

    TextView tvShowPublishInfoTitle;
    TextView tvShowPublishInfoLikes;

    GeneralTest fishinfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_publish_info);

        tvShowPublishInfoTitle = (TextView) findViewById(R.id.tvShowPublishInfoTitle);
        tvShowPublishInfoLikes = (TextView) findViewById(R.id.tvShowPublishInfoLikes);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                fishinfo = new GeneralTest();
            } else {
                //grab the data from the intent passed in, this should be the data that was read from firebase
                fishinfo = (GeneralTest) extras.getSerializable("fishInfo");
            }
        } else {
//            newString= (String) savedInstanceState.getSerializable("STRING_I_NEED");
        }


        tvShowPublishInfoTitle.setText(fishinfo.getTitle());
        tvShowPublishInfoLikes.setText("" + fishinfo.getLikes());


    }
}
