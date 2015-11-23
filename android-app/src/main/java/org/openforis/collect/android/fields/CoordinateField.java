package org.openforis.collect.android.fields;


import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.screens.FormScreen;
import org.openforis.collect.android.service.ServiceFactory;
import org.openforis.collect.model.NodeChangeSet;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.SpatialReferenceSystem;
import org.openforis.idm.model.Coordinate;
import org.openforis.idm.model.CoordinateAttribute;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.Node;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author K. Waga
 *
 */
public class CoordinateField extends InputField implements OnClickListener {
	
	private EditText txtLatitude;
	private EditText txtLongitude;
	private TextView txtAltitude;
	private TextView txtAccuracy;
	private TextView txtBearing;
	private Button btnGetCoordinates;
	
	private TextView coordLabel;
	
	private static FormScreen form;
	
	ArrayList<String> options;
	ArrayList<String> codes;
	
	private ArrayAdapter<String> aa;
	private Spinner spinner;
	
	public SpatialReferenceSystem srs;
	private List<SpatialReferenceSystem> srsList;
	
	public CoordinateField(Context context, NodeDefinition nodeDef) {		
		super(context, nodeDef);

		CoordinateField.form = (FormScreen)context;
		
		//this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	String descr = CoordinateField.this.nodeDefinition.getDescription(ApplicationManager.selectedLanguage);
	        	if (descr==null){
	        		descr="";
	        	}
	        	ToastMessage.displayToastMessage(CoordinateField.this.getContext(), CoordinateField.this.getLabelText()+descr, Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		this.label.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	    			        	
	        }
	    });
		
		this.txtLongitude = new EditText(context);
		this.txtLongitude.addTextChangedListener(this);
		
		this.txtLongitude.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
		    	//Get current settings about software keyboard for numeric fields
		    	if(hasFocus){
			    	boolean valueForNum = ApplicationManager.appPreferences.getBoolean(getResources().getString(R.string.showSoftKeyboardOnNumericField), false);
			    	//Switch on or off Software keyboard depend of settings
			    	if(valueForNum){	
			    		txtLongitude.setKeyListener(new DigitsKeyListener(true,true));
			        }
			    	else {
			    		txtLongitude.setInputType(InputType.TYPE_NULL);
			    	}
		    	}	    	
			}
		});
		
		this.txtLatitude = new EditText(context);
		this.txtLatitude.addTextChangedListener(this);

		this.txtLatitude.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus) {					    	
		    	//Get current settings about software keyboard for numeric fields
		    	if(hasFocus){
		    		boolean valueForNum = false;				   
			    	if (ApplicationManager.appPreferences!=null){
			    		valueForNum = ApplicationManager.appPreferences.getBoolean(getResources().getString(R.string.showSoftKeyboardOnNumericField), false);
			    	}
			    	//Switch on or off Software keyboard depend of settings
			    	if(valueForNum){	
			    		txtLatitude.setKeyListener(new DigitsKeyListener(true,true));
			        }
			    	else {
			    		txtLatitude.setInputType(InputType.TYPE_NULL);
			    	}
		    	}		    	
			}
		});
		
		//Check if value is numeric
		this.txtLatitude.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start,  int count, int after) {}				 
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() > 0){
					if(!isNumeric(s.toString())){
						String strReplace = "";
						if (before==0){//inputting characters
							strReplace = s.toString().substring(0, start+count-1);
							strReplace += s.toString().substring(start+count);
						} else {//deleting characters
							//do nothing - number with deleted digit is still a number
						}
						CoordinateField.this.txtLatitude.setText(strReplace);
						CoordinateField.this.txtLatitude.setSelection(start);
					}
				}
			}
		});
		this.txtLongitude.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start,  int count, int after) {}				 
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				try {
					if (s.length() > 0){
						if(!isNumeric(s.toString())){
							String strReplace = "";
							if (before==0){//inputting characters
								strReplace = s.toString().substring(0, start+count-1);
								strReplace += s.toString().substring(start+count);
							} else {//deleting characters
								//do nothing - number with deleted digit is still a number
							}
							CoordinateField.this.txtLongitude.setText(strReplace);
							CoordinateField.this.txtLongitude.setSelection(start);
						}
					}
				} catch (Exception e){
					e.printStackTrace();
				}
				
			}
		});

		this.srsList = ApplicationManager.getSurvey().getSpatialReferenceSystems();
		if (this.srsList.size()>0){
			this.coordLabel = new TextView(context);
			this.coordLabel.setText(getResources().getString(R.string.spatialReferenceSystemLabel));

			this.codes = new ArrayList<String>();
			this.codes.add("null");
			this.options = new ArrayList<String>();
			this.options.add("");
			for (int i=0;i<this.srsList.size();i++){
				SpatialReferenceSystem srs = this.srsList.get(i);
				this.codes.add(srs.getId());
				this.options.add(extractLabel(srs));
			}
			this.spinner = new Spinner(context);
			this.spinner.setPrompt(this.label.getText());
			this.aa = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, this.options);
			this.aa.setDropDownViewResource(R.layout.codelistitem);
			this.spinner.setAdapter(aa);
			this.spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			    @Override
			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
			    	if (position>0)
			    		CoordinateField.this.srs = CoordinateField.this.srsList.get(position-1);
			    	else
			    		CoordinateField.this.srs = null;

					String srsId = null;
					if (CoordinateField.this.srs!=null){						
						srsId = CoordinateField.this.srs.getId();
					}
			    	if (CoordinateField.this.nodeDefinition.isMultiple()){
			    		CoordinateField.this.setValue(CoordinateField.form.currInstanceNo, CoordinateField.this.txtLongitude.getText().toString(), CoordinateField.this.txtLatitude.getText().toString(), srsId, CoordinateField.this.txtAltitude.getText().toString(), CoordinateField.this.txtAccuracy.getText().toString(), CoordinateField.this.txtBearing.getText().toString(), CoordinateField.form.getFormScreenId(),false);	
			    	} else {
			    		CoordinateField.this.setValue(0, CoordinateField.this.txtLongitude.getText().toString(), CoordinateField.this.txtLatitude.getText().toString(), srsId, CoordinateField.this.txtAltitude.getText().toString(), CoordinateField.this.txtAccuracy.getText().toString(), CoordinateField.this.txtBearing.getText().toString(), CoordinateField.form.getFormScreenId(),false);
			    	}
			    }

			    @Override
			    public void onNothingSelected(AdapterView<?> parentView) {
			    	
			    }

			});
			this.spinner.setSelection(0);
			
			this.txtAltitude = new TextView(context);
			this.txtAccuracy = new TextView(context);
			this.txtBearing = new TextView(context);
			
			this.addView(this.coordLabel);
			this.addView(this.spinner);
		
			this.addView(this.txtLongitude);
			this.addView(this.txtLatitude);
			
			this.addView(this.txtAltitude);
			
			this.addView(this.txtAccuracy);
			this.addView(this.txtBearing);
		}	
		
		this.btnGetCoordinates = new Button(context);
		this.btnGetCoordinates.setText(getResources().getString(R.string.internalGpsButton));
		this.btnGetCoordinates.setOnClickListener(this);  
		this.addView(this.btnGetCoordinates);
	}
	
	public void setValue(Integer position, String lon, String lat, String srsId, String altitude, String accuracy, String bearing, String path, boolean isTextChanged)
	{
		if (!isTextChanged){
			this.txtLongitude.setText(lon);
			this.txtLatitude.setText(lat);
			this.txtAltitude.setText(altitude);
			if (accuracy.length()>0)
				accuracy = (String) accuracy.subSequence(accuracy.indexOf(" "), accuracy.length()-1);
			if (bearing.length()>0)
				bearing = (String) bearing.subSequence(bearing.indexOf(" "), bearing.length()-1);
			this.txtAccuracy.setText("Accuracy: "+accuracy);
			this.txtBearing.setText("Bearing: "+bearing);
		}
		
		int srsIdPosition = 0;
		for (int i=0;i<this.srsList.size();i++){
			SpatialReferenceSystem srs = this.srsList.get(i);
			if (srs.getId().equals(srsId)){
				srsIdPosition = i+1;
			}
		}
		this.spinner.setSelection(srsIdPosition);
		
		Node<? extends NodeDefinition> node = this.findParentEntity(path).get(this.nodeDefinition.getName(), position);
		NodeChangeSet nodeChangeSet = null;
		Entity parentEntity = this.findParentEntity(path);

		if (node!=null){
			if ((lat.equals("")&&lon.equals(""))){	
				nodeChangeSet = ServiceFactory.getMobileRecordManager().updateAttribute((CoordinateAttribute)node, new Coordinate(null, null, srsId));
			} else if (lat.equals("")){
				nodeChangeSet = ServiceFactory.getMobileRecordManager().updateAttribute((CoordinateAttribute)node, new Coordinate(Double.valueOf(lon), null, srsId));
			} else if (lon.equals("")){
				nodeChangeSet = ServiceFactory.getMobileRecordManager().updateAttribute((CoordinateAttribute)node, new Coordinate(null,  Double.valueOf(lat), srsId));
			} else {
				nodeChangeSet = ServiceFactory.getMobileRecordManager().updateAttribute((CoordinateAttribute)node, new Coordinate(Double.valueOf(lon),  Double.valueOf(lat), srsId));
			}
		} else {
			if ((lat.equals("")&&lon.equals(""))){
				nodeChangeSet = ServiceFactory.getMobileRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new Coordinate(null, null, srsId), null, null);
			} else if (lat.equals("")){
				nodeChangeSet = ServiceFactory.getMobileRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new Coordinate(Double.valueOf(lon), null, srsId), null, null);
			} else if (lon.equals("")){
				nodeChangeSet = ServiceFactory.getMobileRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new Coordinate(null, Double.valueOf(lat), srsId), null, null);
			} else {
				nodeChangeSet = ServiceFactory.getMobileRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new Coordinate(Double.valueOf(lon), Double.valueOf(lat), srsId), null, null);
			}	
		}
		
		validateField(nodeChangeSet);
	}
	
	@Override
	public void setKeyboardType(KeyListener keyListener){
		this.txtLatitude.setKeyListener(keyListener);
		this.txtLongitude.setKeyListener(keyListener);
	}
	
	@Override
	public void setAlignment(int alignment){
		this.txtLatitude.setGravity(alignment);
		this.txtLongitude.setGravity(alignment);
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		String srsId = null;
		if (CoordinateField.this.srs!=null){						
			srsId = CoordinateField.this.srs.getId();
		}
		this.setValue(0, CoordinateField.this.txtLongitude.getText().toString(), CoordinateField.this.txtLatitude.getText().toString(), srsId, CoordinateField.this.txtAltitude.getText().toString(), CoordinateField.this.txtAccuracy.getText().toString(), CoordinateField.this.txtBearing.getText().toString(), CoordinateField.form.getFormScreenId(),true);
	}
	
	@Override
	public void addTextChangedListener(TextWatcher textWatcher) {
		
	}
	
	//Check if given value is a number
	private Boolean isNumeric(String strValue){
		Boolean result = false;
		try{
			Double.parseDouble(strValue);
			result = true;
		} catch(NumberFormatException e){
			result = false;
		}
		return result;
	}
	
	@Override
	public void onClick(View arg0) {
		CoordinateField.form.currentCoordinateField = this;
		CoordinateField.form.startInternalGps(this);
	}
	
	public void setCoordinateLabelTextColor(int color){
		if (this.coordLabel!=null)
			this.coordLabel.setTextColor(color);
	}

	private String extractLabel(SpatialReferenceSystem srs){
		String label = srs.getLabel(ApplicationManager.selectedLanguage);
		if (label==null){
			label = srs.getLabel(null);
			if (label==null){
				label = "";
			}
		}
		return label;
	}
}