package org.openforis.collect.android.gestures;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ActivitySwipeDetector implements View.OnTouchListener {

 //private Activity activity;
 static final int MIN_DISTANCE = 70;
 private float downX, downY, upX, upY;

 public ActivitySwipeDetector(/*final Activity activity*/) { 
  //this.activity = activity;
 }

 public final void onRightToLeftSwipe() {
  Log.i("RightToLeftSwipe!","==");
 }

 public void onLeftToRightSwipe(){
  Log.i( "LeftToRightSwipe!","==");
 }

 public void onTopToBottomSwipe(){
  Log.i( "onTopToBottomSwipe!","==");
 }

 public void onBottomToTopSwipe(){
  Log.i( "onBottomToTopSwipe!","==");
 }

	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN: {
				downX = event.getX();
				downY = event.getY();
			}
			case MotionEvent.ACTION_UP: {
				upX = event.getX();
				upY = event.getY();
				
				Log.e("X",downX+"=="+upX);
				Log.e("Y",downY+"=="+upY);
				
				float deltaX = downX - upX;
				float deltaY = downY - upY;
				
				Log.e("deltaX","=="+deltaX);
				Log.e("deltaY","=="+deltaY);
				
				// swipe horizontal?
				if(Math.abs(deltaX) > MIN_DISTANCE){
					// left or right
					if(deltaX<0) { this.onLeftToRightSwipe(); return true; }
					if(deltaX>0) { this.onRightToLeftSwipe(); return true; }
				}
				else { 
					Log.i( "Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE,"=="); 
				}
				
				// swipe vertical?
				if(Math.abs(deltaY) > MIN_DISTANCE){
					// top or down
					if(deltaY<0) { this.onTopToBottomSwipe(); return true; }
					if(deltaY>0) { this.onBottomToTopSwipe(); return true; }
				} 
				else { 
					Log.i( "Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE,"=="); 
				}
			}
		}
		return false;
	}
}