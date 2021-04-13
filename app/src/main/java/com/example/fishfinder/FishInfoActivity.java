package com.example.fishfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fishfinder.adapters.FishInfoAdapter;
import com.example.fishfinder.data.FishInfo;

import java.io.InputStream;

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
        new DownloadImageTask(imageViewFish).execute(img_url);

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

    // TODO: stop using depreciated AsyncTask
    // TODO: Consolidate! Reuse this code
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;

            // Check if URL is valid
            if (!URLUtil.isValidUrl(urldisplay)) {
//                Log.e("Error", "Invalid URL: " + urldisplay);
                return null;
            }

            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in); // ERROR: Failed to create image decoder with message 'unimplemented', from Incomplete image data
            } catch (Exception e) {
                Log.e("Error", e.getMessage() + ", URL: " + urldisplay);
//                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}