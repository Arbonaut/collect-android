package org.openforis.collect.android.maps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.logs.RunnableHandler;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.management.DataManager;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.screens.FormScreen;
import org.openforis.collect.model.CollectRecord;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.model.Coordinate;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.IntegerValue;
import org.openforis.idm.model.Node;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * 
 * @author K. Waga
 *
 */
public class OsmMapActivity extends Activity {
	
	private static final String TAG = "MapActivity";
 
	private MapView mapView;
	
    private int MAP_DEFAULT_ZOOM = 14;

    private double MAP_DEFAULT_LATITUDE = 60.2483128;
    private double MAP_DEFAULT_LONGITUDE = 25.0179958;
    
    LocationReceiver locRec;
 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
		setContentView(R.layout.osm_map);
		
		ApplicationManager.mapActivity = this;
		this.locRec = null;
 
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
        //Drawable marker=getResources().getDrawable(android.R.drawable.ic_menu_myplaces);
        //addPolygon();
        //drawLine(new GeoPoint(62.6, 29.7),new GeoPoint(62.9, 29.9));
        //showCurrentLocation();
	}
	
	public void onResume(){
		super.onResume();
		Log.i(getResources().getString(R.string.app_name),TAG+":onResume");
		/*ArrayList<OverlayItem> overlayItemArray = new ArrayList<OverlayItem>();                
        OverlayItem olItem1 = new OverlayItem("2", "Title2", "Description2",  new GeoPoint(62.61,29.79));
        //OverlayItem olItem2 = new OverlayItem("1", "Title1", "Description1", new GeoPoint(62.62,29.8));
        //OverlayItem olItem3 = new OverlayItem("3", "Title3", "Description3", new GeoPoint(62.63,29.81));        
        overlayItemArray.add(olItem1);
        //overlayItemArray.add(olItem2);
        //overlayItemArray.add(olItem3);
        
        PlotMarker overlay = new PlotMarker(this, overlayItemArray);
        mapView.getOverlays().add(overlay);*/
        
        DataManager dataManager = new DataManager((CollectSurvey) ApplicationManager.getSurvey(),ApplicationManager.getSurvey().getSchema().getRootEntityDefinition(ApplicationManager.currRootEntityId).getName(),ApplicationManager.getLoggedInUser());
        List<CollectRecord> savedRecordsSummaries = dataManager.loadSummaries();
        ArrayList<OverlayItem> overlayItemArray = new ArrayList<OverlayItem>();
        for (CollectRecord savedRecordSummary : savedRecordsSummaries){
        	CollectRecord savedRecord = dataManager.loadRecord(savedRecordSummary.getId());
        	List<Node<? extends NodeDefinition>> plotsList = savedRecord.getRootEntity().getAll(getResources().getString(R.string.plotEntityField));
        	for (Node<? extends NodeDefinition> plot : plotsList){
        		Entity plotEntity = (Entity)plot;
        		Coordinate plotCenter = (Coordinate) plotEntity.getValue(getResources().getString(R.string.plotCoordinatesField), 0);
        		Log.e("plotcoords",plotCenter.getY()+"=="+plotCenter.getX());
        		IntegerValue plotNo = (IntegerValue)plotEntity.getValue(getResources().getString(R.string.plotIdField), 0);
        		OverlayItem olItem = new OverlayItem(String.valueOf(savedRecord.getId()), plotNo.getValue().toString(), plotCenter.getX()+","+plotCenter.getY(),  new GeoPoint(plotCenter.getX(),plotCenter.getY()));
            	overlayItemArray.add(olItem);
        	}        	
        }
        PlotMarker overlay = new PlotMarker(this, overlayItemArray);
        mapView.getOverlays().add(overlay);
        
        //mapView.getOverlays().add(new PlotOverlay(this));
        mapView.getOverlays().add(new MapGestureDetectorOverlay(this, mapView));
        mapView.invalidate();
	}	
	
	public void showCurrentLocation(){
		Log.e("showCurrentLocation","==================");
		this.turnGPSOn();
		locRec = new LocationReceiver((LocationManager) getSystemService(Context.LOCATION_SERVICE));
		locRec.getCurrentLocation();		
	}
	
	public void drawUserMarker(Location location){
		Log.e("drawUserMarker","================");
		location.setLatitude(62.6);
		location.setLongitude(29.78);
		Drawable marker=getResources().getDrawable(android.R.drawable.ic_menu_mylocation);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
		ResourceProxy resourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
		List<Overlay> overlays = mapView.getOverlays();
		for (int i=0;i<overlays.size();i++){
			if (overlays.get(i) instanceof UserMarker){
				this.removeOverlay(overlays.get(i));
				break;
			}
		}
		mapView.getOverlays().add(new UserMarker(marker, location.getLatitude(), location.getLongitude(), resourceProxy));
		//mapView.getOverlays().add(new UserMarker(marker, location.getLongitude(), location.getLatitude(), resourceProxy));
		mapView.invalidate();
	}
	
	public PathOverlay drawLine(GeoPoint pt1, GeoPoint pt2){
		Log.e("drawingLine","============");
		PathOverlay myOverlay= new PathOverlay(Color.RED, this);
	    //myOverlay.getPaint().setStyle(Paint.Style.FILL);
	    myOverlay.addPoint(pt1);
	    myOverlay.addPoint(pt2);
	    //mapView.getOverlays().remove(mapView.getOverlays().size()-1);
	    mapView.getOverlays().add(myOverlay);
	    mapView.invalidate();
	    //Log.e("iloscOverlays","===="+mapView.getOverlays().size());

		/*List<Overlay> oldOverlays = mapView.getOverlays();
	    if (oldOverlays.size()>0){
	    	oldOverlays.remove(oldOverlays.size()-1);	
	    }
	    Log.e("numberOfOverlays","=="+oldOverlays.size());
		mapView.getOverlays().clear();
	    mapView.getOverlays().add(myOverlay);
	    //mapView.getOverlays().addAll(oldOverlays);
	    mapView.invalidate();*/
	    return myOverlay;
	}
	
	public void removeOverlay(int overlayIndex){
		if (overlayIndex==-1){
			mapView.getOverlays().remove(mapView.getOverlays().size()-1);
		} else {
			mapView.getOverlays().remove(overlayIndex);	
		}		
	    mapView.invalidate();
	}
	
	public void removeOverlay(Overlay overlay){
		mapView.getOverlays().remove(overlay);
	    mapView.invalidate();
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
	
	public void navigateToPlot(String plotId){
		Log.e("navigation","started"+plotId);
		this.turnGPSOn();
		locRec = new LocationReceiver((LocationManager) getSystemService(Context.LOCATION_SERVICE));
		locRec.startNavigating(new GeoPoint(62.61,29.79));
		//mapView.getOverlays().remove(mapView.getOverlays().size()-1);
		
	}
	
	public void stopNavigationToPlot(){
		Log.e("navigation","===STOPPED");
		locRec.stopListeningForLocationUpdates();
		this.turnGPSOff();
	    mapView.getOverlays().remove(mapView.getOverlays().size()-1);
	    mapView.invalidate();
	}
	
	public void stopGPS(){
		Log.e("stopGPS","===STOPPED");
		locRec.stopListeningForLocationUpdates();
		this.turnGPSOff();
	}
	
	public void openRecordData(int recordId){
		Log.e("RECORD","IS BEING OPENED"+recordId);
		CollectSurvey collectSurvey = (CollectSurvey)ApplicationManager.getSurvey();
		ApplicationManager.dataManager = new DataManager(collectSurvey,collectSurvey.getSchema().getRootEntityDefinitions().get(0).getName(),ApplicationManager.getLoggedInUser());
    	ApplicationManager.currentRecord = ApplicationManager.dataManager.loadRecord(recordId);
    	Log.e("recordId","=="+recordId);
    	Entity rootEntity = ApplicationManager.currentRecord.getRootEntity();
		rootEntity.setId(ApplicationManager.currRootEntityId);
		Intent intent = new Intent(this,FormScreen.class);
		EntityDefinition rootEntityDef = (EntityDefinition)ApplicationManager.getSurvey().getSchema().getDefinitionById(ApplicationManager.currRootEntityId);
		intent.putExtra(getResources().getString(R.string.breadcrumb), ApplicationManager.getLabel(rootEntityDef));
		intent.putExtra(getResources().getString(R.string.screenTitle), ApplicationManager.getLabel(rootEntityDef));
		intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.singleEntityIntent));
		intent.putExtra(getResources().getString(R.string.parentFormScreenId), "");
		intent.putExtra(getResources().getString(R.string.idmlId), ApplicationManager.currRootEntityId);
		intent.putExtra(getResources().getString(R.string.instanceNo), 0);
		List<NodeDefinition> entityAttributes = rootEntityDef.getChildDefinitions();
        int counter = 0;
        for (NodeDefinition formField : entityAttributes){
			intent.putExtra(getResources().getString(R.string.attributeId)+counter, formField.getId());
			counter++;
        }
		this.startActivityForResult(intent,getResources().getInteger(R.integer.startingFormScreen));
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
    public boolean onPrepareOptionsMenu (Menu menu) {
    	menu.findItem(R.id.menu_map_plot_drawing_starts).setVisible(!ApplicationManager.isPlotDrawingStarted);
    	menu.findItem(R.id.menu_map_plot_drawing_ends).setVisible(ApplicationManager.isPlotDrawingStarted);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        switch (item.getItemId())
        {
			case R.id.menu_map_plot_drawing_starts:
				this.startDrawingPlot();
			    return true;
			case R.id.menu_map_plot_drawing_ends:
				this.stopDrawingPlot();
			    return true;			    
			case R.id.menu_map_refresh_location:
				this.showCurrentLocation();
			    return true;
			case R.id.menu_map_exit:
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
    
    public double haversine(double lat1, double lon1, double lat2, double lon2) {
    	double R = 6372.8;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
 
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }
    
	/*public void launchGPS(){
		  Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		  startActivityForResult(myIntent, 1);
	  }*/
	
	public void turnGPSOn()
	{
	     Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
	     intent.putExtra("enabled", true);
	     this.sendBroadcast(intent);

	    String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	    if(!provider.contains("gps")){ //if gps is disabled
	        final Intent poke = new Intent();
	        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
	        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	        poke.setData(Uri.parse("3")); 
	        this.sendBroadcast(poke);


	    }
	}

	public void turnGPSOff()
	{
	    String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	    if(provider.contains("gps")){ //if gps is enabled
	        final Intent poke = new Intent();
	        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
	        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	        poke.setData(Uri.parse("3")); 
	        this.sendBroadcast(poke);
	    }
	}
	
	public void startDrawingPlot(){
		ApplicationManager.isPlotDrawingStarted = true;
		Log.e("drawing","STARTED");
	}
	
	public void stopDrawingPlot(){
		ApplicationManager.isPlotDrawingStarted = false;
		int savingResult = savePlotShapeToFile(MapGestureDetectorOverlay.plotCorners);
		MapGestureDetectorOverlay.plotCorners.clear();
		Log.e("drawing","FINISHED"+savingResult);
	}
	
	private int savePlotShapeToFile(List<GeoPoint> plotCorners){
		Log.e("saving plot","to KML file"+plotCorners.size());
		/*
		 <kml xmlns="http://earth.google.com/kml/2.0">
  <Placemark>
   <name>Bora-Bora Airport</name>
   <Point>
     <coordinates>-151.752044,-16.443118</coordinates>
   </Point>
  </Placemark>
</kml>
		 */
		if (plotCorners.size()>2){
			try{				
				File file = new File(Environment.getExternalStorageDirectory().toString()
						+getResources().getString(R.string.plotBoundariesSavingPath)
						+getResources().getString(R.string.plotBoundariesSavingBaseFileName)
						+System.currentTimeMillis()
						+getResources().getString(R.string.plotBoundariesSavingFileExtension));
				if (!file.exists()) {
					file.createNewFile();
				}
	 
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write("<kml xmlns=\"http://earth.google.com/kml/2.0\">\r\n\t<Placemark>\r\n");
				for (GeoPoint plotCorner : plotCorners){
					Log.e("plotCorner", (plotCorner.getLatitudeE6()/1E6)+"=="+(plotCorner.getLongitudeE6()/1E6));
					bw.write("\t<Point>\r\n\t\t<coordinates>"+(plotCorner.getLatitudeE6()/1E6)+","+(plotCorner.getLongitudeE6()/1E6)+"</coordinates>\r\n\t</Point>\r\n");
				}
				bw.write("\t</Placemark>\r\n</kml>");
				bw.close();
			} catch (Exception e){
				RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":savePlotShapeToFile",
	    				Environment.getExternalStorageDirectory().toString()
	    				+getResources().getString(R.string.logs_folder)
	    				+getResources().getString(R.string.logs_file_name)
	    				+System.currentTimeMillis()
	    				+getResources().getString(R.string.log_file_extension));
			}			
		} else {
			return -1;//plot has to have at least 3 corners
		}
		return 0;
	}
}