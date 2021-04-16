package com.example.fishfinder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fishfinder.R;
import com.example.fishfinder.data.GeneralTest;

import java.util.ArrayList;


//This adapter is not used anymore it was a demo adapter for community list view.
public class ComInfoAdapter extends ArrayAdapter<GeneralTest> {

    private ArrayList<GeneralTest> fishInfoList = new ArrayList<>();
    private Context activityContext;
    private int textViewid;

    final long ONE_MEGABYTE = 1024 * 1024;
    public ComInfoAdapter(Context context, int textViewResourceId, ArrayList<GeneralTest> objects) {
        super(context, textViewResourceId, objects);

        textViewid = textViewResourceId;
        activityContext = context;
        fishInfoList = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activityContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_view_community_info, parent, false);
            System.out.println("Inflating" + position);
        } else {
            row = convertView;
        }


        TextView txtComDesc = (TextView) row.findViewById(R.id.txtComDesc);
        ImageView imgComCaughtImage = (ImageView) row.findViewById(R.id.imgComCaughtImage);
        Button btnComShowInfo = (Button) row.findViewById(R.id.btnComShowInfo);
        Button btnComLike = (Button) row.findViewById(R.id.btnComLike);
        Button btnComVet = (Button) row.findViewById(R.id.btnComVet);

        GeneralTest fishInfo = fishInfoList.get(position); //get the particular positioned item from the provided arraylist
        txtComDesc.setText(fishInfo.getUserId()); //just have it show user id for now.


        return row;

    }

}
