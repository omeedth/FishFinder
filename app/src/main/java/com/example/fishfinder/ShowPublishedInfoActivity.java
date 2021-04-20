package com.example.fishfinder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fishfinder.data.GeneralTest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


//USED FOR LISTVIEW SHOWING INFORMATION ON THE PUBLISHED FISH CAUGHTS
//The listview's way to show information from each of their row
public class ShowPublishedInfoActivity extends AppCompatActivity {

    LinearLayout catchInfo;
    LinearLayout svLinearLayout;
    TextView tvShowPublishInfoTitle;
    TextView tvShowPublishInfoLikes;
    TextView tvFishName;
    TextView tvLatLong;
    TextView tvComments;
    ImageView imgUserCatch;
    ScrollView svComments;




    final long ONE_MEGABYTE = 1024 * 1024;
    GeneralTest fishinfo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_publish_info);

        tvShowPublishInfoTitle = (TextView) findViewById(R.id.tvCatchTitleInfo);
        tvShowPublishInfoLikes = (TextView) findViewById(R.id.tvShowPublishInfoLikes);
        tvFishName = (TextView) findViewById(R.id.tvFishNameInfo);
        tvLatLong = (TextView) findViewById(R.id.tvLocationInfo);
        imgUserCatch = (ImageView) findViewById(R.id.imgUserCatch);
        svComments = (ScrollView) findViewById(R.id.svComments);

        String comments;

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();



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


        tvShowPublishInfoTitle.setText("CATCH TITLE: " + fishinfo.getTitle());
        tvShowPublishInfoLikes.setText("LIKES: " + fishinfo.getLikes());
        tvLatLong.setText("LOCATION: " + fishinfo.getLatitude() + fishinfo.getLongitude());
        tvFishName.setText("FISHNAME: " + fishinfo.getFishname());
        svComments.addView(svLinearLayout);
        tvComments.setText("testing this!");
//        for (int i =0; i< fishinfo.getComments().size(); i++){
//            tvComments.append(fishinfo.getComments().get(i).toString());
//        }

        StorageReference pathReference = storageRef.child("UserFishImages/" + fishinfo.getImgId() + ".jpg"); //get the path to download the image
//        byte[] downloadedBytes = null;
        pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
//                downloadedBytes = bytes;
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgUserCatch.setImageBitmap(bitmap);
            }
        });


    }
}
