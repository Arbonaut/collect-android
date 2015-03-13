package org.openforis.collect.android.gestures;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 
 * @author K. Waga
 *
 */
public class ScrollViewSwipeDetector implements View.OnTouchListener {

	//private Activity activity;
	static final int MIN_DISTANCE = 100;
	private float downX, downY, upX, upY;

	public ScrollViewSwipeDetector(/*final Activity activity*/) {
		//this.activity = activity;
	}

 public final void onRightToLeftSwipe() {
  Log.i("RightToLeftSwipe!","RightToLeftSwipe!");
 }

 public void onLeftToRightSwipe(){
  Log.i( "LeftToRightSwipe!","RightToLeftSwipe!");
 }

 public void onTopToBottomSwipe(){
  Log.i( "onTopToBottomSwipe!","RightToLeftSwipe!");
 }

 public void onBottomToTopSwipe(){
  Log.i( "onBottomToTopSwipe!","RightToLeftSwipe!");
 }

 public boolean onTouch(View v, MotionEvent event) {
  switch(event.getAction()){
  case MotionEvent.ACTION_DOWN: {
   downX = event.getX();
   downY = event.getY();
   //   return true;
  }
  case MotionEvent.ACTION_UP: {
   upX = event.getX();
   upY = event.getY();

   float deltaX = downX - upX;
   float deltaY = downY - upY;

   // swipe horizontal?
   if(Math.abs(deltaX) > MIN_DISTANCE){
    // left or right
    if(deltaX < 0) { this.onLeftToRightSwipe(); return true; }
    if(deltaX > 0) { this.onRightToLeftSwipe(); return true; }
   } else { Log.i( "Swipe was only " + Math.abs(deltaX) + " long", "need at least " + MIN_DISTANCE); }

   // swipe vertical?
   if(Math.abs(deltaY) > MIN_DISTANCE){
    // top or down
    if(deltaY < 0) { this.onTopToBottomSwipe(); return true; }
    if(deltaY > 0) { this.onBottomToTopSwipe(); return true; }
   } else { Log.i( "Swipe was only " + Math.abs(deltaX) + " long", "need at least " + MIN_DISTANCE); }

   //     return true;
  }
  }
  return false;
 }
}