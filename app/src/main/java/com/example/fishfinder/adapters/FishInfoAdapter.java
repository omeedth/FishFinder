package com.example.fishfinder.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
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
import com.example.fishfinder.util.RestAPIUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

// TODO: Consider changing to RecyclerView for performance improvement
public class FishInfoAdapter extends ArrayAdapter<FishInfo> {

    /* Variables */
    ExecutorService service = Executors.newFixedThreadPool(1);
    ArrayList<FishInfo> fishInfoList = new ArrayList<>();
    ArrayList<Bitmap> fishImageList = new ArrayList<>();
    Context activityContext;
    ExecutorService parentActivityService;

    private Runnable callback;  // A call back function from the activity to run (UNUSED)

    public FishInfoAdapter(Context context, int textViewResourceId, ArrayList<FishInfo> objects, ExecutorService parentActivityService, Runnable callback) {
        super(context, textViewResourceId, objects);
        activityContext = context;
        fishInfoList = objects;
        this.parentActivityService = parentActivityService;
        this.callback = callback;
    }

    public FishInfoAdapter(Context context, int textViewResourceId, ArrayList<FishInfo> objects, Runnable callback) {
        this(context, textViewResourceId, objects, null, callback);
    }

    public FishInfoAdapter(Context context, int textViewResourceId, ArrayList<FishInfo> objects, ExecutorService parentActivityService) {
        this(context, textViewResourceId, objects, parentActivityService, null);
    }

    public FishInfoAdapter(Context context, int textViewResourceId, ArrayList<FishInfo> objects) {
        this(context, textViewResourceId, objects, null, null);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    // https://stackoverflow.com/questions/6081497/listview-shows-wrong-images
    // BUG - wrong images are shown sometimes, or it takes forever for it to load the correct image
    // Sometimes you need to scroll out and back in to get the right image
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // If there is already a View then just return it (Didn't Do Anything)
        // We only need to inflate the layout if the layout is not present (it is expensive to keep doing this)
        View row;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_view_fish_info, parent, false);
        } else {
            row = convertView;
        }

        /* Initialize Components from custom list view XML */
        TextView textView = (TextView) row.findViewById(R.id.textView);
        ImageView imageView = (ImageView) row.findViewById(R.id.imageView);
        Button buttonShowInfo = (Button) row.findViewById(R.id.buttonShowInfo);
        Button buttonShowMap = (Button) row.findViewById(R.id.buttonShowMap);

        FishInfo fishInfo = fishInfoList.get(position);

        // Have empty imageView Initially
        imageView.setImageResource(android.R.color.transparent);

        // Retrieve name from the FishInfo object and set that to text of textView
        textView.setText(fishInfoList.get(position).getFBname());

        /* Initialize Listeners */
        buttonShowInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* If the parent activity has other processes running on the other thread, terminate all of them */
                if (parentActivityService != null) {
//                    parentActivityService.shutdownNow();
                }

                try {
                    Intent goToFishInfoActivity = new Intent(v.getContext(), FishInfoActivity.class);
                    goToFishInfoActivity.putExtra("fishInfo", fishInfo);

                    // based on item add info to intent
                    goToFishInfoActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   // This flag is required to be added to activity for us to navigate there from this class
                    activityContext.startActivity(goToFishInfoActivity);
                } catch (Exception e) {
                    Log.e("Error", e.getLocalizedMessage());
                }

            }
        });

        buttonShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* If the parent activity has other processes running on the other thread, terminate all of them */
                if (parentActivityService != null) {
//                    parentActivityService.shutdownNow();
                }

                try {
                    Intent goToSearchForFishActivity = new Intent(v.getContext(), SearchForFishActivity.class);
                    goToSearchForFishActivity.putExtra("fishInfo", fishInfo);

                    //based on item add info to intent
                    goToSearchForFishActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  // This flag is required to be added to activity for us to navigate there from this class
                    activityContext.startActivity(goToSearchForFishActivity);
                } catch (Exception e) {
                    Log.e("Error", e.getLocalizedMessage());
                }

            }
        });

        // Download Image Task
        if (fishInfo.getImageBytes() != null) {
            Bitmap fishImageBM = BitmapFactory.decodeByteArray(fishInfo.getImageBytes(), 0, fishInfo.getImageBytes().length);
            imageView.setImageBitmap(fishImageBM);
        } else {

            // Image not included in the ImageView... Download the Image
            String img_url = fishInfoList.get(position).getImage();

//            Log.i("Info", "Grabbing Fish Image for Position: " + position); // DEBUGGING

            service.execute(new Runnable() {
                @Override
                public void run() {
                    Bitmap fishImageBM = RestAPIUtil.getImage(img_url);

                    /* Compressing image to bytes (if we get an image) */
                    if (fishImageBM != null) {

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        fishImageBM.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] imageBytes = stream.toByteArray();

                        // Adding the image bytes to FishInfo
                        fishInfo.setImageBytes(imageBytes);

                    }

                    // This will post a command to the main UI Thread
                    // This is necessary so that the code knows the variables for this class
                    // https://stackoverflow.com/questions/27737769/how-to-properly-use-a-handler
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(fishImageBM);
                        }
                    });

                }
            });

        }

        return row;

    }


    public void setParentActivityService(ExecutorService parentActivityService) {
        this.parentActivityService = parentActivityService;
    }

    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }

        return hasImage;
    }

}
