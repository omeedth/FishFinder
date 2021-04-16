package com.example.fishfinder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fishfinder.adapters.ComInfoAdapter;
import com.example.fishfinder.adapters.CommunityInfoAdapter;
import com.example.fishfinder.adapters.FishInfoAdapter;
import com.example.fishfinder.data.FishInfo;
import com.example.fishfinder.data.GeneralTest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainPageActivity extends AppCompatActivity {

    //Components
    private Button btnSearchFish;
    private TextView tvMainPage;
    private Button btnGoToProfile;
    private ListView lvCommunity;
    private Button btnLogout;


//    ListView listViewCom2;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private Context ctx;


    private CommunityInfoAdapter comInfoAdapter;
//    private ComInfoAdapter comInfoAdapter;
    private ArrayList<GeneralTest> comInfoList;

    SharedPreferences pref;
    boolean isOnSyncDataAuto;
    boolean isPublicPostVisible;



    private ExecutorService service = Executors.newFixedThreadPool(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        btnSearchFish = (Button) findViewById(R.id.btnSearchFish);
        tvMainPage = (TextView) findViewById(R.id.tvMainPage);
        btnGoToProfile = (Button) findViewById(R.id.btnGoToProfile);
        lvCommunity = (ListView) findViewById(R.id.lvCommunity);
        btnLogout = (Button) findViewById(R.id.btnLogout);

//        listViewCom2 = (ListView) findViewById(R.id.listViewCom2);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser(); //get the current user based on the auth

        ctx = this.getBaseContext();
//        ArrayList<GeneralTest> comInfoList = new ArrayList<GeneralTest>();
        comInfoList = new ArrayList<GeneralTest>();
        //init the settings preferences
        pref = getSharedPreferences("UserSettings", MODE_PRIVATE);
        isOnSyncDataAuto = pref.getBoolean("SyncDataAuto", true); //if true then we sync the community list view automatically
        isPublicPostVisible = pref.getBoolean("PostingVisibility", true); //set main page posting visibility to be visible by default or fetch from local

        /* Listeners */
        btnSearchFish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent goToSearchForFishActivity = new Intent(v.getContext(), SearchForFishActivity.class);
//                startActivity(goToSearchForFishActivity);

                /* Go to FishInfoPage */
                Intent goToFishInfoActivity = new Intent(v.getContext(), FishListActivity.class);
                startActivity(goToFishInfoActivity);
            }
        });

        btnGoToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent goToProfile = new Intent(v.getContext(), UserProfileActivity.class);
                startActivity(goToProfile);

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut(); //signs out with firebase
                Intent i = new Intent(v.getContext(), LoginActivity.class);
                startActivity(i); //sign out and go back to main screen to check
            }
        });



        //check shared preferences to see if we should show lv of the community page or not base on user settings.
        if(isPublicPostVisible) {
            lvCommunity.setVisibility(View.VISIBLE);
        } else {
            lvCommunity.setVisibility(View.GONE);
        }

        //check shared preferences to see if we should load the community page and fetch automatically or not based on user setting
        if (isOnSyncDataAuto) {
            //load the data to an arraylist if it is true then add to adapter to view.
//            comInfoAdapter.clear();
            comInfoList = new ArrayList<GeneralTest>();

            //look into database and build the arraylist for our adapter
            DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("CommunitySaves");
            ref.addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    ArrayList<GeneralTest> comInfoTemp = new ArrayList<GeneralTest>();

                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        GeneralTest toAdd = new GeneralTest();

                        toAdd.setUserId(datas.child("userId").getValue().toString());
                        toAdd.setEmail(datas.child("email").getValue().toString());
                        toAdd.setTitle(datas.child("title").getValue().toString());
//                        toAdd.setLatitude(datas.child("latitude").getValue().toString());
//                        toAdd.setLongitude(datas.child("longitude").getValue().toString());
                        toAdd.setImgId(datas.child("imgId").getValue().toString());
                        toAdd.setFishname(datas.child("fishname").getValue().toString());
                        toAdd.setWeight(datas.child("weight").getValue().toString());
                        toAdd.setLength(datas.child("length").getValue().toString());
                        toAdd.setGenus(datas.child("genus").getValue().toString());
                        toAdd.setSpecies(datas.child("species").getValue().toString());
                        toAdd.setBait(datas.child("bait").getValue().toString());
                        toAdd.setBodyshape(datas.child("bodyshape").getValue().toString());
                        toAdd.setUsercomment(datas.child("usercomment").getValue().toString());
//                        toAdd.setUsername(datas.child("username").getValue().toString());
//                        toAdd.setLikes(Integer.parseInt(datas.child("likes").getValue().toString()));
//                        toAdd.setVets(Integer.parseInt(datas.child("title").getValue().toString()));

                        //create the arraylist objects for who had liked this before, purpose is so we can disable this button for people who already liked this
//                        ArrayList likedby = new ArrayList();
//                        ArrayList vettedby = new ArrayList();
//
//
//                        //go into database and build the arraylist for who liked this.
//                        if (datas.child("likedby").getChildrenCount() != 0) {
//                            for (DataSnapshot datalikedby : datas.child("likedby").getChildren()) {
//                                likedby.add(datalikedby.getValue());  //if exists add ot om/
//                            }
//                        }
//                        if (datas.child("vettedby").getChildrenCount() != 0) {
//                            for (DataSnapshot datavettedby : datas.child("vettedby").getChildren()) {
//                                vettedby.add(datavettedby.getValue());  //if exists add ot om/
//                            }
//                        }
                        comInfoList.add(toAdd);
                        System.out.println(comInfoList.size()); //check in logs for stuff
                        System.out.println(toAdd.getUserId());
                        System.out.println(toAdd.getEmail());
//                        comInfoAdapter.notifyDataSetChanged();
//                        listViewCom2.deferNotifyDataSetChanged();
                    }

                    //reverse the list that we got so we can get the most recent first
                    ArrayList<GeneralTest> reverseSortComInfo = new ArrayList<GeneralTest>();
                    for (int i = 0; i < comInfoList.size(); i++ ){
                        reverseSortComInfo.add( comInfoList.get(comInfoList.size() - i - 1)); //get from the back fo comInfoList

                    }
                    CommunityInfoAdapter comInfoAdapter = new CommunityInfoAdapter(ctx, R.layout.list_view_community_info, reverseSortComInfo);
                    lvCommunity.setAdapter(comInfoAdapter); //set the adapter to the lv using our community adapter
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
//            comInfoAdapter = new CommunityInfoAdapter(ctx, R.layout.list_view_community_info, comInfoList);
//            comInfoAdapter.addAll(comInfoList);
//            lvCommunity.setAdapter(comInfoAdapter);
//            comInfoAdapter.notifyDataSetChanged();
        } else {
            System.out.println("Not synced so dont show");
            comInfoList.clear();
            comInfoAdapter = new CommunityInfoAdapter(ctx, R.layout.list_view_community_info,comInfoList); //reinstantiate with the cleared adapter
            lvCommunity.setAdapter(comInfoAdapter);
        }


//        System.out.println("Hello" + comInfoList.size());


//        comInfoAdapter = new CommunityInfoAdapter(ctx, R.layout.list_view_community_info, comInfoList);
//        comInfoAdapter.addAll(comInfoList);
        //it doesnt even sout any convert view calls.

//        service.execute(new Runnable() {
//            @Override
//            public void run() {
////                comInfoAdapter = new CommunityInfoAdapter(ctx, R.layout.list_view_community_info, comInfoList);
//                comInfoAdapter = new ComInfoAdapter(ctx, R.layout.list_view_community_info, comInfoList);
//                lvCommunity.setAdapter(comInfoAdapter);
//                comInfoAdapter.notifyDataSetChanged();
//                lvCommunity.setVisibility(View.VISIBLE);
//            }
//        });



//        Toast.makeText(getBaseContext(), "Outside If statement for sync", Toast.LENGTH_SHORT).show();




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);   //get rid of default behavior.

        // Inflate the menu; this adds items to the action bar
        getMenuInflater().inflate(R.menu.simple_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.mnu_zero) { //replacing given menu code with new ones
            Toast.makeText(getBaseContext(), "Settings", Toast.LENGTH_LONG).show();
            Intent goToSettings = new Intent(getBaseContext(), SettingsPageActivity.class);
            startActivity(goToSettings);

        }

            return true;
        }
}