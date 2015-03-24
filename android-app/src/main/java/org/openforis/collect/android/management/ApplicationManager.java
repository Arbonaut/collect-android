package org.openforis.collect.android.management;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openforis.collect.android.R;
import org.openforis.collect.android.config.Configuration;
import org.openforis.collect.android.database.DatabaseHelper;
import org.openforis.collect.android.fields.UIElement;
import org.openforis.collect.android.lists.FormChoiceActivity;
import org.openforis.collect.android.lists.RecordChoiceActivity;
import org.openforis.collect.android.lists.RootEntityChoiceActivity;
import org.openforis.collect.android.logs.RunnableHandler;
import org.openforis.collect.android.maps.OsmMapActivity;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.misc.CodeListItemsStorage;
import org.openforis.collect.android.misc.Pair;
import org.openforis.collect.android.misc.ViewBacktrack;
import org.openforis.collect.android.screens.BaseActivity;
import org.openforis.collect.android.screens.FormScreen;
import org.openforis.collect.android.service.ServiceFactory;
import org.openforis.collect.model.CollectRecord;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.model.User;
import org.openforis.idm.metamodel.CodeList;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.LanguageSpecificText;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.NodeLabel.Type;
import org.openforis.idm.metamodel.Survey;
import org.openforis.idm.model.Entity;
import org.osmdroid.util.GeoPoint;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * 
 * @author K. Waga
 *
 */
public class ApplicationManager extends BaseActivity {
	
	private static final String TAG = "ApplicationManager";
	
	private static String sessionId;

	private static CollectSurvey survey;
	//private static Schema schema;
	private static User loggedInUser;
	
	//public static List<NodeDefinition> fieldsDefList;
	
	public static SharedPreferences appPreferences;
	
	private static Map<Integer,UIElement> uiElementsMap;
	
	public static CollectRecord currentRecord;
	public static int currRootEntityId;
	public static List<ViewBacktrack> selectedViewsBacktrackList;
	//public static boolean isToBeScrolled;
	
	public static DataManager dataManager;
	
	public static ProgressDialog pd;
	
	public static String selectedLanguage;
	
	public static Activity mainActivity;
	public static Activity rootEntitySelectionActivity;
	public static Activity recordSelectionActivity;
	public static List<Activity> formScreenActivityList;
	public static Activity formSelectionActivity;
	public static OsmMapActivity mapActivity;
	
	public static List<CodeListItemsStorage> storedItemsList;
	
	public static boolean addNewEntity;
	
	public static List<CollectRecord> recordsList;
	//public static List<CollectSurvey> formsList;
	public static boolean isRecordListUpToDate;
	
	public static float dpiScale;
	
	public static boolean isBackFromTaxonSearch;
	
	public static List<List<Pair<Integer,GeoPoint>>> plots;
	public static List<Pair<Integer,GeoPoint>> lineEnds;
	public static List<Pair<Integer,GeoPoint>> points;
	public static boolean isPlotDrawingStarted;
	public static boolean isLineDrawingStarted;
	public static boolean isDotDrawingStarted;
	public static boolean isGpsOn;
	
	private Thread creationThread = new Thread() {
		@Override
		public void run() {
			try {
				super.run();
				Log.i(getResources().getString(R.string.app_name), TAG+":run");
	        	
	            initSession();
	            
	            Configuration config = Configuration.getDefault(ApplicationManager.this);
	            
	            DatabaseHelper.init(ApplicationManager.this, config);	            
	            
	            /*String dbFileName = DatabaseHelper.DB_PATH + DatabaseHelper.DB_NAME;		
	    		File file = new File(dbFileName);*/
			    ServiceFactory.init(config);
			    
	            ApplicationManager.currentRecord = null;
	            ApplicationManager.currRootEntityId = getResources().getInteger(R.integer.unsavedRecordId);
	            ApplicationManager.selectedViewsBacktrackList = new ArrayList<ViewBacktrack>();
	            //ApplicationManager.isToBeScrolled = false;
	            
	            ApplicationManager.storedItemsList = new ArrayList<CodeListItemsStorage>();	            	
				
				//creating file structure used by the application
	        	String sdcardPath = Environment.getExternalStorageDirectory().toString();
				File folder = new File(sdcardPath+getResources().getString(R.string.application_folder));
				folder.mkdirs();
				folder = new File(sdcardPath+getResources().getString(R.string.data_folder));
			    folder.mkdirs();
				folder = new File(sdcardPath+getResources().getString(R.string.photo_folder));
			    folder.mkdirs();
				folder = new File(sdcardPath+getResources().getString(R.string.exported_data_folder));
			    folder.mkdirs();
			    folder = new File(sdcardPath+getResources().getString(R.string.imported_data_folder));
			    folder.mkdirs();
			    folder = new File(sdcardPath+getResources().getString(R.string.backup_folder));
			    folder.mkdirs();
			    folder = new File(sdcardPath+getResources().getString(R.string.logs_folder));
			    folder.mkdirs();
			    folder = new File(sdcardPath+getResources().getString(R.string.codelists_folder));
			    folder.mkdirs();
			    folder = new File(sdcardPath+getResources().getString(R.string.plotBoundariesSavingPath));
			    folder.mkdirs();
			    folder = new File(sdcardPath+getResources().getString(R.string.maps_folder));
			    folder.mkdirs();
			    
	        	ApplicationManager.uiElementsMap = new HashMap<Integer,UIElement>();        	
	        	
	        	//adding default user to database if not exists        	
	        	User defaultUser = new User();
	        	defaultUser.setName(getResources().getString(R.string.defaultUsername));
	        	defaultUser.setPassword(getResources().getString(R.string.defaultUserPassword));
	        	defaultUser.setEnabled(true);
	        	defaultUser.setId(getResources().getInteger(R.integer.defaulUsertId));
	        	defaultUser.addRole(getResources().getString(R.string.defaultUserRole));
	        	/*if (!userExists(defaultUser)){
	        		ServiceFactory.getUserManager().insert(defaultUser);
	        	}*/
	        	ApplicationManager.loggedInUser = defaultUser;
	        	
	        	ApplicationManager.dataManager = null;
	            ApplicationManager.pd.dismiss();
	            
	            //showRootEntitiesListScreen();
	            ApplicationManager.isPlotDrawingStarted = false;
	            ApplicationManager.isLineDrawingStarted = false;
	            ApplicationManager.isDotDrawingStarted = false;
	            ApplicationManager.plots = new ArrayList<List<Pair<Integer,GeoPoint>>>();
	            ApplicationManager.lineEnds = new ArrayList<Pair<Integer,GeoPoint>>();
	            ApplicationManager.points = new ArrayList<Pair<Integer,GeoPoint>>();
	            ApplicationManager.isGpsOn = false;
	            showFormsListScreen();
	            
			} catch (Exception e) {
				RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":run",
	    				Environment.getExternalStorageDirectory().toString()
	    				+getResources().getString(R.string.logs_folder)
	    				+getResources().getString(R.string.logs_file_name)
	    				+System.currentTimeMillis()
	    				+getResources().getString(R.string.log_file_extension));
			} finally {
				//finish();
	            DatabaseHelper.closeConnection();
			}
		}
	};
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        	ApplicationManager.pd = ProgressDialog.show(this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.launchAppMessage));
        	Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
        	setContentView(R.layout.welcomescreen);        	        	
        	
        	ApplicationManager.appPreferences = getPreferences(MODE_PRIVATE);

			int backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);
			SharedPreferences.Editor editor = ApplicationManager.appPreferences.edit();
			editor.putInt(getResources().getString(R.string.backgroundColor), backgroundColor);

			int gpsTimeout = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.gpsTimeout), getResources().getInteger(R.integer.gpsTimeoutInMs));
			//editor = ApplicationManager.appPreferences.edit();
			editor.putInt(getResources().getString(R.string.gpsTimeout), gpsTimeout);
            
	    	Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
	    	Boolean valueForNum = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnNumericField));			
	    	Boolean valueForText = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnTextField));
	    	if(valueForNum == null)
	    		editor.putBoolean(getResources().getString(R.string.showSoftKeyboardOnNumericField), false);
	    	if(valueForText == null)
	    		editor.putBoolean(getResources().getString(R.string.showSoftKeyboardOnTextField), false);	    	
			
			String formDefinitionPath = ApplicationManager.appPreferences.getString(getResources().getString(R.string.formDefinitionPath), getResources().getString(R.string.defaultFormDefinitionPath));
			//editor = ApplicationManager.appPreferences.edit();
			editor.putString(getResources().getString(R.string.formDefinitionPath), formDefinitionPath);
			
			String survey_id = ApplicationManager.appPreferences.getString(getResources().getString(R.string.surveyId), getResources().getString(R.string.defaultSurveyId));
			editor.putString(getResources().getString(R.string.surveyId), survey_id);
			
			String username = ApplicationManager.appPreferences.getString(getResources().getString(R.string.username), getResources().getString(R.string.defaultUsername));
			editor.putString(getResources().getString(R.string.username), username);

			String recordsDownloadPath = ApplicationManager.appPreferences.getString(getResources().getString(R.string.recordsDownloadPath), getResources().getString(R.string.defaultRecordsDownloadPath));
			editor.putString(getResources().getString(R.string.recordsDownloadPath), recordsDownloadPath);
			
			String recordsUploadPath = ApplicationManager.appPreferences.getString(getResources().getString(R.string.recordsUploadPath), getResources().getString(R.string.defaultRecordsUploadPath));
			editor.putString(getResources().getString(R.string.recordsUploadPath), recordsUploadPath);			
			
			String screenOrientation = ApplicationManager.appPreferences.getString(getResources().getString(R.string.screenOrientation), getResources().getString(R.string.defaultScreenOrientation));
			editor.putString(getResources().getString(R.string.screenOrientation), screenOrientation);
			
			String fontSize = String.valueOf(ApplicationManager.appPreferences.getString(getResources().getString(R.string.fontSize), getResources().getString(R.string.defaultFontSize)));
			editor.putString(getResources().getString(R.string.fontSize), fontSize);
			
			String userLocationLat = ApplicationManager.appPreferences.getString(getResources().getString(R.string.userLocationLat), getResources().getString(R.string.defaultUserLocationLat));
			editor.putString(getResources().getString(R.string.userLocationLat), userLocationLat);

			String userLocationLon = ApplicationManager.appPreferences.getString(getResources().getString(R.string.userLocationLon), getResources().getString(R.string.defaultUserLocationLon));
			editor.putString(getResources().getString(R.string.userLocationLon), userLocationLon);
			
			int selectedZoomLevel = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.zoomLevel), getResources().getInteger(R.integer.defaultZoomLevel));
			editor.putInt(getResources().getString(R.string.zoomLevel), selectedZoomLevel);
			
	    	editor.commit();
	    	
	    	ApplicationManager.this.setScreenOrientation();
	    	
            //Intent enableBtIntent = new Intent(ApplicationManager.this, BluetoothActivity.class);
		    //startActivity(enableBtIntent);
	    	
        	creationThread.start();
        	
    		Thread thread = new Thread(new RunnableHandler(0, Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+"DEBUG_LOG"
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension)));
    		thread.start();
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
	public void onResume()
	{
		super.onResume();
		Log.i(getResources().getString(R.string.app_name),TAG+":onResume");
		try{
			
		} catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onResume",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
		}
	}
    
    @Override
    public void onPause() {
        super.onPause();
        Log.i(getResources().getString(R.string.app_name),TAG+":onPause");
        loadingFormDefinitionThread.interrupt();
    }
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {    	
	    super.onActivityResult(requestCode, resultCode, data);
	    try{
	 	    if (requestCode==getResources().getInteger(R.integer.clusterSelection)){
	 	    	if (resultCode==getResources().getInteger(R.integer.clusterChoiceSuccessful)){//record was selected	 	    		
	 	    		
	 	    		int recordId = data.getIntExtra(getResources().getString(R.string.recordId), getResources().getInteger(R.integer.unsavedRecordId));
	 	    		if (recordId==getResources().getInteger(R.integer.unsavedRecordId)){//new record
	 	    			String versionName = survey.getVersions().isEmpty() ? null: survey.getVersions().get(survey.getVersions().size()-1).getName();
	 	    			ApplicationManager.currentRecord = ServiceFactory.getMobileRecordManager().create(survey, ApplicationManager.getSurvey().getSchema().getRootEntityDefinition(ApplicationManager.currRootEntityId).getName(), ApplicationManager.dataManager.getUser(), versionName);
	 	    			Entity rootEntity = ApplicationManager.currentRecord.getRootEntity();
	 					rootEntity.setId(ApplicationManager.currRootEntityId);
	 	    		} else {//record from database
	 	    			CollectSurvey collectSurvey = (CollectSurvey)ApplicationManager.getSurvey();	        	
	 	    			ApplicationManager.dataManager = new DataManager(this,collectSurvey,collectSurvey.getSchema().getRootEntityDefinitions().get(0).getName(),ApplicationManager.getLoggedInUser());
			        	ApplicationManager.currentRecord = dataManager.loadRecord(recordId);
			        	Entity rootEntity = ApplicationManager.currentRecord.getRootEntity();
	    				rootEntity.setId(ApplicationManager.currRootEntityId);
	 	    		}
	 	    		showFormRootScreen();
    	            ApplicationManager.pd.dismiss();    	            
	 	    	} else if (resultCode==getResources().getInteger(R.integer.backButtonPressed)){
	 	    		if (ApplicationManager.getSurvey().getSchema().getRootEntityDefinitions().size()==1){
	 	    			showFormsListScreen();
	 	    		} else {
	 	    			showRootEntitiesListScreen();
	 	    		}	 	    	
	 	    	}
	 	    } else if (requestCode==getResources().getInteger(R.integer.rootEntitySelection)){
	 	    	if (resultCode==getResources().getInteger(R.integer.rootEntityChoiceSuccessful)){//root entity was selected	    	
	 	    		ApplicationManager.currRootEntityId = data.getIntExtra(getResources().getString(R.string.rootEntityId), getResources().getInteger(R.integer.unsavedRecordId));
	 	    		ApplicationManager.dataManager = new DataManager(this,survey,survey.getSchema().getRootEntityDefinition(ApplicationManager.currRootEntityId).getName(),ApplicationManager.getLoggedInUser());
	 	    		showRecordsListScreen(ApplicationManager.currRootEntityId);	
	 	    	} else if (resultCode==getResources().getInteger(R.integer.backButtonPressed)){
	 	    		showFormsListScreen();
	 	    	}
	 	    } else if (requestCode==getResources().getInteger(R.integer.formDefinitionSelection)){
	 	    	if (resultCode==getResources().getInteger(R.integer.formDefinitionChoiceSuccessful)){//form was selected
	 	    		int formId = data.getIntExtra(getResources().getString(R.string.formId), -1);
	 	    		if (formId==-1){//new form to be added from file
	 	    			ApplicationManager.pd = ProgressDialog.show(this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.loadingNewFormDefinitionMessage));
	 	    			new loadingFormDefinition().execute(data.getStringExtra(getResources().getString(R.string.formFileName)));
	 	    		} else {
	 	    			survey = ServiceFactory.getSurveyManager().getById(formId);
	 	    			showRootEntitiesListScreen();
	 	    		}
	 	    		if (codeListsLoadingThread.isAlive()){
	 	    			codeListsLoadingThread.interrupt();
	 	    		}
	 	    		codeListsLoadingThread.setPriority(Thread.MIN_PRIORITY);
	 	    		codeListsLoadingThread.start();
	 	    	} else if (resultCode==getResources().getInteger(R.integer.backButtonPressed)){
	 	    		ApplicationManager.this.finish();
	 	    	}
	 	    } else if (requestCode==getResources().getInteger(R.integer.startingFormScreen)){
	 	    	CollectSurvey collectSurvey = (CollectSurvey)ApplicationManager.getSurvey();	        	
		    	DataManager dataManager = new DataManager(this,collectSurvey,collectSurvey.getSchema().getRootEntityDefinition(ApplicationManager.currRootEntityId).getName(),ApplicationManager.getLoggedInUser());
		    	if (!ApplicationManager.isRecordListUpToDate){
		    		ApplicationManager.recordsList = dataManager.loadSummaries();
		    	}		    	
				//if (dataManager.loadSummaries().size()==0){
		    	if (ApplicationManager.recordsList.size()==0){
		        	if (ApplicationManager.getSurvey().getSchema().getRootEntityDefinitions().size()==1){
		        		AlertMessage.createPositiveNegativeDialog(ApplicationManager.this, false, getResources().getDrawable(R.drawable.warningsign),
			 					getResources().getString(R.string.selectFormDefinitionTitle), getResources().getString(R.string.selectFormDefinitionMessage),
			 					getResources().getString(R.string.yes), getResources().getString(R.string.no),
			 		    		new DialogInterface.OnClickListener() {
			 						@Override
			 						public void onClick(DialogInterface dialog, int which) {
			 							//ApplicationManager.this.finish();
			 							showFormsListScreen();
			 						}
			 					},
			 		    		new DialogInterface.OnClickListener() {
			 						@Override
			 						public void onClick(DialogInterface dialog, int which) {
			 							showFormRootScreen();
			 						}
			 					},
			 					null).show();		        		
		        	} else {
		        		AlertMessage.createPositiveNegativeDialog(ApplicationManager.this, false, getResources().getDrawable(R.drawable.warningsign),
			 					getResources().getString(R.string.selectRootEntityTitle), getResources().getString(R.string.selectRootEntityMessage),
			 					getResources().getString(R.string.yes), getResources().getString(R.string.no),
			 		    		new DialogInterface.OnClickListener() {
			 						@Override
			 						public void onClick(DialogInterface dialog, int which) {
			 							showRootEntitiesListScreen();						
			 						}
			 					},
			 		    		new DialogInterface.OnClickListener() {
			 						@Override
			 						public void onClick(DialogInterface dialog, int which) {
			 							showFormRootScreen();
			 						}
			 					},
			 					null).show();		        		
		        	}
				} else {
					AlertMessage.createPositiveNegativeDialog(ApplicationManager.this, false, getResources().getDrawable(R.drawable.warningsign),
		 					getResources().getString(R.string.selectRecordTitle), getResources().getString(R.string.selectRecordMessage),
		 					getResources().getString(R.string.yes), getResources().getString(R.string.no),
		 		    		new DialogInterface.OnClickListener() {
		 						@Override
		 						public void onClick(DialogInterface dialog, int which) {
		 							showRecordsListScreen(ApplicationManager.currRootEntityId);						
		 						}
		 					},
		 		    		new DialogInterface.OnClickListener() {
		 						@Override
		 						public void onClick(DialogInterface dialog, int which) {
		 							showFormRootScreen();
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
    
	private static Thread codeListsLoadingThread = new Thread() {
		@Override
		public void run() {
			try {
				super.run();
				List<CodeList> codeLists = survey.getCodeLists();
	        	for (CodeList codeList : codeLists){
	        		if (!codeList.isExternal())
	        			ServiceFactory.getCodeListManager().loadRootItems(codeList);
	        	}	        	            
			} catch (Exception e) {
				e.printStackTrace();
			} finally {

			}
		}
	};
    
    private class loadingFormDefinition extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
			try {
    			try{
    				Log.i(getResources().getString(R.string.app_name),TAG+":loadingForm");
    	        	
    	        	String selectedFormDefinitionFile = params[0];//ApplicationManager.appPreferences.getString(getResources().getString(R.string.formDefinitionPath), getResources().getString(R.string.defaultFormDefinitionPath));
 
    	        	MobileSurveyManager surveyManager = ServiceFactory.getSurveyManager();
    	        	File idmlFile = new File(selectedFormDefinitionFile);
    	        	
    	        	changeMessage(getResources().getString(R.string.unmarshallingSurveyMessage));

            		survey = surveyManager.unmarshalSurvey(idmlFile, false, false);
    	        	
            		changeMessage(getResources().getString(R.string.importingSurveyToDatabaseMessage));
            		            		
            		List<LanguageSpecificText> projectNamesList = survey.getProjectNames();
            		if (projectNamesList.size()>0){
            			survey.setName(projectNamesList.get(0).getText());
            		} else {
            			survey.setName("defaultSurveyName");
            		}

            		CollectSurvey loadedSurvey = surveyManager.get(survey.getName());
            		if (loadedSurvey==null){            			
    					survey = surveyManager.importModel(idmlFile, survey.getName(), false);
            		} else {
            			survey = loadedSurvey;
            		}
    			} catch (Exception e){
    				e.printStackTrace();
    				survey = null;
    				return e.getLocalizedMessage();
    			}
	            
			} catch (Exception e) {
				RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":run",
	    				Environment.getExternalStorageDirectory().toString()
	    				+getResources().getString(R.string.logs_folder)
	    				+getResources().getString(R.string.logs_file_name)
	    				+System.currentTimeMillis()
	    				+getResources().getString(R.string.log_file_extension));
			}
			return "Executed correctly.";
        }      

        @Override
        protected void onPostExecute(String result) {
			/*AlertDialog.Builder builder = new AlertDialog.Builder(ApplicationManager.this);
			builder.setTitle(getResources().getString(R.string.adding_survey_from_file_failure));
			builder.setMessage(result);
			builder.setNegativeButton(getResources().getString(R.string.okay), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					}
			});
			builder.show();*/

			if (survey!=null){
        		ApplicationManager.pd.dismiss();
        		SharedPreferences.Editor editor = ApplicationManager.appPreferences.edit();
				String language = ApplicationManager.appPreferences.getString(getResources().getString(R.string.selectedLanguage), getResources().getString(R.string.defaultLanguage));			
				boolean languageFound = false;
				List<String> languageList = ApplicationManager.getSurvey().getLanguages();
				if (ApplicationManager.getSurvey()!=null){	        		        
		    		for (int i=0;i<languageList.size();i++){
		    			if (languageList.get(i).equals(language)){
		    				languageFound = true;
		    			}
		    		}
		        }
				if (!languageFound){
					if (languageList.size()>0){
						language = languageList.get(0);
					} else {
						language = "null";
					}
				}
				editor = ApplicationManager.appPreferences.edit();
				editor.putString(getResources().getString(R.string.selectedLanguage), language);
				editor.commit();
				ApplicationManager.selectedLanguage = language;
        		showRootEntitiesListScreen();		    	            
        	} else {
        		ApplicationManager.pd.dismiss();
        		showFormsListScreen();
        	}
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
  }
    
	private Thread loadingFormDefinitionThread = new Thread() {
		@Override
		public void run() {
			try {
				super.run();

    			try{
    				Log.i(getResources().getString(R.string.app_name),TAG+":loadingForm");
     	           	
        			String sdcardPath = Environment.getExternalStorageDirectory().toString();

    	        	String selectedFormDefinitionFile = ApplicationManager.appPreferences.getString(getResources().getString(R.string.formDefinitionPath), getResources().getString(R.string.defaultFormDefinitionPath));
    	        	
    	        	MobileSurveyManager surveyManager = ServiceFactory.getSurveyManager();
    	        	File idmlFile = new File(sdcardPath, selectedFormDefinitionFile);
    	        	
    	        	changeMessage(getResources().getString(R.string.unmarshallingSurveyMessage));

            		survey = surveyManager.unmarshalSurvey(idmlFile, false, false);
    	        	
            		changeMessage(getResources().getString(R.string.importingSurveyToDatabaseMessage));
            		            		
            		List<LanguageSpecificText> projectNamesList = survey.getProjectNames();
            		if (projectNamesList.size()>0){
            			survey.setName(projectNamesList.get(0).getText());
            		} else {
            			survey.setName("defaultSurveyName");
            		}
            		CollectSurvey loadedSurvey = surveyManager.get(survey.getName());
            		if (loadedSurvey==null){
    					survey = surveyManager.importModel(idmlFile, survey.getName(), false);
            		} else {
            			survey = loadedSurvey;
            		}		
    			} catch (Exception e){
    				e.printStackTrace();
    				survey = null;
    			}
            	if (survey!=null){
            		ApplicationManager.pd.dismiss();
            		SharedPreferences.Editor editor = ApplicationManager.appPreferences.edit();
					String language = ApplicationManager.appPreferences.getString(getResources().getString(R.string.selectedLanguage), getResources().getString(R.string.defaultLanguage));			
					boolean languageFound = false;
					List<String> languageList = ApplicationManager.getSurvey().getLanguages();
					if (ApplicationManager.getSurvey()!=null){	        		        
			    		for (int i=0;i<languageList.size();i++){
			    			if (languageList.get(i).equals(language)){
			    				languageFound = true;
			    			}
			    		}
			        }
					if (!languageFound){
						if (languageList.size()>0){
							language = languageList.get(0);
						} else {
							language = "null";
						}
					}
					editor = ApplicationManager.appPreferences.edit();
					editor.putString(getResources().getString(R.string.selectedLanguage), language);
					editor.commit();
					ApplicationManager.selectedLanguage = language;
            		showRootEntitiesListScreen();		    	            
            	} else {
            		ApplicationManager.pd.dismiss();
            		AlertMessage.createPositiveDialog(ApplicationManager.this, false, getResources().getDrawable(R.drawable.warningsign),
		 					getResources().getString(R.string.loadFormDefinitionTitle), getResources().getString(R.string.loadFormDefinitionMessage),
		 					getResources().getString(R.string.okay),
		 		    		new DialogInterface.OnClickListener() {
		 						@Override
		 						public void onClick(DialogInterface dialog, int which) {
		 							//ApplicationManager.this.finish();
		 							showFormsListScreen();
		 						}
		 					},
		 					null).show();	
            	}
	            
			} catch (Exception e) {
				RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":run",
	    				Environment.getExternalStorageDirectory().toString()
	    				+getResources().getString(R.string.logs_folder)
	    				+getResources().getString(R.string.logs_file_name)
	    				+System.currentTimeMillis()
	    				+getResources().getString(R.string.log_file_extension));
			}
		}
	};
	
	/*private Runnable changeMessage = new Runnable() {
	    @Override
	    public void run() {
	        ApplicationManager.pd.setMessage(getResources().getString(R.string.unmarshallingSurveyMessage));
	    }
	};*/
	
	public void changeMessage(final String message) {
	    runOnUiThread(new Runnable() {
	        public void run() {
	            // use data here
	        	 ApplicationManager.pd.setMessage(message/*getResources().getString(R.string.unmarshallingSurveyMessage)*/);
	        }
	    });
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
    	try{
    		AlertMessage.createPositiveNegativeDialog(ApplicationManager.this, false, getResources().getDrawable(R.drawable.warningsign),
    				getResources().getString(R.string.exitAppTitle), getResources().getString(R.string.exitAppMessage),
    				getResources().getString(R.string.yes), getResources().getString(R.string.no),
    	    		new DialogInterface.OnClickListener() {
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    						ApplicationManager.this.finish();
    					}
    				},
    	    		new DialogInterface.OnClickListener() {
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    						
    					}
    				},
    				null).show();
    	}catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onBackPressed",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
    	}
    }
    
    private void initSession() {
    	ApplicationManager.sessionId = "1";//UUID.randomUUID().toString();
    	ApplicationManager.mainActivity = this;
    	ApplicationManager.rootEntitySelectionActivity = null;
    	ApplicationManager.recordSelectionActivity = null;
    	ApplicationManager.formScreenActivityList = new ArrayList<Activity>();
    	ApplicationManager.formSelectionActivity = null;
    	ApplicationManager.addNewEntity = false;
    	ApplicationManager.recordsList = null;
    	ApplicationManager.isRecordListUpToDate = false;
    	ApplicationManager.dpiScale = getBaseContext().getResources().getDisplayMetrics().density;
    	ApplicationManager.isBackFromTaxonSearch = false;
    	

	}
	
	public static User getLoggedInUser(){
		return ApplicationManager.loggedInUser;
	}
	
	private void showFormRootScreen(){	
		//List<EntityDefinition> rootEntitiesDefsList = schema.getRootEntityDefinitions();		
		Intent intent = new Intent(this,FormScreen.class);
		EntityDefinition rootEntityDef = (EntityDefinition)ApplicationManager.getSurvey().getSchema().getDefinitionById(ApplicationManager.currRootEntityId);
		intent.putExtra(getResources().getString(R.string.breadcrumb), ApplicationManager.getLabel(rootEntityDef));
		intent.putExtra(getResources().getString(R.string.screenTitle), ApplicationManager.getLabel(rootEntityDef));
		intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.singleEntityIntent));
		intent.putExtra(getResources().getString(R.string.parentFormScreenId), "");
		intent.putExtra(getResources().getString(R.string.idmlId), ApplicationManager.currRootEntityId);
		intent.putExtra(getResources().getString(R.string.instanceNo), 0);
		List<NodeDefinition> entityAttributes = rootEntityDef.getChildDefinitions();
        int counter = 0;
        for (NodeDefinition formField : entityAttributes){
			intent.putExtra(getResources().getString(R.string.attributeId)+counter, formField.getId());
			counter++;
        }
		/*intent.putExtra(getResources().getString(R.string.breadcrumb), "");
		intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.singleEntityIntent));
		intent.putExtra(getResources().getString(R.string.parentFormScreenId), "");
        intent.putExtra(getResources().getString(R.string.idmlId), 0);
        intent.putExtra(getResources().getString(R.string.instanceNo), 0);
        intent.putExtra(getResources().getString(R.string.attributeId)+0, ApplicationManager.currRootEntityId);*/
		this.startActivityForResult(intent,getResources().getInteger(R.integer.startingFormScreen));		
	}
	
	public void showRecordsListScreen(int rootEntityId){
		ApplicationManager.currentRecord = null;
		Intent recordLoadIntent = new Intent(this, RecordChoiceActivity.class);
		recordLoadIntent.putExtra(getResources().getString(R.string.rootEntityId), rootEntityId);
		this.startActivityForResult(recordLoadIntent, getResources().getInteger(R.integer.clusterSelection));
	}
	
	public void showRootEntitiesListScreen(){
		ApplicationManager.currRootEntityId = getResources().getInteger(R.integer.unsavedRecordId);		
		this.startActivityForResult(new Intent(this, RootEntityChoiceActivity.class),getResources().getInteger(R.integer.rootEntitySelection));
	}
	
	public void showFormsListScreen(){
		this.startActivityForResult(new Intent(this, FormChoiceActivity.class),getResources().getInteger(R.integer.formDefinitionSelection));
	}
	
	public static NodeDefinition getNodeDefinition(int nodeId){
		//return schema.getDefinitionById(nodeId);
		return survey.getSchema().getDefinitionById(nodeId);
	}
	
    public static Survey getSurvey(){
    	return ApplicationManager.survey;
    }
    
    public static void setSurvey(CollectSurvey collectSurvey){
    	ApplicationManager.survey = collectSurvey;
    }
	
	public static UIElement getUIElement(int elementId){
		return ApplicationManager.uiElementsMap.get(elementId);
	}
	
	public static void putUIElement(int key, UIElement uiEl){
		ApplicationManager.uiElementsMap.put(key, uiEl);
	}
	
	public static String getSessionId(){
		return ApplicationManager.sessionId;
	}
	
	public static String getLabel(NodeDefinition nodeDef){
		String label = nodeDef.getLabel(Type.INSTANCE, ApplicationManager.selectedLanguage);
		if (label==null){
			if (nodeDef.getLabels().size()>0){
				label = nodeDef.getLabels().get(0).getText();	
			} else {
				label = "";
			}			
		}
		return label;
	}

	public static CodeListItemsStorage getStoredItems(Integer definitionId, Integer selectedPosition){
		CodeListItemsStorage foundItemsStorage = null;
		for (CodeListItemsStorage storage : ApplicationManager.storedItemsList){
			if (storage.definitionId.equals(definitionId)){
				if (storage.selectedPositionInParent.equals(selectedPosition)){
					foundItemsStorage = storage;
					break;
				}				
			}
		}
		return foundItemsStorage;
	}
	
	public static View getDividerLine (Context ctx){
		View dividerView = new View(ctx);
	    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
	            ViewGroup.LayoutParams.FILL_PARENT, 2);
	    dividerView.setLayoutParams(lp);
	    dividerView.setBackgroundColor(Color.BLACK);

	    /*TypedArray array = ctx.getTheme().obtainStyledAttributes(new int[] {android.R.attr.listDivider});
	    Drawable draw = array.getDrawable(0);       
	    array.recycle();

	    dividerView.setBackgroundDrawable(draw);
	    //mParentLayout.addView(dividerView);*/
	    return dividerView;
	}
	
	public void setScreenOrientation(){
		String screenOrientation = getPreferences(MODE_PRIVATE).getString(getResources().getString(R.string.screenOrientation), getResources().getString(R.string.defaultScreenOrientation)); 
		if (screenOrientation.equals("vertical")){
    		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	} else if (screenOrientation.equals("horizontal")){
    		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);	
    	} else {
    		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    	}
	}
	
	public static void setImageButtonEnabled(Context ctxt, boolean enabled, Button item,
	        int iconResId) {
	    item.setEnabled(enabled);
	    Drawable originalIcon = ctxt.getResources().getDrawable(iconResId);
	    Drawable icon = enabled ? originalIcon : convertDrawableToGrayScale(originalIcon);
	    item.setBackgroundDrawable(icon);
	}

	public static Drawable convertDrawableToGrayScale(Drawable drawable) {
	    if (drawable == null) {
	        return null;
	    }
	    Drawable res = drawable.mutate();
	    res.setColorFilter(Color.GRAY, Mode.SRC_IN);
	    return res;
	}
}