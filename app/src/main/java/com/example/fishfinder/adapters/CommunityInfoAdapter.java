package com.example.fishfinder.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fishfinder.FishInfoActivity;
import com.example.fishfinder.R;
import com.example.fishfinder.SearchForFishActivity;
import com.example.fishfinder.ShowPublishedInfoActivity;
import com.example.fishfinder.data.FishInfo;
import com.example.fishfinder.data.GeneralTest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;



// TODO: Consider changing to RecyclerView for performance improvement
public class CommunityInfoAdapter extends ArrayAdapter<GeneralTest> {

    /* Variables */
    private ExecutorService service = Executors.newFixedThreadPool(1);
    private ArrayList<GeneralTest> fishInfoList = new ArrayList<>();
    private Context activityContext;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    Semaphore mutex = new Semaphore(1);

    final long ONE_MEGABYTE = 1024 * 1024;
    public CommunityInfoAdapter(Context context, int textViewResourceId, ArrayList<GeneralTest> objects) {
        super(context, textViewResourceId, objects);
        activityContext = context;
        fishInfoList = objects;
    }

    @Override
    public int getCount() {
        super.getCount();
        return fishInfoList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


//        System.out.println("Inside" + position);
        // If there is already a View then just return it (Didn't Do Anything)
        // We only need to inflate the layout if the layout is not present (it is expensive to keep doing this)
        View row;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activityContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_view_community_info, parent, false);
            System.out.println("Inflating" + position);
        } else {
            row = convertView;
        }



        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
//        StorageReference pathReference = storageRef.child("UserFishImages/");

        //gs://fishfinder-8c4d5.appspot.com/UserFishImages/0.jpg format of the url
        //gs://fishfinder-8c4d5.appspot.com/UserFishImages/

        //** Init the row views so we can grab and reference them **
        TextView txtComDesc = (TextView) row.findViewById(R.id.txtComDesc);
        ImageView imgComCaughtImage = (ImageView) row.findViewById(R.id.imgComCaughtImage);
        Button btnComShowInfo = (Button) row.findViewById(R.id.btnComShowInfo);
        Button btnComLike = (Button) row.findViewById(R.id.btnComLike);
        Button btnComVet = (Button) row.findViewById(R.id.btnComVet);


        //get firebase info so we can check for likes vets etc and disable if liked or vetted
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser(); //get the current user based on the auth
        String userId = firebaseUser.getUid().toString(); //used to check if we can set the button to disabled or not

        GeneralTest fishInfo = fishInfoList.get(position); //get the particular positioned item from the provided arraylist
        txtComDesc.setText("Title: " + fishInfo.getTitle() + "\n" +
                " by user: " + fishInfo.getUsername());

        //** Download Images into this row **
        StorageReference pathReference = storageRef.child("UserFishImages/" + fishInfo.getImgId() + ".jpg"); //get the path to download the image
//        byte[] downloadedBytes = null;
        pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
//                downloadedBytes = bytes;
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgComCaughtImage.setImageBitmap(bitmap);
            }
        });



        //** Listeners **
        // Listener Part 2: Operation Read, Reading from firebase
        //1. create listeners for this row in the listadapter
        btnComShowInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set up our intent to show the info for this row's caught fish
                try {
                    Intent goToShowPublishInfoActivity = new Intent(v.getContext(), ShowPublishedInfoActivity.class);
                    goToShowPublishInfoActivity.putExtra("fishInfo", fishInfo);
                    // based on item add info to intent
                    goToShowPublishInfoActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   // This flag is required to be added to activity for us to navigate there from this class
                    activityContext.startActivity(goToShowPublishInfoActivity);
                } catch (Exception e) {
                    Log.e("Error ShowInfo", e.getLocalizedMessage());
                }
            }
        });



        //** Listener Part 2: Operation Update, Button initialization for CRUD operation Update;
        //2. Check if the instance of the user liked it or vetted it before if vetted or liked, disable the button accordingly.
        //Loop through their fishinfo arrays and check
//        for (int i = 0; i < fishInfo.getLikedby().size(); i++) {
//            if (userId.equals(fishInfo.getLikedby().get(i))) {
//                //means this current firebase instance did like this post before so disable the like
//                btnComLike.setEnabled(false);
//                btnComLike.setAlpha(0.3f);
//            }
//        }
//
//        for (int i = 0; i < fishInfo.getVettedby().size(); i++) {
//            if (userId.equals(fishInfo.getVettedby().get(i))) {
//                //means this current firebase instance did like this post before so disable the like
//                btnComVet.setEnabled(false);
//                btnComVet.setAlpha(0.3f);
//            }
//        }


        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("CommunitySaves");
        //checked liked by
        //checked vetted by
        // **BUGGED ONLY BECAUSE THIS USES LISTVIEWS INSTEAD OF RECYCLER VIEW *UNCOMMENT TO RUN AND SHOW IN CLASS WHAT THE ISSUE WAS AND WHY RECYCLER VIEW FIXES THIS
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String row_img_id = fishInfo.getImgId();
//                //2.1 find the record associated with this row.
//                for(DataSnapshot datas: dataSnapshot.getChildren()){
//                    if (datas.child("imgId").getValue().toString().equals(row_img_id)) {
//                        //found the unique identifier for this record and row
//                        //2.2 Check liked and vetted field to check if they are vetted or liked
//                        //2.2.1 checkvetted and if user is in there, disable the vet button
//                        if (datas.child("vettedby").getChildrenCount() != 0) {
//                            for (DataSnapshot datavettedby : datas.child("vettedby").getChildren()) {
//                                if ( datavettedby.getValue().toString().equals(userId) ) {
//                                    //disable since we found it in list of vetted
//                                    btnComVet.setEnabled(false);
//                                    btnComVet.setAlpha(0.3f);
//                                    break;
//                                }
//                            }
//                        }
//                        //2.2.2 checkliked and if user is in there, disable the like button
//                        if (datas.child("likedby").getChildrenCount() != 0) {
//                            for (DataSnapshot datalikedby : datas.child("likedby").getChildren()) {
//                                if ( datalikedby.getValue().toString().equals(userId) ) {
//                                    //disable since we found it in list of liked
//                                    btnComLike.setEnabled(false);
//                                    btnComLike.setAlpha(0.3f);
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        //BUGGED END **

        //Finally deal with updating
        btnComVet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if clickable means did not vet this before

                //1 Tell fire base To update the vet for this
                //add to vetted and its count
                DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("CommunitySaves");
                DatabaseReference profilesaveref = FirebaseDatabase.getInstance().getReference().child("ProfileSaves"); //reference to the profilesave database to update that too

                //ATTEMPT ON A MUTEX SEMAPHORE: DOESNT WORK IT CRASHES FOR SOME REASON.
//                service.execute(new Runnable() {
////                    Semaphore mutex2 = new Semaphore(1);
//                    @Override
//                    public void run() {
//                        ref.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                String row_img_id = fishInfo.getImgId();
//                                for(DataSnapshot datas: dataSnapshot.getChildren()){
//                                    //1. look for this caught fish record and update it
//                                    //Since img id is unique to each record in our app. Just match the field of image id. if the same we are clear to update this record
//                                    //^Bad practice but easiest way with how we framed our app and database
//                                    //also update this data for our profile saves record
////                            System.out.println("Record :" + datas.getValue().toString());
//
//                                    //1.1 do a while semaphore for mutex so it waits to operation is done
//                                    try {
//                                        mutex.acquire();
//                                        System.out.println("Attempt to acquire mutex1");
//                                        if ( datas.child("imgId").getValue().toString().equals(row_img_id) ) {
//                                            //2. Found matching imgId record. Prepare to vet it
//                                            //3. Check if already vetted
//                                            boolean alreadyVetted = false;
//                                            if (datas.child("vettedby").getChildrenCount() != 0) {
//                                                for (DataSnapshot datavettedby : datas.child("vettedby").getChildren()) {
//                                                    //if exists add to vettedby list
//                                                    if ( datavettedby.getValue().toString().equals(userId) ) {
//                                                        alreadyVetted = true;
//                                                    }
//                                                }
//                                            }
//
//                                            if(alreadyVetted) {
//                                                break; //3.1 dont update if it is vetted already. DONT DELETE OTHERWISE IT LOOPS INFINITELY
//                                            }
//
//                                            //4. update variables to be added to the fields on firebase
//                                            int newvet = fishInfo.getVets() + 1; //add 1 to new vet this will be the updated field in firebase
//                                            List newvettedby = fishInfo.getVettedby();
//                                            newvettedby.add(userId); //add this new user id to vettedby this will be the updated field in firebase
//
//                                            ref.child(datas.getKey().toString()).child("vets").setValue(newvet); //update vets to firebase
//                                            ref.child(datas.getKey().toString()).child("vettedby").setValue(newvettedby).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                }
//                                            }); //5.1 update who vetted to firebase
//                                            break;
//                                        }
//                                        Thread.sleep(10000);
//                                        mutex.release();
//                                    }
//
//                                    catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
//                });

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String row_img_id = fishInfo.getImgId();
                        for(DataSnapshot datas: dataSnapshot.getChildren()){
                            //1. look for this caught fish record and update it
                            //Since img id is unique to each record in our app. Just match the field of image id. if the same we are clear to update this record
                            //^Bad practice but easiest way with how we framed our app and database
                            //also update this data for our profile saves record
//                            System.out.println("Record :" + datas.getValue().toString());

                            //1.1 do a while semaphore for mutex so it waits to operation is done
//                            try {
//                                mutex.acquire();
//                                System.out.println("Attempt to acquire mutex1");
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                            if ( datas.child("imgId").getValue().toString().equals(row_img_id) ) {
                                //2. Found matching imgId record. Prepare to vet it
                                //3. Check if already vetted
                                boolean alreadyVetted = false;
                                if (datas.child("vettedby").getChildrenCount() != 0) {
                                     for (DataSnapshot datavettedby : datas.child("vettedby").getChildren()) {
                                        //if exists add to vettedby list
                                        if ( datavettedby.getValue().toString().equals(userId) ) {
                                            alreadyVetted = true;
                                        }
                                    }
                                }

                                if(alreadyVetted) {
                                    break; //3.1 dont update if it is vetted already. DONT DELETE OTHERWISE IT LOOPS INFINITELY
                                }

                                //4. update variables to be added to the fields on firebase
                                int newvet = fishInfo.getVets() + 1; //add 1 to new vet this will be the updated field in firebase
                                List newvettedby = new ArrayList<String>();
                                if (datas.child("vettedby").getChildrenCount() != 0) {
                                    for (DataSnapshot datavettedby : datas.child("vettedby").getChildren()) {
                                        //if exists add to vettedby list
                                        newvettedby.add(datavettedby.getValue().toString());
                                    }
                                }

                                Toast.makeText(activityContext, "Vetted Location", Toast.LENGTH_SHORT).show();
                                newvettedby.add(userId); //add this new user id to vettedby this will be the updated field in firebase
                                ref.child(datas.getKey().toString()).child("vets").setValue(newvet); //update vets to firebase
                                ref.child(datas.getKey().toString()).child("vettedby").setValue(newvettedby).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    }
                                }); //5.1 update who vetted to firebase
//                                mutex.release(); //5. release mutex once we know it is completed

//                              System.out.println("Found matching, imgId breaking");
//                              Toast.makeText(activityContext, "Found matching imgId breaking", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //also update this in our profilesave reference of our database.
                profilesaveref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String row_img_id = fishInfo.getImgId();
                        for(DataSnapshot datas: dataSnapshot.getChildren()){
                            //Same thing as the ref for community ref, but this data for our profile saves record
                            if ( datas.child("imgId").getValue().toString().equals(row_img_id) ) {
                                //1. img id found so prepare to vet
                                //2. Check if already vetted

                                //3. use mutex to wait on operation completes
//                                try {
//                                    mutex2.acquire();
//                                    System.out.println("Attempt to acquire mutex2");
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }

                                boolean alreadyVetted = false;
                                if (datas.child("vettedby").getChildrenCount() != 0) {
                                    for (DataSnapshot datavettedby : datas.child("vettedby").getChildren()) {
                                        //if exists add to vettedby list
                                        if (datavettedby.getValue().toString().equals(userId)) {
                                            alreadyVetted = true;
                                            break;
                                        }
                                    }
                                }

                                if (alreadyVetted) {
                                    break; //dont update if it is vetted already. DONT DELETE OTHERWISE IT LOOPS INFINITELY
                                }

                                //4. update variables to be added to the fields on firebase
                                int newvet = fishInfo.getVets() + 1; //add 1 to new vet this will be the updated field in firebase
//                                List newvettedby = fishInfo.getVettedby();
                                List newvettedby = new ArrayList<String>();
                                if (datas.child("vettedby").getChildrenCount() != 0) {
                                    for (DataSnapshot datavettedby : datas.child("vettedby").getChildren()) {
                                        //if exists add to vettedby list
                                        newvettedby.add(datavettedby.getValue().toString());
                                    }
                                }
                                newvettedby.add(userId); //add this new user id to vettedby this will be the updated field in firebase
                                Toast.makeText(activityContext, "Vetted Location", Toast.LENGTH_SHORT).show();
                                profilesaveref.child(datas.getKey().toString()).child("vets").setValue(newvet); //update vets to firebase
                                profilesaveref.child(datas.getKey().toString()).child("vettedby").setValue(newvettedby).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
//                                        mutex2.release(); //5. release mutex once we know it is completed
                                    }
                                }); //5.1 update who vetted to firebase

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                btnComVet.setAlpha(0.3f);
                btnComVet.setEnabled(false);
            }
        });

        btnComLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if clickable means did not like this before
                //1 Tell fire base To update the like for this
                //add to vetted and its count
                DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("CommunitySaves");
                DatabaseReference profilesaveref = FirebaseDatabase.getInstance().getReference().child("ProfileSaves"); //reference to the profilesave database to update that too
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot datas: dataSnapshot.getChildren()){
                            //look for this caught fish record and update it
                            //break when found
                            //***1. Look at btnComVet button above to understand code!!! Same process ***
                            //For Community saves update the likes
                            ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String row_img_id = fishInfo.getImgId();
                                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                                        if ( datas.child("imgId").getValue().toString().equals(row_img_id) ) {
                                            boolean alreadyliked = false;
                                            if (datas.child("likedby").getChildrenCount() != 0) {
                                                for (DataSnapshot datalikedby : datas.child("likedby").getChildren()) {
                                                    //if exists add to liked list
                                                    if ( datalikedby.getValue().toString().equals(userId) ) {
                                                        alreadyliked = true;
                                                    }
                                                }
                                            }
                                            if(alreadyliked) {
                                                break; //3.1 dont update if it is liked already. DONT DELETE OTHERWISE IT LOOPS INFINITELY
                                            }

                                            //4. update variables to be added to the fields on firebase
                                            int newlike = fishInfo.getLikes() + 1; //add 1 to new like this will be the updated field in firebase
                                            List newlikedby = new ArrayList<String>();
                                            if (datas.child("likedby").getChildrenCount() != 0) {
                                                for (DataSnapshot datalikedby : datas.child("likedby").getChildren()) {
                                                    //if exists add to liked list
                                                    newlikedby.add(datalikedby.getValue().toString());
                                                }
                                            }

                                            Toast.makeText(activityContext, "Liked", Toast.LENGTH_SHORT).show();
                                            newlikedby.add(userId); //add this new user id to likedby this will be the updated field in firebase
                                            ref.child(datas.getKey().toString()).child("likes").setValue(newlike); //update likes to firebase
                                            ref.child(datas.getKey().toString()).child("likedby").setValue(newlikedby).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                }
                                            }); //5.1 update who liked to firebase
                                            break;
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //for profile saves update the likes
                profilesaveref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String row_img_id = fishInfo.getImgId();
                        for(DataSnapshot datas: dataSnapshot.getChildren()){
                            if ( datas.child("imgId").getValue().toString().equals(row_img_id) ) {
                                boolean alreadyliked = false;
                                if (datas.child("likedby").getChildrenCount() != 0) {
                                    for (DataSnapshot datalikedby : datas.child("likedby").getChildren()) {
                                        //if exists add to liked list
                                        if ( datalikedby.getValue().toString().equals(userId) ) {
                                            alreadyliked = true;
                                        }
                                    }
                                }
                                if(alreadyliked) {
                                    break; //3.1 dont update if it is liked already. DONT DELETE OTHERWISE IT LOOPS INFINITELY
                                }

                                //4. update variables to be added to the fields on firebase
                                int newlike = fishInfo.getLikes() + 1; //add 1 to new like this will be the updated field in firebase
                                List newlikedby = new ArrayList<String>();
                                if (datas.child("likedby").getChildrenCount() != 0) {
                                    for (DataSnapshot datalikedby : datas.child("likedby").getChildren()) {
                                        //if exists add to vettedby list
                                        newlikedby.add(datalikedby.getValue().toString());
                                    }
                                }

                                Toast.makeText(activityContext, "Liked", Toast.LENGTH_SHORT).show();
                                newlikedby.add(userId); //add this new user id to likedby this will be the updated field in firebase
                                profilesaveref.child(datas.getKey().toString()).child("likes").setValue(newlike); //update likes to firebase
                                profilesaveref.child(datas.getKey().toString()).child("likedby").setValue(newlikedby).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    }
                                }); //5.1 update who liked to firebase
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

//                Toast.makeText(activityContext, "Liked", Toast.LENGTH_SHORT).show();
                btnComLike.setAlpha(0.3f);
                btnComLike.setEnabled(false);
            }
        });




        return row;

    }


}
