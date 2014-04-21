package org.openforis.collect.android.maps;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.management.ApplicationManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

/**
 * 
 * @author K. Waga
 *
 */
public class MapGestureDetectorOverlay extends Overlay implements OnGestureListener {
	 private GestureDetector gestureDetector;
	 private OnGestureListener onGestureListener;
	 private MapView mapView;
	 private Context context;
	 private double lat;
	 private double lng;
	 public static List<GeoPoint> plotCorners;

	public MapGestureDetectorOverlay(Context ctx, MapView mapView) {
		super(ctx);
		gestureDetector = new GestureDetector(this);
		this.mapView = mapView;
		this.context = ctx;
		this.plotCorners = new ArrayList<GeoPoint>();
	}

	 public MapGestureDetectorOverlay(Context ctx, OnGestureListener onGestureListener, MapView mapView) {
	  this(ctx, mapView);
	  setOnGestureListener(onGestureListener);
	 }

	 @Override
	 public boolean onTouchEvent(MotionEvent event, MapView mapView) {
	  if (gestureDetector.onTouchEvent(event)) {
	   return true;
	  }
	  return false;
	 }

	 @Override
	 public boolean onDown(MotionEvent e) {
	  if (onGestureListener != null) {
	   return onGestureListener.onDown(e);
	  }
	  return false;
	 }

	 @Override
	 public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
	   float velocityY) {
	  if (onGestureListener != null) {
	   return onGestureListener.onFling(e1, e2, velocityX, velocityY);
	  }
	  return false;
	 }

	 @Override
	 public void onLongPress(MotionEvent e) {
		if (onGestureListener != null) {
		 onGestureListener.onLongPress(e);
		}
		Log.e("MapGestureDetectorOverlay","onLongPress");
		if (ApplicationManager.isPlotDrawingStarted){
		    int clickX = (int)e.getX();
		    int clickY = (int)e.getY();

		    Projection projection = mapView.getProjection();
		    GeoPoint tappedGeoPoint = (GeoPoint) projection.fromPixels(clickX, clickY);
		    this.lat = microDegreesToDegrees(tappedGeoPoint.getLatitudeE6());
		    this.lng = microDegreesToDegrees(tappedGeoPoint.getLongitudeE6());
		    
		    Log.e("PLOTOVERLAY","lat"+microDegreesToDegrees(tappedGeoPoint.getLatitudeE6())+"=="+lat);
		    Log.e("PLOTOVERLAY","lng"+microDegreesToDegrees(tappedGeoPoint.getLongitudeE6())+"=="+lng);
		    AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
	        builder.setTitle("MENU (add plot corner)");
	        builder.setNegativeButton("add corner here", new DialogInterface.OnClickListener() {
	             public void onClick(DialogInterface dialog, int id) {
	                 Log.e("drawing corner marker",lat+"=="+lng);
	         		OverlayItem olItem = new OverlayItem("1", "CORNER",lat+","+lng,  new GeoPoint(lat,lng));
	         		ArrayList<OverlayItem> overlayItemArray = new ArrayList<OverlayItem>();
	            	overlayItemArray.add(olItem);
	                 PlotMarker overlay = new PlotMarker(MapGestureDetectorOverlay.this.context, overlayItemArray);
	                 mapView.getOverlays().add(overlay);
	                 
	                 GeoPoint currentPlotCorner = new GeoPoint(lat,lng);
	                 if (MapGestureDetectorOverlay.this.plotCorners.size()>0){
	                	 PathOverlay myOverlay= new PathOverlay(Color.RED, MapGestureDetectorOverlay.this.context);
	 	        	    //myOverlay.getPaint().setStyle(Paint.Style.FILL);
	                	 
	 	        	    myOverlay.addPoint(currentPlotCorner);
	 	        	    myOverlay.addPoint(MapGestureDetectorOverlay.this.plotCorners.get(MapGestureDetectorOverlay.this.plotCorners.size()-1));
	 	        	    mapView.getOverlays().add(myOverlay);
	 	                 	 
	                 }
	                 mapView.invalidate();
	                 MapGestureDetectorOverlay.this.plotCorners.add(currentPlotCorner);
	            }
	        });
	        builder.show();
		}
	 }

	 @Override
	 public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
	   float distanceY) {
	  if (onGestureListener != null) {
	   onGestureListener.onScroll(e1, e2, distanceX, distanceY);
	  }
	  return false;
	 }

	 @Override
	 public void onShowPress(MotionEvent e) {
	  if (onGestureListener != null) {
	   onGestureListener.onShowPress(e);
	  }
	 }

	 @Override
	 public boolean onSingleTapUp(MotionEvent e, final MapView mapView) {

	  Log.e("MapGestureDetectorOverlay","onSingleTapUp1");
	  if (onGestureListener != null) {
		   onGestureListener.onSingleTapUp(e);
		  }
	    Log.e("PLOTOVERLAY","onSingleTapConfirmed");
	    int clickX = (int)e.getX();
	    int clickY = (int)e.getY();

	    Projection projection = mapView.getProjection();
	    GeoPoint tappedGeoPoint = (GeoPoint) projection.fromPixels(clickX, clickY);
	    double lat = microDegreesToDegrees(tappedGeoPoint.getLatitudeE6());
	    double lng = microDegreesToDegrees(tappedGeoPoint.getLongitudeE6());
	    Log.e("PLOTOVERLAY","lat"+microDegreesToDegrees(tappedGeoPoint.getLatitudeE6())+"=="+lat);
	    Log.e("PLOTOVERLAY","lng"+microDegreesToDegrees(tappedGeoPoint.getLongitudeE6())+"=="+lng);
	  return false;
	 }
	 
	 private static double microDegreesToDegrees(int microDegrees) {
		    return microDegrees / 1E6;
		}

	 public boolean isLongpressEnabled() {
	  return gestureDetector.isLongpressEnabled();
	 }

	 public void setIsLongpressEnabled(boolean isLongpressEnabled) {
	  gestureDetector.setIsLongpressEnabled(isLongpressEnabled);
	 }

	 public OnGestureListener getOnGestureListener() {
	  return onGestureListener;
	 }

	 public void setOnGestureListener(OnGestureListener onGestureListener) {
	  this.onGestureListener = onGestureListener;
	 }

	@Override
	protected void draw(Canvas arg0, MapView arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
}