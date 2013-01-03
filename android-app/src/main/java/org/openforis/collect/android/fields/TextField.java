package org.openforis.collect.android.fields;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openforis.collect.android.R;
import org.openforis.collect.android.data.FieldValue;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.screens.FormScreen;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.QwertyKeyListener;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class TextField extends InputField {

	protected List<String> values;
	
	public TextField(Context context, int id, String labelText, String initialText, String hintText,
			boolean isMultiple, boolean isRequired, FieldValue fieldValue) {
		super(context, id, isMultiple, isRequired);
		
		this.values = new ArrayList<String>();
		TextField.this.values.add(TextField.this.currentInstanceNo, "");
		
		this.label.setText(labelText);
		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		//this.label.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(TextField.this.getContext(), TextField.this.getLabelText(), Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		this.txtBox = new EditText(context);
		this.setHint(hintText);
		this.txtBox.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 3));
		//this.txtBox.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,  getResources().getInteger(R.integer.input_field_height)));
		this.txtBox.addTextChangedListener(this);
		this.txtBox.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
		
		this.container.addView(this.label);
		//this.container.addView(this.scrollLeft);		
		this.container.addView(this.txtBox);
		//this.container.addView(this.scrollRight);
		//this.container.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,  LayoutParams.WRAP_CONTENT));
		this.addView(this.container);
		
		this.value = fieldValue;
		/*this.scrollLeft.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	TextField.this.scrollLeft();
	        }
	    });
		
		this.scrollRight.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	TextField.this.scrollRight();        	
	        }
	    });*/
		
		// When TextField gets focus
		this.txtBox.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	// Get current settings about software keyboard for text fields
		    	if(hasFocus){
			    	if(this.getClass().toString().contains("TextField")){
				    	Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
				    	Boolean valueForText = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnText));
				    	// Switch on or off Software keyboard depend of settings
				    	if(valueForText){
				    		TextField.this.setKeyboardType(new QwertyKeyListener(TextKeyListener.Capitalize.NONE, false));
				        }
				    	else {
				    		TextField.this.setKeyboardType(null);
				    	}
				    	Log.e("CHANGING","CURRENT FIELDVALUE"+TextField.this.getElementId());
				    	FormScreen.currentFieldValue = TextField.this.value;
			    	}
		    	}
		    }
	    });				
	}
	
	@Override
	public void scrollLeft(){
    	if (TextField.this.currentInstanceNo>0){
    		TextField.this.values.set(TextField.this.currentInstanceNo, TextField.this.txtBox.getText().toString());	        		
    		TextField.this.currentInstanceNo--;
    		TextField.this.txtBox.setText(TextField.this.values.get(TextField.this.currentInstanceNo));    		
    	}
	} 
	
	@Override
	public void scrollRight(){
    	if (TextField.this.values.size()==(TextField.this.currentInstanceNo+1)){
    		TextField.this.values.add(TextField.this.currentInstanceNo+1, "");
    	}
    	TextField.this.values.set(TextField.this.currentInstanceNo, TextField.this.txtBox.getText().toString());
    	TextField.this.currentInstanceNo++;
		if (TextField.this.values.size()>=(TextField.this.currentInstanceNo+1)){
			TextField.this.txtBox.setText(TextField.this.values.get(TextField.this.currentInstanceNo));
		}
	}
	
	public String getValue(int index){
		//return TextField.this.values.get(index);
		return TextField.this.value.getValue(index).get(0);
	}
	
	public void setValue(int position, String value)
	{
		this.txtBox.setText(value);
		ArrayList<String> valueToAdd = new ArrayList<String>();
		valueToAdd.add(value);
		TextField.this.value.setValue(position, valueToAdd);
	}
	
	/*@Override
	public void afterTextChanged(Editable s) {
		TextField.this.values.set(TextField.this.currentInstanceNo, s.toString().toUpperCase());
	}*/
	
	@Override
	public int getInstancesNo(){
		return this.values.size();
	}
	
	public void resetValues(){
		this.values = new ArrayList<String>();
	}
	
	public void addValue(String value){
		this.values.add(value);
		this.currentInstanceNo++;
	}
	
	public List<String> getValues(){
		return this.values;
	}

	public void addTextChangedListener(TextWatcher textWatcher) {
		this.txtBox.addTextChangedListener(textWatcher);
	}
}
