package com.example.fishfinder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fishfinder.data.GeneralTest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ShowSavedInfoActivity extends AppCompatActivity {
    private TextView tvCatchTitleInfo;
    private TextView tvUserInfo;
    private TextView tvFishNameInfo;
    private TextView tvLocationInfo;
    private TextView tvLikesInfo;
//    private TextView textViewCommentsInfo;
    private TextView tvVetsInfo;
    private ImageView imgUserCatch;
    private ListView lvComments;

//    private Button btnInfoAddAComment;
//    private EditText edtAddAComment;


    final long ONE_MEGABYTE = 1024 * 1024;
    GeneralTest fishinfo;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    int theCurrentCommentSize;
    boolean fromClick;

    //    String username;
    private String listTheComment;
    private Context ctx;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_saved_info);
        tvCatchTitleInfo = (TextView) findViewById(R.id.tvCatchTitleInfoSave);
        tvUserInfo = (TextView) findViewById(R.id.tvUserInfoSave);
        tvFishNameInfo = (TextView) findViewById(R.id.tvFishNameInfoSave);
        tvLocationInfo = (TextView) findViewById(R.id.tvLocationInfoSave);
        tvLikesInfo = (TextView) findViewById(R.id.tvLikesInfoSave);
        tvVetsInfo = (TextView) findViewById(R.id.tvVetsInfoSave);
//        textViewCommentsInfo = (TextView) findViewById(R.id.textViewCommentsInfo);
        imgUserCatch = (ImageView) findViewById(R.id.imgUserCatchSave);
        lvComments = (ListView) findViewById(R.id.lvCommentsSave);

        ctx = getBaseContext();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser(); //get the current user based on the auth

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

        //format location information
        DecimalFormat locationDF = new DecimalFormat("#.#####");
        String latFormatted = "";
        String lonFormatted = "";
        if (! fishinfo.getLatitude().toString().isEmpty()) {
            latFormatted = locationDF.format(Double.parseDouble(fishinfo.getLatitude().toString()));
        }
        if (! fishinfo.getLongitude().toString().isEmpty()) {
            lonFormatted = locationDF.format(Double.parseDouble(fishinfo.getLongitude().toString()));
        }


        tvCatchTitleInfo.setText(fishinfo.getTitle());
        tvFishNameInfo.setText(fishinfo.getFishname());
        tvLikesInfo.setText("" + fishinfo.getLikes());
        tvUserInfo.setText(fishinfo.getUsername());
        tvLocationInfo.setText("lat: " + latFormatted + "long:" + lonFormatted);
        tvVetsInfo.setText("" + fishinfo.getVets());


        DatabaseReference usernameref = FirebaseDatabase.getInstance().getReference().child("Users");


        StorageReference pathReference = storageRef.child("UserFishImages/" + fishinfo.getImgId() + ".jpg"); //get the path to download the image
//        byte[] downloadedBytes = null;
        pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
//                downloadedBytes = bytes;
                if (imgUserCatch.equals(null)) {
                    Log.e("Error", "Inside Show Info Activity Error showing the image");
                } else {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imgUserCatch.setImageBitmap(bitmap);
                }
            }
        });

        //Showing comments in a simple listview
        ArrayList<String> theCommentsToShow = new ArrayList<String>();

        int commentSize = fishinfo.getComments().size();
        int commentBySize = fishinfo.getCommentsBy().size();
        theCurrentCommentSize = commentSize;
        listTheComment = "";

        //Assert they are parallel arrays and make arraylist adapter to show it.
        if (commentBySize == commentSize) {
            for (int i = 0; i < commentBySize; i++) {
                //Grab the fishinfo comments and display them
                String theComment = fishinfo.getComments().get(i).toString();
                String theCommentAuthor = fishinfo.getCommentsBy().get(i).toString();
//                listTheComment = theCommentAuthor + " : " + theComment;

                usernameref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String userId = theCommentAuthor;
                        for(DataSnapshot datas: dataSnapshot.getChildren()){
                            if (datas.getKey().toString().equals(userId)) {
                                //found the userid's for the users record.
                                String theUserNameOfAuthor = datas.child("username").getValue().toString();
                                listTheComment = theUserNameOfAuthor + " : " + theComment;
                                theCommentsToShow.add(listTheComment);
                            }
                        }
                        ArrayAdapter<String> commentsAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, theCommentsToShow);
                        lvComments.setAdapter(commentsAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

//                theCommentsToShow.add(listTheComment);
            }
            //Finish by adding it to the arrayadapter and to listview.
//            ArrayAdapter<String> commentsAdapter = new ArrayAdapter<>(this.getBaseContext(), android.R.layout.simple_list_item_1, theCommentsToShow);
//            lvComments.setAdapter(commentsAdapter);

        } else {
            //Other wise something is wrong.
            //Dont show any comments, it will break the app.
        }


    }
}
