package com.example.fishfinder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fishfinder.adapters.CommunityInfoAdapter;
import com.example.fishfinder.adapters.ProfileSavesAdapter;
import com.example.fishfinder.data.GeneralTest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {


    SharedPreferences pref;
    boolean isOnSyncDataAuto;

    ListView lvSavedFishes;

    ArrayList<GeneralTest> savedInfoList;
    Context ctx;

    private CommunityInfoAdapter savesInfoAdapter;
    //    private ComInfoAdapter comInfoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ctx = this.getBaseContext();
        pref = getSharedPreferences("UserSettings", MODE_PRIVATE);
        isOnSyncDataAuto = pref.getBoolean("SyncDataAuto", true); //if true then we sync the community list view automatically

        //init views
        lvSavedFishes = (ListView) findViewById(R.id.lvSavedFishes);
        savedInfoList = new ArrayList<GeneralTest>();


        if (isOnSyncDataAuto) {
            //load the data to an arraylist if it is true then add to adapter to view.
            savedInfoList = new ArrayList<GeneralTest>();

            //1. look into database and build the arraylist for our adapter
            DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("ProfileSaves");
            ref.addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    savedInfoList = new ArrayList<GeneralTest>();

                    for(DataSnapshot datas: dataSnapshot.getChildren()){

                        //1.0a make our database instance to read and copy the values in our GeneralTest database
                        GeneralTest toAdd = new GeneralTest();
                        toAdd.setUserId(datas.child("userId").getValue().toString());
                        toAdd.setEmail(datas.child("email").getValue().toString());
                        toAdd.setTitle(datas.child("title").getValue().toString());
                        toAdd.setLatitude(datas.child("latitude").getValue().toString());
                        toAdd.setLongitude(datas.child("longitude").getValue().toString());
                        toAdd.setImgId(datas.child("imgId").getValue().toString());
                        toAdd.setFishname(datas.child("fishname").getValue().toString());
                        toAdd.setWeight(datas.child("weight").getValue().toString());
                        toAdd.setLength(datas.child("length").getValue().toString());
                        toAdd.setGenus(datas.child("genus").getValue().toString());
                        toAdd.setSpecies(datas.child("species").getValue().toString());
                        toAdd.setBait(datas.child("bait").getValue().toString());
                        toAdd.setBodyshape(datas.child("bodyshape").getValue().toString());
                        toAdd.setUsercomment(datas.child("usercomment").getValue().toString());
                        toAdd.setUsername(datas.child("username").getValue().toString());
                        toAdd.setLikes(Integer.parseInt(datas.child("likes").getValue().toString()));
                        toAdd.setVets(Integer.parseInt(datas.child("vets").getValue().toString()));

                        //1.1 create the arraylist objects for who had liked this before, purpose is so we can disable this button for people who already liked this
                        ArrayList likedby = new ArrayList();
                        ArrayList vettedby = new ArrayList();
                        ArrayList comments = new ArrayList();
                        ArrayList commentsBy = new ArrayList();

                        //1.2 go into database and build the arraylist for who liked,vetted,comments,commented this
                        if (datas.child("likedby").getChildrenCount() != 0) {
                            for (DataSnapshot datalikedby : datas.child("likedby").getChildren()) {
                                likedby.add(datalikedby.getValue().toString());  //if exists add to likedby list
                                //Toast.makeText(getBaseContext(), "Testing the arrays in likedby " + datalikedby.getValue().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (datas.child("vettedby").getChildrenCount() != 0) {
                            for (DataSnapshot datavettedby : datas.child("vettedby").getChildren()) {
                                vettedby.add(datavettedby.getValue().toString());  //if exists add to vettedby list
                            }
                        }

                        //comments and commentedBy are parallel arrays. i.e. comment by user[0] is the comment user[0] made
                        if (datas.child("comments").getChildrenCount() != 0) {
                            for (DataSnapshot datacomments : datas.child("comments").getChildren()) {
                                comments.add(datacomments.getValue().toString());   //if exists add to comments list
                            }
                        }
                        if (datas.child("commentsBy").getChildrenCount() != 0) {
                            for (DataSnapshot datacommentedby : datas.child("commentsBy").getChildren()) {
                                commentsBy.add(datacommentedby.getValue().toString());   //if exists add to commentsBy list
                            }
                        }

                        //1.3 add these arrays to the instance of GeneralTest
                        toAdd.setLikedby(likedby);
                        toAdd.setVettedby(vettedby);
                        toAdd.setComments(comments);
                        toAdd.setCommentsBy(commentsBy);
                        //1.4 Add the GeneralTest instance to the list which the adapter will take.
                        savedInfoList.add(toAdd);
                    }

                    //2.1
                    //reverse the list that we got so we can get the most recent to show up in the listview first
                    ArrayList<GeneralTest> reverseSortComInfo = new ArrayList<GeneralTest>();
                    for (int i = 0; i < savedInfoList.size(); i++ ){
                        reverseSortComInfo.add( savedInfoList.get(savedInfoList.size() - i - 1)); //get from the back fo comInfoList
                    }
                    //2.2 Inflate views by adding setting the list for the adapter and finally set the adapter for the listview
                    ProfileSavesAdapter savesInfoAdapter = new ProfileSavesAdapter(ctx, R.layout.list_view_saved_info, reverseSortComInfo);
                    lvSavedFishes.setAdapter(savesInfoAdapter); //set the adapter to the lv using our community adapter
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            System.out.println("Not synced so dont show");
            savedInfoList.clear();
            savesInfoAdapter = new CommunityInfoAdapter(ctx, R.layout.list_view_saved_info, savedInfoList); //reinstantiate with the cleared adapter
            lvSavedFishes.setAdapter(savesInfoAdapter);
        }


    }
}
