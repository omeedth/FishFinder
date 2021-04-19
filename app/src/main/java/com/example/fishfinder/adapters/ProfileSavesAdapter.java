package com.example.fishfinder.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fishfinder.FishInfoActivity;
import com.example.fishfinder.R;
import com.example.fishfinder.SearchForFishActivity;
import com.example.fishfinder.ShowPublishedInfoActivity;
import com.example.fishfinder.ShowSavedInfoActivity;
import com.example.fishfinder.data.FishInfo;
import com.example.fishfinder.data.GeneralTest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

// TODO: Consider changing to RecyclerView for performance improvement
public class ProfileSavesAdapter extends ArrayAdapter<GeneralTest> {


    //Basically Community Info Adapter with less functionality
    /* Variables */
    private ExecutorService service = Executors.newFixedThreadPool(1);
    private ArrayList<GeneralTest> fishInfoList = new ArrayList<>();
    private Context activityContext;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    final long ONE_MEGABYTE = 1024 * 1024;
    public ProfileSavesAdapter(Context context, int textViewResourceId, ArrayList<GeneralTest> objects) {
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


        // If there is already a View then just return it (Didn't Do Anything)
        // We only need to inflate the layout if the layout is not present (it is expensive to keep doing this)
        View row;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activityContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_view_saved_info, parent, false);
        } else {
            row = convertView;
        }




        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
//        StorageReference pathReference = storageRef.child("UserFishImages/");

        //gs://fishfinder-8c4d5.appspot.com/UserFishImages/0.jpg format of the url
        //gs://fishfinder-8c4d5.appspot.com/UserFishImages/
        TextView txtSavedDesc = (TextView) row.findViewById(R.id.txtSavedDesc);
        ImageView imgSavedCaughtImage = (ImageView) row.findViewById(R.id.imgSavedCaughtImage);
        Button btnSavedShowInfo = (Button) row.findViewById(R.id.btnSavedShowInfo);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser(); //get the current user based on the auth
        String userId = firebaseUser.getUid().toString(); //used to check if we can set the button to disabled or not

        GeneralTest fishInfo = fishInfoList.get(position); //get the particular positioned item from the provided arraylist
        txtSavedDesc.setText(fishInfo.getUsername() + " " + fishInfo.getTitle()); //just have it show user id for now.


        //** Download Images into this row **
        StorageReference pathReference = storageRef.child("UserFishImages/" + fishInfo.getImgId() + ".jpg"); //get the path to download the image
//        byte[] downloadedBytes = null;
        pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
//                downloadedBytes = bytes;
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgSavedCaughtImage.setImageBitmap(bitmap);
            }
        });

        btnSavedShowInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent goToShowSavedInfoActivity = new Intent(v.getContext(), ShowSavedInfoActivity.class);
                    goToShowSavedInfoActivity.putExtra("fishInfo", fishInfo);
                    // based on item add info to intent
                    goToShowSavedInfoActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   // This flag is required to be added to activity for us to navigate there from this class
                    activityContext.startActivity(goToShowSavedInfoActivity);
                } catch (Exception e) {
                    Log.e("Error ShowInfo", e.getLocalizedMessage());
                }
            }
        });

        return row;

    }


}