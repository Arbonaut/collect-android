package org.openforis.collect.android.gps;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

public class GpsActivity extends Activity {
	
	private static final String TAG = "GpsActivity";

	private LocationManager lm;
	private LocationListener ll;
	public Location loc;
	private Dialog dialog;
	private int waitingTime;//how long to wait for new GPS coords
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
		setContentView(R.layout.welcomescreen);
		this.waitingTime = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.gpsTimeout), getResources().getInteger(R.integer.gpsTimeoutInMs));
		loc = null;
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if ( !lm.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
			buildAlertMessageNoGps();
		}
		else
		{
			ll = new MyLocationListener();
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, ll);
			// Create a Runnable which is used to determine when the new GPS coords were received	
			Runnable showWaitDialog = new Runnable() {

				@Override
				public void run() {
					while ((loc == null) && (SystemClock.currentThreadTimeMillis()<waitingTime)) {
						// Wait for first GPS coords change (do nothing until loc != null)
					}				
					if (loc!=null){
						Intent resultHolder = new Intent();				
						resultHolder.putExtra(getResources().getString(R.string.latitude), String.valueOf(loc.getLatitude()));
						resultHolder.putExtra(getResources().getString(R.string.longitude), String.valueOf(loc.getLongitude()));
						setResult(getResources().getInteger(R.integer.internalGpsLocationReceived),resultHolder);
					}
						
					// After receiving first GPS coordinates dismiss the Progress Dialog
					dialog.dismiss();
					lm.removeUpdates(ll);
					// 	and destroy activity which requests location updates					
					finish();
				}
			};

			// 	Create a dialog to let the user know that we're waiting for a GPS coordinates
			dialog = ProgressDialog.show(GpsActivity.this, "Please wait...",
					"Retrieving GPS data...", true);
			Thread t = new Thread(showWaitDialog);
			t.start();
		}
	}
	
	@Override
	public void onResume()
	{
		super.onResume();   
		Log.i(TAG,"gpsActivity:onResume");
	}	
	
	  private void buildAlertMessageNoGps() {
		    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
		           .setCancelable(false)
		           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		               public void onClick(final DialogInterface dialog,  final int id) {
		                   launchGPS(); 
		               }
		           })
		           .setNegativeButton("No", new DialogInterface.OnClickListener() {
		               public void onClick(final DialogInterface dialog, final int id) {
		                    dialog.cancel();
		    				finish();
		               }
		           });
		    final AlertDialog alert = builder.create();
		    alert.show();
		}

	  private void launchGPS(){
		  Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		  startActivityForResult(myIntent, 1);
	  }
	  
	    protected void onActivityResult(int requestCode, int resultCode, Intent data){
	        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	        if(provider != null){
	            //Start searching for location and update the location text when update available.
	        	lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	        	if ( !lm.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {				
	        		finish();
	        	}
	        	else
	        	{
	        		ll = new MyLocationListener();
	        			
	        		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
	        		// Create a Runnable which is used to determine when the new GPS coords were received	
	        		Runnable showWaitDialog = new Runnable() {

	        			@Override
	        			public void run() {
	        				while ((loc == null) && (SystemClock.currentThreadTimeMillis()<waitingTime)) {
	        					// Wait for first GPS coords change (do nothing until loc != null)
	        				}
	        				// After receiving first GPS coordinates dismiss the Progress Dialog
	        				dialog.dismiss();
	        				stopListeningGpsUpdates();
	        				// 	and destroy activity which requests location updates	        				
	        				finish();
	        			}
	        		};

	        		dialog = ProgressDialog.show(GpsActivity.this, getResources().getString(R.string.retrievingGpsTitle),
	        				getResources().getString(R.string.retrievingGpsMessage), true);
	        		Thread t = new Thread(showWaitDialog);
	        		t.start();
	        	}
	        }
	    }
	
	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				loc = location;
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
	
	public void stopListeningGpsUpdates(){
        if(lm != null){
        	lm.removeUpdates(ll);
        }
    }
	
	public String[] convert(double lat, double lng){ // konverter fra Lat /Long til UTM (WGS84 / NAD84)
		final double knu = 0.9996;  // scale along central meridian of zone
		final double a = 6378137;   //Equatorial radius in meters 
		final double b = 6356752.3142; // Polar radius in meters
	 
		double lngdeg = lng;  // Latitude in "normal" form. dd.dd
		lat = Math.toRadians(lat); // Latitude in radians
		lng = Math.toRadians(lng); // Longitude in radians		
		
		double e = Math.sqrt(1-(b*b)/(a*a)); // e = the eccenticity of the earth's elliptical cross-section 
		double e2 = e*e/(1-(e*e)); // The quantile e' only occurs in even powers
		double n =(a-b)/(a+b);
		int zone = (int) (31 + (lngdeg/6)); // Calculating UTM zone using Longetude in dd.dd form as supplied by the GPS
		double pi = 6* zone -183; // Central meridian of zone
		double pii = (lngdeg-pi)*Math.PI/180; //Differance between Longitude and central meridian of zone
		//double rho = a * (1-e*e)/ Math.pow((1-(e*e) * (Math.sin(lat)*( Math.sin(lat)))), (3/2)); // The radius of the curvature of the earth in meridian plane
		double nu = a/(Math.pow((1-(e*e *(Math.sin(lat))*(Math.sin(lat)))), (1/2))); //The radius of the curvature of the earth perpendicular to the meridian plane
		
		//* A0 - E0 is used for calclulating the Meridinol arc through the given point (lat long) 
		//* The distance from the earth's surface form the equator. All angles are in radians
		
		double A0 =  a*(1 - n + (5/4)*(Math.pow(n,2) - Math.pow(n,3)) + (81/64)*(Math.pow(n,4) - Math.pow(n,5)));
		double B0 =(3*a*n/2)*(1 - n - (7*n*n/8)*(1-n) + (55/64)*(Math.pow(n, 4)-Math.pow(n, 5)));
		double C0 =(15*a*n*n/16)*(1 - n +(3*n*n/4)*(1-n));
		double D0 =(35*a*Math.pow(n, 3)/48)*(1 - n + 11*n*n/16);
		double E0 =(315*a*Math.pow(n,4)/51)*(1-n);
		// Calculation of the Meridional Arc
		double S  = A0* lat - B0 * Math.sin(2*lat) + C0 * Math.sin(4*lat) - D0 * Math.sin(6*lat) + E0 * Math.sin(8*lat);
		
		double Ki = S * knu;
		double Kii = knu * nu *Math.sin(lat)*Math.cos(lat)/2;
		double Kiii = (knu * nu *Math.sin(lat)*Math.pow(Math.cos(lat),3)/24)*(5-Math.pow(Math.tan(lat),2)+9*Math.pow(e2,2)*Math.pow(Math.cos(lat),2)+4*Math.pow(e2 ,2)*Math.pow(Math.cos(lat),4));
		
		double Kiv = knu * nu *Math.cos(lat);
		double Kv = knu * Math.pow(Math.cos(lat),3)*(nu/6)*(1-Math.pow(Math.tan(lat),2)+e2*Math.pow(Math.cos(lat),2));
		  
		double UTMni = (Ki+Kii*Math.pow(pii, 2)+ Kiii * Math.pow(pii,4));// Northing
		double UTMei = 500000 + (Kiv*pii + Kv * Math.pow(pii, 3));  //Easting is relative to the central meridain. Forconvetional UTM Easting add 5000000 meters to x
		int UTMn = (int) UTMni; // Northing, rounded to closest integer
		int UTMe = (int) UTMei; // Easting, rounded to closest integer 
		
		String[] utmLocation = new String[]{String.valueOf(UTMn),String.valueOf(UTMe),String.valueOf(zone)};
		return utmLocation;
}
	
	/*public void onLocationChanged(Location location) {
  
 lat = Math.toRadians(location.getLatitude()); // Latitude in radians
 lng = Math.toRadians(location.getLongitude()); // Longitude in radians
 lngdeg = location.getLongitude();  // Latitude in "normal" form. dd.dd
convert(); 
 
   pos.setText(zone + "\t"+ UTMn +"\n \t\t\t" +UTMe );
   
   
  
 }

 public void convert(){ // konverter fra Lat /Long til UTM (WGS84 / NAD84)
 public final double knu = 0.9996;  // scale along central meridian of zone
 public final double a = 6378137;   //Equatorial radius in meters 
 public final double b = 6356752.3142; // Polar radius in meters
 
e = Math.sqrt(1-(b*b)/(a*a)); // e = the eccenticity of the earth's elliptical cross-section 
e2 = e*e/(1-(e*e)); // The quantile e' only occurs in even powers
n =(a-b)/(a+b);
zone = (int) (31 + (lngdeg/6)); // Calculating UTM zone using Longetude in dd.dd form as supplied by the GPS
double pi = 6* zone -183; // Central meridian of zone
double pii = (lngdeg-pi)*Math.PI/180; //Differance between Longitude and central meridian of zone
rho = a * (1-e*e)/ Math.pow((1-(e*e) * (Math.sin(lat)*( Math.sin(lat)))), (3/2)); // The radius of the curvature of the earth in meridian plane
nu = a/(Math.pow((1-(e*e *(Math.sin(lat))*(Math.sin(lat)))), (1/2))); //The radius of the curvature of the earth perpendicular to the meridian plane

* A0 - E0 is used for calclulating the Meridinol arc through the given point (lat long) 
* The distance from the earth's surface form the equator. All angles are in radians

A0 =  a*(1 - n + (5/4)*(Math.pow(n,2) - Math.pow(n,3)) + (81/64)*(Math.pow(n,4) - Math.pow(n,5)));
 	B0 =(3*a*n/2)*(1 - n - (7*n*n/8)*(1-n) + (55/64)*(Math.pow(n, 4)-Math.pow(n, 5)));
C0 =(15*a*n*n/16)*(1 - n +(3*n*n/4)*(1-n));
D0 =(35*a*Math.pow(n, 3)/48)*(1 - n + 11*n*n/16);
E0 =(315*a*Math.pow(n,4)/51)*(1-n);
// Calculation of the Meridional Arc
S  = A0* lat - B0 * Math.sin(2*lat) + C0 * Math.sin(4*lat) - D0 * Math.sin(6*lat) + E0 * Math.sin(8*lat);

double Ki = S * knu;
double Kii = knu * nu *Math.sin(lat)*Math.cos(lat)/2;
double Kiii = (knu * nu *Math.sin(lat)*Math.pow(Math.cos(lat),3)/24)*(5-Math.pow(Math.tan(lat),2)+9*Math.pow(e2,2)*Math.pow(Math.cos(lat),2)+4*Math.pow(e2 ,2)*Math.pow(Math.cos(lat),4));

double Kiv = knu * nu *Math.cos(lat);
double Kv = knu * Math.pow(Math.cos(lat),3)*(nu/6)*(1-Math.pow(Math.tan(lat),2)+e2*Math.pow(Math.cos(lat),2));
  
double UTMni = (Ki+Kii*Math.pow(pii, 2)+ Kiii * Math.pow(pii,4));// Northing
double UTMei = 500000 + (Kiv*pii + Kv * Math.pow(pii, 3));  //Easting is relative to the central meridain. Forconvetional UTM Easting add 5000000 meters to x
UTMn = (int) UTMni; // Northing, rounded to closest integer
UTMe = (int) UTMei; // Easting, rounded to closest integer 
 }*/
}