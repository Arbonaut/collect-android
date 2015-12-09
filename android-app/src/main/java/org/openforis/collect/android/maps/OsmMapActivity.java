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
import org.openforis.collect.android.misc.Pair;
import org.openforis.collect.android.screens.FormScreen;
import org.openforis.collect.model.CollectRecord;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.model.Coordinate;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.IntegerValue;
import org.openforis.idm.model.Node;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
/**
 * 
 * @author K. Waga
 *
 */
public class OsmMapActivity extends Activity {
	
	private static final String TAG = "OsmMapActivity";
	 
	private MapView mapView;

    //private double MAP_DEFAULT_LATITUDE = 60.2483128;
    //private double MAP_DEFAULT_LATITUDE = 60.4788815;//Naantali
    //private double MAP_DEFAULT_LATITUDE = 1.3011456;
    //private double MAP_DEFAULT_LONGITUDE = 25.0179958;    
    //private double MAP_DEFAULT_LONGITUDE = 103.7879403;
    //private double MAP_DEFAULT_LONGITUDE = 22.0927365;//Naantali
    LocationReceiver locRec;
    
    ImageButton addPlotButton;
    ImageButton finishAddingPlotButton;
 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
		setContentView(R.layout.osm_map);
	    final ProgressDialog pdOpeningMap = ProgressDialog.show(this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.openingMap));
		final Handler openingMapHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				/*RoadManager roadManager = new OSRMRoadManager();
			    ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
			    GeoPoint startPoint = new GeoPoint(1.3387225,103.7056935);
			    waypoints.add(startPoint);
			    GeoPoint endPoint = new GeoPoint(1.2983551,103.7886895);
			    waypoints.add(endPoint);
			    Road road = roadManager.getRoad(waypoints);
			    Polyline roadOverlay = RoadManager.buildRoadOverlay(road, OsmMapActivity.this);
			    mapView.getOverlays().add(roadOverlay);
			    Log.e("road",road.mDuration+"=="+road.mLength);
			    mapView.invalidate();
			    Log.e("drawing","DIRECTIONS");*/
				/*RoadManager roadManager = new OSRMRoadManager();
				RoadManager roadManager = new OSRMRoadManager();
>>>>>>> 5d360b3fffe222a940ae43e6588115bb9f604a91
			    ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
			    GeoPoint startPoint = new GeoPoint(1.3387225,103.7056935);
			    waypoints.add(startPoint);
			    GeoPoint endPoint = new GeoPoint(1.281321,103.83897);
			    waypoints.add(endPoint);
			    Road road = roadManager.getRoad(waypoints);
<<<<<<< HEAD
=======
			    /*Polyline roadOverlay = RoadManager.buildRoadOverlay(road, OsmMapActivity.this);
			    mapView.getOverlays().add(roadOverlay);*/
			    /*Log.e("road",road.mDuration+"=="+road.mLength);
			    Log.e("road",road.mLegs.size()+"=="+road.mNodes.size());
			    for (int i=0;i<road.mNodes.size()-1;i++){
			    	OsmMapActivity.this.drawLine(road.mNodes.get(i).mLocation, road.mNodes.get(i+1).mLocation, Color.BLUE);
			    }*/
			    //}
			    /*OsmMapActivity.this.drawLine(road.mNodes.get(0).mLocation, road.mNodes.get(1).mLocation, Color.RED);
			    OsmMapActivity.this.drawLine(road.mNodes.get(1).mLocation, road.mNodes.get(2).mLocation, Color.BLUE);
			    OsmMapActivity.this.drawLine(road.mNodes.get(2).mLocation, road.mNodes.get(3).mLocation, Color.BLACK);
			    OsmMapActivity.this.drawLine(road.mNodes.get(3).mLocation, road.mNodes.get(4).mLocation, Color.RED);
			    OsmMapActivity.this.drawLine(road.mNodes.get(4).mLocation, road.mNodes.get(5).mLocation, Color.BLUE);
			    OsmMapActivity.this.drawLine(road.mNodes.get(5).mLocation, road.mNodes.get(6).mLocation, Color.BLACK);
			    OsmMapActivity.this.drawLine(road.mNodes.get(6).mLocation, road.mNodes.get(7).mLocation, Color.RED);
			    OsmMapActivity.this.drawLine(road.mNodes.get(7).mLocation, road.mNodes.get(8).mLocation, Color.BLUE);
			    OsmMapActivity.this.drawLine(road.mNodes.get(8).mLocation, road.mNodes.get(9).mLocation, Color.BLACK);
			    OsmMapActivity.this.drawLine(road.mNodes.get(9).mLocation, road.mNodes.get(10).mLocation, Color.RED);
			    OsmMapActivity.this.drawLine(road.mNodes.get(10).mLocation, road.mNodes.get(11).mLocation, Color.BLUE);
			    */mapView.invalidate();

			    /*Log.e("drawing","DIRECTIONS");
			    	    
			    OsmMapActivity.this.drawLine(startPoint, endPoint, Color.GREEN);*/

			    Log.e("drawing","DIRECTIONS");
			    	    
			    //OsmMapActivity.this.drawLine(startPoint, endPoint, Color.GREEN);
			}
		};
    	Thread openingMapThread = new Thread() {
    		@Override
    		public void run() {
    			try {
    				super.run();
    				//ApplicationManager.mapActivity = CopyOfOsmMapActivity.this;
    				OsmMapActivity.this.locRec = null;
    				ApplicationManager.mapActivity = OsmMapActivity.this;
    				
    				//setting up the map
    				mapView = (MapView) findViewById(R.id.mapview);
    		        mapView.setBuiltInZoomControls(true);
    		        mapView.setMultiTouchControls(true);
    		        mapView.setClickable(true);
    		        //mapView.setBackgroundColor(Color.RED);
    		        mapView.setUseDataConnection(false);
    		        int selectedZoomLevel = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.zoomLevel), getResources().getInteger(R.integer.defaultZoomLevel));
    		        mapView.getController().setZoom(selectedZoomLevel);
    		        //mapView.getController().setCenter(new GeoPoint(MAP_DEFAULT_LATITUDE, MAP_DEFAULT_LONGITUDE));
    		        
    		        String userLocationLat = ApplicationManager.appPreferences.getString(getResources().getString(R.string.userLocationLat), getResources().getString(R.string.defaultUserLocationLat));
    		        String userLocationLon = ApplicationManager.appPreferences.getString(getResources().getString(R.string.userLocationLon), getResources().getString(R.string.defaultUserLocationLon));
    		        mapView.getController().setCenter(new GeoPoint(Double.valueOf(userLocationLat), Double.valueOf(userLocationLon)));
    		        mapView.setTileSource(TileSourceFactory.MAPNIK);
    		        mapView.setMapListener(new MapListener() {   
    		            public boolean onZoom(ZoomEvent arg0) {
    		            	SharedPreferences.Editor editor = ApplicationManager.appPreferences.edit();
    		        		int selectedZoomLevel = arg0.getZoomLevel();
    		        		editor.putInt(getResources().getString(R.string.zoomLevel), selectedZoomLevel);
    		        		editor.commit();
    		                return false;
    		            }

    		            public boolean onScroll(ScrollEvent arg0) {
    		                return false;
    		            }
    		        });
    		        //mapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
    		        /*mapView.setTileSource(new XYTileSource("MapQuest",
    		                ResourceProxy.string.mapquest_osm, 0, 18, 256, ".jpg", new String[] {
    		                    "http://otile1.mqcdn.com/tiles/1.0.0/map/",
    		                    "http://otile2.mqcdn.com/tiles/1.0.0/map/",
    		                    "http://otile3.mqcdn.com/tiles/1.0.0/map/",
    		                    "http://otile4.mqcdn.com/tiles/1.0.0/map/"}));*/
    				/*ApplicationManager.mapActivity = OsmMapActivity.this;
    				OsmMapActivity.this.locRec = null;
    				mapView = (MapView) findViewById(R.id.mapview);
    		        mapView.setBuiltInZoomControls(true);
    		        mapView.setMultiTouchControls(true);
    		        mapView.setClickable(true);
    		        //mapView.setBackgroundColor(Color.RED);
    		        mapView.setUseDataConnection(false);
    		        int selectedZoomLevel = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.zoomLevel), getResources().getInteger(R.integer.defaultZoomLevel));
    		        mapView.getController().setZoom(selectedZoomLevel);
    		        //mapView.getController().setCenter(new GeoPoint(MAP_DEFAULT_LATITUDE, MAP_DEFAULT_LONGITUDE));
    		        String userLocationLat = ApplicationManager.appPreferences.getString(getResources().getString(R.string.userLocationLat), getResources().getString(R.string.defaultUserLocationLat));
    		        String userLocationLon = ApplicationManager.appPreferences.getString(getResources().getString(R.string.userLocationLon), getResources().getString(R.string.defaultUserLocationLon));
    		        mapView.getController().setCenter(new GeoPoint(Double.valueOf(userLocationLat), Double.valueOf(userLocationLon)));
    		        Log.e("position",userLocationLat+"=="+userLocationLon);
    		        mapView.setTileSource(TileSourceFactory.MAPNIK);
    		        
    		        //RelativeLayout rl = (RelativeLayout)findViewById(R.id.rootMapView);
    		        */
    				ImageButton locateButton = (ImageButton) findViewById(R.id.locateButton);
    			    locateButton.setOnClickListener(new OnClickListener()
    			    {
    			        @Override
    			        public void onClick(View v) {
    			        	Log.e("isGpsOn","=="+ApplicationManager.isGpsOn);
    			        	if (ApplicationManager.isGpsOn){
    			        		OsmMapActivity.this.stopGPS();
    			        	} else {
    			        		OsmMapActivity.this.showCurrentLocation();	
    			        	}    			            
    			        }
    			    });
    			    locateButton.setImageResource(android.R.drawable.ic_menu_mylocation);
    			    
    			    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 50);
    			    //params.rightMargin = 10;
    			    //params.topMargin = 100;
    			    //rl.addView(locateButton, params);
    			    locateButton.setLayoutParams(params);
    			    
    			    /*ImageButton addPointButton = (ImageButton) findViewById(R.id.addPointButton);
    			    addPointButton.setOnClickListener(new OnClickListener()
    			    {
    			        @Override
    			        public void onClick(View v) {
    			            OsmMapActivity.this.startDrawingDot();
    			        }           
    			    });
    			    addPointButton.setImageResource(android.R.drawable.ic_menu_myplaces);
    			    
    			    ImageButton addLineButton = (ImageButton) findViewById(R.id.addLineButton);
    			    addLineButton.setOnClickListener(new OnClickListener()
    			    {
    			        @Override
    			        public void onClick(View v) {
    						OsmMapActivity.this.startDrawingLine();
    			        }
    			    });
    			    addLineButton.setImageResource(android.R.drawable.ic_menu_directions);
    			    
    			    addPlotButton = (ImageButton) findViewById(R.id.addPlotButton);
    			   	finishAddingPlotButton = (ImageButton) findViewById(R.id.finishAddingPlotButton);
    			    
    			    finishAddingPlotButton.setOnClickListener(new OnClickListener()
    			    {
    			        @Override
    			        public void onClick(View v) {
    			            Log.e("finishAddingPlotButton","CLICKED");
    			            OsmMapActivity.this.stopDrawingPlot();
    			            addPlotButton.setVisibility(View.VISIBLE);
    						finishAddingPlotButton.setVisibility(View.GONE);
    			        }           
    			    });
    			    finishAddingPlotButton.setImageResource(android.R.drawable.star_big_on);
    			    
    			   
    			    addPlotButton.setOnClickListener(new OnClickListener()
    			    {
    			        @Override
    			        public void onClick(View v) {
    			            Log.e("addPlotButton","CLICKED");
    						OsmMapActivity.this.startDrawingPlot();
    						addPlotButton.setVisibility(View.GONE);
    						finishAddingPlotButton.setVisibility(View.VISIBLE);
    			        }           
    			    });
    			    addPlotButton.setImageResource(android.R.drawable.star_big_off);
    			    */

    			    
    				openingMapHandler.sendEmptyMessage(0);
    			} catch (Exception e) {
    				openingMapHandler.sendEmptyMessage(1);
    				RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":run",
    	    				Environment.getExternalStorageDirectory().toString()
    	    				+getResources().getString(R.string.logs_folder)
    	    				+getResources().getString(R.string.logs_file_name)
    	    				+System.currentTimeMillis()
    	    				+getResources().getString(R.string.log_file_extension));
    			} finally {
    				pdOpeningMap.dismiss();    				
    			}
    		}
    	};
    	openingMapThread.start();  
	    
		
	}
	
	public void onResume(){
		super.onResume();
		Log.i(getResources().getString(R.string.app_name),TAG+":onResume");
	    final ProgressDialog pdOpeningMap = ProgressDialog.show(this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.openingMap));
		final Handler openingMapHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				mapView.setBackgroundDrawable(getWallpaper());
		        mapView.getOverlays().add(new MapGestureDetectorOverlay(OsmMapActivity.this, mapView));
		        int selectedZoomLevel = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.zoomLevel), getResources().getInteger(R.integer.defaultZoomLevel));
		        Log.e("onResume","selectedZoomLevel="+selectedZoomLevel);
		        mapView.getController().setZoom(selectedZoomLevel);
		        String userLocationLat = ApplicationManager.appPreferences.getString(getResources().getString(R.string.userLocationLat), getResources().getString(R.string.defaultUserLocationLat));
		        String userLocationLon = ApplicationManager.appPreferences.getString(getResources().getString(R.string.userLocationLon), getResources().getString(R.string.defaultUserLocationLon));
		        mapView.getController().setCenter(new GeoPoint(Double.valueOf(userLocationLat), Double.valueOf(userLocationLon)));
		        
		      //draw user shapes on map from previous map session
		    	int pointsNo = ApplicationManager.points.size();
				int linesNo = ApplicationManager.lineEnds.size();
				int plotsNo = ApplicationManager.plots.size();
//				Log.e("pointsNo","=="+pointsNo);
//				Log.e("linesNo","=="+linesNo);
//				Log.e("plotsNo","=="+plotsNo);
						
				if ((pointsNo+linesNo+plotsNo)>0){
					Integer recordId = ApplicationManager.currentRecord.getId();
					for (int i=0;i<pointsNo;i++){
						if (ApplicationManager.points.get(i).getLeft()==recordId){
							GeoPoint point = ApplicationManager.points.get(i).getRight();
							OsmMapActivity.this.drawPointMarker(point);	
						}				
					}
					for (int i=0;i<linesNo;i++){
						if (ApplicationManager.lineEnds.get(i).getLeft()==recordId){
							GeoPoint startPoint = ApplicationManager.lineEnds.get(i++).getRight();
							/*Log.e("startPoint",startPoint.getLatitudeE6()/1E6+"=="+startPoint.getLongitudeE6()/1E6);
							OverlayItem olItem = new OverlayItem("3", "LINE","",  startPoint);
							ArrayList<OverlayItem> overlayLineArray = new ArrayList<OverlayItem>();
							overlayLineArray.add(olItem);
							PlotMarker overlay = new PlotMarker(OsmMapActivity.this, overlayItemArray);
							mapView.getOverlays().add(overlay);
							*/
							GeoPoint endPoint = ApplicationManager.lineEnds.get(i).getRight();
							/*olItem = new OverlayItem("3", "LINE","",  endPoint);
							overlayLineArray = new ArrayList<OverlayItem>();
							overlayLineArray.add(olItem);
							overlay = new PlotMarker(OsmMapActivity.this, overlayItemArray);
							mapView.getOverlays().add(overlay);
							PathOverlay myOverlay= new PathOverlay(Color.BLUE, this);
							myOverlay.addPoint(endPoint);
							mapView.getOverlays().add(myOverlay);
							mapView.invalidate();*/
							OsmMapActivity.this.drawLine(startPoint, endPoint, Color.BLUE);
							OsmMapActivity.this.drawPointMarker(startPoint);
							OsmMapActivity.this.drawPointMarker(endPoint);	
						}				
					}
					
					for (int i=0;i<plotsNo;i++){
						if (ApplicationManager.plots.get(i).get(0).getLeft()==recordId){
							List<Pair<Integer, GeoPoint>> plotCornersWithRecordId = ApplicationManager.plots.get(i);
							List<GeoPoint> plotCorners = new ArrayList<GeoPoint>();
							for (Pair<Integer,GeoPoint> pair : plotCornersWithRecordId){
								plotCorners.add(pair.getRight());
							}
							//List<GeoPoint> plotCorners =  ApplicationManager.plots.get(i);
							int plotCornersNo = plotCorners.size();
							GeoPoint previousCorner = null;
							for (int j=0;j<plotCornersNo;j++){
								GeoPoint point = plotCorners.get(j);
								OsmMapActivity.this.drawPointMarker(point);
								if (previousCorner!=null){
									OsmMapActivity.this.drawLine(previousCorner, point, Color.RED);
								}
								previousCorner = point;
							}
							if (previousCorner!=null){
								OsmMapActivity.this.drawLine(previousCorner, plotCorners.get(0), Color.RED);
							}	
						}				
					}
					
				}
		        mapView.invalidate();
			}
		};
    	Thread openingMapThread = new Thread() {
    		@Override
    		public void run() {
    			try {
    				super.run();
    				/*mapView.getSettings().setJavaScriptEnabled(true);
    				//webView.loadUrl("http://www.maps.google.com");
    				webView.loadUrl("file://sdcard/OSMexperiments/local_tiles.htm");
    				WebSettings webSettings = webView.getSettings();
    				webSettings.setJavaScriptEnabled(true);*/

    		        //Drawable marker=getResources().getDrawable(android.R.drawable.ic_menu_myplaces);
    		        //addPolygon();
    		        //drawLine(new GeoPoint(62.6, 29.7),new GeoPoint(62.9, 29.9));
    		        //showCurrentLocation();
    		        /*mapView.setBackgroundDrawable(getWallpaper());
    		        mapView.getOverlays().add(new MapGestureDetectorOverlay(OsmMapActivity.this, mapView));*/
    				/*ArrayList<OverlayItem> overlayItemArray = new ArrayList<OverlayItem>();                
    		        OverlayItem olItem1 = new OverlayItem("2", "Title2", "Description2",  new GeoPoint(62.61,29.79));
    		        //OverlayItem olItem2 = new OverlayItem("1", "Title1", "Description1", new GeoPoint(62.62,29.8));
    		        //OverlayItem olItem3 = new OverlayItem("3", "Title3", "Description3", new GeoPoint(62.63,29.81));        
    		        overlayItemArray.add(olItem1);
    		        //overlayItemArray.add(olItem2);
    		        //overlayItemArray.add(olItem3);
    		        
    		        PlotMarker overlay = new PlotMarker(this, overlayItemArray);
    		        mapView.getOverlays().add(overlay);*/
    		        
    		        DataManager dataManager = new DataManager(OsmMapActivity.this,(CollectSurvey) ApplicationManager.getSurvey(),ApplicationManager.getSurvey().getSchema().getRootEntityDefinition(ApplicationManager.currRootEntityId).getName(),ApplicationManager.getLoggedInUser());
    		        List<CollectRecord> savedRecordsSummaries = dataManager.loadSummaries();
    		        Log.e("number of saved records", "=="+savedRecordsSummaries.size());
    		        //ArrayList<OverlayItem> overlayItemArray = new ArrayList<OverlayItem>();
    		        
    		    	for (CollectRecord savedRecordSummary : savedRecordsSummaries){
    		    		//if (savedRecordSummary.getId().equals(ApplicationManager.currentRecord.getId())){//it is currently opened plot
    		    			Log.e("currently opened","PLOT");
    		    			//searching for coordinate fields
    		    			ArrayList<OverlayItem> overlayItemArray = new ArrayList<OverlayItem>();
    		    			//opening plot based on strict structure of form
    			        	CollectRecord savedRecord = dataManager.loadRecord(savedRecordSummary.getId());
    			        	List<Node<? extends NodeDefinition>> plotsList = savedRecord.getRootEntity().getAll(getResources().getString(R.string.plotEntityField));
    			        	Log.e("plot_details_fields","=="+plotsList.size());
    			        	for (Node<? extends NodeDefinition> plot : plotsList){
    			        		Entity plotEntity = (Entity)plot;
    			        		Coordinate plotCenter = (Coordinate) plotEntity.getValue(getResources().getString(R.string.plotCoordinatesField), 0);
    			        		Log.e("plotCenter==null","=="+(plotCenter==null));
    			        		if (plotCenter!=null){
    			        			Log.e("draw",plotCenter.getX()+"=="+plotCenter.getY());
    			        			Log.e("plotEntity==null","=="+(plotEntity==null));    			        			
    				        		IntegerValue plotNo = (IntegerValue)plotEntity.getParent().getValue(getResources().getString(R.string.plotIdField), 0);
    				        		if (plotNo!=null)
    				        			Log.e("plotNo","=="+plotNo.getValue());
    				        		else 
    				        			Log.e("plotNo","==null");
    				        		Log.e("savedRecord.getId()","=="+savedRecord.getId());
    				        		if (plotNo!=null)
    				        			Log.e("plotNo.getValue()","=="+plotNo.getValue());
    				        		Log.e("plotCenter.get(Y)","=="+plotCenter.getY());
    				        		Log.e("plotCenter.get(X)","=="+plotCenter.getX());   
    				        		if (plotNo==null)
    				        			plotNo = new IntegerValue(-1,null);
    				        		OverlayItem olItem = new OverlayItem(String.valueOf(savedRecord.getId()), plotNo.getValue().toString(), "Y: "+plotCenter.getY()+"\r\nX:"+plotCenter.getX(),  new GeoPoint(plotCenter.getY(),plotCenter.getX()));
    	    				        Drawable newMarker = getResources().getDrawable(R.drawable.forest_marker);
    	    				        olItem.setMarker(newMarker);
    				        		Log.e("olItem.getUid()","=="+olItem.getUid());
    				            	overlayItemArray.add(olItem);	
    				            	Log.e("item","ADDED"+overlayItemArray.size());
    			        		}	        		
    			        	}        	
    				        PlotMarker overlay = new PlotMarker(OsmMapActivity.this, overlayItemArray);
    				        Log.e("overlaysNo1","=="+mapView.getOverlays().size());
    				        mapView.getOverlays().add(overlay);
    				        Log.e("overlaysNo2","=="+mapView.getOverlays().size());
    				        //break;
    		    		//}
    		    	}
    		        
    		        //mapView.getOverlays().add(new PlotOverlay(this));
    		        //mapView.getOverlays().add(new MapGestureDetectorOverlay(this, mapView));
    		        //drawUserMarker(new Location(""));
    		    	
    		    	
    		        //mapView.invalidate();
    				openingMapHandler.sendEmptyMessage(0);
    			} catch (Exception e) {
    				openingMapHandler.sendEmptyMessage(1);
    				RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":run",
    	    				Environment.getExternalStorageDirectory().toString()
    	    				+getResources().getString(R.string.logs_folder)
    	    				+getResources().getString(R.string.logs_file_name)
    	    				+System.currentTimeMillis()
    	    				+getResources().getString(R.string.log_file_extension));
    			} finally {
    				pdOpeningMap.dismiss();	        				
    			}
    		}
    	};
    	openingMapThread.start();  
		
	}	
	
	@Override
    protected void onDestroy() {
	    super.onDestroy();
	    Log.i(getResources().getString(R.string.app_name),TAG+":onDestroy");
	    //unbindDrawables(findViewById(R.id.rootMapView));
	    System.gc();
    }
	
	@Override
    protected void onPause() {
	    super.onPause();
	    Log.i(getResources().getString(R.string.app_name),TAG+":onPause");
	    //unbindDrawables(findViewById(R.id.rootMapView));
	    System.gc();
	    SharedPreferences.Editor editor = ApplicationManager.appPreferences.edit();
		int selectedZoomLevel = mapView.getZoomLevel();
		editor.putInt(getResources().getString(R.string.zoomLevel), selectedZoomLevel);
		editor.putString(this.getResources().getString(R.string.userLocationLat), String.valueOf(mapView.getMapCenter().getLatitudeE6()/1E6));
		editor.putString(this.getResources().getString(R.string.userLocationLon), String.valueOf(mapView.getMapCenter().getLongitudeE6()/1E6));
		editor.commit();
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
        view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
            	unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }
	
	public void showCurrentLocation(){
		Log.e("showCurrentLocation","===============================");
		this.turnGPSOn();
		locRec = new LocationReceiver((LocationManager) getSystemService(Context.LOCATION_SERVICE));
		locRec.getCurrentLocation();		
	}

	public void drawPointMarker(GeoPoint point){
		ArrayList<OverlayItem> overlayItemArray = new ArrayList<OverlayItem>();
		//GeoPoint userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
		OverlayItem olItem = new OverlayItem("Current location", "", point);
    	overlayItemArray.add(olItem);
    	mapView.getOverlays().add(new UserMarker(this, overlayItemArray, point.getLatitudeE6()/1E6, point.getLongitudeE6()/1E6));
		//mapView.getOverlays().add(new UserMarker(marker, location.getLatitude(), location.getLongitude(), resourceProxy));
		//mapView.getOverlays().add(new UserMarker(marker, location.getLongitude(), location.getLatitude(), resourceProxy));
		mapView.invalidate();
	}
	
	public void drawUserMarker(Location location){
		Log.e("drawUserMarker","================");
		//location.setLatitude(60.2677);
		//location.setLongitude(25.02);		
		
		Drawable marker=getResources().getDrawable(android.R.drawable.ic_menu_mylocation);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
		//ResourceProxy resourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
		List<Overlay> overlays = mapView.getOverlays();
		for (int i=0;i<overlays.size();i++){
			if (overlays.get(i) instanceof UserMarker){
				this.removeOverlay(overlays.get(i));
				break;
			}
		}
		Log.e("marker drawn",location.getLatitude()+"=="+location.getLongitude());
		ArrayList<OverlayItem> overlayItemArray = new ArrayList<OverlayItem>();
		GeoPoint userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
		OverlayItem olItem = new OverlayItem("Current location", "Y: "+location.getLatitude()+"\r\nX:"+location.getLongitude(), userLocation);
    	overlayItemArray.add(olItem);
    	
    	mapView.getOverlays().add(new UserMarker(this, overlayItemArray, location.getLatitude(), location.getLongitude()));
		//mapView.getOverlays().add(new UserMarker(marker, location.getLatitude(), location.getLongitude(), resourceProxy));
		//mapView.getOverlays().add(new UserMarker(marker, location.getLongitude(), location.getLatitude(), resourceProxy));
		mapView.getController().setCenter(new GeoPoint(location.getLatitude(), location.getLongitude()));
		mapView.invalidate();
	}
	
	public PathOverlay drawLine(GeoPoint pt1, GeoPoint pt2, int color){
		PathOverlay myOverlay= new PathOverlay(color, this);
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
	
	public void navigateToPlot(GeoPoint plotLocation){
		Log.e("navigation","started"+plotLocation.getLatitudeE6()+"=="+plotLocation.getLongitudeE6());
		this.turnGPSOn();
		ApplicationManager.isGpsOn = true;
		locRec = new LocationReceiver((LocationManager) getSystemService(Context.LOCATION_SERVICE));
		locRec.startNavigating(plotLocation);
		//mapView.getOverlays().remove(mapView.getOverlays().size()-1);
		
	}
	
	public void stopNavigationToPlot(){
		Log.e("navigation","===STOPPED");
		if (locRec!=null)
			locRec.stopListeningForLocationUpdates();
		this.turnGPSOff();
	    mapView.getOverlays().remove(mapView.getOverlays().size()-1);
	    mapView.invalidate();
	    ApplicationManager.isGpsOn = false;
	}
	
	public void stopGPS(){
		if (locRec!=null)
			locRec.stopListeningForLocationUpdates();
		this.turnGPSOff();
	}
	
	public void openPlotData(int recordId, int plotId){
		Log.e("PLOT",recordId+"IS BEING OPENED"+plotId);
		CollectSurvey collectSurvey = (CollectSurvey)ApplicationManager.getSurvey();
		ApplicationManager.dataManager = new DataManager(this,collectSurvey,collectSurvey.getSchema().getRootEntityDefinitions().get(0).getName(),ApplicationManager.getLoggedInUser());
    	ApplicationManager.currentRecord = ApplicationManager.dataManager.loadRecord(recordId);
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
		intent.putExtra(getResources().getString(R.string.plotId), plotId);
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
	            addPlotButton.setVisibility(View.GONE);
				finishAddingPlotButton.setVisibility(View.VISIBLE);
			    return true;
			case R.id.menu_map_plot_drawing_ends:
				this.stopDrawingPlot();
	            addPlotButton.setVisibility(View.VISIBLE);
				finishAddingPlotButton.setVisibility(View.GONE);
			    return true;
			case R.id.menu_map_add_point:
				this.startDrawingDot();
			    return true;			
			case R.id.menu_map_plot_add_line:
				this.startDrawingLine();
			    return true;			    
			case R.id.menu_map_refresh_location:
				Log.e("isGpsOn","=="+ApplicationManager.isGpsOn);
				if (ApplicationManager.isGpsOn){
					this.stopGPS();
				} else {
					this.showCurrentLocation();	
				}				
			    return true;
			case R.id.menu_map_save_to_kml:
				int savingResult = this.saveShapesToKML();
				AlertMessage.createPositiveDialog(OsmMapActivity.this, false, getResources().getDrawable(R.drawable.warningsign),
	 					getResources().getString(R.string.map_save_to_kml_result_title), (savingResult==0)?getResources().getString(R.string.map_save_to_kml_result_message_successfull):getResources().getString(R.string.map_save_to_kml_result_message_unsuccessfull),
	 					getResources().getString(R.string.okay),
	 		    		new DialogInterface.OnClickListener() {
	 						@Override
	 						public void onClick(DialogInterface dialog, int which) {
	 							
	 						}
	 					},
	 					null).show();
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
	    ApplicationManager.isGpsOn = true;
	}

	public void turnGPSOff()
	{
	    String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	    Log.e("provider.contains(gps)","=="+provider.contains("gps"));
	    if(provider.contains("gps")){ //if gps is enabled
	    	Log.e("GPS","turning off");
	        final Intent poke = new Intent();
	        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
	        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	        poke.setData(Uri.parse("3")); 
	        this.sendBroadcast(poke);
	    }
	    ApplicationManager.isGpsOn = false;
	}
	
	private void turnGPSOn1(){
	    String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

	    if(!provider.contains("gps")){ //if gps is disabled
	        final Intent poke = new Intent();
	        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
	        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	        poke.setData(Uri.parse("3")); 
	        sendBroadcast(poke);
	    }
	    ApplicationManager.isGpsOn = true;
	}
	
	public void startDrawingPlot(){
		ApplicationManager.isPlotDrawingStarted = true;
		ApplicationManager.isDotDrawingStarted = false;
		ApplicationManager.isLineDrawingStarted = false;
		ApplicationManager.plots.add(new ArrayList<Pair<Integer,GeoPoint>>());
		Log.e("drawing","STARTED");
	}
	
	public void stopDrawingPlot(){
		ApplicationManager.isPlotDrawingStarted = false;
		List<Pair<Integer, GeoPoint>> plotCornersWithRecordId = ApplicationManager.plots.get(ApplicationManager.plots.size()-1);
		if (plotCornersWithRecordId.size()>2){
			List<GeoPoint> plotCorners = new ArrayList<GeoPoint>();
			for (Pair<Integer,GeoPoint> pair : plotCornersWithRecordId){
				plotCorners.add(pair.getRight());
			}
			//List<GeoPoint> plotCorners = ApplicationManager.plots.get(ApplicationManager.plots.size()-1);		
			this.drawLine(plotCorners.get(0), plotCorners.get(plotCorners.size()-1), Color.RED);
			//int savingResult = savePlotShapeToFile(MapGestureDetectorOverlay.plotCorners);
			//MapGestureDetectorOverlay.plotCorners.clear();
		} else {
			
		}
		Log.e("drawing","FINISHED");
	}
	
	public void startDrawingLine(){
		ApplicationManager.isLineDrawingStarted = true;
		ApplicationManager.isPlotDrawingStarted = false;
		ApplicationManager.isDotDrawingStarted = false;
		Log.e("drawing LINE","STARTED");
	}
	
	public static void stopDrawingLine(){
		ApplicationManager.isLineDrawingStarted = false;
		Log.e("drawing LINE","FINISHED");
	}

	public void startDrawingDot(){
		ApplicationManager.isDotDrawingStarted = true;
		ApplicationManager.isPlotDrawingStarted = false;
		ApplicationManager.isLineDrawingStarted = false;
		Log.e("drawing DOT","STARTED");
	}
	
	public static void stopDrawingDot(){
		ApplicationManager.isDotDrawingStarted = false;
		Log.e("drawing DOT","FINISHED");
	}	
	
	private int saveShapesToKML(){
		Log.e("saving shapes","to KML file");
		/*
	 	<kml xmlns="http://earth.google.com/kml/2.0">
		��<Placemark>
		���<Point>
		�����<coordinates>-151.752044,-16.443118</coordinates>
		���</Point>
		��</Placemark>
		</kml>
		 */
		int pointsNo = ApplicationManager.points.size();
		int linesNo = ApplicationManager.lineEnds.size();
		int plotsNo = ApplicationManager.plots.size();
		
		
		if ((pointsNo+linesNo+plotsNo)>0){
			try{
				File file = new File(Environment.getExternalStorageDirectory().toString()
						+getResources().getString(R.string.plotBoundariesSavingPath)
						+getResources().getString(R.string.plotBoundariesSavingBaseFileName)
						+ApplicationManager.currentRecord.getId()
						+System.currentTimeMillis()
						+getResources().getString(R.string.plotBoundariesSavingFileExtension));
				if (!file.exists()) {
					file.createNewFile();
				}
	 
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				
				bw.write("<kml>\r\n");
				
				int recordId = ApplicationManager.currentRecord.getId();
				for (int i=0;i<pointsNo;i++){
					if (recordId==ApplicationManager.points.get(i).getLeft()){
						bw.write("\t<placemark>\r\n");
						GeoPoint point = ApplicationManager.points.get(i).getRight();
						bw.write("\t\t<point>\r\n\t\t\t<coordinates>"+(point.getLatitudeE6()/1E6)+","+(point.getLongitudeE6()/1E6)+"</coordinates>\r\n\t\t</point>\r\n");
						bw.write("\t</placemark>\r\n");	
					}
				}
				for (int i=0;i<linesNo;i++){
					if (recordId==ApplicationManager.lineEnds.get(i).getLeft()){
						bw.write("\t<placemark>\r\n");
						GeoPoint point = ApplicationManager.lineEnds.get(i++).getRight();
						bw.write("\t\t<point>\r\n\t\t\t<coordinates>"+(point.getLatitudeE6()/1E6)+","+(point.getLongitudeE6()/1E6)+"</coordinates>\r\n\t\t</point>\r\n");
						point = ApplicationManager.lineEnds.get(i).getRight();
						bw.write("\t\t<point>\r\n\t\t\t<coordinates>"+(point.getLatitudeE6()/1E6)+","+(point.getLongitudeE6()/1E6)+"</coordinates>\r\n\t\t</point>\r\n");
						bw.write("\t</placemark>\r\n");	
					}
				}
				for (int i=0;i<plotsNo;i++){
					if (recordId==ApplicationManager.plots.get(i).get(0).getLeft()){
						bw.write("\t<placemark>\r\n");
						List<Pair<Integer, GeoPoint>> plotCornersWithRecordId = ApplicationManager.plots.get(i);
						List<GeoPoint> plotCorners = new ArrayList<GeoPoint>();
						for (Pair<Integer,GeoPoint> pair : plotCornersWithRecordId){
							plotCorners.add(pair.getRight());
						}
						//List<GeoPoint> plotCorners = ApplicationManager.plots.get(i);
						int plotCornersNo = plotCorners.size();
						for (int j=0;j<plotCornersNo;j++){
							GeoPoint point = plotCorners.get(j);
							bw.write("\t\t<point>\r\n\t\t\t<coordinates>"+(point.getLatitudeE6()/1E6)+","+(point.getLongitudeE6()/1E6)+"</coordinates>\r\n\t\t</point>\r\n");
						}
						bw.write("\t</placemark>\r\n");
					}					
				}
				
				bw.write("</kml>");
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
			return -1;//no shapes on the map so nothing to save
		}
		
		return 0;
		/*if (plotCorners.size()>2){
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
		return 0;*/
	}
}