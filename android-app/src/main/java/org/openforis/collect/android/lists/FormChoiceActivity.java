package org.openforis.collect.android.lists;

import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.logs.RunnableHandler;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.screens.BaseListActivity;
import org.openforis.collect.android.service.ServiceFactory;
import org.openforis.collect.model.CollectSurvey;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * 
 * @author K. Waga
 *
 */
public class FormChoiceActivity extends BaseListActivity {
	
	private static final String TAG = "FormChoiceActivity";

	//private TextView activityLabel;
	
	private List<CollectSurvey> surveysList;
	private ArrayAdapter<String> adapter;
	
    private ListView lv;
    private LinearLayout mainLayout;  
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
        //setContentView(R.layout.clusterchoiceactivity);
        try{
        	ApplicationManager.formSelectionActivity = this;
        	
        	//this.activityLabel = (TextView)findViewById(R.id.lblList);
        	//this.activityLabel.setText(getResources().getString(R.string.formChoiceListLabel));           
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
		try{
			ApplicationManager.isRecordListUpToDate = false;
			ApplicationManager.recordsList = null;
			
			LayoutParams lp = new LayoutParams(
		            LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			//RecordChoiceActivity.this.sv.setLayoutParams(lp);
			FormChoiceActivity.this.lv = new ListView(FormChoiceActivity.this);
			FormChoiceActivity.this.lv.setLayoutParams(lp);
			FormChoiceActivity.this.lv.setId(android.R.id.list);
			//RecordChoiceActivity.this.sv.addView(lv);
			registerForContextMenu(lv);
			
			FormChoiceActivity.this.mainLayout = new LinearLayout(FormChoiceActivity.this);
			FormChoiceActivity.this.mainLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
			
			TextView screenTitle = new TextView(FormChoiceActivity.this);
			screenTitle.setText(getResources().getString(R.string.formChoiceListLabel));
			screenTitle.setTextSize(getResources().getInteger(R.integer.screenTitleFontSize));
			FormChoiceActivity.this.mainLayout.addView(screenTitle);
			FormChoiceActivity.this.mainLayout.addView(ApplicationManager.getDividerLine(this));
			
			//FormChoiceActivity.this.mainLayout.addView(arrangeButtonsInLine(new Button(this), getResources().getString(R.string.addInstanceButton), this));
			//FormChoiceActivity.this.mainLayout.addView(ApplicationManager.getDividerLine(this));
			
			FormChoiceActivity.this.mainLayout.addView(lv);	
			setContentView(FormChoiceActivity.this.mainLayout);
			
			int backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);	
			changeBackgroundColor(backgroundColor);
			//String selectedFormDefinitionFile = ApplicationManager.appPreferences.getString(getResources().getString(R.string.formDefinitionPath), getResources().getString(R.string.defaultFormDefinitionPath));
			ServiceFactory.getSurveyManager().init();
			this.surveysList = ServiceFactory.getSurveyManager().getAll();
			String[] formsList;
			if (this.surveysList.size()==0){
				formsList = new String[1];
				formsList[0] = "";
			} else {
				formsList = new String[surveysList.size()/*+2*/];
			}
			for (int i=0;i<surveysList.size();i++){
				CollectSurvey survey = surveysList.get(i);
				formsList[i] = survey.getName();
			}
			/*if (this.surveysList.size()==0){			
				formsList[0]=getResources().getString(R.string.addNewSurvey)+selectedFormDefinitionFile;
			} else {
				formsList[surveysList.size()]="";
				formsList[surveysList.size()+1]=getResources().getString(R.string.addNewSurvey)+selectedFormDefinitionFile;
			}*/
			
			int layout = (backgroundColor!=Color.WHITE)?R.layout.localclusterrow_white:R.layout.localclusterrow_black;
	        this.adapter = new ArrayAdapter<String>(this, layout, R.id.plotlabel, formsList);
			this.setListAdapter(this.adapter);
			
		    if (formsList[0].equals("")){
		    	FormChoiceActivity.this.lv.setVisibility(View.GONE);
		    } else {
		    	FormChoiceActivity.this.lv.setVisibility(View.VISIBLE);
		    }
			
			ApplicationManager.setSurvey(null);
		} catch (Exception e){			
			String[] formsList = new String[1];
			formsList[0] = "";						
			int layout = (backgroundColor!=Color.WHITE)?R.layout.localclusterrow_white:R.layout.localclusterrow_black;
	        this.adapter = new ArrayAdapter<String>(this, layout, R.id.plotlabel, formsList);
			this.setListAdapter(this.adapter);			
			ApplicationManager.setSurvey(null);
			
			AlertMessage.createPositiveDialog(FormChoiceActivity.this, false, getResources().getDrawable(R.drawable.warningsign),
 					getResources().getString(R.string.selectFormDefinitionFromDatabaseErrorTitle), getResources().getString(R.string.selectFormDefinitionFromDatabaseErrorMessage),
 					getResources().getString(R.string.okay),
 		    		new DialogInterface.OnClickListener() {
 						@Override
 						public void onClick(DialogInterface dialog, int which) {
 							
 						}
 					},
 					null).show();
			
			RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":run",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
		}		
    }
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.i(getResources().getString(R.string.app_name),TAG+":onListItemClick");
		if (this.surveysList.size()==0){
			Intent resultHolder = new Intent();
			resultHolder.putExtra(getResources().getString(R.string.formId), -1);	
			setResult(getResources().getInteger(R.integer.formDefinitionChoiceSuccessful),resultHolder);
			FormChoiceActivity.this.finish();	
		} else {
			if (position!=this.surveysList.size()){
				Intent resultHolder = new Intent();
				if (position<this.surveysList.size()){
					ApplicationManager.setSurvey(this.surveysList.get(position));
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
					resultHolder.putExtra(getResources().getString(R.string.formId), this.surveysList.get(position).getId());	
				} else {					
					resultHolder.putExtra(getResources().getString(R.string.formId), -1);					
				}			
				setResult(getResources().getInteger(R.integer.formDefinitionChoiceSuccessful),resultHolder);
				FormChoiceActivity.this.finish();	
			}
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
		AlertMessage.createPositiveNegativeDialog(FormChoiceActivity.this, false, getResources().getDrawable(R.drawable.warningsign),
				getResources().getString(R.string.exitAppTitle), getResources().getString(R.string.exitAppMessage),
				getResources().getString(R.string.yes), getResources().getString(R.string.no),
	    		new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						setResult(getResources().getInteger(R.integer.backButtonPressed), new Intent());
						FormChoiceActivity.this.finish();
					}
				},
	    		new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				},
				null).show();
	}
	
    private void changeBackgroundColor(int backgroundColor){
		getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));
		//this.activityLabel.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		
		TextView screenTitle = (TextView)this.mainLayout.getChildAt(0);
		screenTitle.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		View dividerLine = (View)this.mainLayout.getChildAt(1);
		dividerLine.setBackgroundColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);			
	
		    		
		//View dividerLine = (View)this.ll.getChildAt(5);
		/*dividerLine = (View)this.mainLayout.getChildAt(3);
		if (dividerLine!=null){
			dividerLine.setBackgroundColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);	
		}*/
		
		/*if (!(this.mainLayout.getChildAt(2) instanceof ScrollView)){
			RelativeLayout rLayout = (RelativeLayout)this.mainLayout.getChildAt(2);
			LinearLayout ll = (LinearLayout)rLayout.getChildAt(0);
			Button addBtn = (Button)ll.getChildAt(0);
			addBtn.setBackgroundResource((backgroundColor!=Color.WHITE)?R.drawable.add_new_black:R.drawable.add_new_white);
		}*/
    }
}