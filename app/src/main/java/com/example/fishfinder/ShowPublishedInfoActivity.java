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

import com.example.fishfinder.data.FishInfo;
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


//USED FOR LISTVIEW SHOWING INFORMATION ON THE PUBLISHED FISH CAUGHTS
//The listview's way to show information from each of their row
public class ShowPublishedInfoActivity extends AppCompatActivity {

    TextView tvCatchTitleInfo;
    TextView tvUserInfo;
    TextView tvFishNameInfo;
    TextView tvLocationInfo;
    TextView tvLikesInfo;
    TextView textViewCommentsInfo;
    TextView tvVetsInfo;
    ImageView imgUserCatch;
    ListView lvComments;

    Button btnInfoAddAComment;
    EditText edtAddAComment;


    final long ONE_MEGABYTE = 1024 * 1024;
    GeneralTest fishinfo;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    int theCurrentCommentSize;
    boolean fromClick;

//    String username;
    String listTheComment;
    Context ctx;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_publish_info);

        tvCatchTitleInfo = (TextView) findViewById(R.id.tvCatchTitleInfo);
        tvUserInfo = (TextView) findViewById(R.id.tvUserInfo);
        tvFishNameInfo = (TextView) findViewById(R.id.tvFishNameInfo);
        tvLocationInfo = (TextView) findViewById(R.id.tvLocationInfo);
        tvLikesInfo = (TextView) findViewById(R.id.tvLikesInfo);
        tvVetsInfo = (TextView) findViewById(R.id.tvVetsInfo);
        textViewCommentsInfo = (TextView) findViewById(R.id.textViewCommentsInfo);
        imgUserCatch = (ImageView) findViewById(R.id.imgUserCatch);
        lvComments = (ListView) findViewById(R.id.lvComments);
        btnInfoAddAComment = (Button) findViewById(R.id.btnInfoAddAComment);
        edtAddAComment = (EditText) findViewById(R.id.edtAddAComment);


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
        String latFormatted = locationDF.format(Double.parseDouble(fishinfo.getLatitude().toString()));
        String lonFormatted = locationDF.format(Double.parseDouble(fishinfo.getLongitude().toString()));


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
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgUserCatch.setImageBitmap(bitmap);
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
                        String userId = firebaseUser.getUid().toString();
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

        btnInfoAddAComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String theCommentToAdd = edtAddAComment.getText().toString();
                String userId = firebaseUser.getUid().toString(); //add this to firebase
                fromClick = true;
                String theRecordToMatch = fishinfo.getImgId();
                DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("CommunitySaves");
                DatabaseReference profilesaveref = FirebaseDatabase.getInstance().getReference().child("ProfileSaves");

                //Add the comment to the community firebase data instance if exists. We find this with unique identifer of the img id.
                //Since img id from this app is set up to be globally unique in firebase and identifies the record.
                int ranonce = 0;

                Log.e("Error", "inside onClick");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Log.e("Error", "Inside addValueListner");

                        String theRecordToMatch = fishinfo.getImgId();
                        for(DataSnapshot datas: dataSnapshot.getChildren()){
                            if (!fromClick) {
                                //if this change was from click the add it else dont.
                                Log.e("Error", "Stopped onclick false");
                                break;
                            }
                            //1. Check for malformed records that will break the app
                            if (! datas.child("imgId").exists()) {
//                                Log.e("Error", "Stopped imgId");
                                continue;
                            }

                            if (! theRecordToMatch.equals(datas.child("imgId").getValue().toString()) ) {
                                //1.1 if the record img id does not match ignore this data and continue
//                                Log.e("Error", "RecordCheck");
                                continue;
                            }

                            if (! datas.child("username").exists()) {
//                                Log.e("Error", "username check");
                                continue;
                            }

                            if (! datas.child("comments").exists()) {
//                                Log.e("Error", "comments check");
                                continue;
                            }
                            if (! datas.child("commentsBy").exists()) {
//                                Log.e("Error", "commentsBy check");
                                continue;
                            }
//                            Log.e("Error", "Stopped after all the checks");
                            //no duplicate comments

                            ArrayList comments = new ArrayList();
                            ArrayList commentsBy = new ArrayList();

                            String theRecord = datas.getKey().toString();

                            String lastComment = "";
                            String lastCommentBy = "";
                            //2. This imgId matches the right record we are looking in comments for
                            //Now we will add to comments and commentsBy
                            //comments and commentedBy are parallel arrays. i.e. comment by user[0] is the comment user[0] made
                            if (datas.child("comments").getChildrenCount() != 0) {
                                for (DataSnapshot datacomments : datas.child("comments").getChildren()) {
                                    comments.add(datacomments.getValue().toString());
                                    lastComment = datacomments.getValue().toString();
                                }
                            }
                            if (datas.child("commentsBy").getChildrenCount() != 0) {
                                for (DataSnapshot datacommentedby : datas.child("commentsBy").getChildren()) {
                                    commentsBy.add(datacommentedby.getValue().toString());   //if exists add to commentsBy list
                                    lastCommentBy = datacommentedby.getValue().toString();
                                }
                            }

                            //Prep to add to firebase field
                            comments.add(theCommentToAdd);
                            commentsBy.add(userId); //add this to the array list

                            //Set this comments field and comments by field to new child base on this new field
                            ref.child(theRecord).child("comments").setValue(comments);
                            ref.child(theRecord).child("commentsBy").setValue(commentsBy);

                            int commentSize = comments.size();
                            int commentBySize = commentsBy.size();

//                            ArrayList<String> theCommentsToShowInner = new ArrayList<String>();
                            ArrayList<String> theCommentsToShowInner = new ArrayList<String>();
                            if (commentSize == commentBySize) {
                                for (int i = 0; i < commentBySize; i++) {
                                    //Grab the fishinfo comments and display them
                                    String theComment = comments.get(i).toString();
                                    String theCommentAuthor = commentsBy.get(i).toString();
//                                    listTheComment = theCommentAuthor + " : " + theComment;

                                    usernameref.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String userId = firebaseUser.getUid().toString();
                                            for(DataSnapshot datas2: dataSnapshot.getChildren()){
                                                if (datas2.getKey().toString().equals(userId)) {
                                                    //found the userid's for the users record.
                                                    String theUserNameOfAuthor = datas2.child("username").getValue().toString();
                                                    listTheComment = theUserNameOfAuthor + " : " + theComment;
                                                    theCommentsToShowInner.add(listTheComment);
                                                }
                                            }
                                            ArrayAdapter<String> commentsAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, theCommentsToShowInner);
                                            lvComments.setAdapter(commentsAdapter);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

//                                    theCommentsToShowInner.add(listTheComment);
                                }
                                //Finish by adding it to the arrayadapter and to listview.
//                                ArrayAdapter<String> commentsAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, theCommentsToShowInner);
//                                lvComments.setAdapter(commentsAdapter);
                            } else {
                                //Other wise something is wrong.
                                //Dont show any comments, it will break the app.
                            }

                            fromClick = false;

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                ranonce = 1;
                //Add the comment to the profile firebase data instance
//                fromClick = false;
            }
        });




    }
}

