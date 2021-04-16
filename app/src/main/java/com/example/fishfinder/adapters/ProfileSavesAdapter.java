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

//        if (fishInfoList.get(position) == null) {
//            return;
//        }
        System.out.println("Inside" + position);
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


        System.out.println("Inside" + position);


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
//        StorageReference pathReference = storageRef.child("UserFishImages/");

        //gs://fishfinder-8c4d5.appspot.com/UserFishImages/0.jpg format of the url
        //gs://fishfinder-8c4d5.appspot.com/UserFishImages/
        TextView txtComDesc = (TextView) row.findViewById(R.id.txtComDesc);
        ImageView imgComCaughtImage = (ImageView) row.findViewById(R.id.imgComCaughtImage);
        Button btnComShowInfo = (Button) row.findViewById(R.id.btnComShowInfo);
        Button btnComLike = (Button) row.findViewById(R.id.btnComLike);
        Button btnComVet = (Button) row.findViewById(R.id.btnComVet);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser(); //get the current user based on the auth
        String userId = firebaseUser.getUid().toString(); //used to check if we can set the button to disabled or not

        GeneralTest fishInfo = fishInfoList.get(position); //get the particular positioned item from the provided arraylist
        txtComDesc.setText(fishInfo.getUserId()); //just have it show user id for now.


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

        return row;

    }


}