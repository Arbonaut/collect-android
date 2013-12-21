package org.openforis.collect.android.screens;

import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.misc.RunnableHandler;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * 
 * @author K. Waga
 *
 */
public class SettingsScreen extends Activity{

	private static final String TAG = "SettingsScreen";
	
	private TextView tvScreenTitle;
	
	private CheckBox chckSoftKeyboardOnText;
	private CheckBox chckSoftKeyboardOnNumeric;
	private CheckBox chckWhiteBackground;
	
	private TextView tvGpsMaxWaitingTime;
	private EditText txtGpsMaxWaitingTime;
	
	private TextView tvLanguage;
	private Spinner spinLanguage;
	private ArrayAdapter<String> languageAdapter;
	
	private TextView tvFormDefinitionFilePath;
	private EditText txtFormDefinitionFilePath;
	
	private TextView tvUsername;
	private EditText txtUsername;
	
	private TextView tvScreenOrientation;
	private RadioGroup rgScreenOrientation;
	
	private TextView tvSurveyId;
	private EditText txtSurveyId;
	
	private TextView tvRecordsDownloadPath;
	private EditText txtRecordsDownloadPath;

	private TextView tvRecordsUploadPath;
	private EditText txtRecordsUploadPath;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.settingstab);
        try{        	        	
        	this.tvScreenTitle = (TextView)findViewById(R.id.lblTitle);
    		this.tvScreenTitle.setTextSize(getResources().getInteger(R.integer.breadcrumbFontSize));
        	
        	this.chckSoftKeyboardOnText = (CheckBox)findViewById(R.id.chkSoftKeyboardOnText);        	
    		this.chckSoftKeyboardOnText.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				SharedPreferences.Editor editor = ApplicationManager.appPreferences.edit();
    				editor.putBoolean(getResources().getString(R.string.showSoftKeyboardOnTextField), chckSoftKeyboardOnText.isChecked());
    				editor.commit();
      			}
    	    });
    		
        	this.chckSoftKeyboardOnNumeric = (CheckBox)findViewById(R.id.chkSoftKeyboardOnNumeric);        	
    		this.chckSoftKeyboardOnNumeric.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				SharedPreferences.Editor editor = ApplicationManager.appPreferences.edit();
    				editor.putBoolean(getResources().getString(R.string.showSoftKeyboardOnNumericField), chckSoftKeyboardOnNumeric.isChecked());
    				editor.commit();
      			}
    	    });
    		
    		this.chckWhiteBackground = (CheckBox)findViewById(R.id.chckWhiteBackground);        	
    		this.chckWhiteBackground.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				SharedPreferences.Editor editor = ApplicationManager.appPreferences.edit();
    				editor.putInt(getResources().getString(R.string.backgroundColor), (chckWhiteBackground.isChecked()?Color.WHITE:Color.BLACK));
    				editor.commit();
    				int backgroundColor = (ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE)==Color.WHITE)?Color.WHITE:Color.BLACK;
    				changeBackgroundColor(backgroundColor);
      			}
    	    });
    		    	  	
    		this.chckSoftKeyboardOnText.setChecked(ApplicationManager.appPreferences.getBoolean(getResources().getString(R.string.showSoftKeyboardOnTextField), false));
    		this.chckSoftKeyboardOnNumeric.setChecked(ApplicationManager.appPreferences.getBoolean(getResources().getString(R.string.showSoftKeyboardOnNumericField), false));

			this.tvGpsMaxWaitingTime = (TextView)findViewById(R.id.lblGpsTimeout);
			this.txtGpsMaxWaitingTime = (EditText)findViewById(R.id.txtGpsTimeout);
			this.txtGpsMaxWaitingTime.setText(String.valueOf(ApplicationManager.appPreferences.getInt(getResources().getString(R.string.gpsTimeout), getResources().getInteger(R.integer.gpsTimeoutInMs))/1000));
            this.txtGpsMaxWaitingTime.addTextChangedListener(new TextWatcher(){
		        public void afterTextChanged(Editable s) {        			            
					SharedPreferences.Editor editor = ApplicationManager.appPreferences.edit();
					editor = ApplicationManager.appPreferences.edit();
					try{
						editor.putInt(getResources().getString(R.string.gpsTimeout), Integer.valueOf(s.toString())*1000);
					} catch (Exception e){
						int gpsTimeout = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.gpsTimeout), getResources().getInteger(R.integer.gpsTimeoutInMs));
						editor.putInt(getResources().getString(R.string.gpsTimeout), gpsTimeout);	
					}
					editor.commit();
		        }
		        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		        public void onTextChanged(CharSequence s, int start, int before, int count){}
		    });
            
            this.tvLanguage = (TextView)findViewById(R.id.lblLanguage);
            this.spinLanguage = (Spinner) findViewById(R.id.languageSpinner);  
         	this.spinLanguage.setOnItemSelectedListener(new OnItemSelectedListener() {
 			    @Override
 			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
 			    	SharedPreferences.Editor editor = ApplicationManager.appPreferences.edit();
 			    	String language = spinLanguage.getAdapter().getItem(position).toString();
     				editor.putString(getResources().getString(R.string.selectedLanguage), language);
     				editor.commit();
     				ApplicationManager.selectedLanguage = language;
 			    }

 			    @Override
 			    public void onNothingSelected(AdapterView<?> parentView) {
 			    	
 			    }

 			});
         	
         	this.tvFormDefinitionFilePath = (TextView)findViewById(R.id.lblFormDefinitionFile);
			this.txtFormDefinitionFilePath = (EditText)findViewById(R.id.txtFormDefinitionFile);
			this.txtFormDefinitionFilePath.setText(ApplicationManager.appPreferences.getString(getResources().getString(R.string.formDefinitionPath),getResources().getString(R.string.defaultFormDefinitionPath)));
            this.txtFormDefinitionFilePath.addTextChangedListener(new TextWatcher(){
		        public void afterTextChanged(Editable s) {        			            
					SharedPreferences.Editor editor = ApplicationManager.appPreferences.edit();
					editor = ApplicationManager.appPreferences.edit();
					try{
						editor.putString(getResources().getString(R.string.formDefinitionPath), s.toString());
					} catch (Exception e){
	
					}
					editor.commit();
		        }
		        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		        public void onTextChanged(CharSequence s, int start, int before, int count){}
		    });
            
         	this.tvUsername = (TextView)findViewById(R.id.lblUsername);
			this.txtUsername = (EditText)findViewById(R.id.txtUsername);
			this.txtUsername.setText(ApplicationManager.appPreferences.getString(getResources().getString(R.string.username),getResources().getString(R.string.defaultUsername)));
            this.txtUsername.addTextChangedListener(new TextWatcher(){
		        public void afterTextChanged(Editable s) {        			            
					SharedPreferences.Editor editor = ApplicationManager.appPreferences.edit();
					editor = ApplicationManager.appPreferences.edit();
					try{
						editor.putString(getResources().getString(R.string.username), s.toString());
					} catch (Exception e){
	
					}
					editor.commit();
		        }
		        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		        public void onTextChanged(CharSequence s, int start, int before, int count){}
		    });
            
         	this.tvSurveyId = (TextView)findViewById(R.id.lblSurveyId);
			this.txtSurveyId = (EditText)findViewById(R.id.txtSurveyId);
			this.txtSurveyId.setText(ApplicationManager.appPreferences.getString(getResources().getString(R.string.surveyId),getResources().getString(R.string.defaultSurveyId)));
            this.txtSurveyId.addTextChangedListener(new TextWatcher(){
		        public void afterTextChanged(Editable s) {            
					SharedPreferences.Editor editor = ApplicationManager.appPreferences.edit();
					editor = ApplicationManager.appPreferences.edit();
					try{
						editor.putString(getResources().getString(R.string.surveyId), s.toString());
					} catch (Exception e){
	
					}
					editor.commit();
		        }
		        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		        public void onTextChanged(CharSequence s, int start, int before, int count){}
		    });
            
         	this.tvRecordsDownloadPath = (TextView)findViewById(R.id.lblRecordsDownloadPath);
			this.txtRecordsDownloadPath = (EditText)findViewById(R.id.txtRecordsDownloadPath);
			this.txtRecordsDownloadPath.setText(ApplicationManager.appPreferences.getString(getResources().getString(R.string.recordsDownloadPath),getResources().getString(R.string.defaultRecordsDownloadPath)));
            this.txtRecordsDownloadPath.addTextChangedListener(new TextWatcher(){
		        public void afterTextChanged(Editable s) {            
					SharedPreferences.Editor editor = ApplicationManager.appPreferences.edit();
					editor = ApplicationManager.appPreferences.edit();
					try{
						editor.putString(getResources().getString(R.string.recordsDownloadPath), s.toString());
					} catch (Exception e){
	
					}
					editor.commit();
		        }
		        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		        public void onTextChanged(CharSequence s, int start, int before, int count){}
		    });
            
            this.tvRecordsUploadPath = (TextView)findViewById(R.id.lblRecordsUploadPath);
			this.txtRecordsUploadPath = (EditText)findViewById(R.id.txtRecordsUploadPath);
			this.txtRecordsUploadPath.setText(ApplicationManager.appPreferences.getString(getResources().getString(R.string.recordsUploadPath),getResources().getString(R.string.defaultRecordsUploadPath)));
            this.txtRecordsUploadPath.addTextChangedListener(new TextWatcher(){
		        public void afterTextChanged(Editable s) {            
					SharedPreferences.Editor editor = ApplicationManager.appPreferences.edit();
					editor = ApplicationManager.appPreferences.edit();
					try{
						editor.putString(getResources().getString(R.string.recordsUploadPath), s.toString());
					} catch (Exception e){
						
					}
					editor.commit();
		        }
		        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		        public void onTextChanged(CharSequence s, int start, int before, int count){}
		    });
            
         	this.tvScreenOrientation = (TextView)findViewById(R.id.lblScreenOrientation);
			this.rgScreenOrientation = (RadioGroup)findViewById(R.id.radioScreenOrientation);
			String orientation = ApplicationManager.appPreferences.getString(getResources().getString(R.string.screenOrientation),getResources().getString(R.string.defaultScreenOrientation));
			int selectedRadioId = -1;
			if (orientation.equals("vertical")){
				selectedRadioId = R.id.radioVertical;
			} else if (orientation.equals("horizontal")){
				selectedRadioId = R.id.radioHorizontal;
			} else {
				selectedRadioId = R.id.radioAutoOrientation;
			}
			this.rgScreenOrientation.check(selectedRadioId);
			this.rgScreenOrientation.setOnCheckedChangeListener(
				new RadioGroup.OnCheckedChangeListener() {
				    public void onCheckedChanged(RadioGroup group,
				            int checkedId) {
				        SharedPreferences.Editor editor = ApplicationManager.appPreferences.edit();
						editor = ApplicationManager.appPreferences.edit();
						try{
							String orientation = "vertical";
							if (checkedId == R.id.radioHorizontal) orientation = "horizontal";
							else if  (checkedId == R.id.radioAutoOrientation) orientation = "auto";
							editor.putString(getResources().getString(R.string.screenOrientation), orientation);
						} catch (Exception e){
							
						}
						editor.commit();
				    }
				});
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
		int backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);
		this.chckWhiteBackground.setChecked((backgroundColor==Color.WHITE)?true:false);		
		changeBackgroundColor(backgroundColor);
		if (ApplicationManager.getSurvey()!=null){
        	List<String> languageList = ApplicationManager.getSurvey().getLanguages();
            this.languageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languageList);
         	this.languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         	this.spinLanguage.setAdapter(this.languageAdapter);
         	String language = ApplicationManager.appPreferences.getString(getResources().getString(R.string.selectedLanguage), getResources().getString(R.string.defaultLanguage));
    		for (int i=0;i<this.languageAdapter.getCount();i++){
    			if (this.languageAdapter.getItem(i).equals(language)){
    				this.spinLanguage.setSelection(i);
    				break;
    			}
    		}
        } else {
        	this.tvLanguage.setVisibility(View.GONE);
        	this.spinLanguage.setVisibility(View.GONE);
        }
		
	}
    
    private void changeBackgroundColor(int backgroundColor){
		getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));
		this.tvScreenTitle.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		this.chckSoftKeyboardOnText.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		this.chckSoftKeyboardOnNumeric.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		this.chckWhiteBackground.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		this.tvGpsMaxWaitingTime.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		this.tvLanguage.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		this.tvFormDefinitionFilePath.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		this.tvUsername.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		this.tvScreenOrientation.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		for (int i=0;i<this.rgScreenOrientation.getChildCount();i++){
			RadioButton rdBtn = (RadioButton)this.rgScreenOrientation.getChildAt(i);
			rdBtn.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);	
		} 
		this.tvSurveyId.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		this.tvRecordsDownloadPath.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		this.tvRecordsUploadPath.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		//this.txtGpsMaxWaitingTime.setBackgroundColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
    }
}