package org.openforis.collect.android.lists;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.logs.RunnableHandler;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.management.DataManager;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.misc.ServerInterface;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.persistence.xml.DataUnmarshallerException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author K. Waga
 *
 */
public class DownloadActivity extends Activity{
	
	private static final String TAG = "DownloadActivity";

	private TextView activityLabel;
	private TextView columnLabel;
	
	private ListView lv;
	
	private ProgressDialog pd;
	
	private int filesCount;
	
	private List<DataFile> dataFilesList;
	private FileListAdapter adapter;
	
	ProgressBar pb;
    Dialog dialog;
    int downloadedSize = 0;
    int totalSize = 0;
    TextView cur_val;
    
    private boolean isConnectedToInternet;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
        setContentView(R.layout.uploadactivity);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try{
        	this.activityLabel = (TextView)findViewById(R.id.lblList); 
        	this.columnLabel = (TextView)findViewById(R.id.lblHeaders);
        	this.lv = (ListView)findViewById(R.id.file_list);
        	isConnectedToInternet = isNetworkAvailable();
        	if (isConnectedToInternet){
        		      		
            	this.activityLabel.setText(getResources().getString(R.string.dataToDownload));
            	
            	
            	this.columnLabel.setText(getResources().getString(R.string.dataToDownlaodColumnHeaders));
            	
            	
            	
            	//path = Environment.getExternalStorageDirectory().toString()+getResources().getString(R.string.exported_data_folder);            
            	
            	Button btn =(Button) findViewById(R.id.btnUpload);
            	btn.setText(getResources().getString(R.string.downloadFromServerButton));
            	btn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    	pd = ProgressDialog.show(DownloadActivity.this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.downloadingDataToServerMessage));
                    	
                    	 new Thread(new Runnable() {
                             public void run() {
                                  downloadFiles(dataFilesList);
                             }
                           }).start();
                    }
                });              
        	} else {
        		AlertMessage.createPositiveDialog(DownloadActivity.this, true, null,
						getResources().getString(R.string.noInternetTitle), 
						getResources().getString(R.string.noInternetMessage),
							getResources().getString(R.string.okay),
				    		new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									DownloadActivity.this.finish();
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
		
		int backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);	
		changeBackgroundColor(backgroundColor);
		DownloadActivity.this.lv.setCacheColorHint(0);
		/*File dataFilesFolder = new File(path);
		File[] dataFiles = dataFilesFolder.listFiles();
		int filesNo = dataFiles.length;
		filesList = new String[filesNo];
		this.selections = new Boolean[filesNo];*/
		/*List<String> serverFiles = ServerInterface.getFilesList();
		int filesNo = serverFiles.size();
		filesList = new String[filesNo];
		this.selections = new Boolean[filesNo];
		for (int i=0;i<filesNo;i++) {
	        filesList[i] = serverFiles.get(i);
	        this.selections[i] = false;
		}*/
		/*for (int i=0;i<filesNo;i++) {
			File inFile = dataFiles[i];
	        filesList[i] = inFile.getName();
	        this.selections[i] = false;
		}*/
		/*if (filesNo==0){
			this.activityLabel.setText(getResources().getString(R.string.noDataToUpload));
		}*/
		//int layout = (backgroundColor!=Color.WHITE)?R.layout.selectableitem_white:R.layout.selectableitem_black;
		//int layout = (backgroundColor!=Color.WHITE)?R.layout.download_list_item_white:R.layout.download_list_item_black;
		//this.adapter = new ArrayAdapter<String>(this,layout,filesList);
		dataFilesList = new ArrayList<DataFile>();
		List<String> serverFiles = ServerInterface.getFilesList(ApplicationManager.appPreferences.getString(getResources().getString(R.string.recordsDownloadPath), getResources().getString(R.string.defaultRecordsDownloadPath)));
		int filesNo;
		if (serverFiles==null){
			if (isConnectedToInternet)
				Toast.makeText(this, getResources().getString(R.string.dataToDownloadNotExisting), Toast.LENGTH_LONG).show();
	    	filesNo = 0;
		} else {
			filesNo = serverFiles.size();
		}
		for (int i=0;i<filesNo;i++) {
	        //filesList[i] = serverFiles.get(i);
	        dataFilesList.add(new DataFile(serverFiles.get(i),"xml_icon"));
		}
		if (filesNo==0){
			this.activityLabel.setText(getResources().getString(R.string.noDataToDownload));
		}
		int layout = (backgroundColor!=Color.WHITE)?R.layout.download_list_item_white:R.layout.download_list_item_black;
		this.adapter = new FileListAdapter(this, layout, dataFilesList, "download");
		lv.setAdapter(this.adapter);
		
		this.filesCount = 0;
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
		DownloadActivity.this.finish();
	}
	
    private void changeBackgroundColor(int backgroundColor){
		getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));
		this.activityLabel.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		this.columnLabel.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
    }
    
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    
    void downloadFile(String fileName){
        
        try {
        	String dwnload_file_path = ApplicationManager.appPreferences.getString(getResources().getString(R.string.recordsDownloadPath), getResources().getString(R.string.defaultRecordsDownloadPath));
            URL url = new URL(dwnload_file_path+fileName);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
 
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);

            urlConnection.connect();
 
            //set the path where we want to save the file          
            File SDCardRoot = new File(Environment.getExternalStorageDirectory()+getResources().getString(R.string.imported_data_folder));
            //create a new file, to save the downloaded file
            File file = new File(SDCardRoot,fileName);
  
            FileOutputStream fileOutput = new FileOutputStream(file);
 
            //Stream used for reading the data from the Internet
            InputStream inputStream = urlConnection.getInputStream();
 
            //this is the total size of the file which is being downloaded
            totalSize = urlConnection.getContentLength();
 
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
 
            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
            }
            fileOutput.close();
            
            DataManager dataManager = new DataManager(this,(CollectSurvey) ApplicationManager.getSurvey(),ApplicationManager.getSurvey().getSchema().getRootEntityDefinition(ApplicationManager.currRootEntityId).getName(),ApplicationManager.getLoggedInUser());
            fileName = Environment.getExternalStorageDirectory().toString()+getResources().getString(R.string.imported_data_folder)+"/"+fileName;
            dataManager.loadRecordFromXml(fileName);
            filesCount--;
            if (filesCount==0){
            	pd.dismiss();
            	DownloadActivity.this.finish();
            }            	
        } catch (MalformedURLException e) {
            showError("Error : MalformedURLException " + e);       
            e.printStackTrace();
            pd.dismiss();
        } catch (IOException e) {
            showError("Error : IOException " + e);         
            e.printStackTrace();
            pd.dismiss();
        } catch (DataUnmarshallerException e){
        	showError("Parsing error: " + e);
        	e.printStackTrace();
            pd.dismiss();
        } catch (Exception e) {
            showError("Error : Please check your internet connection " + e);
            e.printStackTrace();
            pd.dismiss();
        }      
    }
    
    void downloadFiles(List<DataFile> dataFilesList){    	
    	for (int i=0;i<dataFilesList.size();i++){
    		if (DownloadActivity.this.adapter.checkList.get(i)[0]){
    			downloadFile(adapter.getItem(i).getName());	
    		}    		
    	}
    	ApplicationManager.recordsList = ApplicationManager.dataManager.loadSummaries();
    	pd.dismiss();
    }
     
    void showError(final String err){
        runOnUiThread(new Runnable() {
            public void run() {
            	AlertMessage.createPositiveDialog(DownloadActivity.this, true, null,
						"Error downloading the file(s)", 
						err,
						getResources().getString(R.string.okay),
			    		new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								DownloadActivity.this.finish();
							}
						},
						null).show();
            }
        });
    }
     
    void showProgress(String file_path){
        dialog = new Dialog(DownloadActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.myprogressdialog);
        dialog.setTitle("Download Progress");
 
        TextView text = (TextView) dialog.findViewById(R.id.tv1);
        text.setText("Downloading file from ... " + file_path);
        cur_val = (TextView) dialog.findViewById(R.id.cur_pg_tv);
        cur_val.setText("Starting download...");
        dialog.show();
         
        pb = (ProgressBar)dialog.findViewById(R.id.progress_bar);
        pb.setProgress(0);
        pb.setProgressDrawable(getResources().getDrawable(R.drawable.green_progress)); 
    }
}