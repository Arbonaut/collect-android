package org.openforis.collect.android.lists;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.openforis.collect.android.R;
import org.openforis.collect.android.logs.RunnableHandler;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.misc.ServerInterface;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author K. Waga
 *
 */
public class UploadActivity extends Activity{
	
	private static final String TAG = "UploadActivity";

	private TextView activityLabel;
	private TextView columnLabel;
	
	//private ArrayAdapter<String> adapter;
	
	//private Boolean[] selections;
	
	//private String[] filesList;
	
	private ListView lv;
	
	private String path;
	
	private ProgressDialog pd;
	
	private int filesCount;
	
	private List<DataFile> dataFilesList;
	private FileListAdapter adapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
        setContentView(R.layout.uploadactivity);
        try{
        	this.activityLabel = (TextView)findViewById(R.id.lblList);  
        	this.columnLabel = (TextView)findViewById(R.id.lblHeaders);
        	if (isNetworkAvailable()){
        		//this.activityLabel = (TextView)findViewById(R.id.lblList);        		
            	this.activityLabel.setText(getResources().getString(R.string.dataToUpload));
            	
            	//this.columnLabel = (TextView)findViewById(R.id.lblHeaders);
            	this.columnLabel.setText(getResources().getString(R.string.dataToUplaodColumnHeaders));
            	
            	//this.lv = getListView();
            	this.lv = (ListView)findViewById(R.id.file_list);
            	
            	path = Environment.getExternalStorageDirectory().toString()+getResources().getString(R.string.exported_data_folder);            
            	
            	Button btn =(Button) findViewById(R.id.btnUpload);
            	btn.setText(getResources().getString(R.string.uploadToServerButton));
                btn.setOnClickListener(new OnClickListener() {
    			    @Override
    			    public void onClick(View v) {
    			    	//CheckBox upload;
    			    	//CheckBox overwrite;
    			    	pd = ProgressDialog.show(UploadActivity.this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.uploadingDataToServerMessage));
    			    	for (int i=0;i<adapter.getCount();i++){
    			    		if (adapter.checkList.get(i)[0]){
    			    			(new SendData()).execute(adapter.getItem(i).getName(),adapter.checkList.get(i)[1]);
    		    				filesCount++;	
    			    		}
    			    	}
    			    	if (filesCount==0){
    			    		pd.dismiss();
    			    	}    	
    			    }
    		    });
        	} else {        		
        		AlertMessage.createPositiveDialog(UploadActivity.this, true, null,
						getResources().getString(R.string.noInternetTitle), 
						getResources().getString(R.string.noInternetMessage),
							getResources().getString(R.string.okay),
				    		new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									UploadActivity.this.finish();
								}
							},
							null).show();
        	}
        	
        	this.filesCount = 0;
        } catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onCreate",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
        }
    }
    
    public void onResume(){
		super.onResume();
		Log.i(getResources().getString(R.string.app_name),TAG+":onResume");
		try {
			int backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);	
			changeBackgroundColor(backgroundColor);

			dataFilesList = new ArrayList<DataFile>();
			File dataFilesFolder = new File(path);
			File[] dataFiles = dataFilesFolder.listFiles();
			int filesNo = dataFiles.length;
			for (int i=0;i<filesNo;i++) {
		        dataFilesList.add(new DataFile(dataFiles[i].getName(),"xml_icon"));
			}
			if (filesNo==0){
				this.activityLabel.setText(getResources().getString(R.string.noDataToUpload));
			}
			int layout = (backgroundColor!=Color.WHITE)?R.layout.upload_list_item_white:R.layout.upload_list_item_black;
			this.adapter = new FileListAdapter(this, layout, dataFilesList, "upload");
			lv.setAdapter(this.adapter);
		} catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onCreate",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
        }
	
    }
    
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }
	
	@Override
	public void onBackPressed() {
		UploadActivity.this.finish();
	}
	
    private void changeBackgroundColor(int backgroundColor){
		getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));
		this.activityLabel.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		this.columnLabel.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
    }
    
    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
        	sb.append(line).append("\n");
        }
        return sb.toString();
    }

    private static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        fin.close();        
        return ret;
    }
    
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    
    private class SendData extends AsyncTask {
    	 
        protected String doInBackground(Object... args) {
            try {            
            	String survey_id = ApplicationManager.appPreferences.getString(getResources().getString(R.string.surveyId), "99");
            	String username = ApplicationManager.appPreferences.getString(getResources().getString(R.string.username), "collect");
				return ServerInterface.sendDataFiles(ApplicationManager.appPreferences.getString(getResources().getString(R.string.recordsUploadPath), getResources().getString(R.string.defaultRecordsUploadPath)),UploadActivity.getStringFromFile(Environment.getExternalStorageDirectory().toString()+String.valueOf(getResources().getString(R.string.exported_data_folder)+"/"+args[0])), survey_id, username, (Boolean)args[1]);
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
        }
     
        /**
         * Parse the String result, and create a new array adapter for the list
         * view.
         */
        protected void onPostExecute(Object objResult) {
        	filesCount--;
            if(objResult != null && objResult instanceof String) {
                String result = (String) objResult;

                String[] responseList;
     
                StringTokenizer tk = new StringTokenizer(result, ",");
     
                responseList = new String[tk.countTokens()];
     
                int i = 0;
                while(tk.hasMoreTokens()) {
                    responseList[i++] = tk.nextToken();
                }
            }
            if (filesCount==0){
            	pd.dismiss();
    			AlertMessage.createPositiveDialog(UploadActivity.this, true, null,
    					getResources().getString(R.string.uploadToServerSuccessfulTitle), 
    					getResources().getString(R.string.uploadToServerSuccessfulMessage),
    						getResources().getString(R.string.okay),
    			    		new DialogInterface.OnClickListener() {
    							@Override
    							public void onClick(DialogInterface dialog, int which) {
    								
    							}
    						},
    						null).show();
            }            	
        }
     
    }
}