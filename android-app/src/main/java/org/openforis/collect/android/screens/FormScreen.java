package org.openforis.collect.android.screens;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.fields.BooleanField;
import org.openforis.collect.android.fields.CodeField;
import org.openforis.collect.android.fields.CoordinateField;
import org.openforis.collect.android.fields.DateField;
import org.openforis.collect.android.fields.EntityLink;
import org.openforis.collect.android.fields.Field;
import org.openforis.collect.android.fields.MemoField;
import org.openforis.collect.android.fields.NumberField;
import org.openforis.collect.android.fields.PhotoField;
import org.openforis.collect.android.fields.RangeField;
import org.openforis.collect.android.fields.SummaryList;
import org.openforis.collect.android.fields.SummaryTable;
import org.openforis.collect.android.fields.TaxonField;
import org.openforis.collect.android.fields.TextField;
import org.openforis.collect.android.fields.TimeField;
import org.openforis.collect.android.fields.UIElement;
import org.openforis.collect.android.hardware.CameraActivity;
import org.openforis.collect.android.hardware.GpsActivity;
import org.openforis.collect.android.logs.RunnableHandler;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.management.MobileCodeListManager;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.misc.ViewBacktrack;
import org.openforis.collect.android.service.ServiceFactory;
import org.openforis.idm.metamodel.AttributeDefinition;
import org.openforis.idm.metamodel.BooleanAttributeDefinition;
import org.openforis.idm.metamodel.CodeAttributeDefinition;
import org.openforis.idm.metamodel.CodeList;
import org.openforis.idm.metamodel.CodeListItem;
import org.openforis.idm.metamodel.CoordinateAttributeDefinition;
import org.openforis.idm.metamodel.DateAttributeDefinition;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.FileAttributeDefinition;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.NumberAttributeDefinition;
import org.openforis.idm.metamodel.RangeAttributeDefinition;
import org.openforis.idm.metamodel.TaxonAttributeDefinition;
import org.openforis.idm.metamodel.TextAttributeDefinition;
import org.openforis.idm.metamodel.TimeAttributeDefinition;
import org.openforis.idm.model.BooleanValue;
import org.openforis.idm.model.Code;
import org.openforis.idm.model.Coordinate;
import org.openforis.idm.model.Date;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.EntityBuilder;
import org.openforis.idm.model.File;
import org.openforis.idm.model.IntegerRange;
import org.openforis.idm.model.IntegerValue;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.NumberValue;
import org.openforis.idm.model.RealRange;
import org.openforis.idm.model.RealValue;
import org.openforis.idm.model.TaxonOccurrence;
import org.openforis.idm.model.TextValue;
import org.openforis.idm.model.Time;
import org.openforis.idm.model.Value;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author K. Waga
 *
 */
public class FormScreen extends BaseActivity implements OnClickListener {
	
	private static final String TAG = "FormScreen";

	private ScrollView sv;			
    private LinearLayout ll;
    private LinearLayout mainLayout;
	
	private Intent startingIntent;
	private String parentFormScreenId;
	private String breadcrumb;
	private String screenTitle;
	private int intentType;
	private int fieldsNo;
	private int idmlId;
	public int currInstanceNo;
	public int plotId;
	
	public Entity parentEntity;
	public Entity parentEntitySingleAttribute;
	public Entity parentEntityMultipleAttribute;
	public PhotoField currentPictureField;
	public CoordinateField currentCoordinateField;
	private String photoPath;
	private String latitude;
	private String longitude;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        	Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
    		
        	ApplicationManager.formScreenActivityList.add(this);
        	
    		this.startingIntent = getIntent();
    		this.breadcrumb = this.startingIntent.getStringExtra(getResources().getString(R.string.breadcrumb));
    		this.screenTitle = this.startingIntent.getStringExtra(getResources().getString(R.string.screenTitle));
    		this.intentType = this.startingIntent.getIntExtra(getResources().getString(R.string.intentType),-1);
    		this.idmlId = this.startingIntent.getIntExtra(getResources().getString(R.string.idmlId),-1);
    		this.currInstanceNo = this.startingIntent.getIntExtra(getResources().getString(R.string.instanceNo),-1);
    		this.parentFormScreenId = this.startingIntent.getStringExtra(getResources().getString(R.string.parentFormScreenId));;
    		this.fieldsNo = this.startingIntent.getExtras().size()-5;
    		this.plotId = this.startingIntent.getIntExtra(getResources().getString(R.string.plotId),-1);
    		
    		this.currentPictureField = null;
    		this.currentCoordinateField = null;
    		this.photoPath = null;
    		
    		this.latitude = null;
    		this.longitude = null;
    		
            this.setScreenOrientation();
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
			FormScreen.this.parentEntitySingleAttribute = FormScreen.this.findParentEntity(FormScreen.this.getFormScreenId());
			FormScreen.this.parentEntityMultipleAttribute = FormScreen.this.findParentEntity(FormScreen.this.parentFormScreenId);
			if (FormScreen.this.parentEntitySingleAttribute==null){
				FormScreen.this.parentEntitySingleAttribute = FormScreen.this.findParentEntity2(FormScreen.this.getFormScreenId());
				if (FormScreen.this.parentEntityMultipleAttribute==null
						/*||
					FormScreen.this.parentEntitySingleAttribute.equals(FormScreen.this.parentEntityMultipleAttribute)*/){
					FormScreen.this.parentEntityMultipleAttribute = FormScreen.this.findParentEntity2(FormScreen.this.parentFormScreenId);
				}
			}
			String loadedValue = "";
	
			ArrayList<String> tableColHeaders = new ArrayList<String>();
			tableColHeaders.add("Value");
			
			FormScreen.this.sv = new ScrollView(FormScreen.this);
			FormScreen.this.ll = new LinearLayout(FormScreen.this);
			FormScreen.this.ll.setOrientation(android.widget.LinearLayout.VERTICAL);
			FormScreen.this.sv.addView(ll);
			
			FormScreen.this.mainLayout = new LinearLayout(FormScreen.this);
			FormScreen.this.mainLayout.setOrientation(android.widget.LinearLayout.VERTICAL);		
			
			if (!FormScreen.this.breadcrumb.equals("")){				
				TextView breadcrumb = new TextView(FormScreen.this);
				if (FormScreen.this.intentType != getResources().getInteger(R.integer.singleEntityIntent)){
					if (FormScreen.this.intentType == getResources().getInteger(R.integer.multipleEntityIntent)){
						breadcrumb.setText(FormScreen.this.breadcrumb.substring(0, FormScreen.this.breadcrumb.lastIndexOf(" "))+" "+(FormScreen.this.currInstanceNo+1));	
					} else{
						breadcrumb.setText(FormScreen.this.breadcrumb+" "+(FormScreen.this.currInstanceNo+1));	
					}
				}
				else
					breadcrumb.setText(FormScreen.this.breadcrumb);
				int pixels = (int) (getResources().getInteger(R.integer.breadcrumbFontSize) * ApplicationManager.dpiScale + 0.5f);
	    		breadcrumb.setTextSize(pixels/*getResources().getInteger(R.integer.breadcrumbFontSize)*/);
	    		breadcrumb.setSingleLine();
	    		HorizontalScrollView scroller = new HorizontalScrollView(FormScreen.this);
	    		scroller.addView(breadcrumb);
	    		FormScreen.this.mainLayout.addView(scroller);
	    		FormScreen.this.mainLayout.addView(ApplicationManager.getDividerLine(this));
	    		
	    		TextView screenTitle = new TextView(FormScreen.this);
	    		screenTitle.setText(FormScreen.this.screenTitle);
	    		screenTitle.setOnLongClickListener(new OnLongClickListener() {
	    	        @Override
	    	        public boolean onLongClick(View v) {
	    	        	String descr = ApplicationManager.getNodeDefinition(FormScreen.this.idmlId).getDescription(ApplicationManager.selectedLanguage);
	    	        	if (descr==null){
	    	        		descr="";
	    	        	}
	    	        	ToastMessage.displayToastMessage(FormScreen.this.getApplicationContext(), FormScreen.this.screenTitle+descr, Toast.LENGTH_LONG);
	    	            return true;
	    	        }
	    	    });
				pixels = (int) (getResources().getInteger(R.integer.screenTitleFontSize) * ApplicationManager.dpiScale + 0.5f);
	    		screenTitle.setTextSize(pixels);
	    		FormScreen.this.mainLayout.addView(screenTitle);
	    		FormScreen.this.mainLayout.addView(ApplicationManager.getDividerLine(this));
			}
			
			if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
				FormScreen.this.mainLayout.addView(arrangeButtonsInLine(new Button(FormScreen.this),getResources().getString(R.string.previousInstanceButton),new Button(FormScreen.this),getResources().getString(R.string.nextInstanceButton), new Button(this), getResources().getString(R.string.addInstanceButton), new Button(FormScreen.this), getResources().getString(R.string.deleteInstanceButton), FormScreen.this, false));
				FormScreen.this.mainLayout.addView(ApplicationManager.getDividerLine(this));
			} else if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleEntityIntent)){ 				
				if (ApplicationManager.currentRecord.getRootEntity().getId()!=FormScreen.this.idmlId){
					FormScreen.this.mainLayout.addView(arrangeButtonsInLine(new Button(FormScreen.this),getResources().getString(R.string.previousInstanceButton),new Button(FormScreen.this),getResources().getString(R.string.nextInstanceButton), new Button(this), getResources().getString(R.string.addInstanceButton), new Button(FormScreen.this), getResources().getString(R.string.deleteInstanceButton), FormScreen.this, true));
					FormScreen.this.mainLayout.addView(ApplicationManager.getDividerLine(this));
				}
			}
			
			for (int i=0;i<FormScreen.this.fieldsNo;i++){
				NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(FormScreen.this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+i, -1));
				if (nodeDef instanceof EntityDefinition){
					addEntityDefinitionNode(nodeDef);
				}else {					
					if (nodeDef instanceof TextAttributeDefinition){
						addTextNode(nodeDef, tableColHeaders, true);
	    			} else if (nodeDef instanceof NumberAttributeDefinition){
	    				addNumberNode(nodeDef, tableColHeaders, true, true);
	    			} else if (nodeDef instanceof BooleanAttributeDefinition){
	    				addBooleanNode(nodeDef, tableColHeaders);
	    				/*loadedValue = "";
	    				if (!nodeDef.isMultiple()){
	    					Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					BooleanValue boolValue = (BooleanValue)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	        					if (boolValue!=null){
	        						if (boolValue.getValue()!=null)
	        							loadedValue = boolValue.getValue().toString();
	        					}
		    				}
	        				BooleanField boolField = new BooleanField(FormScreen.this, nodeDef, false, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
	        				boolField.setOnClickListener(FormScreen.this);
	        				boolField.setId(nodeDef.getId());
	        				if (loadedValue.equals("")){
	        					boolField.setValue(0, null, FormScreen.this.getFormScreenId(),false);	
	        				} else {
	        					boolField.setValue(0, Boolean.valueOf(loadedValue), FormScreen.this.getFormScreenId(),false);	
	        				}	        				
	        				ApplicationManager.putUIElement(boolField.getId(), boolField);
	        				FormScreen.this.ll.addView(boolField);
	    				} else if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo);
	    					if (foundNode!=null){
	    						BooleanValue boolValue = (BooleanValue)FormScreen.this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), FormScreen.this.currInstanceNo);
		    					if (boolValue!=null){
	        						if (boolValue.getValue()!=null)
	        							loadedValue = boolValue.getValue().toString();
	        					}
		    				}
	    					BooleanField boolField = new BooleanField(FormScreen.this, nodeDef, false, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
	    					boolField.setOnClickListener(FormScreen.this);
	    					boolField.setId(nodeDef.getId());
	    					if (loadedValue.equals("")){
	    						boolField.setValue(FormScreen.this.currInstanceNo, null, FormScreen.this.parentFormScreenId,false);
	    					} else {
	    						boolField.setValue(FormScreen.this.currInstanceNo, Boolean.valueOf(loadedValue), FormScreen.this.parentFormScreenId,false);
	    					}
	        				ApplicationManager.putUIElement(boolField.getId(), boolField);
	        				FormScreen.this.ll.addView(boolField);
	    				} else {//multiple attribute summary    			    		
							SummaryTable summaryTableView = new SummaryTable(FormScreen.this, nodeDef, tableColHeaders, parentEntitySingleAttribute, FormScreen.this);
	    					summaryTableView.setOnClickListener(FormScreen.this);
	        				summaryTableView.setId(nodeDef.getId());
	        				FormScreen.this.ll.addView(summaryTableView);
	    				}*/
	    			} else if (nodeDef instanceof CodeAttributeDefinition){	    				
	    				loadedValue = "";
	    				//CodeAttributeDefinition codeAttrDef = (CodeAttributeDefinition)nodeDef;
	    				ArrayList<String> options = new ArrayList<String>();
	    				ArrayList<String> codes = new ArrayList<String>();
	    				options.add("");
	    				codes.add("null");
	    				if (!nodeDef.isMultiple()){
	    					Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					Code codeValue = (Code)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	        					if (codeValue!=null){
	        						loadedValue = codeValue.getCode();
	        					}
		    				}
	        				CodeField codeField = new CodeField(FormScreen.this, nodeDef, codes, options, null, FormScreen.this.getFormScreenId());
	        				codeField.setOnClickListener(FormScreen.this);
	        				codeField.setId(nodeDef.getId());
	        				codeField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
	        				ApplicationManager.putUIElement(codeField.getId(), codeField);
	        				FormScreen.this.ll.addView(codeField);
	    				} else if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo);
	    					if (foundNode!=null){
		    					Code codeValue = (Code)FormScreen.this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), FormScreen.this.currInstanceNo);
	        					if (codeValue!=null){
	        						loadedValue = codeValue.getCode();
	        					}
		    				}
	        				CodeField codeField = new CodeField(FormScreen.this, nodeDef, codes, options, null, FormScreen.this.parentFormScreenId);
	        				codeField.setOnClickListener(FormScreen.this);
	        				codeField.setId(nodeDef.getId());
	        				codeField.setValue(FormScreen.this.currInstanceNo, loadedValue, FormScreen.this.parentFormScreenId,false);
	        				ApplicationManager.putUIElement(codeField.getId(), codeField);
	        				FormScreen.this.ll.addView(codeField);
	    				} else {//multiple attribute summary    			    		
							SummaryTable summaryTableView = new SummaryTable(FormScreen.this, nodeDef, tableColHeaders, parentEntitySingleAttribute, FormScreen.this);
	    					summaryTableView.setOnClickListener(FormScreen.this);
	        				summaryTableView.setId(nodeDef.getId());
	        				FormScreen.this.ll.addView(summaryTableView);
	    				}
	    				
	    			} else if (nodeDef instanceof CoordinateAttributeDefinition){
	    				String loadedValueLon = "";
	    				String loadedValueLat = "";
	    				String loadedSrsId = "";
	    				if (!nodeDef.isMultiple()){
	        				final CoordinateField coordField= new CoordinateField(FormScreen.this, nodeDef);
	        				coordField.setId(nodeDef.getId());
	        				if (FormScreen.this.currentCoordinateField!=null && FormScreen.this.currentCoordinateField.getLabelText().equals(coordField.getLabelText())){
	        					if (FormScreen.this.longitude==null)
	        						FormScreen.this.longitude = "";
	        					if (FormScreen.this.latitude==null)
	        						FormScreen.this.latitude = "";
	        					String srsId = null;
	        					if (FormScreen.this.currentCoordinateField.srs!=null){						
	        						srsId = FormScreen.this.currentCoordinateField.srs.getId();
	        					}
	        					coordField.setValue(0, FormScreen.this.longitude, FormScreen.this.latitude, srsId, FormScreen.this.getFormScreenId(), false);
	    		    			FormScreen.this.currentCoordinateField = null;
	    		    			FormScreen.this.longitude = null;
	    		    			FormScreen.this.latitude = null;
	    		    		}
	    					Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					Coordinate coordValue = (Coordinate)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    					if (coordValue!=null){
		    						if (coordValue.getX()!=null)
		    							loadedValueLon = coordValue.getX().toString();
		    						if (coordValue.getY()!=null)
		    							loadedValueLat = coordValue.getY().toString();
		    						if (coordValue.getSrsId()!=null)
		    							loadedSrsId = coordValue.getSrsId().toString();
		    					}	    				
		    				}
	        				coordField.setOnClickListener(FormScreen.this);
	        				coordField.setId(nodeDef.getId());
	        				coordField.setValue(0, loadedValueLon, loadedValueLat, loadedSrsId, FormScreen.this.getFormScreenId(),false);
	        				ApplicationManager.putUIElement(coordField.getId(), coordField);
	        				FormScreen.this.ll.addView(coordField);
	    				} else if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					final CoordinateField coordField= new CoordinateField(FormScreen.this, nodeDef);
	    					coordField.setId(nodeDef.getId());
	        				if (FormScreen.this.currentCoordinateField!=null && FormScreen.this.currentCoordinateField.getLabelText().equals(coordField.getLabelText())){
	        					if (FormScreen.this.longitude==null)
	        						FormScreen.this.longitude = "";
	        					if (FormScreen.this.latitude==null)
	        						FormScreen.this.latitude = "";
	        					String srsId = null;
	        					if (FormScreen.this.currentCoordinateField.srs!=null){						
	        						srsId = FormScreen.this.currentCoordinateField.srs.getId();
	        					}
	        					coordField.setValue(FormScreen.this.currInstanceNo, FormScreen.this.longitude, FormScreen.this.latitude, srsId, FormScreen.this.parentFormScreenId,false);
	    		    			FormScreen.this.currentCoordinateField = null;
	    		    			FormScreen.this.longitude = null;
	    		    			FormScreen.this.latitude = null;
	    		    		}
	    					Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo);
		    				if (foundNode!=null){
		    					Coordinate coordValue = (Coordinate)FormScreen.this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), FormScreen.this.currInstanceNo);
		    					if (coordValue!=null){
		    						if (coordValue.getX()!=null)
		    							loadedValueLon = coordValue.getX().toString();
		    						if (coordValue.getY()!=null)
		    							loadedValueLat = coordValue.getY().toString();
		    						if (coordValue.getSrsId()!=null)
		    							loadedSrsId = coordValue.getSrsId().toString();
		    					}   				
		    				}
	        				//coordField= new CoordinateField(FormScreen.this, nodeDef);
	        				coordField.setOnClickListener(FormScreen.this);
	        				coordField.setId(nodeDef.getId());
	        				coordField.setValue(FormScreen.this.currInstanceNo, loadedValueLon, loadedValueLat, loadedSrsId, FormScreen.this.parentFormScreenId,false);
	        				ApplicationManager.putUIElement(coordField.getId(), coordField);
	        				FormScreen.this.ll.addView(coordField);
	    				} else {//multiple attribute summary    			    		
							SummaryTable summaryTableView = new SummaryTable(FormScreen.this, nodeDef, tableColHeaders, parentEntitySingleAttribute, FormScreen.this);
	    					summaryTableView.setOnClickListener(FormScreen.this);
	        				summaryTableView.setId(nodeDef.getId());
	        				FormScreen.this.ll.addView(summaryTableView);
	    				}
	    			} else if (nodeDef instanceof RangeAttributeDefinition){
	    				loadedValue = "";
	    				if (!nodeDef.isMultiple()){
	    					Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					RangeAttributeDefinition rangeAttrDef = (RangeAttributeDefinition)nodeDef;
		    					if (rangeAttrDef.isReal()){
		    						RealRange rangeValue = (RealRange)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    						if (rangeValue!=null){
			    						if (rangeValue.getFrom()==null && rangeValue.getTo()==null){
			    							loadedValue = "";
			    						} else if (rangeValue.getFrom()==null){
			    							loadedValue = getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
			    						} else if (rangeValue.getTo()==null){
			    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator);
			    						} else {
			    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
			    						}		    						
			    					}	
		    					} else {
		    						IntegerRange rangeValue = (IntegerRange)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    						if (rangeValue!=null){
			    						if (rangeValue.getFrom()==null && rangeValue.getTo()==null){
			    							loadedValue = "";
			    						} else if (rangeValue.getFrom()==null){
			    							loadedValue = getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
			    						} else if (rangeValue.getTo()==null){
			    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator);
			    						} else {
			    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
			    						}		    						
			    					}	
		    					}		    							
		    				}
	        				final RangeField rangeField= new RangeField(FormScreen.this, nodeDef);
	        				rangeField.setOnClickListener(FormScreen.this);
	        				rangeField.setId(nodeDef.getId());
	        				rangeField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
	        				rangeField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {        			            
	        			        	rangeField.setValue(0, s.toString(),  FormScreen.this.getFormScreenId(),true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(rangeField.getId(), rangeField);
	        				FormScreen.this.ll.addView(rangeField);
	    				} else if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo);
		    				if (foundNode!=null){
		    					RangeAttributeDefinition rangeAttrDef = (RangeAttributeDefinition)nodeDef;
		    					if (rangeAttrDef.isReal()){
		    						RealRange rangeValue = (RealRange)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    						if (rangeValue!=null){
			    						if (rangeValue.getFrom()==null && rangeValue.getTo()==null){
			    							loadedValue = "";
			    						} else if (rangeValue.getFrom()==null){
			    							loadedValue = getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
			    						} else if (rangeValue.getTo()==null){
			    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator);
			    						} else {
			    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
			    						}		    						
			    					}	
		    					} else {
		    						IntegerRange rangeValue = (IntegerRange)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    						if (rangeValue!=null){
			    						if (rangeValue.getFrom()==null && rangeValue.getTo()==null){
			    							loadedValue = "";
			    						} else if (rangeValue.getFrom()==null){
			    							loadedValue = getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
			    						} else if (rangeValue.getTo()==null){
			    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator);
			    						} else {
			    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
			    						}		    						
			    					}	
		    					}  				
		    				}
	        				final RangeField rangeField= new RangeField(FormScreen.this, nodeDef);
	        				rangeField.setOnClickListener(FormScreen.this);
	        				rangeField.setId(nodeDef.getId());
	        				rangeField.setValue(FormScreen.this.currInstanceNo, loadedValue, FormScreen.this.parentFormScreenId,false);
	        				rangeField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {        			            
	        			        	rangeField.setValue(FormScreen.this.currInstanceNo, s.toString(), FormScreen.this.parentFormScreenId,true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(rangeField.getId(), rangeField);
	        				FormScreen.this.ll.addView(rangeField);
	    				} else {//multiple attribute summary    			    		
							SummaryTable summaryTableView = new SummaryTable(FormScreen.this, nodeDef, tableColHeaders, parentEntitySingleAttribute, FormScreen.this);
	    					summaryTableView.setOnClickListener(FormScreen.this);
	        				summaryTableView.setId(nodeDef.getId());
	        				FormScreen.this.ll.addView(summaryTableView);
	    				}
	    			} else if (nodeDef instanceof DateAttributeDefinition){
	    				loadedValue = "";
	    				if (!nodeDef.isMultiple()){
	    					Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					Date dateValue = (Date)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    					if (dateValue!=null){
		    						loadedValue = DateField.formatDate(dateValue);
		    					}
		    				}
		    				if (loadedValue.equals("")){
		    					//TODO: get date from device
		    				}
	
	        				final DateField dateField= new DateField(FormScreen.this, nodeDef);
	        				dateField.setOnClickListener(FormScreen.this);
	        				dateField.setId(nodeDef.getId());
	        				dateField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
	        				dateField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {        			            
	        			        	dateField.setValue(0, s.toString(), FormScreen.this.getFormScreenId(),true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(dateField.getId(), dateField);
	        				FormScreen.this.ll.addView(dateField);
	    				} else if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo);
		    				if (foundNode!=null){
		    					Date dateValue = (Date)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    					if (dateValue!=null){
		    						loadedValue = DateField.formatDate(dateValue);
		    					}
		    				}
	        				final DateField dateField= new DateField(FormScreen.this, nodeDef);
	        				dateField.setOnClickListener(FormScreen.this);
	        				dateField.setId(nodeDef.getId());
	        				dateField.setValue(FormScreen.this.currInstanceNo, loadedValue, FormScreen.this.parentFormScreenId,false);
	        				dateField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {        			            
	        			        	dateField.setValue(FormScreen.this.currInstanceNo, s.toString(), FormScreen.this.parentFormScreenId,true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(dateField.getId(), dateField);
	        				FormScreen.this.ll.addView(dateField);
	    				}
	    			} else if (nodeDef instanceof TimeAttributeDefinition){
	    				loadedValue = "";
	    				if (!nodeDef.isMultiple()){
	    					Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					Time timeValue = (Time)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    					if (timeValue!=null){
		    						String hour = "";
		    						if (timeValue.getHour()!=null){
		    							hour = timeValue.getHour().toString();
		    							if (Integer.valueOf(hour)<10){
		    								hour = "0"+hour;
			    						}		
		    						}
		    						String minute = "";
		    						if (timeValue.getMinute()!=null){
		    							minute = timeValue.getMinute().toString();
		    							if (Integer.valueOf(minute)<10){
		    								minute = "0"+minute;
			    						}		
		    						}
		    						if (timeValue.getHour()==null && timeValue.getMinute()==null){
		    							loadedValue = "";
		    						} else if (timeValue.getHour()==null){
		    							loadedValue = getResources().getString(R.string.timeSeparator)+minute;
		    						} else if (timeValue.getMinute()==null){
		    							loadedValue = hour+getResources().getString(R.string.timeSeparator);
		    						} else {
		    							loadedValue = hour+getResources().getString(R.string.timeSeparator)+minute;
		    						}		    						
		    					}	    				
		    				}
		    				if (loadedValue.equals("")){
		    					//TODO: get time from device
		    				}
		    				
	        				final TimeField timeField= new TimeField(FormScreen.this, nodeDef);
	        				timeField.setOnClickListener(FormScreen.this);
	        				timeField.setId(nodeDef.getId()); 
	        				timeField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
	        				timeField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {
	        			        	timeField.setValue(0, s.toString(), FormScreen.this.getFormScreenId(),true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(timeField.getId(), timeField);
	        				FormScreen.this.ll.addView(timeField);
	    				} else if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo);
		    				if (foundNode!=null){
		    					Time timeValue = (Time)FormScreen.this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), FormScreen.this.currInstanceNo);
		    					if (timeValue!=null){
		    						String hour = "";
		    						if (timeValue.getHour()!=null){
		    							hour = timeValue.getHour().toString();
		    							if (Integer.valueOf(hour)<10){
		    								hour = "0"+hour;
			    						}		
		    						}
		    						String minute = "";
		    						if (timeValue.getMinute()!=null){
		    							minute = timeValue.getMinute().toString();
		    							if (Integer.valueOf(minute)<10){
		    								minute = "0"+minute;
			    						}		
		    						}
		    						if (timeValue.getHour()==null && timeValue.getMinute()==null){
		    							loadedValue = "";
		    						} else if (timeValue.getHour()==null){
		    							loadedValue = getResources().getString(R.string.timeSeparator)+minute;
		    						} else if (timeValue.getMinute()==null){
		    							loadedValue = hour+getResources().getString(R.string.timeSeparator);
		    						} else {
		    							loadedValue = hour+getResources().getString(R.string.timeSeparator)+minute;
		    						}		    						
		    					}		   				
		    				}
	        				final TimeField timeField= new TimeField(FormScreen.this, nodeDef);
	        				timeField.setOnClickListener(FormScreen.this);
	        				timeField.setId(nodeDef.getId());
	        				timeField.setValue(FormScreen.this.currInstanceNo, loadedValue, FormScreen.this.parentFormScreenId,false);
	        				timeField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {        			            
	        			        	timeField.setValue(FormScreen.this.currInstanceNo, s.toString(), FormScreen.this.parentFormScreenId,true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(timeField.getId(), timeField);
	        				FormScreen.this.ll.addView(timeField);
	    				} else {//multiple attribute summary    			    		
							SummaryTable summaryTableView = new SummaryTable(FormScreen.this, nodeDef, tableColHeaders, parentEntitySingleAttribute, FormScreen.this);
	    					summaryTableView.setOnClickListener(FormScreen.this);
	        				summaryTableView.setId(nodeDef.getId());
	        				FormScreen.this.ll.addView(summaryTableView);
	    				}
	    			} else if (nodeDef instanceof TaxonAttributeDefinition){
	    				ArrayList<String> options = new ArrayList<String>();
	    				ArrayList<String> codes = new ArrayList<String>();
	    				options.add("");
	    				codes.add("null");
	    				
	    				String code = "";
	    				String sciName = "";
	    				String vernName = "";
	    				String vernLang = "";
	    				String langVariant = "";
	    				if (!nodeDef.isMultiple()){
	    					Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					TaxonOccurrence taxonValue = (TaxonOccurrence)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    					if (taxonValue!=null){
		    						code = taxonValue.getCode();
		    	    				sciName = taxonValue.getScientificName();
		    	    				vernName = taxonValue.getVernacularName();
		    	    				vernLang = taxonValue.getLanguageCode();
		    	    				langVariant = taxonValue.getLanguageVariety();
		    					}	    				
		    				}
	        				final TaxonField taxonField= new TaxonField(FormScreen.this, nodeDef, codes, options, vernLang);
	        				taxonField.setOnClickListener(FormScreen.this);
	        				taxonField.setId(nodeDef.getId());
	        				taxonField.setValue(0, code, sciName, vernName, vernLang, langVariant, FormScreen.this.getFormScreenId(),false);
	        				ApplicationManager.putUIElement(taxonField.getId(), taxonField);
	        				FormScreen.this.ll.addView(taxonField);
	    				} else if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo);
		    				if (foundNode!=null){
		    					TaxonOccurrence taxonValue = (TaxonOccurrence)FormScreen.this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), FormScreen.this.currInstanceNo);
		    					if (taxonValue!=null){
		    						code = taxonValue.getCode();
		    	    				sciName = taxonValue.getScientificName();
		    	    				vernName = taxonValue.getVernacularName();
		    	    				vernLang = taxonValue.getLanguageCode();
		    	    				langVariant = taxonValue.getLanguageVariety();	    						
		    					}	   				
		    				}
		    				final TaxonField taxonField= new TaxonField(FormScreen.this, nodeDef, codes, options, vernLang);
		    				taxonField.setOnClickListener(FormScreen.this);
		    				taxonField.setId(nodeDef.getId());
		    				taxonField.setValue(FormScreen.this.currInstanceNo, code, sciName, vernName, vernLang, langVariant, FormScreen.this.parentFormScreenId,false);
	        				ApplicationManager.putUIElement(taxonField.getId(), taxonField);
	        				FormScreen.this.ll.addView(taxonField);
	    				} else {//multiple attribute summary    			    		
							SummaryTable summaryTableView = new SummaryTable(FormScreen.this, nodeDef, tableColHeaders, parentEntitySingleAttribute, FormScreen.this);
	    					summaryTableView.setOnClickListener(FormScreen.this);
	        				summaryTableView.setId(nodeDef.getId());
	        				FormScreen.this.ll.addView(summaryTableView);
	    				}
					} else if (nodeDef instanceof FileAttributeDefinition){
						FileAttributeDefinition fileDef = (FileAttributeDefinition)nodeDef;
						List<String> extensionsList = fileDef.getExtensions();
						
						if (extensionsList.contains("jpg")||extensionsList.contains("jpeg")){
							loadedValue = "";
		    				if (!nodeDef.isMultiple()){
		        				final PhotoField photoField= new PhotoField(FormScreen.this, nodeDef);		 
		        				if (FormScreen.this.currentPictureField!=null && FormScreen.this.currentPictureField.getLabelText().equals(photoField.getLabelText())){
		    		    			photoField.setValue(0, FormScreen.this.photoPath, FormScreen.this.getFormScreenId(),false);
		    		    			FormScreen.this.currentPictureField = null;
		    		    			FormScreen.this.photoPath = null;
		    		    		}
		        				Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
			    				if (foundNode!=null){
			    					File fileValue = (File)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
			    					if (fileValue!=null){
			    						loadedValue = fileValue.getFilename();
			    					}
			    				}
		        				photoField.setOnClickListener(FormScreen.this);
		        				photoField.setId(nodeDef.getId());
		        				photoField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
		        				ApplicationManager.putUIElement(photoField.getId(), photoField);
		        				FormScreen.this.ll.addView(photoField);
		    				} else if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
		        				final PhotoField photoField= new PhotoField(FormScreen.this, nodeDef);
		        				if (FormScreen.this.currentPictureField!=null && FormScreen.this.currentPictureField.getLabelText().equals(photoField.getLabelText())){
		        					photoField.setValue(FormScreen.this.currInstanceNo, FormScreen.this.photoPath, FormScreen.this.parentFormScreenId,false);
		    		    			FormScreen.this.currentPictureField = null;
		    		    			FormScreen.this.photoPath = null;
		    		    		}
		        				Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo);
			    				if (foundNode!=null){
			    					File fileValue = (File)FormScreen.this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), FormScreen.this.currInstanceNo);
			    					if (fileValue!=null){
			    						loadedValue = fileValue.getFilename();
			    					}
			    				}
		        				photoField.setOnClickListener(FormScreen.this);
		        				photoField.setId(nodeDef.getId());
		        				photoField.setValue(FormScreen.this.currInstanceNo, loadedValue, FormScreen.this.parentFormScreenId,false);
		        				ApplicationManager.putUIElement(photoField.getId(), photoField);
		        				FormScreen.this.ll.addView(photoField);
		    				} else {//multiple attribute summary    			    		
	    						SummaryTable summaryTableView = new SummaryTable(FormScreen.this, nodeDef, tableColHeaders, parentEntitySingleAttribute, FormScreen.this);
	        					summaryTableView.setOnClickListener(FormScreen.this);
	            				summaryTableView.setId(nodeDef.getId());
	            				FormScreen.this.ll.addView(summaryTableView);
	        				}
						}
					}
				}    				
			}
			FormScreen.this.mainLayout.addView(sv);	
			setContentView(FormScreen.this.mainLayout);
				
			int backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);		
			changeBackgroundColor(backgroundColor);
			ApplicationManager.pd.dismiss();
	    	sv.post(new Runnable() {
	    	    @Override
	    	    public void run() {
	    	    	if (ApplicationManager.selectedViewsBacktrackList.size()>0){
	    	    		if (FormScreen.this.getFormScreenId().equals(ApplicationManager.selectedViewsBacktrackList.get(ApplicationManager.selectedViewsBacktrackList.size()-1).getFormScreenId())
	    	    				||
	    	    			ApplicationManager.selectedViewsBacktrackList.get(ApplicationManager.selectedViewsBacktrackList.size()-1).getFormScreenId()==null){
	    	    			sv.scrollTo(0, ApplicationManager.selectedViewsBacktrackList.remove(ApplicationManager.selectedViewsBacktrackList.size()-1).getView().getTop());
	    	    		}	  
	    	    	} 
	    	    } 	
	    	});
	    	if (this.plotId>-1){
	    		boolean isFound = false;
	    		//opening specific plot from the record
	    		EntityLink plotEntityLink = null;
	    		int childNo = FormScreen.this.ll.getChildCount();
	    		for (int i=0;i<childNo;i++){
	    			View view = FormScreen.this.ll.getChildAt(i);
	    			if (view instanceof EntityLink){
	    				plotEntityLink = (EntityLink)view;
	    				String name = plotEntityLink.nodeDefinition.getName();
	    				if (name.equals("plot_details")){
	    					isFound = true;
	    					break;
	    				}
	    			}
	    		}
	    		if (isFound){
	    			ApplicationManager.pd = ProgressDialog.show(this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.loadingMultipleEntitiesList));
	    			this.startActivity(this.prepareIntentForEntityInstancesList(plotEntityLink,plotId));
	    		}
	    		this.plotId=-1;
	    	}
		} catch (Exception e){
			RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onResume",
					Environment.getExternalStorageDirectory().toString()
					+getResources().getString(R.string.logs_folder)
					+getResources().getString(R.string.logs_file_name)
					+System.currentTimeMillis()
					+getResources().getString(R.string.log_file_extension));
		}
		ApplicationManager.isBackFromTaxonSearch = false;
	}
    
    @Override
    public void onPause(){    
		Log.i(getResources().getString(R.string.app_name),TAG+":onPause");
		super.onPause();
    }
	
	private int calcNoOfCharsFitInOneLine(){
		DisplayMetrics metrics = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(metrics);
    	return metrics.widthPixels/10;    	
	}

	@Override
	public void onClick(View arg0) {
		if (arg0 instanceof Button){
			Button btn = (Button)arg0;
			if (btn.getId()==getResources().getInteger(R.integer.leftButtonMultipleAttribute)){
				ApplicationManager.pd = ProgressDialog.show(this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.scrollingToOtherMultipleAttribute));
				refreshMultipleAttributeScreen(0);
				ApplicationManager.pd.dismiss();
			} else if (btn.getId()==getResources().getInteger(R.integer.rightButtonMultipleAttribute)){
				ApplicationManager.pd = ProgressDialog.show(this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.scrollingToOtherMultipleAttribute));
				refreshMultipleAttributeScreen(1);
				ApplicationManager.pd.dismiss();
			} else if (btn.getId()==getResources().getInteger(R.integer.leftButtonMultipleEntity)){
				ApplicationManager.pd = ProgressDialog.show(this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.scrollingToOtherEntity));
				refreshEntityScreen(0);
				ApplicationManager.pd.dismiss();
			} else if (btn.getId()==getResources().getInteger(R.integer.rightButtonMultipleEntity)){
				ApplicationManager.pd = ProgressDialog.show(this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.scrollingToOtherEntity));
				refreshEntityScreen(1);
				ApplicationManager.pd.dismiss();
			} else if (btn.getId()==getResources().getInteger(R.integer.deleteButtonMultipleAttribute)){
				AlertMessage.createPositiveNegativeDialog(FormScreen.this, false, getResources().getDrawable(R.drawable.warningsign),
	 					getResources().getString(R.string.deleteAttributeTitle), getResources().getString(R.string.deleteAttribute),
	 					getResources().getString(R.string.yes), getResources().getString(R.string.no),
	 		    		new DialogInterface.OnClickListener() {
	 						@Override
	 						public void onClick(DialogInterface dialog, int which) {
	 							NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(FormScreen.this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+"0", -1));
	 							Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo);
	 							if (foundNode!=null){
	 								ServiceFactory.getMobileRecordManager().deleteNode(foundNode);
	 								refreshMultipleAttributeScreen(2);
	 								Toast.makeText(FormScreen.this, getResources().getString(R.string.attributeDeletedToast), Toast.LENGTH_SHORT).show();
	 								FormScreen.this.onResume();
	 							}
	 						}
	 					},
	 		    		new DialogInterface.OnClickListener() {
	 						@Override
	 						public void onClick(DialogInterface dialog, int which) {

	 						}
	 					},
	 					null).show();			
			} else if (btn.getId()==getResources().getInteger(R.integer.deleteButtonMultipleEntity)){
				AlertMessage.createPositiveNegativeDialog(FormScreen.this, false, getResources().getDrawable(R.drawable.warningsign),
	 					getResources().getString(R.string.deleteEntityTitle), getResources().getString(R.string.deleteEntity),
	 					getResources().getString(R.string.yes), getResources().getString(R.string.no),
	 		    		new DialogInterface.OnClickListener() {
	 						@Override
	 						public void onClick(DialogInterface dialog, int which) {
 								NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(FormScreen.this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+"0", -1));
	 							NodeDefinition parentNodeDefinition = nodeDef.getParentDefinition();
	 							Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.getParent().get(parentNodeDefinition.getName(), FormScreen.this.currInstanceNo);
	 							
	 							if (foundNode!=null){
	 								ServiceFactory.getMobileRecordManager().deleteNode(foundNode);	 	
	 								Entity tempEntity = findParentEntity2(FormScreen.this.getFormScreenId());		 								
	 								Node<?> tempNode = tempEntity.get(FormScreen.this.parentEntitySingleAttribute.getName(), FormScreen.this.currInstanceNo);
	 								if ((tempNode==null)&&(FormScreen.this.currInstanceNo==0))
	 									EntityBuilder.addEntity(tempEntity, parentNodeDefinition.getName());
	 							}
 								refreshEntityScreen(2);
 								Toast.makeText(FormScreen.this, getResources().getString(R.string.entityDeletedToast), Toast.LENGTH_SHORT).show();
 								if (FormScreen.this.currInstanceNo>0){
 									FormScreen.this.currInstanceNo--;	
 								}	 								
 								FormScreen.this.onResume();
	 						}
	 					},
	 		    		new DialogInterface.OnClickListener() {
	 						@Override
	 						public void onClick(DialogInterface dialog, int which) {

	 						}
	 					},
	 					null).show();				
			} else if (btn.getId()==getResources().getInteger(R.integer.addButtonMultipleAttribute)){
				refreshMultipleAttributeScreen(3);
			} else if (btn.getId()==getResources().getInteger(R.integer.addButtonMultipleEntity)){
				refreshEntityScreen(3);
			}
		} else if (arg0 instanceof TextView){
			TextView tv = (TextView)arg0;
			Object parentView = arg0.getParent().getParent().getParent().getParent();
			if (parentView instanceof SummaryList){
				SummaryList temp = (SummaryList)arg0.getParent().getParent().getParent().getParent();
				ViewBacktrack viewBacktrack = new ViewBacktrack(temp,FormScreen.this.getFormScreenId());
				ApplicationManager.selectedViewsBacktrackList.add(viewBacktrack);
				//ApplicationManager.isToBeScrolled = false;
				this.startActivity(this.prepareIntentForNewScreen(temp));				
			} else if (parentView instanceof SummaryTable){
				SummaryTable temp = (SummaryTable)parentView;
				ViewBacktrack viewBacktrack = new ViewBacktrack(temp,FormScreen.this.getFormScreenId());
				ApplicationManager.selectedViewsBacktrackList.add(viewBacktrack);
				this.startActivity(this.prepareIntentForMultipleField(temp, tv.getId(), temp.getValues()));
			}
			
		} else  if (arg0 instanceof EntityLink){
			ApplicationManager.pd = ProgressDialog.show(this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.loadingMultipleEntitiesList));
			this.startActivity(this.prepareIntentForEntityInstancesList((EntityLink)arg0,-1));	
		}
	}
	
	private Intent prepareIntentForEntityInstancesList(EntityLink entityLink, int plotId){
		Intent intent = new Intent(this,EntityInstancesScreen.class);
		EntityDefinition entityDef = entityLink.getEntityDefinition();
		if (!this.breadcrumb.equals("")){
			String title = "";
			String entityTitle = "";
			if (entityDef.isMultiple()){
				title = this.breadcrumb+getResources().getString(R.string.breadcrumbSeparator)+entityLink.getTitle();//+" "+(this.currInstanceNo+1);
				entityTitle = entityLink.getTitle();//+" "+(this.currInstanceNo+1);
			} else {
				title = this.breadcrumb+getResources().getString(R.string.breadcrumbSeparator)+entityLink.getTitle();
				entityTitle = entityLink.getTitle();
			}
			intent.putExtra(getResources().getString(R.string.breadcrumb), title);
			intent.putExtra(getResources().getString(R.string.screenTitle), entityTitle);
		} else {
			intent.putExtra(getResources().getString(R.string.breadcrumb), entityLink.getTitle());
			intent.putExtra(getResources().getString(R.string.screenTitle), entityLink.getTitle());
		}
		
		intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.multipleEntityIntent));
		intent.putExtra(getResources().getString(R.string.idmlId), entityDef.getId());
		intent.putExtra(getResources().getString(R.string.plotId), plotId);
		intent.putExtra(getResources().getString(R.string.parentFormScreenId), this.getFormScreenId());
        List<NodeDefinition> entityAttributes = entityDef./*entityLink.getEntityDefinition().*/getChildDefinitions();
        int counter = 0;
        for (NodeDefinition formField : entityAttributes){
			intent.putExtra(getResources().getString(R.string.attributeId)+counter, formField.getId());
			counter++;
        }
		return intent;
	}
	
	private Intent prepareIntentForNewScreen(SummaryList summaryList){
		Intent intent = new Intent(this,FormScreen.class);
		if (!this.breadcrumb.equals("")){
			String title = "";
			String entityTitle = "";
			if (summaryList.getEntityDefinition().isMultiple()){
				title = this.breadcrumb+getResources().getString(R.string.breadcrumbSeparator)+summaryList.getTitle()+" "+(this.currInstanceNo+1);
				entityTitle = summaryList.getTitle()/*+" "+(this.currInstanceNo+1)*/;
			} else {
				title = this.breadcrumb+getResources().getString(R.string.breadcrumbSeparator)+summaryList.getTitle();
				entityTitle = summaryList.getTitle();
			}
			intent.putExtra(getResources().getString(R.string.breadcrumb), title);
			intent.putExtra(getResources().getString(R.string.screenTitle), entityTitle);
		} else {
			intent.putExtra(getResources().getString(R.string.breadcrumb), summaryList.getTitle());
			intent.putExtra(getResources().getString(R.string.screenTitle), summaryList.getTitle());
		}
		
		if (summaryList.getEntityDefinition().isMultiple()){
			intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.multipleEntityIntent));	
		} else {
			intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.singleEntityIntent));
		}		
		intent.putExtra(getResources().getString(R.string.idmlId), summaryList.getId());
		intent.putExtra(getResources().getString(R.string.instanceNo), summaryList.getInstanceNo());
		intent.putExtra(getResources().getString(R.string.parentFormScreenId), this.getFormScreenId());
        List<NodeDefinition> entityAttributes = summaryList.getEntityDefinition().getChildDefinitions();
        int counter = 0;
        for (NodeDefinition formField : entityAttributes){
			intent.putExtra(getResources().getString(R.string.attributeId)+counter, formField.getId());
			counter++;
        }
		return intent;
	}

	private Intent prepareIntentForMultipleField(SummaryTable summaryTable, int clickedInstanceNo, List<List<String>> data){
		Intent intent = new Intent(this,FormScreen.class);
		intent.putExtra(getResources().getString(R.string.breadcrumb), this.breadcrumb+getResources().getString(R.string.breadcrumbSeparator)+summaryTable.getTitle());
		intent.putExtra(getResources().getString(R.string.screenTitle), summaryTable.getTitle());
		intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.multipleAttributeIntent));
        intent.putExtra(getResources().getString(R.string.attributeId)+"0", summaryTable.getId());
        intent.putExtra(getResources().getString(R.string.idmlId), summaryTable.getId());
        intent.putExtra(getResources().getString(R.string.instanceNo), clickedInstanceNo);
        intent.putExtra(getResources().getString(R.string.parentFormScreenId), this.getFormScreenId());
        List<List<String>> values = summaryTable.getValues();
        int numberOfInstances = values.size();
        intent.putExtra(getResources().getString(R.string.numberOfInstances), numberOfInstances);
        for (int i=0;i<numberOfInstances;i++){
        	ArrayList<String> instanceValues = (ArrayList<String>)values.get(i);
        	intent.putStringArrayListExtra(getResources().getString(R.string.instanceValues)+i,instanceValues);
        }
		return intent;
	}
	
	@Override
    public void changeBackgroundColor(int backgroundColor){
		super.changeBackgroundColor(backgroundColor);
		boolean hasBreadcrumb = !this.breadcrumb.equals("");
		if (hasBreadcrumb){
			ViewGroup scrollbarViews = (ViewGroup)this.mainLayout.getChildAt(0);
			TextView breadcrumb = (TextView)scrollbarViews.getChildAt(0);
			breadcrumb.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);	
		}
		
		boolean hasTitle = !this.screenTitle.equals("");
		if (hasTitle){
			View dividerLine = (View)this.mainLayout.getChildAt(1);
			dividerLine.setBackgroundColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
			TextView screenTitle = (TextView)this.mainLayout.getChildAt(2);
			screenTitle.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
			dividerLine = (View)this.mainLayout.getChildAt(3);
			dividerLine.setBackgroundColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		}
		
		NodeDefinition currentEntity = (NodeDefinition)ApplicationManager.getSurvey().getSchema().getDefinitionById(FormScreen.this.idmlId);
		if (currentEntity.isMultiple()&&!currentEntity.equals(ApplicationManager.getSurvey().getSchema().getDefinitionById(ApplicationManager.currRootEntityId))){
			View dividerLine = (View)this.mainLayout.getChildAt(5);
			dividerLine.setBackgroundColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);	
		}
		
		int viewsNo = this.ll.getChildCount();
		int start = 0;
		for (int i=start;i<viewsNo;i++){
			View tempView = this.ll.getChildAt(i);
			if (tempView instanceof Field){
				Field field = (Field)tempView;
				field.setLabelTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
				if (tempView instanceof BooleanField){
					BooleanField tempBooleanField = (BooleanField)tempView;
					tempBooleanField.setChoiceLabelsTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
				} else if (tempView instanceof TaxonField){
					TaxonField tempTaxonField = (TaxonField)tempView;
					tempTaxonField.setFieldsLabelsTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
				} else if (tempView instanceof CoordinateField){
					CoordinateField tempCoordinateField = (CoordinateField)tempView;
					tempCoordinateField.setCoordinateLabelTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
				}
			}
			else if (tempView instanceof UIElement){
				if (tempView instanceof SummaryList){
					SummaryList tempSummaryList = (SummaryList)tempView;
					tempSummaryList.changeBackgroundColor(backgroundColor);
				} else if (tempView instanceof SummaryTable){
					SummaryTable tempSummaryTable = (SummaryTable)tempView;
					tempSummaryTable.changeBackgroundColor(backgroundColor);
				} else if (tempView instanceof EntityLink){
					EntityLink tempEntityLink = (EntityLink)tempView;
					tempEntityLink.changeBackgroundColor(backgroundColor);
				}
			}  else if (tempView instanceof RelativeLayout){
				RelativeLayout rLayout = (RelativeLayout)tempView;
				Button leftBtn = (Button)rLayout.getChildAt(0);
				leftBtn.setBackgroundResource((backgroundColor!=Color.WHITE)?R.drawable.arrow_left_black:R.drawable.arrow_left_white);
				LinearLayout lLayout = (LinearLayout)rLayout.getChildAt(1);				
				Button addBtn = (Button)lLayout.getChildAt(0);
				addBtn.setBackgroundResource((backgroundColor!=Color.WHITE)?R.drawable.add_new_black:R.drawable.add_new_white);
				Button deleteBtn = (Button)lLayout.getChildAt(1);
				deleteBtn.setBackgroundResource((backgroundColor!=Color.WHITE)?R.drawable.recycle_bin_black:R.drawable.recycle_bin_white);
				Button rightBtn = (Button)rLayout.getChildAt(2);
				rightBtn.setBackgroundResource((backgroundColor!=Color.WHITE)?R.drawable.arrow_right_black:R.drawable.arrow_right_white);
			}
		}

		if (!(this.mainLayout.getChildAt(4) instanceof ScrollView)&&!(this.mainLayout.getChildAt(4)==null)){		
			RelativeLayout rLayout = (RelativeLayout)this.mainLayout.getChildAt(4);
			viewsNo = rLayout.getChildCount();
			Button leftBtn = (Button)rLayout.getChildAt(0);
			leftBtn.setBackgroundResource((backgroundColor!=Color.WHITE)?R.drawable.arrow_left_black:R.drawable.arrow_left_white);
			RelativeLayout lLayout = (RelativeLayout)rLayout.getChildAt(1);				
			Button addBtn = (Button)lLayout.getChildAt(0);
			addBtn.setBackgroundResource((backgroundColor!=Color.WHITE)?R.drawable.add_new_black:R.drawable.add_new_white);
			Button deleteBtn = (Button)lLayout.getChildAt(1);
			deleteBtn.setBackgroundResource((backgroundColor!=Color.WHITE)?R.drawable.recycle_bin_black:R.drawable.recycle_bin_white);
			Button rightBtn = (Button)rLayout.getChildAt(2);
			rightBtn.setBackgroundResource((backgroundColor!=Color.WHITE)?R.drawable.arrow_right_black:R.drawable.arrow_right_white);
		}
    }
    
    private RelativeLayout arrangeButtonsInLine(Button btnLeft, String btnLeftLabel, Button btnRight, String btnRightLabel, Button btnAdd, String btnAddLabel, Button btnDelete, String btnDeleteLabel, OnClickListener listener, boolean isForEntity){
		RelativeLayout relativeButtonsLayout = new RelativeLayout(this);
	    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
	            RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	    relativeButtonsLayout.setLayoutParams(lp);
	    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(32,32);
	    btnDelete.setLayoutParams(params);
	    btnDelete.setBackgroundResource(R.drawable.recycle_bin_white);
	    btnDelete.setId(11111);
	    params = new RelativeLayout.LayoutParams(getResources().getInteger(R.integer.addButtonWidth),getResources().getInteger(R.integer.addButtonHeight));
	    btnAdd.setLayoutParams(params);
	    btnAdd.setBackgroundResource(R.drawable.add_new_white);
	    btnAdd.setId(22222);
		
	    btnLeft.setBackgroundResource(R.drawable.arrow_left_white);
	    
	    btnRight.setBackgroundResource(R.drawable.arrow_right_white);
	    
		btnLeft.setOnClickListener(listener);
		btnRight.setOnClickListener(listener);
		btnDelete.setOnClickListener(listener);
		btnAdd.setOnClickListener(listener);
		
		RelativeLayout.LayoutParams lpBtnLeft = new RelativeLayout.LayoutParams(
	            48, 64);
		lpBtnLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT);		
		btnLeft.setLayoutParams(lpBtnLeft);
		
		relativeButtonsLayout.addView(btnLeft);
		
		RelativeLayout.LayoutParams lpBtnAdd = new RelativeLayout.LayoutParams(
	            64, 64);
		lpBtnAdd.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lpBtnAdd.addRule(RelativeLayout.LEFT_OF,btnDelete.getId());
		btnAdd.setLayoutParams(lpBtnAdd);
		
		RelativeLayout.LayoutParams lpBtnDelete = new RelativeLayout.LayoutParams(
	            64, 64);
		lpBtnDelete.addRule(RelativeLayout.RIGHT_OF,btnAdd.getId());
		lpBtnDelete.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		btnDelete.setLayoutParams(lpBtnDelete);
		
		RelativeLayout ll = new RelativeLayout(this);
		RelativeLayout.LayoutParams llparams = new RelativeLayout.LayoutParams(
				300, RelativeLayout.LayoutParams.WRAP_CONTENT);		
		llparams.addRule(RelativeLayout.CENTER_IN_PARENT);
		ll.setLayoutParams(llparams);
		ll.addView(btnAdd);
		ll.addView(btnDelete);

		relativeButtonsLayout.addView(ll);
		
		RelativeLayout.LayoutParams lpBtnRight = new RelativeLayout.LayoutParams(
	            48, 64);
		lpBtnRight.addRule(RelativeLayout.RIGHT_OF,btnLeft.getId());
		lpBtnRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		btnRight.setLayoutParams(lpBtnRight);
		relativeButtonsLayout.addView(btnRight);
		
		if (!isForEntity){
			btnLeft.setId(getResources().getInteger(R.integer.leftButtonMultipleAttribute));
			btnRight.setId(getResources().getInteger(R.integer.rightButtonMultipleAttribute));
			btnAdd.setId(getResources().getInteger(R.integer.addButtonMultipleAttribute));
			btnDelete.setId(getResources().getInteger(R.integer.deleteButtonMultipleAttribute));
		} else {
			btnLeft.setId(getResources().getInteger(R.integer.leftButtonMultipleEntity));
			btnRight.setId(getResources().getInteger(R.integer.rightButtonMultipleEntity));
			btnAdd.setId(getResources().getInteger(R.integer.addButtonMultipleEntity));
			btnDelete.setId(getResources().getInteger(R.integer.deleteButtonMultipleEntity));
		}
		btnLeft.requestLayout();
		return relativeButtonsLayout;
    }
    
    public String getFormScreenId(){    	
    	if (this.parentFormScreenId.equals("")){
    		return removeDuplicates(this.idmlId+getResources().getString(R.string.valuesSeparator1)+this.currInstanceNo);
    	} else {
    		return removeDuplicates(this.parentFormScreenId+getResources().getString(R.string.valuesSeparator2)+this.idmlId+getResources().getString(R.string.valuesSeparator1)+this.currInstanceNo);
    	}    		
    }
    
    public String removeDuplicates(String text){
        String[] tablica = text.split(";");
        for (int i=0;i<tablica.length;i++){
        	if (tablica[i]!=null){
        		String piece1 = tablica[i];
            	String firstNumber1 = tablica[i].split(",")[0];
            	for (int j=i+1;j<tablica.length;j++){
            		if (tablica[j]!=null){
            			String piece2 = tablica[j];
                		String firstNumber2 = tablica[j].split(",")[0];
                		if (piece1.equals(piece2) || firstNumber2.equals(firstNumber1)){
                			tablica[i] = null;
                		}	
            		}            	
            	}
        	}
        	
        }
        String newText = "";
        for (int i=0;i<tablica.length;i++){
        	if (tablica[i]!=null){
        		if (i==tablica.length-1){
        			newText += tablica[i];
        		} else{
        			newText += tablica[i]+";";
        		}
        	}
        		
        }
        return newText;
    }
	
	private Entity findParentEntity(String path){
		Entity parentEntity = ApplicationManager.currentRecord.getRootEntity();
		String screenPath = path;
		String[] entityPath = screenPath.split(getResources().getString(R.string.valuesSeparator2));
		try{
			for (int m=1;m<entityPath.length;m++){
				String[] instancePath = entityPath[m].split(getResources().getString(R.string.valuesSeparator1));				
				int id = Integer.valueOf(instancePath[0]);
				int instanceNo = Integer.valueOf(instancePath[1]);
				try{
					parentEntity = (Entity) parentEntity.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(id).getName(), instanceNo);	
				} catch (IllegalArgumentException e){
					parentEntity = (Entity) parentEntity.getParent().get(ApplicationManager.getSurvey().getSchema().getDefinitionById(id).getName(), instanceNo);
				}		
			}
		} catch (ClassCastException e){
			
		}
		return parentEntity;
	}
	
	private Entity findParentEntity2(String path){
		Entity parentEntity = ApplicationManager.currentRecord.getRootEntity();
		String screenPath = path;
		String[] entityPath = screenPath.split(getResources().getString(R.string.valuesSeparator2));
		try{
			for (int m=1;m<entityPath.length-1;m++){
				String[] instancePath = entityPath[m].split(getResources().getString(R.string.valuesSeparator1));				
				int id = Integer.valueOf(instancePath[0]);
				int instanceNo = Integer.valueOf(instancePath[1]);
				parentEntity = (Entity) parentEntity.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(id).getName(), instanceNo);
			}			
		} catch (ClassCastException e){
			e.printStackTrace();
		} catch (IllegalArgumentException e){
			e.printStackTrace();
		}
		return parentEntity;
	}
	
	private void refreshEntityScreen(int actionCode){
		Log.e("refreshEntityScreen","actionCode"+actionCode);
		//0 - previous, 1 - next, 2 - delete, 3 - add
		//setting current instance number of the entity
		if (actionCode==0){//scroll left to previous entity
			if (this.currInstanceNo>0){
				this.currInstanceNo--;
				refreshEntityScreenFields();
			} else {
				return;
			}
		} else if (actionCode==1) {//scroll right to next (or new) entity
			NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+0, -1));
			if (!nodeDef.isMultiple()){
				Node<?> foundNode = this.parentEntitySingleAttribute.getParent().get(this.parentEntitySingleAttribute.getName(), this.currInstanceNo+1);
				if (foundNode!=null){
					this.currInstanceNo++;	
					refreshEntityScreenFields();
				}
			} else {
				Node<?> foundNode = this.parentEntityMultipleAttribute.getParent().get(this.parentEntityMultipleAttribute.getName(), this.currInstanceNo+1);
				if (foundNode!=null){
					this.currInstanceNo++;
					refreshEntityScreenFields();
				} else {
					AlertMessage.createPositiveNegativeDialog(FormScreen.this, true, null,
							getResources().getString(R.string.addNewEntityTitle), 
							getResources().getString(R.string.addNewEntityMessage),
								getResources().getString(R.string.yes),
								getResources().getString(R.string.no),
					    		new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										FormScreen.this.currInstanceNo++;
										refreshEntityScreenFields();
									}
								},
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										
									}
								},
								null).show();
				}
			}
		} else if (actionCode==2){
			
		} else if (actionCode==3){
			NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+0, -1));
			if (!nodeDef.isMultiple()){
				AlertMessage.createPositiveNegativeDialog(FormScreen.this, true, null,
						getResources().getString(R.string.addNewEntityTitle), 
						getResources().getString(R.string.addNewEntityMessage),
							getResources().getString(R.string.yes),
							getResources().getString(R.string.no),
				    		new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.getParent().get(FormScreen.this.parentEntitySingleAttribute.getName(), FormScreen.this.currInstanceNo+1);
									while (foundNode!=null){
										FormScreen.this.currInstanceNo++;
										foundNode = FormScreen.this.parentEntitySingleAttribute.getParent().get(FormScreen.this.parentEntitySingleAttribute.getName(), FormScreen.this.currInstanceNo+1);
									}
									FormScreen.this.currInstanceNo++;	
									refreshEntityScreenFields();
								}
							},
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
								}
							},
							null).show();				
			} else {
				AlertMessage.createPositiveNegativeDialog(FormScreen.this, true, null,
						getResources().getString(R.string.addNewEntityTitle), 
						getResources().getString(R.string.addNewEntityMessage),
							getResources().getString(R.string.yes),
							getResources().getString(R.string.no),
				    		new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.getParent().get(FormScreen.this.parentEntitySingleAttribute.getName(), FormScreen.this.currInstanceNo+1);
									while (foundNode!=null){
										FormScreen.this.currInstanceNo++;
										foundNode = FormScreen.this.parentEntityMultipleAttribute.getParent().get(FormScreen.this.parentEntitySingleAttribute.getName(), FormScreen.this.currInstanceNo+1);
									}
									FormScreen.this.currInstanceNo++;	
									refreshEntityScreenFields();
								}
							},
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
								}
							},
							null).show();
			}
		}
	}
	
	private void refreshEntityScreenFields(){
		NodeDefinition currentScreenNodeDef = ApplicationManager.getSurvey().getSchema().getDefinitionById(this.idmlId);
		if (currentScreenNodeDef.getMaxCount()!=null){
			if (currentScreenNodeDef.getMaxCount()<=this.currInstanceNo){			
				this.currInstanceNo--;
				AlertMessage.createPositiveDialog(FormScreen.this, true, null,
						getResources().getString(R.string.maxCountTitle), 
						getResources().getString(R.string.maxCountMessage),
							getResources().getString(R.string.okay),
				    		new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
								}
							},
							null).show();
				return;
			}	
		}
		View firstView = FormScreen.this.mainLayout.getChildAt(0);
		if (firstView instanceof HorizontalScrollView){
			ViewGroup scrollbarView = ((ViewGroup)this.mainLayout.getChildAt(0));
			TextView breadcrumb = (TextView)scrollbarView.getChildAt(0);
			breadcrumb.setText(this.breadcrumb.substring(0, this.breadcrumb.lastIndexOf(" "))+" "+(this.currInstanceNo+1));
			int pixels = (int) (getResources().getInteger(R.integer.breadcrumbFontSize) * ApplicationManager.dpiScale + 0.5f);
			breadcrumb.setTextSize(pixels);
			breadcrumb.setSingleLine();
		}
		this.breadcrumb = this.breadcrumb.substring(0, this.breadcrumb.lastIndexOf(" "))+" "+(this.currInstanceNo+1);		
		//enabling/disabling buttons
		if (FormScreen.this.findParentEntity(FormScreen.this.getFormScreenId())!=null){
			RelativeLayout buttonsBar = (RelativeLayout)this.mainLayout.getChildAt(4);
			Button leftBtn = (Button)buttonsBar.getChildAt(0);
			if (FormScreen.this.currInstanceNo==0){
				ApplicationManager.setImageButtonEnabled(this, false, leftBtn, R.drawable.arrow_left_black);
			} else {
				leftBtn.setClickable(true);
				leftBtn.setEnabled(true);
			}
			Button rightBtn = (Button)buttonsBar.getChildAt(2);
			Node<?> foundNode = this.parentEntityMultipleAttribute.getParent().get(this.parentEntityMultipleAttribute.getName(), this.currInstanceNo+1);
			if (foundNode!=null){
				rightBtn.setClickable(false);
				rightBtn.setEnabled(false);
			} else {
				rightBtn.setClickable(true);
				rightBtn.setEnabled(true);
			}
		}

		TextView screenTitle = new TextView(FormScreen.this);
		screenTitle.setText(FormScreen.this.screenTitle);
		int pixels = (int) (getResources().getInteger(R.integer.screenTitleFontSize) * ApplicationManager.dpiScale + 0.5f);
		screenTitle.setTextSize(pixels);
		this.ll.removeAllViews();
		this.mainLayout.removeAllViews();
		this.sv.removeAllViews();
		this.mainLayout.addView(firstView);
		FormScreen.this.mainLayout.addView(ApplicationManager.getDividerLine(this));
		this.mainLayout.addView(screenTitle,2);
		FormScreen.this.mainLayout.addView(ApplicationManager.getDividerLine(this));
		
		if (this.intentType==getResources().getInteger(R.integer.multipleEntityIntent)){ 				
			if (ApplicationManager.currentRecord.getRootEntity().getId()!=this.idmlId){
				this.mainLayout.addView(arrangeButtonsInLine(new Button(this),getResources().getString(R.string.previousInstanceButton),new Button(this),getResources().getString(R.string.nextInstanceButton), new Button(this), getResources().getString(R.string.addInstanceButton), new Button(this), getResources().getString(R.string.deleteInstanceButton), this, true));
				FormScreen.this.mainLayout.addView(ApplicationManager.getDividerLine(this));
			}	
		}
		
		//refreshing values of fields in the entity 
		Entity parentEntity = this.findParentEntity(this.getFormScreenId());	
		if (parentEntity==null){
			String path = this.getFormScreenId().substring(0,this.getFormScreenId().lastIndexOf(getResources().getString(R.string.valuesSeparator2)));
			parentEntity = this.findParentEntity(path);
			try{
				EntityBuilder.addEntity(parentEntity, ApplicationManager.getSurvey().getSchema().getDefinitionById(this.idmlId).getName());
			} catch (IllegalArgumentException e){
				parentEntity = parentEntity.getParent();
				EntityBuilder.addEntity(parentEntity, ApplicationManager.getSurvey().getSchema().getDefinitionById(this.idmlId).getName());
			}
			parentEntity = this.findParentEntity(this.getFormScreenId());
		}

		this.parentEntitySingleAttribute = this.findParentEntity(this.getFormScreenId());
		this.parentEntityMultipleAttribute = this.findParentEntity(this.parentFormScreenId);
		String loadedValue = "";
		ArrayList<String> tableColHeaders = new ArrayList<String>();
		tableColHeaders.add("Value");
		for (int i=0;i<this.fieldsNo;i++){
			NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+i, -1));
			if (nodeDef instanceof EntityDefinition){
				addEntityDefinitionNode(nodeDef);
			}else {					
				if (nodeDef instanceof TextAttributeDefinition){
					addTextNode(nodeDef, tableColHeaders, false);
    			} else if (nodeDef instanceof NumberAttributeDefinition){
    				addNumberNode(nodeDef, tableColHeaders, false, false);
    			} else if (nodeDef instanceof BooleanAttributeDefinition){
    				addBooleanNode(nodeDef, tableColHeaders);
    				/*loadedValue = "";
    				if (!nodeDef.isMultiple()){
    					
    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
	    				if (foundNode!=null){
	    					BooleanValue boolValue = (BooleanValue)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
        					if (boolValue!=null){
        						if (boolValue.getValue()!=null)
        							loadedValue = boolValue.getValue().toString();
        					}
	    				}
	    				
        				BooleanField boolField = null;
        				if (loadedValue.equals("")){
        					boolField = new BooleanField(this, nodeDef, false, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
        				} else if (loadedValue.equals("false")) {
    						boolField = new BooleanField(this, nodeDef, false, true, getResources().getString(R.string.yes), getResources().getString(R.string.no));
    					} else {
    						boolField = new BooleanField(this, nodeDef, true, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
    					}
        				boolField.setOnClickListener(this);
        				boolField.setId(nodeDef.getId());       				
        				ApplicationManager.putUIElement(boolField.getId(), boolField);
        				this.ll.addView(boolField);
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
    					if (foundNode!=null){
    						BooleanValue boolValue = (BooleanValue)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
	    					if (boolValue!=null){
        						if (boolValue.getValue()!=null)
        							loadedValue = boolValue.getValue().toString();
        					}
	    				}
    					BooleanField boolField = null;
        				if (loadedValue.equals("")){
        					boolField = new BooleanField(this, nodeDef, false, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
        				} else if (loadedValue.equals("false")) {
    						boolField = new BooleanField(this, nodeDef, false, true, getResources().getString(R.string.yes), getResources().getString(R.string.no));
    					} else {
    						boolField = new BooleanField(this, nodeDef, true, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
    					}
    					boolField.setOnClickListener(this);
    					boolField.setId(nodeDef.getId());
        				ApplicationManager.putUIElement(boolField.getId(), boolField);
        				this.ll.addView(boolField);
    				} else {//multiple attribute summary    			    		
						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}*/
    			} else if (nodeDef instanceof CodeAttributeDefinition){
    				loadedValue = "";
    				CodeAttributeDefinition codeAttrDef = (CodeAttributeDefinition)nodeDef;
    				ArrayList<String> options = new ArrayList<String>();
    				ArrayList<String> codes = new ArrayList<String>();
    				options.add("");
    				codes.add("null");
    				MobileCodeListManager codeListManager = ServiceFactory.getCodeListManager();
					CodeList list = codeAttrDef.getList();
					if ( ! list.isExternal() ) {
						List<CodeListItem> codeListItemsList = codeListManager.loadRootItems(list);
	    				for (CodeListItem codeListItem : codeListItemsList){
	    					codes.add(codeListItem.getCode());
	    					if (codeListItem.getLabel(null)==null){
	    						options.add(codeListItem.getLabel(ApplicationManager.selectedLanguage));
	    					} else {
	    						options.add(codeListItem.getLabel(null));	    						
	    					}
	    				}
					}
    				if (!nodeDef.isMultiple()){
    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
	    				if (foundNode!=null){
	    					Code codeValue = (Code)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
        					if (codeValue!=null){
        						loadedValue = codeValue.getCode();
        					}
	    				}
        				CodeField codeField = new CodeField(this, nodeDef, codes, options, loadedValue,FormScreen.this.getFormScreenId());
        				codeField.setOnClickListener(this);
        				codeField.setId(nodeDef.getId());
        				ApplicationManager.putUIElement(codeField.getId(), codeField);
        				this.ll.addView(codeField);
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
    					if (foundNode!=null){
	    					Code codeValue = (Code)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
        					if (codeValue!=null){
        						loadedValue = codeValue.getCode();
        					}
	    				}
        				CodeField codeField = new CodeField(this, nodeDef, codes, options, loadedValue,this.parentFormScreenId);
        				codeField.setOnClickListener(this);
        				codeField.setId(nodeDef.getId());
        				ApplicationManager.putUIElement(codeField.getId(), codeField);
        				this.ll.addView(codeField);
    				} else {//multiple attribute summary
						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}
    			} else if (nodeDef instanceof CoordinateAttributeDefinition){
    				if (!nodeDef.isMultiple()){
        				final CoordinateField coordField= new CoordinateField(this, nodeDef);
        				if (this.currentCoordinateField!=null){
        					if (this.longitude==null)
        						this.longitude = "";
        					if (this.latitude==null)
        						this.latitude = "";
        					String srsId = null;
        					if (FormScreen.this.currentCoordinateField.srs!=null){						
        						srsId = FormScreen.this.currentCoordinateField.srs.getId();
        					}
        					coordField.setValue(0, this.longitude, this.latitude, srsId, this.parentFormScreenId,false);
    		    			this.currentCoordinateField = null;
    		    			this.longitude = null;
    		    			this.latitude = null;
    		    		}
        				coordField.setOnClickListener(this);
        				coordField.setId(nodeDef.getId());
        				ApplicationManager.putUIElement(coordField.getId(), coordField);
        				this.ll.addView(coordField);
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
    					final CoordinateField coordField= new CoordinateField(this, nodeDef);
        				if (this.currentCoordinateField!=null){
        					if (this.longitude==null)
        						this.longitude = "";
        					if (this.latitude==null)
        						this.latitude = "";
        					String srsId = null;
        					if (FormScreen.this.currentCoordinateField.srs!=null){						
        						srsId = FormScreen.this.currentCoordinateField.srs.getId();
        					}
        					coordField.setValue(this.currInstanceNo, this.longitude, this.latitude, srsId, this.parentFormScreenId,false);
    		    			this.currentCoordinateField = null;
    		    			this.longitude = null;
    		    			this.latitude = null;
    		    		}
        				coordField.setOnClickListener(this);
        				coordField.setId(nodeDef.getId());
        				ApplicationManager.putUIElement(coordField.getId(), coordField);
        				this.ll.addView(coordField);
    				} else {//multiple attribute summary    			    		
						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}
    			} else if (nodeDef instanceof RangeAttributeDefinition){
    				loadedValue = "";
    				if (!nodeDef.isMultiple()){
    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
	    				if (foundNode!=null){
	    					RangeAttributeDefinition rangeAttrDef = (RangeAttributeDefinition)nodeDef;
	    					if (rangeAttrDef.isReal()){
	    						RealRange rangeValue = (RealRange)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    						if (rangeValue!=null){
		    						if (rangeValue.getFrom()==null && rangeValue.getTo()==null){
		    							loadedValue = "";
		    						} else if (rangeValue.getFrom()==null){
		    							loadedValue = getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
		    						} else if (rangeValue.getTo()==null){
		    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator);
		    						} else {
		    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
		    						}		    						
		    					}	
	    					} else {
	    						IntegerRange rangeValue = (IntegerRange)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    						if (rangeValue!=null){
		    						if (rangeValue.getFrom()==null && rangeValue.getTo()==null){
		    							loadedValue = "";
		    						} else if (rangeValue.getFrom()==null){
		    							loadedValue = getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
		    						} else if (rangeValue.getTo()==null){
		    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator);
		    						} else {
		    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
		    						}		    						
		    					}	
	    					}		    							
	    				}
        				final RangeField rangeField= new RangeField(this, nodeDef);
        				rangeField.setOnClickListener(this);
        				rangeField.setId(nodeDef.getId());
        				rangeField.addTextChangedListener(new TextWatcher(){
        			        public void afterTextChanged(Editable s) {        			            
        			        	rangeField.setValue(0, s.toString(),  FormScreen.this.getFormScreenId(),true);
        			        }
        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
        			    });
        				ApplicationManager.putUIElement(rangeField.getId(), rangeField);
        				this.ll.addView(rangeField);
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
	    				if (foundNode!=null){
	    					RangeAttributeDefinition rangeAttrDef = (RangeAttributeDefinition)nodeDef;
	    					if (rangeAttrDef.isReal()){
	    						RealRange rangeValue = (RealRange)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    						if (rangeValue!=null){
		    						if (rangeValue.getFrom()==null && rangeValue.getTo()==null){
		    							loadedValue = "";
		    						} else if (rangeValue.getFrom()==null){
		    							loadedValue = getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
		    						} else if (rangeValue.getTo()==null){
		    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator);
		    						} else {
		    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
		    						}		    						
		    					}	
	    					} else {
	    						IntegerRange rangeValue = (IntegerRange)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    						if (rangeValue!=null){
		    						if (rangeValue.getFrom()==null && rangeValue.getTo()==null){
		    							loadedValue = "";
		    						} else if (rangeValue.getFrom()==null){
		    							loadedValue = getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
		    						} else if (rangeValue.getTo()==null){
		    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator);
		    						} else {
		    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
		    						}		    						
		    					}	
	    					}  				
	    				}
        				final RangeField rangeField= new RangeField(this, nodeDef);
        				rangeField.setOnClickListener(this);
        				rangeField.setId(nodeDef.getId());
        				rangeField.addTextChangedListener(new TextWatcher(){
        			        public void afterTextChanged(Editable s) {        			            
        			        	rangeField.setValue(FormScreen.this.currInstanceNo, s.toString(), FormScreen.this.parentFormScreenId,true);
        			        }
        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
        			    });
        				ApplicationManager.putUIElement(rangeField.getId(), rangeField);
        				this.ll.addView(rangeField);
    				} else {//multiple attribute summary    			    		
						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}
    			} else if (nodeDef instanceof DateAttributeDefinition){
    				loadedValue = "";
    				if (!nodeDef.isMultiple()){
    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
	    				if (foundNode!=null){
	    					Date dateValue = (Date)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    					if (dateValue!=null){
	    						if (dateValue.getMonth()==null && dateValue.getDay()==null && dateValue.getYear()==null){
	    							loadedValue = "";
	    						} else if (dateValue.getMonth()==null && dateValue.getDay()==null){
	    							loadedValue = dateValue.getYear()+getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator);		    							
	    						} else if (dateValue.getMonth()==null && dateValue.getYear()==null){
	    							loadedValue = getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator)+dateValue.getDay();
	    						} else if (dateValue.getDay()==null && dateValue.getYear()==null){
	    							loadedValue = getResources().getString(R.string.dateSeparator)+dateValue.getMonth()+getResources().getString(R.string.dateSeparator);
	    						} else if (dateValue.getMonth()==null){
	    							loadedValue = dateValue.getYear()+getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator)+dateValue.getDay();		    							
	    						} else if (dateValue.getDay()==null){
	    							loadedValue = dateValue.getYear()+getResources().getString(R.string.dateSeparator)+dateValue.getMonth()+getResources().getString(R.string.dateSeparator);
	    						} else if (dateValue.getYear()==null){
	    							loadedValue = getResources().getString(R.string.dateSeparator)+dateValue.getMonth()+getResources().getString(R.string.dateSeparator)+dateValue.getDay();
	    						} else {
	    							loadedValue = dateValue.getYear()+getResources().getString(R.string.dateSeparator)+dateValue.getMonth()+getResources().getString(R.string.dateSeparator)+dateValue.getDay();
	    						}
	    					}
	    				}
	    				if (loadedValue.equals("")){
	    					//TODO: get date from device
	    				}

        				final DateField dateField= new DateField(this, nodeDef);
        				dateField.setOnClickListener(this);
        				dateField.setId(nodeDef.getId());
        				dateField.addTextChangedListener(new TextWatcher(){
        			        public void afterTextChanged(Editable s) {        			            
        			        	dateField.setValue(0, s.toString(), FormScreen.this.getFormScreenId(),true);
        			        }
        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
        			    });
        				ApplicationManager.putUIElement(dateField.getId(), dateField);
        				this.ll.addView(dateField);
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
	    				if (foundNode!=null){
	    					Date dateValue = (Date)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    					if (dateValue!=null){
	    						if (dateValue.getMonth()==null && dateValue.getDay()==null && dateValue.getYear()==null){
	    							loadedValue = "";
	    						} else if (dateValue.getMonth()==null && dateValue.getDay()==null){
	    							loadedValue = dateValue.getYear()+getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator);		    							
	    						} else if (dateValue.getMonth()==null && dateValue.getYear()==null){
	    							loadedValue = getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator)+dateValue.getDay();
	    						} else if (dateValue.getDay()==null && dateValue.getYear()==null){
	    							loadedValue = getResources().getString(R.string.dateSeparator)+dateValue.getMonth()+getResources().getString(R.string.dateSeparator);
	    						} else if (dateValue.getMonth()==null){
	    							loadedValue = dateValue.getYear()+getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator)+dateValue.getDay();		    							
	    						} else if (dateValue.getDay()==null){
	    							loadedValue = dateValue.getYear()+getResources().getString(R.string.dateSeparator)+dateValue.getMonth()+getResources().getString(R.string.dateSeparator);
	    						} else if (dateValue.getYear()==null){
	    							loadedValue = getResources().getString(R.string.dateSeparator)+dateValue.getMonth()+getResources().getString(R.string.dateSeparator)+dateValue.getDay();
	    						} else {
	    							loadedValue = dateValue.getYear()+getResources().getString(R.string.dateSeparator)+dateValue.getMonth()+getResources().getString(R.string.dateSeparator)+dateValue.getDay();
	    						}
	    					}
	    				}
        				final DateField dateField= new DateField(this, nodeDef);
        				dateField.setOnClickListener(this);
        				dateField.setId(nodeDef.getId());
        				dateField.addTextChangedListener(new TextWatcher(){
        			        public void afterTextChanged(Editable s) {        			            
        			        	dateField.setValue(FormScreen.this.currInstanceNo, s.toString(), FormScreen.this.parentFormScreenId,true);
        			        }
        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
        			    });
        				ApplicationManager.putUIElement(dateField.getId(), dateField);
        				this.ll.addView(dateField);
    				}
    			} else if (nodeDef instanceof TimeAttributeDefinition){
    				loadedValue = "";
    				if (!nodeDef.isMultiple()){
    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
	    				if (foundNode!=null){
	    					Time timeValue = (Time)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    					if (timeValue!=null){
	    						if (timeValue.getHour()==null && timeValue.getMinute()==null){
	    							loadedValue = "";
	    						} else if (timeValue.getHour()==null){
	    							loadedValue = getResources().getString(R.string.timeSeparator)+timeValue.getMinute();
	    						} else if (timeValue.getMinute()==null){
	    							loadedValue = timeValue.getHour()+getResources().getString(R.string.timeSeparator);
	    						} else {
	    							loadedValue = timeValue.getHour()+getResources().getString(R.string.timeSeparator)+timeValue.getMinute();
	    						}		    						
	    					}	    				
	    				}
	    				if (loadedValue.equals("")){
	    					//TODO: get timee from device
	    				}
	    				
        				final TimeField timeField= new TimeField(this, nodeDef);
        				timeField.setOnClickListener(this);
        				timeField.setId(nodeDef.getId());
        				timeField.addTextChangedListener(new TextWatcher(){
        			        public void afterTextChanged(Editable s) {
        			        	timeField.setValue(0, s.toString(), FormScreen.this.getFormScreenId(),true);
        			        }
        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
        			    });
        				ApplicationManager.putUIElement(timeField.getId(), timeField);
        				this.ll.addView(timeField);
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
	    				if (foundNode!=null){
	    					Time timeValue = (Time)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
	    					if (timeValue!=null){
	    						if (timeValue.getHour()==null && timeValue.getMinute()==null){
	    							loadedValue = "";
	    						} else if (timeValue.getHour()==null){
	    							loadedValue = getResources().getString(R.string.timeSeparator)+timeValue.getMinute();
	    						} else if (timeValue.getMinute()==null){
	    							loadedValue = timeValue.getHour()+getResources().getString(R.string.timeSeparator);
	    						} else {
	    							loadedValue = timeValue.getHour()+getResources().getString(R.string.timeSeparator)+timeValue.getMinute();
	    						}		    						
	    					}	   				
	    				}
        				final TimeField timeField= new TimeField(this, nodeDef);
        				timeField.setOnClickListener(this);
        				timeField.setId(nodeDef.getId());
        				timeField.addTextChangedListener(new TextWatcher(){
        			        public void afterTextChanged(Editable s) {        			            
        			        	timeField.setValue(FormScreen.this.currInstanceNo, s.toString(), FormScreen.this.parentFormScreenId,true);
        			        }
        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
        			    });
        				ApplicationManager.putUIElement(timeField.getId(), timeField);
        				this.ll.addView(timeField);
    				} else {//multiple attribute summary    			    		
						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}
    			} else if (nodeDef instanceof TaxonAttributeDefinition){
    				ArrayList<String> options = new ArrayList<String>();
    				ArrayList<String> codes = new ArrayList<String>();
    				options.add("");
    				codes.add("null");
    				String vernLang = "";
    				if (!nodeDef.isMultiple()){
    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
	    				if (foundNode!=null){
	    					TaxonOccurrence taxonValue = (TaxonOccurrence)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    					if (taxonValue!=null){
	    	    				vernLang = taxonValue.getLanguageCode();
	    					}	    				
	    				}
        				final TaxonField taxonField= new TaxonField(this, nodeDef, codes, options, vernLang);
        				taxonField.setOnClickListener(this);
        				taxonField.setId(nodeDef.getId());
        				ApplicationManager.putUIElement(taxonField.getId(), taxonField);
        				this.ll.addView(taxonField);
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
	    				if (foundNode!=null){
	    					TaxonOccurrence taxonValue = (TaxonOccurrence)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
	    					if (taxonValue!=null){
	    	    				vernLang = taxonValue.getLanguageCode();	    						
	    					}	   				
	    				}
	    				final TaxonField taxonField= new TaxonField(this, nodeDef, codes, options, vernLang);
	    				taxonField.setOnClickListener(this);
	    				taxonField.setId(nodeDef.getId());
        				ApplicationManager.putUIElement(taxonField.getId(), taxonField);
        				this.ll.addView(taxonField);
    				} else {//multiple attribute summary    			    		
						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}
				} else if (nodeDef instanceof FileAttributeDefinition){
					FileAttributeDefinition fileDef = (FileAttributeDefinition)nodeDef;
					List<String> extensionsList = fileDef.getExtensions();
					if (extensionsList.contains("jpg")||extensionsList.contains("jpeg")){
						loadedValue = "";
	    				if (!nodeDef.isMultiple()){
	        				final PhotoField photoField= new PhotoField(this, nodeDef);
	        				if (FormScreen.this.currentPictureField!=null && FormScreen.this.currentPictureField.getLabelText().equals(photoField.getLabelText())){
	    		    			photoField.setValue(0, this.photoPath, FormScreen.this.getFormScreenId(),false);
	    		    			this.currentPictureField = null;
	    		    			this.photoPath = null;
	    		    		}
	        				Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					File fileValue = (File)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    					if (fileValue!=null){
		    						loadedValue = fileValue.getFilename();
		    					}
		    				}
	        				photoField.setOnClickListener(this);
	        				photoField.setId(nodeDef.getId());
	        				ApplicationManager.putUIElement(photoField.getId(), photoField);
	        				this.ll.addView(photoField);
	    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	        				final PhotoField photoField= new PhotoField(this, nodeDef);
	        				if (FormScreen.this.currentPictureField!=null && FormScreen.this.currentPictureField.getLabelText().equals(photoField.getLabelText())){
	        					photoField.setValue(this.currInstanceNo, this.photoPath, this.parentFormScreenId,false);
	    		    			this.currentPictureField = null;
	    		    			this.photoPath = null;
	    		    		}
	        				Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
		    				if (foundNode!=null){
		    					File fileValue = (File)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
		    					if (fileValue!=null){
		    						loadedValue = fileValue.getFilename();
		    					}
		    				}
	        				photoField.setOnClickListener(this);
	        				photoField.setId(nodeDef.getId());
	        				ApplicationManager.putUIElement(photoField.getId(), photoField);
	        				this.ll.addView(photoField);
	    				} else {//multiple attribute summary    			    		
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
        				}
					}
				}
			}    				
		}
		
		int backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);		
		changeBackgroundColor(backgroundColor);
		
		for (int i=0;i<this.fieldsNo;i++){
			NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+i, -1));
			if (nodeDef!=null){
				if (nodeDef instanceof TextAttributeDefinition){
					loadedValue = "";
					if (((TextAttributeDefinition) nodeDef).getType().toString().toUpperCase().equals(getResources().getString(R.string.shortTextField))){
						TextValue textValue = (TextValue)parentEntity.getValue(nodeDef.getName(), 0);							
						if (textValue!=null)
							if (textValue.getValue()!=null)
								loadedValue = textValue.getValue();
    					TextField textField = (TextField) ApplicationManager.getUIElement(nodeDef.getId());
    					if (textField!=null)
    						textField.setValue(0, loadedValue, this.getFormScreenId(), false);
					} else {
						TextValue textValue = (TextValue)parentEntity.getValue(nodeDef.getName(), 0);							
						if (textValue!=null)
							if (textValue.getValue()!=null)
								loadedValue = textValue.getValue();
    					MemoField memoField = (MemoField) ApplicationManager.getUIElement(nodeDef.getId());
    					if (memoField!=null)
    						memoField.setValue(0, loadedValue, this.getFormScreenId(), false);
					}								
				} else if (nodeDef instanceof NumberAttributeDefinition){
					loadedValue = "";
					if (((NumberAttributeDefinition) nodeDef).isInteger()){
						IntegerValue intValue = (IntegerValue)parentEntity.getValue(nodeDef.getName(), 0);
						if (intValue!=null)
							if (intValue.getValue()!=null)
								loadedValue = intValue.getValue().toString();	
					} else {
						RealValue realValue = (RealValue)parentEntity.getValue(nodeDef.getName(), 0);					
						if (realValue!=null)
							if (realValue.getValue()!=null)
								loadedValue = realValue.getValue().toString();
					}					
					NumberField numberField = (NumberField) ApplicationManager.getUIElement(nodeDef.getId());
					if (numberField!=null)
						numberField.setValue(0, loadedValue, this.getFormScreenId(), false);
				}  else if (nodeDef instanceof BooleanAttributeDefinition){
					loadedValue = "";
					BooleanValue boolValue = (BooleanValue)parentEntity.getValue(nodeDef.getName(), 0);
					if (boolValue!=null)
						if (boolValue.getValue()!=null)
							loadedValue = boolValue.getValue().toString();
					BooleanField boolField = (BooleanField) ApplicationManager.getUIElement(nodeDef.getId());
					if (boolField!=null){
						
						if (loadedValue.equals("")){
							boolField.setValue(0, null, this.getFormScreenId(), false);
						} else {
							boolField.setValue(0, Boolean.valueOf(loadedValue), this.getFormScreenId(), false);
						}
					}					
				} else if (nodeDef instanceof CodeAttributeDefinition){
					loadedValue = "";
					Code codeValue = (Code)parentEntity.getValue(nodeDef.getName(), 0);
					if (codeValue!=null)
						if (codeValue.getCode()!=null)
							loadedValue = codeValue.getCode();
					CodeField codeField = (CodeField) ApplicationManager.getUIElement(nodeDef.getId());
					if (codeField!=null){
						codeField.setValue(0, loadedValue, this.getFormScreenId(), false);
					}
						
				} else if (nodeDef instanceof CoordinateAttributeDefinition){
					String loadedValueLat = "";
					String loadedValueLon = "";
					String loadedSrsId = "";
					Coordinate coordValue = (Coordinate)parentEntity.getValue(nodeDef.getName(), 0);
					if (coordValue!=null){
						if (coordValue.getX()!=null)
							loadedValueLon = coordValue.getX().toString();
						if (coordValue.getY()!=null)
							loadedValueLat = coordValue.getY().toString();
						if (coordValue.getSrsId()!=null)
							loadedSrsId = coordValue.getSrsId().toString();
					}
						
					CoordinateField coordField = (CoordinateField) ApplicationManager.getUIElement(nodeDef.getId());
					if (coordField!=null)
						coordField.setValue(0, loadedValueLon, loadedValueLat, loadedSrsId, this.getFormScreenId(), false);
				} else if (nodeDef instanceof RangeAttributeDefinition){
					String from = "";
					String to = "";
					
					RangeAttributeDefinition rangeAttrDef = (RangeAttributeDefinition)nodeDef;
					if (rangeAttrDef.isReal()){
						RealRange rangeValue = (RealRange)parentEntity.getValue(nodeDef.getName(), 0);
						if (rangeValue!=null){
							if (rangeValue.getFrom()!=null)
								from = rangeValue.getFrom().toString();
							if (rangeValue.getTo()!=null)
								to = rangeValue.getTo().toString();						
						}
					} else {
						IntegerRange rangeValue = (IntegerRange)parentEntity.getValue(nodeDef.getName(), 0);
						if (rangeValue!=null){
							if (rangeValue.getFrom()!=null)
								from = rangeValue.getFrom().toString();
							if (rangeValue.getTo()!=null)
								to = rangeValue.getTo().toString();						
						}
					}
											
					RangeField rangeField = (RangeField) ApplicationManager.getUIElement(nodeDef.getId());
					if (rangeField!=null)
						rangeField.setValue(0, from+getResources().getString(R.string.rangeSeparator)+to, this.getFormScreenId(), false);
				} else if (nodeDef instanceof DateAttributeDefinition){
					Date dateValue = (Date)parentEntity.getValue(nodeDef.getName(), 0);
					if (dateValue!=null){
						loadedValue = DateField.formatDate(dateValue);
					}
					DateField dateField = (DateField) ApplicationManager.getUIElement(nodeDef.getId());
					if (dateField!=null)
						dateField.setValue(0, loadedValue, this.getFormScreenId(), false);
				} else if (nodeDef instanceof TimeAttributeDefinition){
					String hour = "";
					String minute = "";
					Time timeValue = (Time)parentEntity.getValue(nodeDef.getName(), 0);
					if (timeValue!=null){
						if (timeValue.getHour()!=null)
							hour = timeValue.getHour().toString();
						if (timeValue.getMinute()!=null)
							minute = timeValue.getMinute().toString();
					}						
					TimeField timeField = (TimeField) ApplicationManager.getUIElement(nodeDef.getId());
					if (timeField!=null)
						timeField.setValue(0, hour+getResources().getString(R.string.timeSeparator)+minute, this.getFormScreenId(), false);					
				} else if (nodeDef instanceof TaxonAttributeDefinition){
    				String code = "";
    				String sciName = "";
    				String vernName = "";
    				String vernLang = "";
    				String langVariant = "";
					TaxonOccurrence taxonValue = (TaxonOccurrence)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
					if (taxonValue!=null){
						code = taxonValue.getCode();
	    				sciName = taxonValue.getScientificName();
	    				vernName = taxonValue.getVernacularName();
	    				vernLang = taxonValue.getLanguageCode();
	    				langVariant = taxonValue.getLanguageVariety();	    						
					}
					TaxonField taxonField = (TaxonField) ApplicationManager.getUIElement(nodeDef.getId());
					if (taxonField!=null){
						taxonField.setValue(0, code, sciName, vernName, vernLang, langVariant, this.getFormScreenId(), false);
					}						
				} else if (nodeDef instanceof FileAttributeDefinition){
					String fileName = "";
					File fileValue = (File)parentEntity.getValue(nodeDef.getName(), 0);
					if (fileValue!=null){
						if (fileValue.getFilename()!=null)
							fileName = fileValue.getFilename();
					}						
					PhotoField photoField = (PhotoField) ApplicationManager.getUIElement(nodeDef.getId());
					if (photoField!=null)
						photoField.setValue(0, fileName, this.getFormScreenId(), false);					
				}
			}
		}
		
		this.sv.addView(ll);
		FormScreen.this.mainLayout.addView(sv);
		sv.post(new Runnable() {
    	    @Override
    	    public void run() {
				sv.scrollTo(0, 0);
			}	
    	});
	}
	
	private void refreshMultipleAttributeScreen(int actionCode){
		//0 - previous, 1 - next, 2 - delete, 3 - add
		if (actionCode==0){
			if (this.currInstanceNo>0){
				this.currInstanceNo--;
				refreshMultipleAttributeScreenField();
			} else {
				return;
			}	
		} else if (actionCode==1) {
			NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+0, -1));
			if (!nodeDef.isMultiple()){
				Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), this.currInstanceNo+1);
				if (foundNode!=null){
					this.currInstanceNo++;
					refreshMultipleAttributeScreenField();
				} else {
					AlertMessage.createPositiveNegativeDialog(FormScreen.this, true, null,
							getResources().getString(R.string.addNewAttributeTitle), 
							getResources().getString(R.string.addNewAttributeMessage),
								getResources().getString(R.string.yes),
								getResources().getString(R.string.no),
					    		new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										FormScreen.this.currInstanceNo++;	
										refreshMultipleAttributeScreenField();
									}
								},
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										
									}
								},
								null).show();
				}
			} else {
				Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo+1);
				if (foundNode!=null){
					this.currInstanceNo++;
					refreshMultipleAttributeScreenField();
				} else {
					AlertMessage.createPositiveNegativeDialog(FormScreen.this, true, null,
							getResources().getString(R.string.addNewAttributeTitle), 
							getResources().getString(R.string.addNewAttributeMessage),
								getResources().getString(R.string.yes),
								getResources().getString(R.string.no),
					    		new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										FormScreen.this.currInstanceNo++;
										refreshMultipleAttributeScreenField();
									}
								},
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										
									}
								},
								null).show();
				}
			}
		} else if (actionCode==2){
			
		} else if (actionCode==3) {
			NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+0, -1));
			if (!nodeDef.isMultiple()){
				AlertMessage.createPositiveNegativeDialog(FormScreen.this, true, null,
						getResources().getString(R.string.addNewAttributeTitle), 
						getResources().getString(R.string.addNewAttributeMessage),
							getResources().getString(R.string.yes),
							getResources().getString(R.string.no),
				    		new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(FormScreen.this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+0, -1));
									Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo+1);
									while (foundNode!=null){
										FormScreen.this.currInstanceNo++;
										foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo+1);
									}
									FormScreen.this.currInstanceNo++;										
									refreshMultipleAttributeScreenField();
								}
							},
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
								}
							},
							null).show();
			} else {
				AlertMessage.createPositiveNegativeDialog(FormScreen.this, true, null,
						getResources().getString(R.string.addNewAttributeTitle), 
						getResources().getString(R.string.addNewAttributeMessage),
							getResources().getString(R.string.yes),
							getResources().getString(R.string.no),
				    		new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(FormScreen.this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+0, -1));
									Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo+1);
									while (foundNode!=null){
										FormScreen.this.currInstanceNo++;
										foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo+1);
									}
									FormScreen.this.currInstanceNo++;										
									refreshMultipleAttributeScreenField();
								}
							},
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
								}
							},
							null).show();
			}		
		}
	}
	
	private void refreshMultipleAttributeScreenField(){
		NodeDefinition currentScreenNodeDef = ApplicationManager.getSurvey().getSchema().getDefinitionById(this.idmlId);
		if (currentScreenNodeDef.getMaxCount()!=null)
			if (currentScreenNodeDef.getMaxCount()<=this.currInstanceNo){
				this.currInstanceNo--;
				AlertMessage.createPositiveDialog(FormScreen.this, true, null,
						getResources().getString(R.string.maxCountTitle), 
						getResources().getString(R.string.maxCountMessage),
							getResources().getString(R.string.okay),
				    		new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
								}
							},
							null).show();
				return;
			}
		
		View firstView = this.ll.getChildAt(0);
		if (firstView instanceof TextView){
			TextView screenTitle = (TextView)firstView;
			screenTitle.setText(this.breadcrumb);
		}
		Entity parentEntity = this.parentEntityMultipleAttribute;
		if (parentEntity!=null){
			NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+0, -1));
			
			if (nodeDef!=null){
				if (nodeDef instanceof TextAttributeDefinition){
					String loadedValue = "";
					if (((TextAttributeDefinition) nodeDef).getType().toString().toUpperCase().equals(getResources().getString(R.string.shortTextField))){
						TextValue textValue = (TextValue)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);						
						if (textValue!=null)
							loadedValue = textValue.getValue();
    					TextField textField = (TextField) ApplicationManager.getUIElement(nodeDef.getId());
    					if (textField!=null)
    						textField.setValue(this.currInstanceNo, loadedValue, this.getFormScreenId(), false);	
					} else {
						TextValue textValue = (TextValue)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
						if (textValue!=null)
							loadedValue = textValue.getValue();
    					TextField textField = (TextField) ApplicationManager.getUIElement(nodeDef.getId());
    					if (textField!=null)
    						textField.setValue(this.currInstanceNo, loadedValue, this.getFormScreenId(), false);
					} 
				} else if (nodeDef instanceof NumberAttributeDefinition){
					String loadedValue = "";
					if (((NumberAttributeDefinition) nodeDef).isInteger()){
						IntegerValue intValue = (IntegerValue)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
						if (intValue!=null)
							if (intValue.getValue()!=null)
								loadedValue = intValue.getValue().toString();
							else 
								loadedValue = "";
					} else {
						RealValue realValue = (RealValue)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
						if (realValue!=null)
							if (realValue.getValue()!=null)
								loadedValue = realValue.getValue().toString();
							else 
								loadedValue = "";
					}					
					NumberField numberField = (NumberField) ApplicationManager.getUIElement(nodeDef.getId());
					if (numberField!=null)
						numberField.setValue(this.currInstanceNo, loadedValue, this.getFormScreenId(), false);
				} else if (nodeDef instanceof BooleanAttributeDefinition){
					String loadedValue = "";
					BooleanValue boolValue = (BooleanValue)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
					if (boolValue!=null)
						loadedValue = boolValue.getValue().toString();
					BooleanField boolField = (BooleanField) ApplicationManager.getUIElement(nodeDef.getId());
					if (boolField!=null){
						if (loadedValue.equals("")){
							boolField.setValue(this.currInstanceNo, null, this.getFormScreenId(), false);
						} else {
							boolField.setValue(this.currInstanceNo, Boolean.valueOf(loadedValue), this.getFormScreenId(), false);
						}
					}					
				} else if (nodeDef instanceof CodeAttributeDefinition){
					String loadedValue = "";
					Code codeValue = (Code)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
					if (codeValue!=null)
						loadedValue = codeValue.getCode();
					CodeField codeField = (CodeField) ApplicationManager.getUIElement(nodeDef.getId());
					if (codeField!=null){
						codeField.setValue(this.currInstanceNo, loadedValue, this.getFormScreenId(), false);
					}						
				} else if (nodeDef instanceof CoordinateAttributeDefinition){
					String loadedValueLat = "";
					String loadedValueLon = "";
					String loadedSrsId = "";
					Coordinate coordValue = (Coordinate)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
					if (coordValue!=null){
						loadedValueLon = coordValue.getX().toString();
						if (loadedValueLon==null)
							loadedValueLon = "";
						loadedValueLat = coordValue.getY().toString();
						if (loadedValueLat==null)
							loadedValueLat = "";
						loadedSrsId = coordValue.getSrsId().toString();
						if (loadedSrsId==null)
							loadedSrsId = "";
					}						
					CoordinateField coordField = (CoordinateField) ApplicationManager.getUIElement(nodeDef.getId());
					if (coordField!=null)
						coordField.setValue(this.currInstanceNo, loadedValueLon, loadedValueLat, loadedSrsId, this.getFormScreenId(), false);
				} else if (nodeDef instanceof RangeAttributeDefinition){
					String from = "";
					String to = "";
					RangeAttributeDefinition rangeAttrDef = (RangeAttributeDefinition)nodeDef;
					if (rangeAttrDef.isReal()){
						RealRange rangeValue = (RealRange)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
						if (rangeValue!=null){
							from = rangeValue.getFrom().toString();
							if (from==null)
								from = "";
							to = rangeValue.getTo().toString();
							if (to == null)
								to = "";						
						}
					} else {
						IntegerRange rangeValue = (IntegerRange)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
						if (rangeValue!=null){
							from = rangeValue.getFrom().toString();
							if (from==null)
								from = "";
							to = rangeValue.getTo().toString();
							if (to == null)
								to = "";						
						}
					}															
					RangeField rangeField = (RangeField) ApplicationManager.getUIElement(nodeDef.getId());
					if (rangeField!=null)
						rangeField.setValue(this.currInstanceNo, from+getResources().getString(R.string.rangeSeparator)+to, this.getFormScreenId(), false);
				} else if (nodeDef instanceof DateAttributeDefinition){
					Date dateValue = (Date)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
					String loadedValue = "";
					if (dateValue!=null){
						loadedValue = DateField.formatDate(dateValue);
					}	
					DateField dateField = (DateField) ApplicationManager.getUIElement(nodeDef.getId());
					if (dateField!=null)
						dateField.setValue(this.currInstanceNo, loadedValue, this.getFormScreenId(), false);
				} else if (nodeDef instanceof TimeAttributeDefinition){
					String hour = "";
					String minute = "";
					Time timeValue = (Time)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
					if (timeValue!=null){
						hour = timeValue.getHour().toString();
						if (hour==null)
							hour = "";
						minute = timeValue.getMinute().toString();
						if (minute==null)
							minute = "";
					}						
					TimeField timeField = (TimeField) ApplicationManager.getUIElement(nodeDef.getId());
					if (timeField!=null)
						timeField.setValue(this.currInstanceNo, hour+getResources().getString(R.string.timeSeparator)+minute, this.getFormScreenId(), false);					
				} else if (nodeDef instanceof TaxonAttributeDefinition){
					String code = "";
    				String sciName = "";
    				String vernName = "";
    				String vernLang = "";
    				String langVariant = "";
					TaxonOccurrence taxonValue = (TaxonOccurrence)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
					if (taxonValue!=null){
						code = taxonValue.getCode();
	    				sciName = taxonValue.getScientificName();
	    				vernName = taxonValue.getVernacularName();
	    				vernLang = taxonValue.getLanguageCode();
	    				langVariant = taxonValue.getLanguageVariety();	    						
					}
					TaxonField taxonField = (TaxonField) ApplicationManager.getUIElement(nodeDef.getId());
					if (taxonField!=null){
						taxonField.setValue(this.currInstanceNo, code, sciName, vernName, vernLang, langVariant, this.getFormScreenId(), false);	
					}						
				} else if (nodeDef instanceof FileAttributeDefinition){
					
				}
			}
		}
	}
	
    public void startCamera(PhotoField photoField){
		Intent cameraIntent = new Intent(this, CameraActivity.class);
		EntityDefinition rootEntityDef = (EntityDefinition)ApplicationManager.getSurvey().getSchema().getDefinitionById(ApplicationManager.currRootEntityId);
		Entity rootEntity = ApplicationManager.currentRecord.getRootEntity();
		AttributeDefinition keyAttrDef = rootEntityDef.getKeyAttributeDefinitions().get(0);		
		Value keyAttributeValue = (Value)rootEntity.getValue(keyAttrDef.getName(),0);
		cameraIntent.putExtra("plotId", convertValueToString(keyAttributeValue,keyAttrDef));
		cameraIntent.putExtra("fieldName", photoField.getNodeDefinition().getName());
		this.startActivityForResult(cameraIntent,getResources().getInteger(R.integer.cameraStarted));
	}
    
    public void startInternalGps(CoordinateField coordField){
		Intent gpsIntent = new Intent(this, GpsActivity.class); 
		this.startActivityForResult(gpsIntent,getResources().getInteger(R.integer.internalGpsStarted));
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {    	
	    super.onActivityResult(requestCode, resultCode, data);
	    try{
	 	    if (requestCode==getResources().getInteger(R.integer.cameraStarted)){
	 	    	if (resultCode==getResources().getInteger(R.integer.photoTaken)){
	 	    		this.photoPath = data.getStringExtra(getResources().getString(R.string.photoPath));
	 	    	}
	 	    } else if (requestCode==getResources().getInteger(R.integer.internalGpsStarted)){
	 	    	if (resultCode==getResources().getInteger(R.integer.internalGpsLocationReceived)){
	 	    		this.latitude = data.getStringExtra(getResources().getString(R.string.latitude));
	 	    		this.longitude = data.getStringExtra(getResources().getString(R.string.longitude));
	 	    		AlertMessage.createPositiveDialog(FormScreen.this, true, null,
							getResources().getString(R.string.gettingCoordsFinishedTitle), 
							getResources().getString(R.string.gettingCoordsSuccessMessage),
								getResources().getString(R.string.okay),
					    		new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										
									}
								},
								null).show();
	 	    	} else {
	 	    		AlertMessage.createPositiveDialog(FormScreen.this, true, null,
							getResources().getString(R.string.gettingCoordsFinishedTitle), 
							getResources().getString(R.string.gettingCoordsFailureMessage),
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
    
    /*private String formatDate(Date dateValue){
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
			formattedDateValue = year+getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator);		    							
		} else if (dateValue.getMonth()==null && dateValue.getYear()==null){
			formattedDateValue = getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator)+day;
		} else if (dateValue.getDay()==null && dateValue.getYear()==null){
			formattedDateValue = getResources().getString(R.string.dateSeparator)+month+getResources().getString(R.string.dateSeparator);
		} else if (dateValue.getMonth()==null){
			formattedDateValue = year+getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator)+day;		    							
		} else if (dateValue.getDay()==null){
			formattedDateValue = year+getResources().getString(R.string.dateSeparator)+month+getResources().getString(R.string.dateSeparator);
		} else if (dateValue.getYear()==null){
			formattedDateValue = getResources().getString(R.string.dateSeparator)+month+getResources().getString(R.string.dateSeparator)+day;
		} else {
			formattedDateValue = year+getResources().getString(R.string.dateSeparator)+month+getResources().getString(R.string.dateSeparator)+day;
		}
		return formattedDateValue;
    }*/
    
/*	public void setScreenOrientation(){
		String screenOrientation = ApplicationManager.appPreferences.getString(getResources().getString(R.string.screenOrientation), getResources().getString(R.string.defaultScreenOrientation)); 
		if (screenOrientation.equals("vertical")){
    		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	} else if (screenOrientation.equals("horizontal")){
    		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);	
    	} else {
    		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    	}
	}*/
    
    private void addEntityDefinitionNode(NodeDefinition nodeDef){
		if (ApplicationManager.currentRecord.getRootEntity().getId()!=nodeDef.getId()){
			Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0/*FormScreen.this.currInstanceNo*/);
			if (foundNode==null&&(nodeDef.isMultiple())){
				//EntityBuilder.addEntity(FormScreen.this.parentEntitySingleAttribute, ApplicationManager.getSurvey().getSchema().getDefinitionById(nodeDef.getId()).getName(), 0);
			}
		}		
		EntityDefinition entityDef = (EntityDefinition)nodeDef;

		EntityLink entityLinkView = new EntityLink(FormScreen.this, entityDef, calcNoOfCharsFitInOneLine(),FormScreen.this);
		entityLinkView.setOnClickListener(FormScreen.this);
		entityLinkView.setId(nodeDef.getId());
		FormScreen.this.ll.addView(entityLinkView);
    }
    
    private void addTextNode(NodeDefinition nodeDef,ArrayList<String> tableColHeaders, boolean setValue){
		String loadedValue = "";
		if (((TextAttributeDefinition) nodeDef).getType().toString().toUpperCase().equals(getResources().getString(R.string.shortTextField))){
			if (!nodeDef.isMultiple()){
				Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
				if (foundNode!=null){
					TextValue textValue = (TextValue)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
					if (textValue!=null)
						loadedValue = textValue.getValue();	    				
				}
				final TextField textField= new TextField(FormScreen.this, nodeDef);
				textField.setOnClickListener(FormScreen.this);
				textField.setId(nodeDef.getId());
				if (setValue)
					textField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
				textField.addTextChangedListener(new TextWatcher(){
			        public void afterTextChanged(Editable s) {        			            
			        	textField.setValue(0, s.toString(), FormScreen.this.getFormScreenId(),true);
			        }
			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			        public void onTextChanged(CharSequence s, int start, int before, int count){}
			    });
				ApplicationManager.putUIElement(textField.getId(), textField);
				FormScreen.this.ll.addView(textField);
			} else if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
				Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo);
				if (foundNode!=null){
					TextValue textValue = (TextValue)FormScreen.this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), FormScreen.this.currInstanceNo);
					if (textValue!=null)
						loadedValue = textValue.getValue();	    				
				}
				final TextField textField= new TextField(FormScreen.this, nodeDef);
				textField.setOnClickListener(FormScreen.this);
				textField.setId(nodeDef.getId());
				if (setValue)
					textField.setValue(FormScreen.this.currInstanceNo, loadedValue, FormScreen.this.parentFormScreenId,false);
				textField.addTextChangedListener(new TextWatcher(){
			        public void afterTextChanged(Editable s) {        			            
			        	textField.setValue(FormScreen.this.currInstanceNo, s.toString(), FormScreen.this.parentFormScreenId,true);
			        }
			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			        public void onTextChanged(CharSequence s, int start, int before, int count){}
			    });
				ApplicationManager.putUIElement(textField.getId(), textField);
				FormScreen.this.ll.addView(textField);
			} else {//multiple attribute summary    			    		
				SummaryTable summaryTableView = new SummaryTable(FormScreen.this, nodeDef, tableColHeaders, parentEntitySingleAttribute, FormScreen.this);
				summaryTableView.setOnClickListener(FormScreen.this);
				summaryTableView.setId(nodeDef.getId());
				FormScreen.this.ll.addView(summaryTableView);
			}
		} else {//memo field
			if (!nodeDef.isMultiple()){
				Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
				if (foundNode!=null){
					TextValue textValue = (TextValue)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
					if (textValue!=null)
						loadedValue = textValue.getValue();	    				
				}
				final MemoField memoField= new MemoField(FormScreen.this, nodeDef);
				memoField.setOnClickListener(FormScreen.this);
				memoField.setId(nodeDef.getId());
				if (setValue)
					memoField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
				memoField.addTextChangedListener(new TextWatcher(){
			        public void afterTextChanged(Editable s) {        			            
			        	memoField.setValue(0, s.toString(), FormScreen.this.getFormScreenId(),true);
			        }
			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			        public void onTextChanged(CharSequence s, int start, int before, int count){}
			    });
				ApplicationManager.putUIElement(memoField.getId(), memoField);
				FormScreen.this.ll.addView(memoField);
			} else if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
				Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo);
				if (foundNode!=null){
					TextValue textValue = (TextValue)FormScreen.this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), FormScreen.this.currInstanceNo);
					if (textValue!=null)
						loadedValue = textValue.getValue();	    				
				}
				final MemoField memoField= new MemoField(FormScreen.this, nodeDef);
				memoField.setOnClickListener(FormScreen.this);
				memoField.setId(nodeDef.getId());
				if (setValue)
					memoField.setValue(FormScreen.this.currInstanceNo, loadedValue, FormScreen.this.parentFormScreenId,false);
				memoField.addTextChangedListener(new TextWatcher(){
			        public void afterTextChanged(Editable s) {        			            
			        	memoField.setValue(FormScreen.this.currInstanceNo, s.toString(), FormScreen.this.parentFormScreenId,true);
			        }
			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			        public void onTextChanged(CharSequence s, int start, int before, int count){}
			    });
				ApplicationManager.putUIElement(memoField.getId(), memoField);
				FormScreen.this.ll.addView(memoField);
			} else {//multiple attribute summary    			    		
				SummaryTable summaryTableView = new SummaryTable(FormScreen.this, nodeDef, tableColHeaders, parentEntitySingleAttribute, FormScreen.this);
				summaryTableView.setOnClickListener(FormScreen.this);
				summaryTableView.setId(nodeDef.getId());
				FormScreen.this.ll.addView(summaryTableView);
			}
		}
    }
    
    private void addNumberNode(NodeDefinition nodeDef, ArrayList<String> tableColHeaders, boolean setValue, boolean checkForNullNode){
    	String loadedValue = "";
    	boolean isNullNode = true;
		if (!nodeDef.isMultiple()){
			if (checkForNullNode){
				Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
				isNullNode = (foundNode==null)?true:false;
			}			
			if (!isNullNode){
				if (((NumberAttributeDefinition) nodeDef).isInteger()){
					IntegerValue intValue = (IntegerValue)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
					if (intValue!=null)
						if (intValue.getValue()!=null)
							loadedValue = intValue.getValue().toString();
						else 
							loadedValue = "";
				} else {
					RealValue realValue = (RealValue)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
					if (realValue!=null)
						if (realValue.getValue()!=null)
							loadedValue = realValue.getValue().toString();
						else 
							loadedValue = "";
				}
			}
			final NumberField numberField= new NumberField(FormScreen.this, nodeDef);
			numberField.setOnClickListener(FormScreen.this);
			numberField.setId(nodeDef.getId());
			if (setValue)
				numberField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
			numberField.addTextChangedListener(new TextWatcher(){
		        public void afterTextChanged(Editable s) {        			            
		        	numberField.setValue(0, s.toString(), FormScreen.this.getFormScreenId(),true);
		        }
		        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		        public void onTextChanged(CharSequence s, int start, int before, int count){}
		    });
			
			ApplicationManager.putUIElement(numberField.getId(), numberField);			
			FormScreen.this.ll.addView(numberField);
		} else if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){			
			if (checkForNullNode){
				Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo);
				isNullNode = (foundNode==null)?true:false;
			}			
			if (!isNullNode){
				if (((NumberAttributeDefinition) nodeDef).isInteger()){
					IntegerValue intValue = (IntegerValue)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
					if (intValue!=null)
						if (intValue.getValue()!=null)
							loadedValue = intValue.getValue().toString();
						else 
							loadedValue = "";
				} else {
					RealValue realValue = (RealValue)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
					if (realValue!=null)
						if (realValue.getValue()!=null)
							loadedValue = realValue.getValue().toString();
						else 
							loadedValue = "";
				}
			}
			final NumberField numberField= new NumberField(FormScreen.this, nodeDef);
			numberField.setOnClickListener(FormScreen.this);
			numberField.setId(nodeDef.getId());
			if (setValue)
				numberField.setValue(FormScreen.this.currInstanceNo, loadedValue, FormScreen.this.parentFormScreenId,false);
			numberField.addTextChangedListener(new TextWatcher(){
		        public void afterTextChanged(Editable s) {        			            
		        	numberField.setValue(FormScreen.this.currInstanceNo, s.toString(), FormScreen.this.parentFormScreenId,true);
		        }
		        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		        public void onTextChanged(CharSequence s, int start, int before, int count){}
		    });
			ApplicationManager.putUIElement(numberField.getId(), numberField);
			FormScreen.this.ll.addView(numberField);
		} else {//multiple attribute summary    			    		
			SummaryTable summaryTableView = new SummaryTable(FormScreen.this, nodeDef, tableColHeaders, parentEntitySingleAttribute, FormScreen.this);
			summaryTableView.setOnClickListener(FormScreen.this);
			summaryTableView.setId(nodeDef.getId());
			FormScreen.this.ll.addView(summaryTableView);
		}
    }
    private void addBooleanNode(NodeDefinition nodeDef, ArrayList<String> tableColHeaders){
    	String loadedValue = "";
		if (!nodeDef.isMultiple()){
			
			Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
			if (foundNode!=null){
				BooleanValue boolValue = (BooleanValue)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
				if (boolValue!=null){
					if (boolValue.getValue()!=null)
						loadedValue = boolValue.getValue().toString();
				}
			}
			
			BooleanField boolField = null;
			if (loadedValue.equals("")){
				boolField = new BooleanField(this, nodeDef, false, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
			} else if (loadedValue.equals("false")) {
				boolField = new BooleanField(this, nodeDef, false, true, getResources().getString(R.string.yes), getResources().getString(R.string.no));
			} else {
				boolField = new BooleanField(this, nodeDef, true, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
			}
			boolField.setOnClickListener(this);
			boolField.setId(nodeDef.getId());       				
			ApplicationManager.putUIElement(boolField.getId(), boolField);
			this.ll.addView(boolField);
		} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
			Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
			if (foundNode!=null){
				BooleanValue boolValue = (BooleanValue)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
				if (boolValue!=null){
					if (boolValue.getValue()!=null)
						loadedValue = boolValue.getValue().toString();
				}
			}
			BooleanField boolField = null;
			if (loadedValue.equals("")){
				boolField = new BooleanField(this, nodeDef, false, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
			} else if (loadedValue.equals("false")) {
				boolField = new BooleanField(this, nodeDef, false, true, getResources().getString(R.string.yes), getResources().getString(R.string.no));
			} else {
				boolField = new BooleanField(this, nodeDef, true, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
			}
			boolField.setOnClickListener(this);
			boolField.setId(nodeDef.getId());
			ApplicationManager.putUIElement(boolField.getId(), boolField);
			this.ll.addView(boolField);
		} else {//multiple attribute summary    			    		
			SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
			summaryTableView.setOnClickListener(this);
			summaryTableView.setId(nodeDef.getId());
			this.ll.addView(summaryTableView);
		}
    }
    
    @SuppressWarnings("deprecation")
	private String convertValueToString(Value value, NodeDefinition nodeDef){
		String valueToReturn = null;
		if (value!=null){
			if (value instanceof TextValue){
				TextValue textValue = (TextValue)value;
				valueToReturn = textValue.getValue();
			} else if (value instanceof NumberValue){
				NumberValue<?> numberValue = (NumberValue<?>)value;
				if (numberValue.getValue()==null){
					valueToReturn = "";
				} else {
					if (((NumberAttributeDefinition) nodeDef).isInteger()){
						valueToReturn = String.valueOf(numberValue.getValue().intValue());	
					} else {
						valueToReturn = String.valueOf(numberValue.getValue().doubleValue());
					}	
				}				
			} else if (value instanceof BooleanValue){
				BooleanValue booleanValue = (BooleanValue)value;
				if (booleanValue.getValue()!=null)
					valueToReturn = String.valueOf(booleanValue.getValue());
			} else if (value instanceof Code){
				Code codeValue = (Code)value;
				CodeAttributeDefinition codeDef = (CodeAttributeDefinition)nodeDef;
				if (codeValue.getCode()!=null && !codeValue.getCode().equals("null") && !codeValue.getCode().equals("")){
					try{
						valueToReturn = ApplicationManager.getSurvey().getCodeList(codeDef.getList().getName()).findItem(codeValue.getCode()).getLabel(null);//codeValue.getCode();		
					} catch (NullPointerException e){
						valueToReturn = codeValue.getCode();	
					}
				}
			} else if (value instanceof RealRange){
				RealRange rangeValue = (RealRange)value;
				valueToReturn = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
			} else if (value instanceof IntegerRange){
				IntegerRange rangeValue = (IntegerRange)value;
				valueToReturn = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
			} else if (value instanceof Coordinate){
				Coordinate coordinateValue = (Coordinate)value;
				if (coordinateValue.getX()==null && coordinateValue.getY()==null){
					valueToReturn = "";
				} else if (coordinateValue.getX()==null) {
					valueToReturn = getResources().getString(R.string.coordinateSeparator)+coordinateValue.getY();
				} else if (coordinateValue.getY()==null) {
					valueToReturn = coordinateValue.getX()+getResources().getString(R.string.coordinateSeparator);
				} else {
					valueToReturn = coordinateValue.getX()+getResources().getString(R.string.coordinateSeparator)+coordinateValue.getY();	
				}
			} else if (value instanceof Date){
				Date dateValue = (Date)value;
				String day = "";
				String month = "";
				String year = "";
				if (dateValue.getDay()!=null)
					day = dateValue.getDay().toString();
				if (dateValue.getMonth()!=null)
					month = dateValue.getMonth().toString();
				if (dateValue.getYear()!=null)
					year = dateValue.getYear().toString();
				valueToReturn = year+getResources().getString(R.string.dateSeparator)+month+getResources().getString(R.string.dateSeparator)+day;
			} else if (value instanceof Time){
				Time timeValue = (Time)value;
				String hour = "";
				String minute = "";
				if (timeValue.getHour()!=null){
					hour = timeValue.getHour().toString();
					if (timeValue.getHour()<10){
						hour = "0"+hour;
					}	
				}					
				if (timeValue.getMinute()!=null){
					minute = timeValue.getMinute().toString();
					if (timeValue.getMinute()<10){
						minute = "0"+minute;
					}
				}					
				valueToReturn = hour+getResources().getString(R.string.timeSeparator)+minute;
			} else if (value instanceof TaxonOccurrence){
				TaxonOccurrence taxonValue = (TaxonOccurrence)value;
				String code = "";
				String sciName = "";
				String vernName = "";
				String vernLang = "";
				String langVariant = "";
				if (taxonValue.getCode()!=null)
					code = taxonValue.getCode();
				if (taxonValue.getScientificName()!=null)
					sciName = taxonValue.getScientificName();
				if (taxonValue.getVernacularName()!=null)
					vernName = taxonValue.getVernacularName();
				if (taxonValue.getLanguageCode()!=null)
					vernLang = taxonValue.getLanguageCode();
				if (taxonValue.getLanguageVariety()!=null)
					langVariant = taxonValue.getLanguageVariety();
				valueToReturn = code+getResources().getString(R.string.taxonSeparator)+
						sciName+getResources().getString(R.string.taxonSeparator)+
						vernName+getResources().getString(R.string.taxonSeparator)+
						vernLang+getResources().getString(R.string.taxonSeparator)+
						langVariant;
			} else if (value instanceof File){
				File fileValue = (File)value;
				if (fileValue.getFilename()!=null)
					valueToReturn = fileValue.getFilename();
			}
		}
		return valueToReturn;
	}
}