package com.example.fishfinder.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
        View v;
//        if (parent.getChildCount() > position && (v = parent.getChildAt(position)) != null) {
//            Log.i("Info", "View: " + v.toString());
//            return v;
//        }
//        Log.i("Info", "No View! Making...");

        // If there is no View then create a new View and inflate it with the appropriate components
        v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.list_view_fish_info, null);
        TextView textView = (TextView) v.findViewById(R.id.textView);
        ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
        textView.setText(fishInfoList.get(position).getFBname());

        // Download Image Task
        String img_url = fishInfoList.get(position).getImage();
        new DownloadImageTask(imageView).execute(img_url);

        return v;

    }

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
