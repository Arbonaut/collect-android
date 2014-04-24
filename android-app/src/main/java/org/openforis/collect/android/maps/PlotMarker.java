package org.openforis.collect.android.maps;

import java.util.List;

import org.openforis.collect.android.management.ApplicationManager;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * 
 * @author K. Waga
 *
 */
public class PlotMarker extends ItemizedIconOverlay<OverlayItem> {
    protected Context mContext;

    public PlotMarker(final Context context, final List<OverlayItem> aList) {
         super(context, aList, new OnItemGestureListener<OverlayItem>() {
                @Override public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        return false;
                }
                @Override public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                }
              } );
         mContext = context;
    }

    @Override
    protected boolean onSingleTapUpHelper(final int index, final OverlayItem item, final MapView mapView) {
        //Toast.makeText(mContext, "Item " + index + " has been tapped!", Toast.LENGTH_SHORT).show();
        /*AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle(item.getTitle());
        dialog.setMessage(item.getSnippet()+item.getUid());
        dialog.show();*/
        final String uid = item.getUid();
        //Log.e("UID","=="+uid);
        //if (uid=="111"){
        String title = item.getTitle();
        String description = item.getSnippet();
           AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
           builder.setTitle("MENU (plot "+title+": "+description+")");
           builder.setPositiveButton("NAVIGATE", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ApplicationManager.mapActivity.navigateToPlot(uid);
               }
           });
           builder.setNegativeButton("OPEN", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ApplicationManager.mapActivity.openPlotData(Integer.valueOf(uid),Integer.valueOf(item.getTitle()));
               }
           });
            builder.show();
        //}
        return true;
    }
}