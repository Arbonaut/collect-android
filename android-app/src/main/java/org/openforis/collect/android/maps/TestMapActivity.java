package org.openforis.collect.android.maps;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.app.Activity;
import android.os.Bundle;
/**
 * 
 * @author K. Waga
 *
 */



public class TestMapActivity extends Activity {

    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);



        MapView mapView = new MapView(this, 256); //constructor

        mapView.setClickable(true);

        mapView.setBuiltInZoomControls(true);

        setContentView(mapView); //displaying the MapView

        mapView.getController().setZoom(14); //set initial zoom-level, depends on your need

        mapView.getController().setCenter(new GeoPoint(62.617786, 29.814448)); //This point is in Enschede, Netherlands. You should select a point in your map or get it from user's location.
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setUseDataConnection(false); //keeps the mapView from loading online tiles using network connection.
        /*TextView myTextView = new TextView(this);

        myTextView.setTextAppearance(this, android.R.style.TextAppearance_Large_Inverse);

        myTextView.setText("Enschede, Netherlands");

        Button myUselessButton = new Button(this);

        myUselessButton.setText("Click");



        final RelativeLayout relativeLayout = new RelativeLayout(this);

        final RelativeLayout.LayoutParams mapViewLayoutParams = new RelativeLayout.LayoutParams(

                        RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.FILL_PARENT);

        final RelativeLayout.LayoutParams textViewLayoutParams = new RelativeLayout.LayoutParams(

                        RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        final RelativeLayout.LayoutParams buttonLayoutParams = new RelativeLayout.LayoutParams(

                        RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        buttonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);



        relativeLayout.addView(mapView, mapViewLayoutParams);

        relativeLayout.addView(myTextView, textViewLayoutParams);

        relativeLayout.addView(myUselessButton,buttonLayoutParams);

        setContentView(relativeLayout);*/
    }

}