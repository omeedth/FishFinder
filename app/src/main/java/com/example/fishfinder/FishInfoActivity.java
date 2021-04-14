package com.example.fishfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fishfinder.adapters.FishInfoAdapter;
import com.example.fishfinder.data.FishInfo;
import com.example.fishfinder.util.RestAPIUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FishInfoActivity extends AppCompatActivity {

    /* Components */
    private ImageView imageViewFish;
    private TextView textViewFBNameInfo;
    private TextView textViewSpeciesInfo;
    private TextView textViewGenusInfo;
    private TextView textViewBodyShapeInfo;
    private TextView textViewLengthInfo;
    private TextView textViewWeightInfo;
    private TextView textViewDangerousInfo;
    private TextView textViewWaterTypeInfo;
    private TextView textViewCommentsInfo;

    /* Variables */
    private final String LENGTH_UNITS = "cm";
    private final String WEIGHT_UNITS = "g";
    private FishInfo fishInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish_info);

        /* Retrieve FishInfo from Bundle */
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                // No Information
                fishInfo = new FishInfo(); // Default is ALL null
            } else {
                fishInfo = (FishInfo) extras.getSerializable("fishInfo");
            }
        }

        /* Initialize Components */
        imageViewFish = findViewById(R.id.imageViewFish);
        textViewFBNameInfo = findViewById(R.id.textViewFBNameInfo);
        textViewSpeciesInfo = findViewById(R.id.textViewSpeciesInfo);
        textViewGenusInfo = findViewById(R.id.textViewGenusInfo);
        textViewBodyShapeInfo = findViewById(R.id.textViewBodyShapeInfo);
        textViewLengthInfo = findViewById(R.id.textViewLengthInfo);
        textViewWeightInfo = findViewById(R.id.textViewWeightInfo);
        textViewDangerousInfo = findViewById(R.id.textViewDangerousInfo);
        textViewWaterTypeInfo = findViewById(R.id.textViewWaterTypeInfo);
        textViewCommentsInfo = findViewById(R.id.textViewCommentsInfo);

        /* Setup (Input values into Components from FishInfo) */

        // Download Fish Image
        String img_url = fishInfo.getImage();
//        new DownloadImageTask(imageViewFish).execute(img_url);

        if (fishInfo.getImageBytes() != null) {
//            Log.i("Info", "Image Already Grabbed!");  // DEBUGGING
            Bitmap fishImageBM = BitmapFactory.decodeByteArray(fishInfo.getImageBytes(), 0, fishInfo.getImageBytes().length);
            imageViewFish.setImageBitmap(fishImageBM);
        } else {
            ExecutorService service = Executors.newFixedThreadPool(1);
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
                            imageViewFish.setImageBitmap(fishImageBM);
                        }
                    });

                }
            });
        }

        // TODO: Make layout look better
        // TODO: Fix Comments overflow problem
        textViewFBNameInfo.setText(fishInfo.getFBname());
        textViewSpeciesInfo.setText(fishInfo.getSpecies());
        textViewGenusInfo.setText(fishInfo.getGenus());
        textViewBodyShapeInfo.setText(fishInfo.getBodyShapeI());
        textViewLengthInfo.setText("" + fishInfo.getLength() + " " + LENGTH_UNITS);
        textViewWeightInfo.setText("" + fishInfo.getWeight() + " " + WEIGHT_UNITS);
        textViewDangerousInfo.setText(fishInfo.getDangerous());
        textViewWaterTypeInfo.setText((fishInfo.isFresh() ? "FreshWater" : "SaltWater")); // TODO: Maybe fish is both?
        textViewCommentsInfo.setText(fishInfo.getComments());

    }

//    // TODO: stop using depreciated AsyncTask
//    // TODO: Consolidate! Reuse this code
//    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//        ImageView bmImage;
//
//        public DownloadImageTask(ImageView bmImage) {
//            this.bmImage = bmImage;
//        }
//
//        protected Bitmap doInBackground(String... urls) {
//            String urldisplay = urls[0];
//            Bitmap mIcon11 = null;
//
//            // Check if URL is valid
//            if (!URLUtil.isValidUrl(urldisplay)) {
////                Log.e("Error", "Invalid URL: " + urldisplay);
//                return null;
//            }
//
//            try {
//                InputStream in = new java.net.URL(urldisplay).openStream();
//                mIcon11 = BitmapFactory.decodeStream(in); // ERROR: Failed to create image decoder with message 'unimplemented', from Incomplete image data
//            } catch (Exception e) {
//                Log.e("Error", e.getMessage() + ", URL: " + urldisplay);
////                e.printStackTrace();
//            }
//            return mIcon11;
//        }
//
//        protected void onPostExecute(Bitmap result) {
//            bmImage.setImageBitmap(result);
//        }
//    }
}