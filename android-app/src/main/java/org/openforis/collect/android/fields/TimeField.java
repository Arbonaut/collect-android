package org.openforis.collect.android.fields;

import java.util.Calendar;
import java.util.Random;

import org.openforis.collect.android.R;
import org.openforis.collect.android.dialogs.TimeSetDialog;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.misc.ViewBacktrack;
import org.openforis.collect.android.service.ServiceFactory;
import org.openforis.collect.model.NodeChangeSet;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.Time;
import org.openforis.idm.model.TimeAttribute;

import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 
 * @author K. Waga
 *
 */
public class TimeField extends InputField implements TextWatcher {
	
	public TimeField(Context context, NodeDefinition nodeDef) {
		super(context, nodeDef);
		
		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 2));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	String descr = TimeField.this.nodeDefinition.getDescription(ApplicationManager.selectedLanguage);
	        	if (descr==null){
	        		descr="";
	        	}
	        	ToastMessage.displayToastMessage(TimeField.this.getContext(), TimeField.this.getLabelText()+descr, Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		this.txtBox = new EditText(context);
		//this.setHint(hintText);
		this.txtBox.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		this.txtBox.addTextChangedListener(this);
		
		this.addView(this.txtBox);
		
		// When TimeField got focus
		this.txtBox.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	//Get current settings about software keyboard for text fields
		    	if(hasFocus){
			    	if(this.getClass().toString().contains("TimeField")){
				    	boolean valueForNum = ApplicationManager.appPreferences.getBoolean(getResources().getString(R.string.showSoftKeyboardOnNumericField), false);
				    	//Switch on or off Software keyboard depend of settings
				    	if(valueForNum){
				    		TimeField.this.makeReal();			    		
				        }
				    	else {
				    		txtBox.setInputType(InputType.TYPE_NULL);
				    	}
				    	//Generate random id for text box
				    	final Random myRandom = new Random();
				    	TimeField.this.txtBox.setId(myRandom.nextInt());
				    	//Show Time picker
				    	showTimePickerDialog(TimeField.this.elemDefId);				    	
			    	}
		    	}
		    }
	    });
	}

	private void showTimePickerDialog(int id) {
		Intent timePickerIntent = new Intent(TimeField.this.getContext(), TimeSetDialog.class);
		timePickerIntent.putExtra("timefield_id", id);
		timePickerIntent.putExtra("timeFieldPath", TimeField.this.form.getFormScreenId());
		timePickerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		ViewBacktrack viewBacktrack = new ViewBacktrack(this,null);
    	ApplicationManager.selectedViewsBacktrackList.add(viewBacktrack);
		
    	super.getContext().startActivity(timePickerIntent);	
	}
	
	public void setValue(Integer position, String value, String path, boolean isTextChanged)
	{
		if (!isTextChanged)
			this.txtBox.setText(value);
		
		Calendar today = null;
		if (value==null){
			today = Calendar.getInstance();
		} else if (value.equals("")){
			today = Calendar.getInstance();
		}
		
		if (today!=null){
			value = String.valueOf(today.get(Calendar.HOUR))+getResources().getString(R.string.timeSeparator)+String.valueOf(today.get(Calendar.MINUTE));
		}
		
		String hour = "";
		String minute = "";
		int separatorIndex = value.indexOf(getResources().getString(R.string.timeSeparator));
		if (separatorIndex!=-1){
			hour = value.substring(0,separatorIndex);
			if (separatorIndex+1<value.length())
				minute = value.substring(separatorIndex+1);
		}
		Node<? extends NodeDefinition> node = this.findParentEntity(path).get(this.nodeDefinition.getName(), position);
		NodeChangeSet nodeChangeSet = null;
		Entity parentEntity = this.findParentEntity(path);		
		if (node!=null){
			if (hour.equals("") && minute.equals("")){
				nodeChangeSet = ServiceFactory.getMobileRecordManager().updateAttribute((TimeAttribute)node, new Time(null,null));
			} else if (hour.equals("")){
				nodeChangeSet = ServiceFactory.getMobileRecordManager().updateAttribute((TimeAttribute)node, new Time(null,Integer.valueOf(minute)));
			} else if (minute.equals("")){
				nodeChangeSet = ServiceFactory.getMobileRecordManager().updateAttribute((TimeAttribute)node, new Time(Integer.valueOf(hour),null));
			} else {
				nodeChangeSet = ServiceFactory.getMobileRecordManager().updateAttribute((TimeAttribute)node, new Time(Integer.valueOf(hour),Integer.valueOf(minute)));
			}
		} else {
			if (hour.equals("") && minute.equals("")){
				nodeChangeSet = ServiceFactory.getMobileRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new Time(null,null), null, null);
			} else if (hour.equals("")){
				nodeChangeSet = ServiceFactory.getMobileRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new Time(null,Integer.valueOf(minute)), null, null);
			} else if (minute.equals("")){
				nodeChangeSet = ServiceFactory.getMobileRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new Time(Integer.valueOf(hour),null), null, null);
			} else {
				nodeChangeSet = ServiceFactory.getMobileRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new Time(Integer.valueOf(hour),Integer.valueOf(minute)), null, null);
			}			
		}
		validateField(nodeChangeSet);
	}
}
