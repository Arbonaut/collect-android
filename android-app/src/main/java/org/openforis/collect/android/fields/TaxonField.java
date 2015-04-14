package org.openforis.collect.android.fields;

import java.util.ArrayList;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.misc.SearchTaxonActivity;
import org.openforis.collect.android.screens.FormScreen;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.TaxonAttributeDefinition;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.QwertyKeyListener;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author K. Waga
 *
 */
public class TaxonField extends InputField {
	
	private TextView codeLabel;
	private TextView sciNameLabel;
	private TextView venacNamesLabel;
	private TextView venacLangLabel;
	private TextView langVariantLabel;
	
	private EditText txtCodes;
	private EditText txtSciName;
	private EditText txtVernacularName;
	private ArrayAdapter<String> aa;
	private Spinner spinner;
	ArrayList<String> options;
	ArrayList<String> codes;
	private EditText txtLangVariant;
	
	private Button btnSearchByCode;
	private Button btnSearchBySciName;
	private Button btnSearchByVernName;
	
	boolean searchable;
	private static FormScreen form;
	
	String taxonomyName;
	
	private final String[] languageCodes = {"", "", "acm", "Mesopotamian Arabic", "afr", "Afrikaans", "ara", "Arabic", "arz", "Egyptian Arabic", "bel", "Belarusian", "ben", "Bengali", "bos", "Bosnian", "bre", "Breton", "bul", "Bulgarian", "cat", "Catalan", "ces", "Czech", "cha", "Chamorro", "cmn", "Mandarin Chinese", "dan", "Danish", "deu", "German", "ell", "Modern Greek (1453-)", "eng", "English", "epo", "Esperanto", "est", "Estonian", "eus", "Basque", "fao", "Faroese", "fin", "Finnish", "fra", "French", "fry", "Western Frisian", "gle", "Irish", "glg", "Galician", "heb", "Hebrew", "hin", "Hindi", "hrv", "Croatian", "hun", "Hungarian", "hye", "Armenian", "ina", "Interlingua (International Auxiliary Language Association)", "ind", "Indonesian", "isl", "Icelandic", "ita", "Italian", "jbo", "Lojban", "kat", "Georgian", "kaz", "Kazakh", "kor", "Korean", "lat", "Latin", "lit", "Lithuanian", "lvs", "Standard Latvian", "lzh", "Literary Chinese", "mal", "Malayalam", "mon", "Mongolian", "nan", "Min Nan Chinese", "nds", "Low German", "nld", "Dutch", "nob", "Norwegian Bokmål", "non", "Old Norse", "orv", "Old Russian", "oss", "Ossetian", "pes", "Iranian Persian", "pol", "Polish", "por", "Portuguese", "que", "Quechua", "roh", "Romansh", "ron", "Romanian", "rus", "Russian", "scn", "Sicilian", "slk", "Slovak", "slv", "Slovenian", "spa", "Spanish", "sqi", "Albanian", "srp", "Serbian", "swe", "Swedish", "swh", "Swahili (individual language)", "tat", "Tatar", "tgl", "Tagalog", "tha", "Thai", "tlh", "Klingon", "tur", "Turkish", "uig", "Uighur", "ukr", "Ukrainian", "urd", "Urdu", "uzb", "Uzbek", "vie", "Vietnamese", "vol", "Volapük", "wuu", "Wu Chinese", "yid", "Yiddish", "yue", "Yue Chinese", "zsm", "Standard Malay"};
	
	public TaxonField(Context context, NodeDefinition nodeDef, 
			ArrayList<String> codes, ArrayList<String> options, 
			String selectedItem) {
		super(context, nodeDef);
		Log.e("selectedItem","=="+selectedItem);
		TaxonField.form = (FormScreen)context;
		
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	String descr = TaxonField.this.nodeDefinition.getDescription(ApplicationManager.selectedLanguage);
	        	if (descr==null){
	        		descr="";
	        	}
	        	ToastMessage.displayToastMessage(TaxonField.this.getContext(), TaxonField.this.getLabelText()+descr, Toast.LENGTH_LONG);
	            return true;
	        }
	    });

		this.codeLabel = new TextView(context);
		this.codeLabel.setText(getResources().getString(R.string.taxonCodeLabel));
		this.codeLabel.setTextColor(Color.BLACK);

		this.txtCodes = new EditText(context);
		this.txtCodes.setText("");
		this.txtCodes.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));

		this.txtCodes.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	// Get current settings about software keyboard for text fields
		    	if(hasFocus){
			    	if(this.getClass().toString().contains("TaxonField")){
			    		boolean valueForText = false;				   
				    	if (ApplicationManager.appPreferences!=null){
				    		valueForText = ApplicationManager.appPreferences.getBoolean(getResources().getString(R.string.showSoftKeyboardOnTextField), false);
				    	}
				    	// Switch on or off Software keyboard depend of settings
				    	if(valueForText){
				    		TaxonField.this.txtCodes.setKeyListener(new QwertyKeyListener(TextKeyListener.Capitalize.NONE, false));
				        }
				    	else {
				    		txtCodes.setInputType(InputType.TYPE_NULL);
				    	}
			    	}
		    	}
		    }
	    });	
		this.txtCodes.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start,  int count, int after) {}				 
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length()>0){
					TaxonField.this.btnSearchByCode.setEnabled(true);
				} else {
					TaxonField.this.btnSearchByCode.setEnabled(false);
				}
				Log.e("listenerTXTCODES","setValue");
				TaxonField.this.setValue(0, s.toString(), 
						TaxonField.this.txtSciName.getText().toString(), 
						TaxonField.this.txtVernacularName.getText().toString(), 
						TaxonField.this.languageCodes[getVernacularLanguageCodeIndex(TaxonField.this.spinner.getSelectedItemPosition())-1], 
						TaxonField.this.txtLangVariant.getText().toString(),
						TaxonField.form.getFormScreenId(),true);
				/*if ((s.length()>2)&&(!ApplicationManager.isBackFromTaxonSearch))
					TaxonField.this.startSearchScreen(s.toString(), "Code");*/
			}	
		});

		this.btnSearchByCode = new Button(context);
		this.btnSearchByCode.setText(getResources().getString(R.string.searchButton));
		this.btnSearchByCode.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 4));		
		this.btnSearchByCode.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//Get category and value
				String strValue = TaxonField.this.txtCodes.getText().toString();
				//Check the value is not empty  and Run SearchTaxon activity
				//TODO: in future validator should check it
				if (!strValue.isEmpty())
					TaxonField.this.startSearchScreen(strValue, "Code");
			}
		});
		this.btnSearchByCode.setEnabled(false);
		
		//Create layout and add input field "Code" into there
		LinearLayout codeLL = new LinearLayout(context);		
		codeLL.setOrientation(HORIZONTAL);
		codeLL.addView(this.txtCodes);
		codeLL.addView(this.btnSearchByCode);
		this.addView(this.codeLabel);
		this.addView(codeLL);

		this.sciNameLabel = new TextView(context);
		this.sciNameLabel.setMaxLines(1);
		this.sciNameLabel.setTextColor(Color.BLACK);
		this.sciNameLabel.setText(getResources().getString(R.string.taxonScientificNameLabel));
		//Add text box for scientific names
		this.txtSciName = new EditText(context);
		this.txtSciName.setText(""/*initialText[1]*/);	
		this.txtSciName.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));

		// When txtSciName gets focus
		this.txtSciName.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	// Get current settings about software keyboard for text fields
		    	if(hasFocus){
		    		if(this.getClass().toString().contains("TaxonField")){
		    			boolean valueForText = false;				   
				    	if (ApplicationManager.appPreferences!=null){
				    		valueForText = ApplicationManager.appPreferences.getBoolean(getResources().getString(R.string.showSoftKeyboardOnTextField), false);
				    	}
				    	// Switch on or off Software keyboard depend of settings
				    	if(valueForText){
				    		TaxonField.this.txtSciName.setKeyListener(new QwertyKeyListener(TextKeyListener.Capitalize.NONE, false));
				        }
				    	else {
				    		txtSciName.setInputType(InputType.TYPE_NULL);
				    	}
			    	}
		    	}
		    }
	    });	
		this.txtSciName.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start,  int count, int after) {}				 
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length()>0){
					TaxonField.this.btnSearchBySciName.setEnabled(true);
				} else {
					TaxonField.this.btnSearchBySciName.setEnabled(false);
				}
				Log.e("TaxonFieldTextChangedListener","setValue");
				TaxonField.this.setValue(0, TaxonField.this.txtCodes.getText().toString(), 
						s.toString(), 
						TaxonField.this.txtVernacularName.getText().toString(), 
						TaxonField.this.languageCodes[getVernacularLanguageCodeIndex(TaxonField.this.spinner.getSelectedItemPosition())-1]/*TaxonField.this.txtVernacularLang.getText().toString()*/, 
						TaxonField.this.txtLangVariant.getText().toString(),
						TaxonField.form.getFormScreenId(),true);
				/*if ((s.length()>2)&&(!ApplicationManager.isBackFromTaxonSearch))
					TaxonField.this.startSearchScreen(s.toString(), "SciName");*/
			}	
		});

		this.btnSearchBySciName = new Button(context);
		this.btnSearchBySciName.setText(getResources().getString(R.string.searchButton));
		this.btnSearchBySciName.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 4));
		this.btnSearchBySciName.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//Get category and value
				String strValue = TaxonField.this.txtSciName.getText().toString();				
				//Add current value to FormScreen.currentFieldValue
		    	ArrayList<String> tempValue = new ArrayList<String>();
				tempValue.add(TaxonField.this.txtCodes.getText().toString());
				tempValue.add(TaxonField.this.txtSciName.getText().toString());
				tempValue.add(TaxonField.this.txtVernacularName.getText().toString());
				tempValue.add(TaxonField.this.languageCodes[getVernacularLanguageCodeIndex(TaxonField.this.spinner.getSelectedItemPosition())-1]/*TaxonField.this.txtVernacularLang.getText().toString()*/);
				tempValue.add(TaxonField.this.txtLangVariant.getText().toString());
				//Check the value is not empty  and Run SearchTaxon activity
				//TODO: in future validator should check it
				if (!strValue.isEmpty())
					TaxonField.this.startSearchScreen(strValue, "SciName");
			}
		});
		this.btnSearchBySciName.setEnabled(false);
		
		//Create layout and add input field "Scientific name" into there
		LinearLayout sciNameLL = new LinearLayout(context);		
		sciNameLL.setOrientation(HORIZONTAL);
		sciNameLL.addView(this.txtSciName);
		sciNameLL.addView(this.btnSearchBySciName);
		this.addView(this.sciNameLabel);
		this.addView(sciNameLL);
		
		this.venacNamesLabel = new TextView(context);
		this.venacNamesLabel.setMaxLines(1);
		this.venacNamesLabel.setTextColor(Color.BLACK);
		this.venacNamesLabel.setText(getResources().getString(R.string.taxonVernacularNameLabel));
		this.txtVernacularName = new EditText(context);

		this.txtVernacularName.setText(""/*initialText[2]*/);		
		this.txtVernacularName.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));

		// When txtVernacularName gets focus
		this.txtVernacularName.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	// Get current settings about software keyboard for text fields
		    	if(hasFocus){
		    		if(this.getClass().toString().contains("TaxonField")){
		    			boolean valueForText = false;				   
				    	if (ApplicationManager.appPreferences!=null){
				    		valueForText = ApplicationManager.appPreferences.getBoolean(getResources().getString(R.string.showSoftKeyboardOnTextField), false);
				    	}
				    	// Switch on or off Software keyboard depend of settings
				    	if(valueForText){
				    		TaxonField.this.txtVernacularName.setKeyListener(new QwertyKeyListener(TextKeyListener.Capitalize.NONE, false));
				        }
				    	else {
				    		txtVernacularName.setInputType(InputType.TYPE_NULL);
				    	}
			    	}
		    	}
		    }
	    });	
		this.txtVernacularName.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start,  int count, int after) {}				 
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length()>0){
					TaxonField.this.btnSearchByVernName.setEnabled(true);
				} else {
					TaxonField.this.btnSearchByVernName.setEnabled(false);
				}
				Log.e("TaxonFieldVerncularListener","setValue");
				TaxonField.this.setValue(TaxonField.form.currInstanceNo, TaxonField.this.txtCodes.getText().toString(), 
						TaxonField.this.txtSciName.getText().toString(), 
						s.toString(), 
						TaxonField.this.languageCodes[getVernacularLanguageCodeIndex(TaxonField.this.spinner.getSelectedItemPosition())-1]/*TaxonField.this.txtVernacularLang.getText().toString()*/, 
						TaxonField.this.txtLangVariant.getText().toString(),
						TaxonField.form.getFormScreenId(),true);
				/*if ((s.length()>2)&&(!ApplicationManager.isBackFromTaxonSearch))
					TaxonField.this.startSearchScreen(s.toString(), "VernacularName");*/
			}	
		});

		this.btnSearchByVernName = new Button(context);
		this.btnSearchByVernName.setText(getResources().getString(R.string.searchButton));
		this.btnSearchByVernName.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 4));
		this.btnSearchByVernName.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//Get category and value
				String strValue = TaxonField.this.txtVernacularName.getText().toString();
				//Check the value is not empty  and Run SearchTaxon activity
				//TODO: in future validator should check it
				if (!strValue.isEmpty())
					TaxonField.this.startSearchScreen(strValue, "VernacularName");
			}
		});
		this.btnSearchByVernName.setEnabled(false);
		LinearLayout vernNameLL = new LinearLayout(context);		
		vernNameLL.setOrientation(HORIZONTAL);
		vernNameLL.addView(this.txtVernacularName);
		vernNameLL.addView(this.btnSearchByVernName);
		this.addView(this.venacNamesLabel);
		this.addView(vernNameLL);
		
		this.venacLangLabel = new TextView(context);
		this.venacLangLabel.setMaxLines(1);
		this.venacLangLabel.setTextColor(Color.BLACK);
		this.venacLangLabel.setText(getResources().getString(R.string.taxonVernacularLanguageLabel));
		
		this.spinner = new Spinner(context);
		this.spinner.setPrompt(nodeDef.getName());
		
		this.codes = new ArrayList<String>();
		this.options = new ArrayList<String>();
		for (int i=0;i<Math.floor(this.languageCodes.length/2);i++){
			this.codes.add(this.languageCodes[2*i]);
			this.options.add(this.languageCodes[2*i+1]);
		}

		this.aa = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, this.options);
		this.aa.setDropDownViewResource(R.layout.codelistitem);

		this.spinner.setAdapter(aa);
		this.spinner.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 3));
		this.spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	Log.e("TaxonFieldonitemSelected","setValue");
		    	Log.e("txtSciName.getText().toString()","=="+TaxonField.this.txtSciName.getText().toString());
		    	if (TaxonField.this.nodeDefinition.isMultiple()){
		    		TaxonField.this.setValue(TaxonField.form.currInstanceNo, 
		    		TaxonField.this.txtCodes.getText().toString(), 
		    		TaxonField.this.txtSciName.getText().toString(),
					TaxonField.this.txtVernacularName.getText().toString(), 
					TaxonField.this.languageCodes[getVernacularLanguageCodeIndex(position)-1], 
					TaxonField.this.txtLangVariant.getText().toString(),
					TaxonField.form.getFormScreenId(),true);
		    	} else {
		    		TaxonField.this.setValue(0, 
				    		TaxonField.this.txtCodes.getText().toString(), 
				    		TaxonField.this.txtSciName.getText().toString(),
							TaxonField.this.txtVernacularName.getText().toString(), 
							TaxonField.this.languageCodes[getVernacularLanguageCodeIndex(position)-1], 
							TaxonField.this.txtLangVariant.getText().toString(),
							TaxonField.form.getFormScreenId(),true);
		    	}
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		    	
		    }
		});
		
		boolean isFound = false;
		int position = 0;
		if (selectedItem!=null && !selectedItem.equals("")){
			while (!isFound&&position<this.codes.size()){
				if (this.codes.get(position).equals(selectedItem)){
					isFound = true;
				}
				position++;
			}
		} else {
			position = findLanguageCodeOnList(ApplicationManager.selectedLanguage, TaxonField.this.languageCodes);
			isFound = true;
		}
		if (isFound)
			this.spinner.setSelection(position-1);
		else
			this.spinner.setSelection(0);
	
		this.addView(this.venacLangLabel);
		this.addView(this.spinner);

		this.langVariantLabel = new TextView(context);
		this.langVariantLabel.setMaxLines(1);
		this.langVariantLabel.setTextColor(Color.BLACK);
		this.langVariantLabel.setText(getResources().getString(R.string.taxonLanguageVariantLabel));	
		this.txtLangVariant = new EditText(context);
		this.txtLangVariant.setText("");	
		// When txtLangVariant gets focus
		this.txtLangVariant.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	// Get current settings about software keyboard for text fields
		    	if(hasFocus){
		    		if(this.getClass().toString().contains("TaxonField")){
		    			boolean valueForText = false;				   
				    	if (ApplicationManager.appPreferences!=null){
				    		valueForText = ApplicationManager.appPreferences.getBoolean(getResources().getString(R.string.showSoftKeyboardOnTextField), false);
				    	}
				    	// Switch on or off Software keyboard depend of settings
				    	if(valueForText){
				    		TaxonField.this.txtLangVariant.setKeyListener(new QwertyKeyListener(TextKeyListener.Capitalize.NONE, false));
				        }
				    	else {
				    		txtLangVariant.setInputType(InputType.TYPE_NULL);
				    	}
			    	}
		    	}
		    }
	    });
		this.txtLangVariant.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start,  int count, int after) {}				 
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Log.e("TaxonFieldVariantListener","setValue");
				TaxonField.this.setValue(/*TaxonField.this.form.currInstanceNo*/0, TaxonField.this.txtCodes.getText().toString(), 
						TaxonField.this.txtSciName.getText().toString(), 
						TaxonField.this.txtVernacularName.getText().toString(), 
						TaxonField.this.languageCodes[getVernacularLanguageCodeIndex(TaxonField.this.spinner.getSelectedItemPosition())-1]/*TaxonField.this.txtVernacularLang.getText().toString()*/, 
						TaxonField.this.txtLangVariant.getText().toString(),
						TaxonField.form.getFormScreenId(),true);
			}	
		});	
		this.addView(this.langVariantLabel);
		this.addView(this.txtLangVariant);
		
		if (this.searchable){
			this.label.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
				}});
		}
		this.searchable = true;			
		
		this.setOrientation(VERTICAL);
		
		TaxonAttributeDefinition taxonAttrDef = (TaxonAttributeDefinition)this.nodeDefinition;
		this.taxonomyName = taxonAttrDef.getTaxonomy();
	}
	
	/*public String getHint()
	{
//		return this.txtBox.getHint().toString();
		return "";
	}
	
	public void setHint(String value)
	{
//		this.txtBox.setHint(value);
	}*/
	
	private int findLanguageCodeOnList(String languageCode, String[] languageCodesList){
		int codeIndex = 0;
		int listLength = languageCodesList.length;
		if (languageCode.equals("es")){
			languageCode = "spa";
		} else if (languageCode.equals("en")){
			languageCode = "eng";
		}
		for (int i=0;i<listLength;i++){
			if (languageCode.equals(languageCodesList[i])){
				codeIndex = i/2+1;
				break;
			}
		}
		return codeIndex;
	}
	
	public void setValue(int position, String code, String sciName, String vernName, String vernLang, String langVariant, String path, boolean isTextChanged){
		Log.e("setValueCODE","=="+code);
		Log.e("setValueSCIName","=="+sciName);
		Log.e("setValue",(isTextChanged)+"=="+this.nodeDefinition.getName());
		if (!isTextChanged){
			Log.e("actualCode","=="+code);
			this.txtCodes.setText(code);
			this.txtSciName.setText(sciName);
			this.txtVernacularName.setText(vernName);
			this.txtLangVariant.setText(langVariant);
		}
		/*if (vernName!=null)
			if (vernName.trim().equals("")){
				vernName = null;
			}
		if (vernLang!=null)
			if (vernLang.trim().equals("")){
				vernLang = null;
			}
		if (langVariant!=null)
			if (langVariant.trim().equals("")){
				langVariant = null;
			}
		
		Entity parentEntity = this.findParentEntity(path);
		Node<? extends NodeDefinition> node = this.findParentEntity(path).get(this.nodeDefinition.getName(), position);
		NodeChangeSet nodeChangeSet = null;
		if (node!=null){
			nodeChangeSet = ServiceFactory.getRecordManager().updateAttribute((TaxonAttribute)node, new TaxonOccurrence(code, sciName, vernName, vernLang, langVariant));
		} else {
			nodeChangeSet = ServiceFactory.getRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new TaxonOccurrence(code, sciName, vernName, vernLang, langVariant), null, null);
		}*/
	}

	private void startSearchScreen(String strContent, String strCriteria){
		int taxonId = TaxonField.this.elemDefId;
		Intent searchTaxonIntent = new Intent(TaxonField.this.getContext(), SearchTaxonActivity.class);
		searchTaxonIntent.putExtra("content", strContent);
		searchTaxonIntent.putExtra("criteria", strCriteria);
		searchTaxonIntent.putExtra("taxonId", taxonId);
		searchTaxonIntent.putExtra("taxonomyName", TaxonField.this.taxonomyName);
		searchTaxonIntent.putExtra("path", TaxonField.form.getFormScreenId());
		searchTaxonIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	super.getContext().startActivity(searchTaxonIntent);
	}
	
	public void setFieldsLabelsTextColor(int color){
		this.codeLabel.setTextColor(color);
		this.sciNameLabel.setTextColor(color);
		this.venacNamesLabel.setTextColor(color);
		this.venacLangLabel.setTextColor(color);
		this.langVariantLabel.setTextColor(color);
	}
	
	public int getVernacularLanguageCodeIndex(int selectedItemPosition){
		return 2*selectedItemPosition+1;		
	}
}
