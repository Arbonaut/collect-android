package org.openforis.collect.android.maps;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.management.ApplicationManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

class UserMarker extends ItemizedIconOverlay<OverlayItem>{ 
	protected Context ctx;

	private List<OverlayItem> locations = new ArrayList<OverlayItem>();
	//private Drawable marker;

	public UserMarker(final Context context, final List<OverlayItem> aList/*Drawable defaultMarker, double latitude, double longitude, ResourceProxy pResourceProxy*/, double latitude, double longitude) {
		//super(defaultMarker,pResourceProxy);
		super(context, aList, new OnItemGestureListener<OverlayItem>() {
            @Override public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                return false;
        }
        @Override public boolean onItemLongPress(final int index, final OverlayItem item) {
                return false;
        }
      } );
		//this.marker=defaultMarker;
		//GeoPoint userLocation = new GeoPoint(latitude, longitude);
		//locations.add(new OverlayItem("Current location", "Current location", userLocation));
		//populate();
		this.ctx = context;
	}
	
	@Override
    protected boolean onSingleTapUpHelper(final int index, final OverlayItem item, final MapView mapView) {
        //Toast.makeText(mContext, "Item " + index + " has been tapped!", Toast.LENGTH_SHORT).show();
        /*AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle(item.getTitle());
        dialog.setMessage(item.getSnippet()+item.getUid());
        dialog.show();*/
        //final String uid = item.getUid();
        //Log.e("UID","=="+uid);
        //if (uid=="111"){
        //String title = item.getTitle();
        String description = item.getSnippet();
           AlertDialog.Builder builder = new AlertDialog.Builder(this.ctx);
           builder.setTitle("USER POSITION");
           builder.setMessage(description); 
           builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    
               }
           });
            builder.show();
        //}
        return true;
    }
}