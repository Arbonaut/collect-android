package org.openforis.collect.android.bluetooth;

import org.openforis.collect.android.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothActivity extends Activity {

	private static final String TAG = "BluetoothActivity";
	private static final int REQUEST_ENABLE_BT = 0;  
    private static final int REQUEST_DISCOVERABLE_BT = 0;  
  @Override  
  protected void onCreate(Bundle savedInstanceState) {  
      super.onCreate(savedInstanceState);  
      setContentView(R.layout.bluetooth);  
  final TextView out=(TextView)findViewById(R.id.out);  
  final Button button1 = (Button) findViewById(R.id.button1);  
  final Button button2 = (Button) findViewById(R.id.button2);  
  final Button button3 = (Button) findViewById(R.id.button3);  
  final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();  
  if (mBluetoothAdapter == null) {  
     out.append("device not supported");  
  }  
  button1.setOnClickListener(new View.OnClickListener() {  
      public void onClick(View v) {  
          if (!mBluetoothAdapter.isEnabled()) {  
              Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);  
              startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);  
          }  
      }  
  });  
  button2.setOnClickListener(new View.OnClickListener() {  
   @Override  
      public void onClick(View arg0) {  
          if (!mBluetoothAdapter.isDiscovering()) {  
                //out.append("MAKING YOUR DEVICE DISCOVERABLE");  
                 Toast.makeText(getApplicationContext(), "MAKING YOUR DEVICE DISCOVERABLE",  
           Toast.LENGTH_LONG);  

              Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);  
              startActivityForResult(enableBtIntent, REQUEST_DISCOVERABLE_BT);  
                  
          }  
      }  
  });  
  button3.setOnClickListener(new View.OnClickListener() {  
      @Override  
      public void onClick(View arg0) {     
          mBluetoothAdapter.disable();  
          //out.append("TURN_OFF BLUETOOTH");  
          Toast.makeText(getApplicationContext(), "TURNING_OFF BLUETOOTH", Toast.LENGTH_LONG);  
         
          }  
  }); 
}  

 /* @Override  
  public boolean onCreateOptionsMenu(Menu menu) {  
      // Inflate the menu; this adds items to the action bar if it is present.  
      getMenuInflater().inflate(R.menu., menu);  
      return true;  
  }  */
	/*private BluetoothAdapter mBluetoothAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth);
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
		    // Device does not support Bluetooth
			Log.e(TAG,"Device does not support Bluetooth");			
		} else {
			Log.e(TAG,"Device supports Bluetooth");
			if (!mBluetoothAdapter.isEnabled()) {
				Log.e(TAG,"bluetooth is NOT yet enabled");
			    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    startActivityForResult(enableBtIntent, getResources().getInteger(R.integer.bluetoothEnabled));
			} else {
				Log.e(TAG,"bluetooth is already enabled");
			
			}
		}
	}	
	
    @Override
	public void onResume(){
		super.onResume();
		if (mBluetoothAdapter != null){
			if (mBluetoothAdapter.isEnabled()){
				String mydeviceaddress = mBluetoothAdapter.getAddress();
			    String mydevicename = mBluetoothAdapter.getName();
			    String status = mydevicename + ":" + mydeviceaddress+ ":" + mBluetoothAdapter.getState();
			    Log.e("deviceSTATUS","=="+status);
				Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
				// If there are paired devices
				if (pairedDevices.size() > 0) {
				    // Loop through paired devices
				    for (BluetoothDevice device : pairedDevices) {
				        // Add the name and address to an array adapter to show in a ListView
				        //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				    	Log.e(TAG,"device1:"+device.getName() + "\n" + device.getAddress());
				    }
				} else {
					// Create a BroadcastReceiver for ACTION_FOUND
						final BroadcastReceiver mReceiver = new BroadcastReceiver() {
					    public void onReceive(Context context, Intent intent) {
					        String action = intent.getAction();
					        // When discovery finds a device
					        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					            // Get the BluetoothDevice object from the Intent
					            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					            // Add the name and address to an array adapter to show in a ListView
					            //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
					            Log.e(TAG,"device2:"+device.getName() + "\n" + device.getAddress());
					        }
					    }
					};
					// Register the BroadcastReceiver
					IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
					registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
					if (mBluetoothAdapter.isDiscovering()) {
						mBluetoothAdapter.cancelDiscovery();
					}
					boolean discoveryStatus = mBluetoothAdapter.startDiscovery();
					Log.e("discoveryStatus","=="+discoveryStatus);
				}
			}
		}		
	}	
    

	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {    	
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode==getResources().getInteger(R.integer.bluetoothEnabled)){
	    	if (resultCode==RESULT_OK){
	    		Log.e(TAG,"bluetooth was successfully enabled");
	    	} else {
	    		Log.e(TAG,"bluetooth was not enabled");
	    	}
	    }
	}
	
	@Override
	public void onDestroy()
	{
		mBluetoothAdapter.cancelDiscovery();
		super.onDestroy();
	}	*/
}
