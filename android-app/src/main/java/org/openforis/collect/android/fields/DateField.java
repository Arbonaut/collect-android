package org.openforis.collect.android.fields;

import java.util.Calendar;
import java.util.Random;

import org.openforis.collect.android.R;
import org.openforis.collect.android.dialogs.DateSetDialog;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.misc.ViewBacktrack;
import org.openforis.collect.android.service.ServiceFactory;
import org.openforis.collect.model.NodeChangeSet;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.model.Date;
import org.openforis.idm.model.DateAttribute;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.Node;

import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 
 * @author K. Waga
 *
 */
public class DateField extends InputField {
	
	public DateField(Context context, NodeDefinition nodeDef) {
		super(context, nodeDef);

		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 2));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	String descr = DateField.this.nodeDefinition.getDescription(ApplicationManager.selectedLanguage);
	        	if (descr==null){
	        		descr="";
	        	}
	        	ToastMessage.displayToastMessage(DateField.this.getContext(), DateField.this.getLabelText()+descr, Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		this.txtBox = new EditText(context);
//		this.setHint(hintText);
		this.txtBox.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		this.txtBox.addTextChangedListener(this);
		
		//this.addView(this.label);
		this.addView(this.txtBox);
	
		// When text box in DateField got focus
		this.txtBox.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	//Get current settings about software keyboard for text fields
		    	if(hasFocus){
			    	if(this.getClass().toString().contains("DateField")){
				    	//Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
				    	//Boolean valueForNum = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnNumericField));
			    		boolean valueForNum = ApplicationManager.appPreferences.getBoolean(getResources().getString(R.string.showSoftKeyboardOnNumericField), false);
				    	//Switch on or off Software keyboard depend of settings
				    	if(valueForNum){
				    		DateField.this.makeReal();			    		
				        }
				    	else {
				    		txtBox.setInputType(InputType.TYPE_NULL);
//				    		DateField.this.setKeyboardType(null);
				    	}
				    	//Generate random id for text box
				    	final Random myRandom = new Random();
				    	DateField.this.txtBox.setId(myRandom.nextInt());
				    	//Show Date picker
				    	showDatePickerDialog(DateField.this.elemDefId);
			    	}
		    	}
		    }
	    });
	}
	
	private void showDatePickerDialog(int id) {
		Intent datePickerIntent = new Intent(DateField.this.getContext(), DateSetDialog.class);
    	datePickerIntent.putExtra("datefield_id", id);
    	datePickerIntent.putExtra("dateFieldPath", DateField.this.form.getFormScreenId());
    	datePickerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	
    	ViewBacktrack viewBacktrack = new ViewBacktrack(this,null);
    	ApplicationManager.selectedViewsBacktrackList.add(viewBacktrack);
    	//ApplicationManager.isToBeScrolled = true;
    	
    	super.getContext().startActivity(datePickerIntent);	
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
			value = String.valueOf(today.get(Calendar.YEAR))+getResources().getString(R.string.dateSeparator)+String.valueOf(today.get(Calendar.MONTH)+1)+getResources().getString(R.string.dateSeparator)+String.valueOf(today.get(Calendar.DAY_OF_MONTH));
		}
		
		String day = "";
		String month = "";
		String year = "";
		int firstSeparatorIndex = value.indexOf(getResources().getString(R.string.dateSeparator));
		int secondSeparatorIndex = value.lastIndexOf(getResources().getString(R.string.dateSeparator));
		if (firstSeparatorIndex!=-1){
			if (secondSeparatorIndex!=-1){
				year = value.substring(0,firstSeparatorIndex);
				if (secondSeparatorIndex>(firstSeparatorIndex+1)){
					month = value.substring(firstSeparatorIndex+1,secondSeparatorIndex);	
				}
				if (secondSeparatorIndex+1<value.length())
					day = value.substring(secondSeparatorIndex+1);
			}
		}

		Node<? extends NodeDefinition> node = this.findParentEntity(path).get(this.nodeDefinition.getName(), position);
		NodeChangeSet nodeChangeSet = null;
		Entity parentEntity = this.findParentEntity(path);
		if (node!=null){
			if (month.equals("") && day.equals("") && year.equals("")){
				nodeChangeSet = ServiceFactory.getMobileRecordManager().updateAttribute((DateAttribute)node, new Date(null,null,null));
			} else if (month.equals("") && day.equals("")){	
				nodeChangeSet = ServiceFactory.getMobileRecordManager().updateAttribute((DateAttribute)node, new Date(Integer.valueOf(year),null,null));
			} else if (month.equals("") && year.equals("")){	
				nodeChangeSet = ServiceFactory.getMobileRecordManager().updateAttribute((DateAttribute)node, new Date(null,null,Integer.valueOf(day)));
			} else if (day.equals("") && year.equals("")){
//				dateAtr.setValue(new Date(null,Integer.valueOf(month),null));
				nodeChangeSet = ServiceFactory.getMobileRecordManager().updateAttribute((DateAttribute)node, new Date(null,Integer.valueOf(month),null));
			} else if (month.equals("")){
//				dateAtr.setValue(new Date(Integer.valueOf(year),null,Integer.valueOf(day)));
				nodeChangeSet = ServiceFactory.getMobileRecordManager().updateAttribute((DateAttribute)node, new Date(Integer.valueOf(year),null,Integer.valueOf(day)));
			} else if (day.equals("")){
//				dateAtr.setValue(new Date(Integer.valueOf(year),Integer.valueOf(month),null));	
				nodeChangeSet = ServiceFactory.getMobileRecordManager().updateAttribute((DateAttribute)node, new Date(Integer.valueOf(year),Integer.valueOf(month),null));
			} else if (year.equals("")){
//				dateAtr.setValue(new Date(null,Integer.valueOf(month),Integer.valueOf(day)));	
				nodeChangeSet = ServiceFactory.getMobileRecordManager().updateAttribute((DateAttribute)node, new Date(null,Integer.valueOf(month),Integer.valueOf(day)));
			} else {
//				dateAtr.setValue(new Date(Integer.valueOf(year),Integer.valueOf(month),Integer.valueOf(day)));
				nodeChangeSet = ServiceFactory.getMobileRecordManager().updateAttribute((DateAttribute)node, new Date(Integer.valueOf(year),Integer.valueOf(month),Integer.valueOf(day)));
			}
		} else {
			if (month.equals("") && day.equals("") && year.equals("")){
//				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Date(null,null,null), position);
				nodeChangeSet = ServiceFactory.getMobileRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new Date(null,null,null), null, null);
			} else if (month.equals("") && day.equals("")){
//				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Date(Integer.valueOf(year),null,null), position);
				nodeChangeSet = ServiceFactory.getMobileRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new Date(Integer.valueOf(year),null,null), null, null);
			} else if (month.equals("") && year.equals("")){
//				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Date(null,null,Integer.valueOf(day)), position);	
				nodeChangeSet = ServiceFactory.getMobileRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new Date(null,null,Integer.valueOf(day)), null, null);
			} else if (day.equals("") && year.equals("")){
//				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Date(null,Integer.valueOf(month),null), position);	
				nodeChangeSet = ServiceFactory.getMobileRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new Date(null,Integer.valueOf(month),null), null, null);
			} else if (month.equals("")){
//				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Date(Integer.valueOf(year),null,Integer.valueOf(day)), position);	
				nodeChangeSet = ServiceFactory.getMobileRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new Date(Integer.valueOf(year),null,Integer.valueOf(day)), null, null);
			} else if (day.equals("")){
//				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Date(Integer.valueOf(year),Integer.valueOf(month),null), position);	
				nodeChangeSet = ServiceFactory.getMobileRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new Date(Integer.valueOf(year),Integer.valueOf(month),null), null, null);
			} else if (year.equals("")){
//				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Date(null,Integer.valueOf(month),Integer.valueOf(day)), position);
				nodeChangeSet = ServiceFactory.getMobileRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new Date(null,Integer.valueOf(month),Integer.valueOf(day)), null, null);
			} else {
//				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Date(Integer.valueOf(year),Integer.valueOf(month),Integer.valueOf(day)), position);
				nodeChangeSet = ServiceFactory.getMobileRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new Date(Integer.valueOf(year),Integer.valueOf(month),Integer.valueOf(day)), null, null);
			}	
		}
		validateField(nodeChangeSet);
	}
	
	public static String formatDate(Date dateValue){
    	String formattedDateValue = "";
    	String year = String.valueOf(dateValue.getYear());
    	String month = String.valueOf(dateValue.getMonth());
    	if (month!=null){
    		if (month.length()==1){
    			month = "0"+month;
    		}
    	}
    	String day = String.valueOf(dateValue.getDay());
    	if (day!=null){
    		if (day.length()==1){
    			day = "0"+day;
    		}
    	}
		if (dateValue.getMonth()==null && dateValue.getDay()==null && dateValue.getYear()==null){
			formattedDateValue = "";
		} else if (dateValue.getMonth()==null && dateValue.getDay()==null){
			formattedDateValue = year+ApplicationManager.mainActivity.getResources().getString(R.string.dateSeparator)+ApplicationManager.mainActivity.getResources().getString(R.string.dateSeparator);		    							
		} else if (dateValue.getMonth()==null && dateValue.getYear()==null){
			formattedDateValue = ApplicationManager.mainActivity.getResources().getString(R.string.dateSeparator)+ApplicationManager.mainActivity.getResources().getString(R.string.dateSeparator)+day;
		} else if (dateValue.getDay()==null && dateValue.getYear()==null){
			formattedDateValue = ApplicationManager.mainActivity.getResources().getString(R.string.dateSeparator)+month+ApplicationManager.mainActivity.getResources().getString(R.string.dateSeparator);
		} else if (dateValue.getMonth()==null){
			formattedDateValue = year+ApplicationManager.mainActivity.getResources().getString(R.string.dateSeparator)+ApplicationManager.mainActivity.getResources().getString(R.string.dateSeparator)+day;		    							
		} else if (dateValue.getDay()==null){
			formattedDateValue = year+ApplicationManager.mainActivity.getResources().getString(R.string.dateSeparator)+month+ApplicationManager.mainActivity.getResources().getString(R.string.dateSeparator);
		} else if (dateValue.getYear()==null){
			formattedDateValue = ApplicationManager.mainActivity.getResources().getString(R.string.dateSeparator)+month+ApplicationManager.mainActivity.getResources().getString(R.string.dateSeparator)+day;
		} else {
			formattedDateValue = year+ApplicationManager.mainActivity.getResources().getString(R.string.dateSeparator)+month+ApplicationManager.mainActivity.getResources().getString(R.string.dateSeparator)+day;
		}
		return formattedDateValue;
    }
}
