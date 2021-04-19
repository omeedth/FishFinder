package com.example.fishfinder.data;

import android.content.Context;
import android.graphics.Color;

import androidx.core.graphics.ColorUtils;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/* https://stackoverflow.com/questions/30967961/android-maps-utils-cluster-icon-color */
public class MapsClusterItemRenderer extends DefaultClusterRenderer<MapsClusterItem> {

    /* Variables */
    private final int ONE = 1;
    private final int colorModifier = 100;
    private final int MARKER_COLOR;
    private float[] MARKER_HSL = new float[3]; // [Hue, Saturation, Lightness]
    private Context ctx;

    /* Constructor */
    public MapsClusterItemRenderer(Context context, GoogleMap map, ClusterManager<MapsClusterItem> clusterManager, int markerColor) {
        super(context, map, clusterManager);

        this.MARKER_COLOR = markerColor;
        ColorUtils.colorToHSL(MARKER_COLOR, MARKER_HSL);
        this.ctx = context;
    }

    /* Logic Methods */

    @Override
    protected int getColor(int clusterSize) {
        return super.getColor(clusterSize);
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<MapsClusterItem> cluster, MarkerOptions markerOptions) {
        super.onBeforeClusterRendered(cluster, markerOptions);
    }

    @Override
    protected void onBeforeClusterItemRendered(MapsClusterItem item, MarkerOptions markerOptions) {

        BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.defaultMarker(MARKER_HSL[0]);
        markerOptions.icon(markerDescriptor);

    }

    @Override
    protected void onClusterItemRendered(MapsClusterItem clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
    }

}
