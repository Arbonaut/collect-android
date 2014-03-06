package org.openforis.collect.android.maps;

import org.openforis.collect.android.management.ApplicationManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.PathOverlay;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class LocationChangeListener implements LocationListener {

	private GeoPoint destination;
	private final double distanceThreshold = 10; 
	
	private PathOverlay latestDirection;

	public LocationChangeListener(){
		this.destination = null;
		this.latestDirection = null;		
	}
	
	public LocationChangeListener(GeoPoint destination){
		this.destination = destination;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			Log.e("newLocation",location.getLongitude()+"==="+location.getLatitude());
			if (this.destination!=null){
				Log.e("user->plot distance","=="+ApplicationManager.mapActivity.haversine(location.getLatitude(), location.getLongitude(), destination.getLatitudeE6()/1000000, destination.getLongitudeE6()/1000000));
				if (ApplicationManager.mapActivity.haversine(location.getLatitude(), location.getLongitude(), destination.getLatitudeE6()/1000000, destination.getLongitudeE6()/1000000)>distanceThreshold){				    
				    ApplicationManager.mapActivity.removeOverlay(this.latestDirection);
					this.latestDirection = ApplicationManager.mapActivity.drawLine(new GeoPoint(location),this.destination);	
				} else {
					ApplicationManager.mapActivity.stopNavigationToPlot();
				}
			}
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	

}