package org.openforis.collect.android.bluetooth;

import org.openforis.collect.android.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothActivity extends Activity {

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
	          Toast.makeText(getApplicationContext(), "TURNING_OFF BLUETOOTH", Toast.LENGTH_LONG);  
	         
	          }  
	  }); 
	}  

}
