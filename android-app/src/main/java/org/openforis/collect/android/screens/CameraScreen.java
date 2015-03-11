package org.openforis.collect.android.screens;

import java.io.File;

import org.openforis.collect.android.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

/**
 * 
 * @author K. Waga
 *
 */
public class CameraScreen extends Activity
{
	private static final String TAG = "CameraScreen";
	private String photoPath;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
	    this.photoPath = null;
	    startCameraActivity();
	}
	
	protected void startCameraActivity()
	{
		this.photoPath = Environment.getExternalStorageDirectory().toString()+"/mofc/"+System.currentTimeMillis()+".jpg";
	    File file = new File(this.photoPath);
	    Uri outputFileUri = Uri.fromFile(file);
	    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, outputFileUri);
	    startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.i(getResources().getString(R.string.app_name),TAG+":onActivityResult");
		Log.e("cameraResultCode",requestCode+"=="+resultCode);
	    switch( resultCode )
	    {
	    	case 0:
	    		finish();
	    		break;
	    	case -1:
	    		onPhotoTaken();
	    		break;
	    }
	}
	protected void onPhotoTaken()
	{
		Log.i(getResources().getString(R.string.app_name),TAG+":onPhotoTaken");
		Intent resultHolder = new Intent();
		resultHolder.putExtra(getResources().getString(R.string.photoPath), this.photoPath);
		setResult(getResources().getInteger(R.integer.photoTaken),resultHolder);
		this.photoPath = null;
		finish();
	}
}