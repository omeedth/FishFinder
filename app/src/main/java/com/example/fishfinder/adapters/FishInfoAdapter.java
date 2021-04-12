package com.example.fishfinder.adapters;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fishfinder.R;
import com.example.fishfinder.data.FishInfo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// TODO: Consider changing to RecyclerView for performance improvement
public class FishInfoAdapter extends ArrayAdapter<FishInfo> {

    /* Variables */
    ExecutorService service = Executors.newFixedThreadPool(1);
    ArrayList<FishInfo> fishInfoList = new ArrayList<>();

    public FishInfoAdapter(Context context, int textViewResourceId, ArrayList<FishInfo> objects) {
        super(context, textViewResourceId, objects);
        fishInfoList = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

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

        // If there is no View then create a new View and inflate it with the appropriate components
        // TODO: Make an Image object that holds the image so we don't have to regrab the image (Check professor's CustomListAdapter example code)
//        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        row = inflater.inflate(R.layout.list_view_fish_info, null);

        /* Initialize Components from custom list view XML */
        TextView textView = (TextView) row.findViewById(R.id.textView);
        ImageView imageView = (ImageView) row.findViewById(R.id.imageView);

        // Retrieve name from the FishInfo object and set that to text of textView
        textView.setText(fishInfoList.get(position).getFBname());

        // Download Image Task - TODO: Optimize this by not re-downloading the image if we already have the image downloaded
        if(hasImage(imageView)) {
            // Image already downloaded... Do Nothing
            Log.i("Debug","Position: " + position + ", Image Already Exists!");
        } else {
            // Image not included in the ImageView... Download the Image
            String img_url = fishInfoList.get(position).getImage();
            new DownloadImageTask(imageView).execute(img_url);
        }

        return row;

    }

    // TODO: stop using depreciated AsyncTask
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

    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }

        return hasImage;
    }

}
