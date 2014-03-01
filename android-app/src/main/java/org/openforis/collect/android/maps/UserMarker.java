package org.openforis.collect.android.maps;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.ResourceProxy;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

class UserMarker extends ItemizedOverlay<OverlayItem>{    	 

	private List<OverlayItem> locations = new ArrayList<OverlayItem>();
	//private Drawable marker;

	public UserMarker(Drawable defaultMarker, int LatitudeE6, int LongitudeE6, ResourceProxy pResourceProxy) {
		super(defaultMarker,pResourceProxy);
		//this.marker=defaultMarker;
		GeoPoint myPlace = new GeoPoint(LatitudeE6,LongitudeE6);
		locations.add(new OverlayItem("Current location", "Current location", myPlace));
		populate();
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return locations.get(i);
	}
	
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return locations.size();
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// TODO Auto-generated method stub
		super.draw(canvas, mapView, shadow);
	}

	@Override
	public boolean onSnapToItem(int arg0, int arg1, Point arg2, MapView arg3) {
		// TODO Auto-generated method stub
		return false;
	}
}