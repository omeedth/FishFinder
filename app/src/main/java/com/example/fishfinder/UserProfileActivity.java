package com.example.fishfinder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fishfinder.adapters.CommunityInfoAdapter;
import com.example.fishfinder.data.GeneralTest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.w3c.dom.Text;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {

    private ScrollView scrollView2;
    private LinearLayout ProfileItems;
    private ImageView imgProfilePic;
    private TextView txtProfileName;
    private TextView txtProfileEmail;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private Context ctx;

//    private ProfileInfoAdapter profileInfoAdapter;
    //    private ComInfoAdapter comInfoAdapter;
    private ArrayList<GeneralTest> profileInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

//        scrollView2 = (ScrollView) findViewById(R.id.scrollView2);
//        ProfileItems = (LinearLayout) findViewById(R.id.ProfileItems);
//        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
//        txtProfileName = (TextView) findViewById(R.id.txtProfileName);
//        txtProfileEmail = (TextView) findViewById(R.id.txtProfileEmail);
//
//        firebaseAuth = FirebaseAuth.getInstance();
//        firebaseUser = firebaseAuth.getCurrentUser(); //get the current user based on the auth
//
//        ctx = this.getBaseContext();
////        ArrayList<GeneralTest> comInfoList = new ArrayList<GeneralTest>();
//
//        //look into database and build the arraylist for our adapter
//        System.out.println(firebaseUser.getDisplayName());


//        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("CommunitySaves");
//        ref.addValueEventListener(new ValueEventListener(){
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                profileInfoList = new ArrayList<GeneralTest>();
//                ArrayList<GeneralTest> comInfoTemp = new ArrayList<GeneralTest>();
//
//                for(DataSnapshot datas: dataSnapshot.getChildren()){
//                    GeneralTest toAdd = new GeneralTest();
//
//                    toAdd.setUserId(datas.child("userId").getValue().toString());
//                    toAdd.setEmail(datas.child("email").getValue().toString());
//                    toAdd.setTitle(datas.child("title").getValue().toString());
////                        toAdd.setLatitude(datas.child("latitude").getValue().toString());
////                        toAdd.setLongitude(datas.child("longitude").getValue().toString());
//                    toAdd.setImgId(datas.child("imgId").getValue().toString());
//                    toAdd.setFishname(datas.child("fishname").getValue().toString());
//                    toAdd.setWeight(datas.child("weight").getValue().toString());
//                    toAdd.setLength(datas.child("length").getValue().toString());
//                    toAdd.setGenus(datas.child("genus").getValue().toString());
//                    toAdd.setSpecies(datas.child("species").getValue().toString());
//                    toAdd.setBait(datas.child("bait").getValue().toString());
//                    toAdd.setBodyshape(datas.child("bodyshape").getValue().toString());
//                    toAdd.setUsercomment(datas.child("usercomment").getValue().toString());
////                        toAdd.setUsername(datas.child("username").getValue().toString());
////                        toAdd.setLikes(Integer.parseInt(datas.child("likes").getValue().toString()));
////                        toAdd.setVets(Integer.parseInt(datas.child("title").getValue().toString()));
//
//                    //create the arraylist objects for who had liked this before, purpose is so we can disable this button for people who already liked this
////                        ArrayList likedby = new ArrayList();
////                        ArrayList vettedby = new ArrayList();
////
////
////                        //go into database and build the arraylist for who liked this.
////                        if (datas.child("likedby").getChildrenCount() != 0) {
////                            for (DataSnapshot datalikedby : datas.child("likedby").getChildren()) {
////                                likedby.add(datalikedby.getValue());  //if exists add ot om/
////                            }
////                        }
////                        if (datas.child("vettedby").getChildrenCount() != 0) {
////                            for (DataSnapshot datavettedby : datas.child("vettedby").getChildren()) {
////                                vettedby.add(datavettedby.getValue());  //if exists add ot om/
////                            }
////                        }
//                    profileInfoList.add(toAdd);
//                    System.out.println(profileInfoList.size()); //check in logs for stuff
//                    System.out.println(toAdd.getUserId());
//                    System.out.println(toAdd.getEmail());
////                        comInfoAdapter.notifyDataSetChanged();
////                        listViewCom2.deferNotifyDataSetChanged();
//                }
//
//                //reverse the list that we got so we can get the most recent first
//                ArrayList<GeneralTest> reverseSortComInfo = new ArrayList<GeneralTest>();
//                for (int i = 0; i < profileInfoList.size(); i++ ){
//                    reverseSortComInfo.add( profileInfoList.get(profileInfoList.size() - i - 1)); //get from the back fo comInfoList
//
//                }
//                CommunityInfoAdapter comInfoAdapter = new CommunityInfoAdapter(ctx, R.layout.list_view_community_info, reverseSortComInfo);
////                lvCommunity.setAdapter(comInfoAdapter); //set the adapter to the lv using our community adapter
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
////            comInfoAdapter = new CommunityInfoAdapter(ctx, R.layout.list_view_community_info, comInfoList);
////            comInfoAdapter.addAll(comInfoList);
////            lvCommunity.setAdapter(comInfoAdapter);
////            comInfoAdapter.notifyDataSetChanged();
    }



}

