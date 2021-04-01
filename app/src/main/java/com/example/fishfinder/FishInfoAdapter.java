package com.example.fishfinder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class FishInfoAdapter extends ArrayAdapter<FishInfo> {

    /* Variables */
    ExecutorService service = Executors.newFixedThreadPool(1); //Executor meant to handle heavy load non-UI tasks. Not used here currently but was used in an older code
    ArrayList<FishInfo> fishInfoList = new ArrayList<>(); //local var reference to hold our fishinfo objects

    public FishInfoAdapter(Context context, int textViewResourceId, ArrayList<FishInfo> objects) {
        super(context, textViewResourceId, objects);
        fishInfoList = objects; //init in constructor the fishinfo objects that are passed in
    }

    @Override
    public int getCount() {
        //a must implement for the adapter
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Basic adapter view variables setup and inflating to view
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.list_view_fish_info, null);

        //setting information to view by grabbing the row and its representing components it is to set information on
        TextView textView = (TextView) v.findViewById(R.id.textView);
        ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
        textView.setText(fishInfoList.get(position).getFBname());

        // Download Image Task
        String img_url = fishInfoList.get(position).getImage();
        new DownloadImageTask(imageView).execute(img_url); //Handle showing the downloaded image based on the image url provided. Image will show based on the row it is meant to be for

        return v;

    }


    //Class to download in an image from a url and place it in a specified image view. Stackoverflow reference.
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
