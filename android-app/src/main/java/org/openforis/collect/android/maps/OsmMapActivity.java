package org.openforis.collect.android.maps;

import java.util.ArrayList;

import org.openforis.collect.android.R;
import org.openforis.collect.android.logs.RunnableHandler;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.AlertMessage;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
 
public class OsmMapActivity extends Activity {
	
	private static final String TAG = "MapActivity";
 
	private MapView mapView;
	
    private int MAP_DEFAULT_ZOOM = 14;

    private double MAP_DEFAULT_LATITUDE = 62.6;
    private double MAP_DEFAULT_LONGITUDE = 29.78;
 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.osm_map);
 
		mapView = (MapView) findViewById(R.id.mapview);
		/*mapView.getSettings().setJavaScriptEnabled(true);
		//webView.loadUrl("http://www.maps.google.com");		
		webView.loadUrl("file://sdcard/OSMexperiments/local_tiles.htm");
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);*/
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setClickable(true);
        mapView.setUseDataConnection(false);
        mapView.getController().setZoom(MAP_DEFAULT_ZOOM);
        mapView.getController().setCenter(new GeoPoint(MAP_DEFAULT_LATITUDE, MAP_DEFAULT_LONGITUDE));
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        
        ArrayList<OverlayItem> overlayItemArray = new ArrayList<OverlayItem>();                
        OverlayItem olItem0 = new OverlayItem("Title0", "Description0", new GeoPoint(62.7,29.7));
        OverlayItem olItem1 = new OverlayItem("Title1", "Description1", new GeoPoint(62.5,29.8));
        OverlayItem olItem2 = new OverlayItem("Title2", "Description2", new GeoPoint(62.8,29.9));
        overlayItemArray.add(olItem0);
       
        PlotItemizedOverlay overlay = new PlotItemizedOverlay(this, overlayItemArray);
        mapView.getOverlays().add(overlay);
        
        //Drawable marker=getResources().getDrawable(android.R.drawable.ic_menu_myplaces);
        Drawable marker=getResources().getDrawable(android.R.drawable.ic_menu_mylocation);
        	   marker.setBounds(0, 0, marker.getIntrinsicWidth(), 
        	     marker.getIntrinsicHeight());
        	   ResourceProxy resourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
        	   mapView.getOverlays().add(new UserMarker(marker, 62600000, 29780000, resourceProxy));
        	   
        //addPolygon();
        //drawLine(new GeoPoint(62.6, 29.7),new GeoPoint(62.9, 29.9));
	}
	
	private void drawLine(GeoPoint pt1, GeoPoint pt2){
		PathOverlay myOverlay= new PathOverlay(Color.RED, this);
	    //myOverlay.getPaint().setStyle(Paint.Style.FILL);
	    myOverlay.addPoint(pt1);
	    myOverlay.addPoint(pt2);
	    
	    mapView.getOverlays().add(myOverlay);
	}
	
	private void addPolygon() {
	    int diff=1000;

	    GeoPoint pt1=new GeoPoint(62.6, 29.78);
	    GeoPoint pt2= new GeoPoint(pt1.getLatitudeE6()+diff, pt1.getLongitudeE6());
	    GeoPoint pt3= new GeoPoint(pt1.getLatitudeE6()+diff, pt1.getLongitudeE6()+diff);
	    GeoPoint pt4= new GeoPoint(pt1.getLatitudeE6(), pt1.getLongitudeE6()+diff);
	    GeoPoint pt5= new GeoPoint(pt1);


	    PathOverlay myOverlay= new PathOverlay(Color.RED, this);
	    myOverlay.getPaint().setStyle(Paint.Style.FILL);

	    myOverlay.addPoint(pt1);
	    myOverlay.addPoint(pt2);
	    myOverlay.addPoint(pt3);
	    myOverlay.addPoint(pt4);
	    myOverlay.addPoint(pt5);

	    mapView.getOverlays().add(myOverlay);
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
    	try{
			OsmMapActivity.this.finish();
    	}catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onBackPressed",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
    	}
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.map_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        switch (item.getItemId())
        {
			/*case R.id.menu_map:
			    
			    return true;*/	
			case R.id.menu_exit:
				AlertMessage.createPositiveNegativeDialog(OsmMapActivity.this, false, getResources().getDrawable(R.drawable.warningsign),
	 					getResources().getString(R.string.exitAppTitle), getResources().getString(R.string.exitAppMessage),
	 					getResources().getString(R.string.yes), getResources().getString(R.string.no),
	 		    		new DialogInterface.OnClickListener() {
	 						@Override
	 						public void onClick(DialogInterface dialog, int which) {
	 							if (ApplicationManager.rootEntitySelectionActivity!=null){
	 								ApplicationManager.rootEntitySelectionActivity.finish();
	 							}
	 							if (ApplicationManager.recordSelectionActivity!=null){
	 								ApplicationManager.recordSelectionActivity.finish();
	 							}
	 							if (ApplicationManager.formScreenActivityList!=null){
	 								for (Activity formScreenActivity : ApplicationManager.formScreenActivityList){
	 									formScreenActivity.finish();
	 								}
	 							}
	 							if (ApplicationManager.formSelectionActivity!=null){
	 								ApplicationManager.formSelectionActivity.finish();
	 							}
	 							ApplicationManager.mainActivity.finish();
	 							OsmMapActivity.this.finish();
	 						}
	 					},
	 		    		new DialogInterface.OnClickListener() {
	 						@Override
	 						public void onClick(DialogInterface dialog, int which) {
	 							
	 						}
	 					},
	 					null).show();
			    return true;			    
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}
    
}