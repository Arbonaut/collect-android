package org.openforis.collect.android.maps;

import org.osmdroid.util.GeoPoint;

import android.location.Location;
import android.location.LocationManager;

public class LocationReceiver {
	
	private LocationManager lm;
	private LocationChangeListener ll;
	private Location loc;
	
	public LocationReceiver(LocationManager lm){
		this.loc = null;
		this.lm = lm;		
	}
	
	public void startTracking(){
		ll = new LocationChangeListener("");
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, ll);
	}
	
	public void stopTracking(){
		lm.removeUpdates(ll);
	}
	
	public void startNavigating(GeoPoint destination){
		ll = new LocationChangeListener(destination);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, ll);
	}
	
	public void stopListeningForLocationUpdates(){
		lm.removeUpdates(ll);
	}
	
	public void getCurrentLocation(){
		ll = new LocationChangeListener("currentLocation");
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, ll);
	}
	
	/*private void buildAlertMessageNoGps() {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(ApplicationManager.mapActivity);
	    builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
	           .setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(final DialogInterface dialog,  final int id) {
	                   ApplicationManager.mapActivity.launchGPS(); 
	               }
	           })
	           .setNegativeButton("No", new DialogInterface.OnClickListener() {
	               public void onClick(final DialogInterface dialog, final int id) {
	                    dialog.cancel();
	               }
	           });
	    final AlertDialog alert = builder.create();
	    alert.show();
	}*/

}
