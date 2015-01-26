package org.openforis.collect.android.screens;

import org.openforis.collect.android.R;
import org.openforis.collect.android.config.Configuration;
import org.openforis.collect.android.database.DatabaseHelper;
import org.openforis.collect.android.filechooser.FileChooser;
import org.openforis.collect.android.lists.DownloadActivity;
import org.openforis.collect.android.lists.FileImportActivity;
import org.openforis.collect.android.lists.UploadActivity;
import org.openforis.collect.android.logs.RunnableHandler;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.management.DataManager;
import org.openforis.collect.android.maps.OsmMapActivity;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.service.ServiceFactory;
import org.openforis.collect.model.CollectSurvey;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;


public class BaseActivity extends Activity {
	
	private static final String TAG = "BaseActivity";
	
	protected int backgroundColor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
    @Override
	public void onResume()
	{
		super.onResume();
		Log.i(getResources().getString(R.string.app_name),TAG+":onResume");
		try{
			this.backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);
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
        Log.e("onCreateOptionsMenu","menu");
        menuInflater.inflate(R.layout.menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    //public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
			case R.id.menu_map:
			    final ProgressDialog pdOpeningMap = ProgressDialog.show(this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.openingMap));
				final Handler openingMapHandler = new Handler() {
					@Override
					public void handleMessage(Message msg) {						
					}
				};
	        	Thread openingMapThread = new Thread() {
	        		@Override
	        		public void run() {
	        			try {
	        				super.run();
	        				startActivity(new Intent(BaseActivity.this,OsmMapActivity.class));
	        				openingMapHandler.sendEmptyMessage(0);
	        			} catch (Exception e) {
	        				openingMapHandler.sendEmptyMessage(1);
	        				RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":run",
	        	    				Environment.getExternalStorageDirectory().toString()
	        	    				+getResources().getString(R.string.logs_folder)
	        	    				+getResources().getString(R.string.logs_file_name)
	        	    				+System.currentTimeMillis()
	        	    				+getResources().getString(R.string.log_file_extension));
	        			} finally {
	        				pdOpeningMap.dismiss();	        				
	        			}
	        		}
	        	};
	        	openingMapThread.start();  
	        	return true;
			case R.id.menu_exit:
				AlertMessage.createPositiveNegativeDialog(BaseActivity.this, false, getResources().getDrawable(R.drawable.warningsign),
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
	 							if (ApplicationManager.formScreenActivityList!=null){
	 								for (Activity formScreenActivity : ApplicationManager.formScreenActivityList){
	 									formScreenActivity.finish();
	 								}
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
	        case R.id.menu_save:
	        	CollectSurvey collectSurveySave = (CollectSurvey)ApplicationManager.getSurvey();	        	
	        	DataManager dataManagerSave = new DataManager(this,collectSurveySave,collectSurveySave.getSchema().getRootEntityDefinitions().get(0).getName(),ApplicationManager.getLoggedInUser());
	        	boolean isSuccess = dataManagerSave.saveRecord();
	        	if (isSuccess){
	        		AlertMessage.createPositiveDialog(BaseActivity.this, true, null,
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
	        		AlertMessage.createPositiveDialog(BaseActivity.this, true, null,
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
	        	return true;
	        case R.id.menu_export:        	
	        	final ProgressDialog pdSavingRecordToXml = ProgressDialog.show(this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.backupingData));
				final Handler savingRecordToXmlHandler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						if (msg.what==0){//success
			        		AlertMessage.createPositiveDialog(BaseActivity.this, true, null,
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
			        		AlertMessage.createPositiveDialog(BaseActivity.this, true, null,
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
	        	        	DataManager dataManagerExport = new DataManager(BaseActivity.this,collectSurveyExport,collectSurveyExport.getSchema().getRootEntityDefinitions().get(0).getName(),ApplicationManager.getLoggedInUser());
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
	        	return true;
	        case R.id.menu_export_all:
	        	final ProgressDialog pd = ProgressDialog.show(this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.backupingData));
				final Handler dataBackupHandler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						if (msg.what==0){//success
			        		AlertMessage.createPositiveDialog(BaseActivity.this, true, null,
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
			        		AlertMessage.createPositiveDialog(BaseActivity.this, true, null,
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
	        	        	DataManager dataManagerExportAll = new DataManager(BaseActivity.this,collectSurveyExportAll,collectSurveyExportAll.getSchema().getRootEntityDefinitions().get(0).getName(),ApplicationManager.getLoggedInUser());
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
			case R.id.menu_upload:
				startActivity(new Intent(BaseActivity.this, UploadActivity.class));
			    return true;
			case R.id.menu_download:
				startActivity(new Intent(BaseActivity.this, DownloadActivity.class));
			    return true;
			case R.id.menu_import_from_file:
				startActivity(new Intent(BaseActivity.this, FileImportActivity.class));
			    return true;
			/*case R.id.menu_import_codelist_from_file:				
				try {
					String sdcardPath = Environment.getExternalStorageDirectory().toString();
				    CodeListImportProcess codeListImportProcess = new CodeListImportProcess(
				    		ServiceFactory.getCodeListManager(),
				    		ApplicationManager.getSurvey().getCodeList("species_code"), CodeScope.LOCAL, "en",
							new File(sdcardPath+getResources().getString(R.string.codelists_folder)+"/treecodes.csv"), true);
					codeListImportProcess.startProcessing();
				} catch (Exception e) {
					e.printStackTrace();
				}
			    return true;*/
			case R.id.menu_import_species_from_file:
				//startActivity(new Intent(BaseActivity.this, ImportSpeciesFromCsvActivity.class));
				Intent speciesListFileIntent = new Intent(BaseActivity.this, FileChooser.class);
				speciesListFileIntent.putExtra(getResources().getString(R.string.fileNameRequestType), getResources().getInteger(R.integer.chooseSpeciesListFile));
				startActivityForResult(speciesListFileIntent, getResources().getInteger(R.integer.chooseSpeciesListFile));
				return true;
			case R.id.menu_settings:
				startActivity(new Intent(BaseActivity.this,SettingsScreen.class));
			    return true;	    
			case R.id.menu_about:
				String versionName;
				try {
					versionName = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
				} catch (NameNotFoundException e) {
					versionName = "";
				}
				String aboutText = 						getResources().getString(R.string.lblApplicationName)+getResources().getString(R.string.app_name)
						+"\n"
						+getResources().getString(R.string.lblProgramVersionName)+versionName
						+"\n"
						+getResources().getString(R.string.lblFormVersionName)
						+ApplicationManager.getSurvey().getProjectName(ApplicationManager.selectedLanguage);
				if (ApplicationManager.getSurvey().getVersions()!=null){
					if (ApplicationManager.getSurvey().getVersions().size()>0){
						aboutText += " "+ApplicationManager.getSurvey().getVersions().get(ApplicationManager.getSurvey().getVersions().size()-1).getName();	
					}					
				}
				AlertMessage.createPositiveDialog(BaseActivity.this, true, null,
						getResources().getString(R.string.aboutTabTitle), 
						aboutText,
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
	    	Log.e("onActivityResult","BaseActivity");
	    	Log.e("requestCode"+requestCode,"resultCode"+resultCode);
	    	Log.e("P"+getResources().getInteger(R.integer.chooseFormFile),"K"+getResources().getInteger(R.integer.formFileChosen));
	    	if (requestCode==getResources().getInteger(R.integer.chooseSpeciesListFile)
	    			&&
	    		resultCode==getResources().getInteger(R.integer.speciesListFileChosen)){
				Log.e("choosing species list","=========================");
				Log.e("CHOSEN FILE","=="+data.getStringExtra(getResources().getString(R.string.speciesListFileName)));
				String selectedFileName = data.getStringExtra(getResources().getString(R.string.speciesListFileName));
				if (selectedFileName.endsWith(".csv")){
					/*DatabaseHelper.copyDataBase(selectedFileName);
					Configuration config = Configuration.getDefault(BaseActivity.this);
					ServiceFactory.init(config, false);*/
					DatabaseHelper.importSpeciesFileList(selectedFileName);
					Configuration config = Configuration.getDefault(BaseActivity.this);
					ServiceFactory.init(config, false);
				} else {
					AlertMessage.createPositiveDialog(BaseActivity.this, true, null,
							getResources().getString(R.string.importingSpeciesListTitle), 
							getResources().getString(R.string.importingSpeciesListWrongFileExtensionMessage),
								getResources().getString(R.string.okay),
					    		new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										
									}
								},
								null).show();
				}
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
    
    @Override
    public void onPause(){
    	Log.i(getResources().getString(R.string.app_name),TAG+":onPause");
    	super.onPause();
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
	
	protected void changeBackgroundColor (int backgroundColor){
		try{
			getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));
		} catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":changeBackgroundColor",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
		}
	}
}