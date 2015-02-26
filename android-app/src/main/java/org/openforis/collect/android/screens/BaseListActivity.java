package org.openforis.collect.android.screens;

import java.io.IOException;

import org.openforis.collect.android.R;
import org.openforis.collect.android.config.Configuration;
import org.openforis.collect.android.database.DatabaseHelper;
import org.openforis.collect.android.filechooser.FileChooser;
import org.openforis.collect.android.lists.DownloadActivity;
import org.openforis.collect.android.lists.FileImportActivity;
import org.openforis.collect.android.logs.RunnableHandler;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.management.DataManager;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.service.ServiceFactory;
import org.openforis.collect.model.CollectSurvey;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;


public class BaseListActivity extends ListActivity {
	
	private static final String TAG = "BaseListActivity";
	
	protected int backgroundColor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");    
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.setScreenOrientation();
	}
	
    @Override
	public void onResume()
	{
		super.onResume();
		Log.i(getResources().getString(R.string.app_name),TAG+":onResume");
		try {
			this.backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);
			if (ApplicationManager.getSurvey()==null){
				if (Build.VERSION.SDK_INT >= 11) {
					//invalidateOptionsMenu();
					ActivityCompat.invalidateOptionsMenu(BaseListActivity.this);
				}
			}
			this.setScreenOrientation();
		}		
		catch (Exception e){
			e.printStackTrace();
		}
		Thread thread = new Thread(new RunnableHandler(0, Environment.getExternalStorageDirectory().toString()
				+getResources().getString(R.string.logs_folder)
				+getResources().getString(R.string.logs_file_name)
				+"DEBUG_LOG_SAFETY"
				+System.currentTimeMillis()
				+getResources().getString(R.string.log_file_extension)));
		thread.start();
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.list_menu, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
    	menu.findItem(R.id.menu_add_survey).setVisible(ApplicationManager.getSurvey()==null);
    	menu.findItem(R.id.menu_export_all).setVisible(ApplicationManager.getSurvey()!=null);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    { 
        switch (item.getItemId())
        {
        	case R.id.menu_exit:
				AlertMessage.createPositiveNegativeDialog(BaseListActivity.this, false, getResources().getDrawable(R.drawable.warningsign),
	 					getResources().getString(R.string.exitAppTitle), getResources().getString(R.string.exitAppMessage),
	 					getResources().getString(R.string.yes), getResources().getString(R.string.no),
	 		    		new DialogInterface.OnClickListener() {
	 						@Override
	 						public void onClick(DialogInterface dialog, int which) {
	 							if (ApplicationManager.rootEntitySelectionActivity!=null){
	 								ApplicationManager.rootEntitySelectionActivity.finish();
	 							}
	 							if (ApplicationManager.recordSelectionActivity!=null){
	 								ApplicationManager.recordSelectionActivity.finish();
	 							}
	 							if (ApplicationManager.formSelectionActivity!=null){
	 								ApplicationManager.formSelectionActivity.finish();
	 							}
	 							ApplicationManager.mainActivity.finish();					
	 						}
	 					},
	 		    		new DialogInterface.OnClickListener() {
	 						@Override
	 						public void onClick(DialogInterface dialog, int which) {
	 							
	 						}
	 					},
	 					null).show();
			    return true;
        	/*case R.id.menu_export_all:
	        	final ProgressDialog pd = ProgressDialog.show(this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.backupingData));
	        	Thread backupThread = new Thread() {
	        		@Override
	        		public void run() {
	        			try {s
	        				super.run();
	        				Log.i(getResources().getString(R.string.app_name),TAG+":run");
	        				CollectSurvey collectSurveyExportAll = (CollectSurvey)ApplicationManager.getSurvey();	        	
	        	        	DataManager dataManagerExportAll = new DataManager(collectSurveyExportAll,collectSurveyExportAll.getSchema().getRootEntityDefinitions().get(0).getName(),ApplicationManager.getLoggedInUser());
	        	        	dataManagerExportAll.saveAllRecordsToFile(Environment.getExternalStorageDirectory().toString()+getResources().getString(R.string.exported_data_folder));
	        			} catch (Exception e) {
	        				RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":run",
	        	    				Environment.getExternalStorageDirectory().toString()
	        	    				+getResources().getString(R.string.logs_folder)
	        	    				+getResources().getString(R.string.logs_file_name)
	        	    				+System.currentTimeMillis()
	        	    				+getResources().getString(R.string.log_file_extension));
	        			} finally {
	        				pd.dismiss();
	        			}
	        		}
	        	};
	        	backupThread.start();	        	
	        	return true;*/    
			/*case R.id.menu_upload:
				startActivity(new Intent(BaseListActivity.this, UploadActivity.class));
			    return true;
			case R.id.menu_download:
				startActivity(new Intent(BaseListActivity.this, DownloadActivity.class));
			    return true;*/
			/*case R.id.menu_download:
				startActivity(new Intent(BaseListActivity.this, DownloadActivity.class));
			    return true;*/
        	case R.id.menu_download:
				startActivity(new Intent(BaseListActivity.this, DownloadActivity.class));
			    return true;
			case R.id.menu_import_from_file:
				startActivity(new Intent(BaseListActivity.this, FileImportActivity.class));
			    return true;
			case R.id.menu_import_database_from_file:
				//startActivity(new Intent(BaseListActivity.this, FileChooser.class));
				Intent databaseFileIntent = new Intent(BaseListActivity.this, FileChooser.class);
				databaseFileIntent.putExtra(getResources().getString(R.string.fileNameRequestType), getResources().getInteger(R.integer.chooseDatabaseFile));
				startActivityForResult(databaseFileIntent, getResources().getInteger(R.integer.chooseDatabaseFile));
			    return true;
			case R.id.menu_backup_database: 
				try {
					DatabaseHelper.backupDatabase(Environment.getExternalStorageDirectory().toString()+getResources().getString(R.string.application_folder), getResources().getString(R.string.database_file_name)+System.currentTimeMillis()+getResources().getString(R.string.database_file_extension));
					AlertMessage.createPositiveDialog(BaseListActivity.this, false, getResources().getDrawable(R.drawable.warningsign),
		 					getResources().getString(R.string.backupingDatabaseTitle), getResources().getString(R.string.backupingDatabaseMessageSuccess),
		 					getResources().getString(R.string.okay), 
		 		    		null,
		 					null).show();
				} catch (NotFoundException e) {
					AlertMessage.createPositiveDialog(BaseListActivity.this, false, getResources().getDrawable(R.drawable.warningsign),
		 					getResources().getString(R.string.backupingDatabaseTitle), getResources().getString(R.string.backupingDatabaseMessageFileNotFound),
		 					getResources().getString(R.string.okay), 
		 		    		null,
		 					null).show();
				} catch (IOException e) {
					AlertMessage.createPositiveDialog(BaseListActivity.this, false, getResources().getDrawable(R.drawable.warningsign),
		 					getResources().getString(R.string.backupingDatabaseTitle), getResources().getString(R.string.backupingDatabaseMessageIOException),
		 					getResources().getString(R.string.okay), 
		 		    		null,
		 					null).show();
				}
			    return true;
			    
			/*case R.id.menu_export:        	
	        	final ProgressDialog pdSavingRecordToXml = ProgressDialog.show(this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.backupingData));
				final Handler savingRecordToXmlHandler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						if (msg.what==0){//success
			        		AlertMessage.createPositiveDialog(BaseListActivity.this, true, null,
									getResources().getString(R.string.savingDataTitle), 
									getResources().getString(R.string.savingDataSuccessMessage),
										getResources().getString(R.string.okay),
							    		new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												
											}
										},
										null).show();
						} else {
			        		AlertMessage.createPositiveDialog(BaseListActivity.this, true, null,
									getResources().getString(R.string.savingDataTitle), 
									getResources().getString(R.string.savingDataFailureMessage),
										getResources().getString(R.string.okay),
							    		new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												
											}
										},
										null).show();
						}
					}
				};
	        	Thread savingRecordToXmlThread = new Thread() {
	        		@Override
	        		public void run() {
	        			try {
	        				super.run();
	        				CollectSurvey collectSurveyExport = (CollectSurvey)ApplicationManager.getSurvey();	        	
	        	        	DataManager dataManagerExport = new DataManager(BaseListActivity.this,collectSurveyExport,collectSurveyExport.getSchema().getRootEntityDefinitions().get(0).getName(),ApplicationManager.getLoggedInUser());
	        	        	dataManagerExport.saveRecordToXml(ApplicationManager.currentRecord, Environment.getExternalStorageDirectory().toString()+getResources().getString(R.string.exported_data_folder));
	        	        	savingRecordToXmlHandler.sendEmptyMessage(0);
	        			} catch (Exception e) {
	        				savingRecordToXmlHandler.sendEmptyMessage(1);
	        				RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":run",
	        	    				Environment.getExternalStorageDirectory().toString()
	        	    				+getResources().getString(R.string.logs_folder)
	        	    				+getResources().getString(R.string.logs_file_name)
	        	    				+System.currentTimeMillis()
	        	    				+getResources().getString(R.string.log_file_extension));
	        			} finally {
	        				pdSavingRecordToXml.dismiss();	        				
	        			}
	        		}
	        	};
	        	savingRecordToXmlThread.start();  
	        	return true;*/
	        case R.id.menu_export_all:
	        	final ProgressDialog pd = ProgressDialog.show(this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.backupingData));
				final Handler dataBackupHandler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						if (msg.what==0){//success
			        		AlertMessage.createPositiveDialog(BaseListActivity.this, true, null,
									getResources().getString(R.string.savingDataTitle), 
									getResources().getString(R.string.backingupDataSuccessMessage),
										getResources().getString(R.string.okay),
							    		new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												
											}
										},
										null).show();
						} else {
			        		AlertMessage.createPositiveDialog(BaseListActivity.this, true, null,
									getResources().getString(R.string.savingDataTitle), 
									getResources().getString(R.string.backingupDataFailureMessage),
										getResources().getString(R.string.okay),
							    		new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												
											}
										},
										null).show();
						}
					}
				};
	        	Thread backupThread = new Thread() {
	        		@Override
	        		public void run() {
	        			try {
	        				super.run();
	        				Log.i(getResources().getString(R.string.app_name),TAG+":run");
	        				CollectSurvey collectSurveyExportAll = (CollectSurvey)ApplicationManager.getSurvey();	        	
	        	        	DataManager dataManagerExportAll = new DataManager(BaseListActivity.this,collectSurveyExportAll,collectSurveyExportAll.getSchema().getRootEntityDefinitions().get(0).getName(),ApplicationManager.getLoggedInUser());
	        	        	dataManagerExportAll.saveAllRecordsToFile(Environment.getExternalStorageDirectory().toString()+getResources().getString(R.string.exported_data_folder));
	        	        	dataBackupHandler.sendEmptyMessage(0);
	        			} catch (Exception e) {
	        				dataBackupHandler.sendEmptyMessage(1);
	        				RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":run",
	        	    				Environment.getExternalStorageDirectory().toString()
	        	    				+getResources().getString(R.string.logs_folder)
	        	    				+getResources().getString(R.string.logs_file_name)
	        	    				+System.currentTimeMillis()
	        	    				+getResources().getString(R.string.log_file_extension));
	        			} finally {
	        				pd.dismiss();
	        			}
	        		}
	        	};
	        	backupThread.start();	        	
	        	return true;    
			case R.id.menu_add_new_survey:
				Intent formFileIntent = new Intent(BaseListActivity.this, FileChooser.class);
				formFileIntent.putExtra(getResources().getString(R.string.fileNameRequestType), getResources().getInteger(R.integer.chooseFormFile));
				startActivityForResult(formFileIntent, getResources().getInteger(R.integer.chooseFormFile));				
			    return true;	
			case R.id.menu_settings:
				startActivity(new Intent(BaseListActivity.this, SettingsScreen.class));
			    return true;			    
			case R.id.menu_about:
				String versionName;
				try {
					versionName = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
				} catch (NameNotFoundException e) {
					versionName = "";
				}
				String about = getResources().getString(R.string.lblApplicationName)+getResources().getString(R.string.app_name)
						+"\n"
						+getResources().getString(R.string.lblProgramVersionName)+versionName
						+"\n";
				if (ApplicationManager.getSurvey()!=null){
					String formVersionName = ApplicationManager.getSurvey().getProjectName(null);
					if (ApplicationManager.getSurvey().getVersions().size()>=1){
						formVersionName +=" "+ApplicationManager.getSurvey().getVersions().get(ApplicationManager.getSurvey().getVersions().size()-1).getName();	
					}					
					if (formVersionName!=null){
						about+= getResources().getString(R.string.lblFormVersionName)+formVersionName;
					}	
				}
				AlertMessage.createPositiveDialog(BaseListActivity.this, true, null,
						getResources().getString(R.string.aboutTabTitle), 
						about,
							getResources().getString(R.string.okay),
				    		new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
								}
							},
							null).show();
			        return true;
			        
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {    	
	    super.onActivityResult(requestCode, resultCode, data);
	    try{
	    	Log.e("onActivityResult","BaseListActivity");
	    	Log.e("requestCode"+requestCode,"resultCode"+resultCode);
	    	Log.e(""+getResources().getInteger(R.integer.chooseFormFile),""+getResources().getInteger(R.integer.formFileChosen));
	    	if (requestCode==getResources().getInteger(R.integer.chooseDatabaseFile)
	    			&&
	    		resultCode==getResources().getInteger(R.integer.databaseFileChosen)){
				Log.e("choosing database","=========================");
				Log.e("CHOSEN FILE","=="+data.getStringExtra(getResources().getString(R.string.databaseFileName)));
				String selectedFileName = data.getStringExtra(getResources().getString(R.string.databaseFileName));
				if (selectedFileName.endsWith(".db")){
					DatabaseHelper.copyDataBase(selectedFileName);
					Configuration config = Configuration.getDefault(BaseListActivity.this);
					ServiceFactory.init(config, false);					
				} else {
					AlertMessage.createPositiveDialog(BaseListActivity.this, true, null,
							getResources().getString(R.string.copyingDatabaseTitle), 
							getResources().getString(R.string.copyingDatabaseWrongFileExtensionMessage),
								getResources().getString(R.string.okay),
					    		new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										
									}
								},
								null).show();
				}
			} else if (requestCode==getResources().getInteger(R.integer.chooseFormFile)
						&&
					   resultCode==getResources().getInteger(R.integer.formFileChosen)){
				Intent resultHolder = new Intent();
				String selectedFileName = data.getStringExtra(getResources().getString(R.string.formFileName));
				resultHolder.putExtra(getResources().getString(R.string.formId), -1);
				resultHolder.putExtra(getResources().getString(R.string.formFileName), selectedFileName);	
				setResult(getResources().getInteger(R.integer.formDefinitionChoiceSuccessful),resultHolder);
				ApplicationManager.formSelectionActivity.finish();
				ApplicationManager.formSelectionActivity = null;
			}
	    } catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onActivityResult",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
	    }
    }

	public void setScreenOrientation(){
		String screenOrientation = ApplicationManager.appPreferences.getString(getResources().getString(R.string.screenOrientation), getResources().getString(R.string.defaultScreenOrientation)); 
		if (screenOrientation.equals("vertical")){
    		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	} else if (screenOrientation.equals("horizontal")){
    		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);	
    	} else {
    		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    	}
	}
}