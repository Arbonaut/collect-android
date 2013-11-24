package org.openforis.collect.android.fields;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.misc.ViewBacktrack;
import org.openforis.collect.android.screens.EntityInstancesScreen;
import org.openforis.collect.android.screens.FormScreen;
import org.openforis.collect.android.service.ServiceFactory;
import org.openforis.idm.metamodel.AttributeDefinition;
import org.openforis.idm.metamodel.CodeAttributeDefinition;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.NumberAttributeDefinition;
import org.openforis.idm.model.BooleanValue;
import org.openforis.idm.model.Code;
import org.openforis.idm.model.Coordinate;
import org.openforis.idm.model.Date;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.File;
import org.openforis.idm.model.IntegerRange;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.NumberValue;
import org.openforis.idm.model.RealRange;
import org.openforis.idm.model.TaxonOccurrence;
import org.openforis.idm.model.TextValue;
import org.openforis.idm.model.Time;
import org.openforis.idm.model.Value;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class SummaryList extends UIElement {
	
	private TableLayout tableLayout;
	
	private EntityDefinition entityDefinition;
	
	//private FormScreen context;
	private EntityInstancesScreen context;

	private int instanceNo;
	
	public SummaryList(Context context, EntityDefinition entityDef, int threshold,
			OnClickListener listener, int entityInstanceNo) {
		super(context, entityDef);
		
		//this.context = (FormScreen)context;
		this.context = (EntityInstancesScreen)context;
		
		this.instanceNo = entityInstanceNo;
		
		this.tableLayout  = new TableLayout(context);  
		this.tableLayout.setStretchAllColumns(true); 
	    this.tableLayout.setShrinkAllColumns(true);
		this.tableLayout.setPadding(5, 10, 5, 10);
		
		this.entityDefinition = entityDef;
		//Log.e("entityDef",this.entityDefinition.getName()+"=="+this.instanceNo);
		
		//adding the entity and its nodes if do not exist yet
		/*for (int i=0;i<entityDef.getChildDefinitions().size();i++){
			try{
				Entity parentEntity1 = ApplicationManager.currentRecord.getRootEntity();
				String screenPath = this.context.getFormScreenId();
				String[] entityPath = screenPath.split(getResources().getString(R.string.valuesSeparator2));
				int pathLength = entityPath.length;
				for (int m=2;m<pathLength;m++){
					String[] instancePath = entityPath[m].split(getResources().getString(R.string.valuesSeparator1));
					int id = Integer.valueOf(instancePath[0]);
					int instanceNo = Integer.valueOf(instancePath[1]);
					parentEntity1 = (Entity) parentEntity1.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(id).getName(), instanceNo);
					parentEntity1.setId(id);
				}
				parentEntity1 = (Entity)parentEntity1.get(entityDef.getName(), entityInstanceNo);			
			} catch (Exception e){
			
			}
		}*/
		
		ArrayList<List<String>> keysList = new ArrayList<List<String>>();
		//ArrayList<List<String>> detailsList = new ArrayList<List<String>>();
		

		
		Entity parentEntity = this.findParentEntity(this.context.getFormScreenId()).getParent();		
		//Log.e("parentEntity","=="+parentEntity.getName());
		Entity currentEntity = null;
		if (parentEntity.getName().equals(ApplicationManager.currentRecord.getRootEntity().getName())
				&&
				entityDef.getName().equals(ApplicationManager.currentRecord.getRootEntity().getName())){
			currentEntity = parentEntity;
			parentEntity = null;
		} else {
			currentEntity = (Entity)parentEntity.get(entityDef.getName(), entityInstanceNo);
		}
		
		boolean isAtLeastOneKeyValue = false;
		//Log.e("currentEntity","=="+currentEntity.getName());
		//if (this.context.getFormScreenId()!=null){			
			//fetching keys and their values
		List<AttributeDefinition> keyAttrDefsList = entityDef.getKeyAttributeDefinitions();
		for (AttributeDefinition attrDef : keyAttrDefsList){
			List<String> key = new ArrayList<String>();
			Value attrValue = null;			
			attrValue = (Value)currentEntity.getValue(attrDef.getName(),0);
			//key.add(attrDef.getName());
			String label = ApplicationManager.getLabel(attrDef);
			key.add(label);
			String stringValue = convertValueToString(attrValue, (NodeDefinition)attrDef);
			if (stringValue!=null && !stringValue.equals("")){
				Log.e("keyAttribute","=="+attrDef.getName());
				key.add(stringValue);
				isAtLeastOneKeyValue = true;
			}					
			keysList.add(key);
		}
			
			String keysLine = "";
			Log.e("has>1key","=="+isAtLeastOneKeyValue);
			if (isAtLeastOneKeyValue){
				for (List<String> key : keysList){
					if (key.size()==1){
						keysLine += key.get(0) + getResources().getString(R.string.valuesSeparator1);
					} else {
						keysLine += key.get(0) + getResources().getString(R.string.valuesEqualsTo) + key.get(1) + getResources().getString(R.string.valuesSeparator1);	
					}
					if (keysLine.length()>threshold){
						break;
					}
					isAtLeastOneKeyValue = false;
				}
			} else {
				
				keysLine += (entityInstanceNo+1);
				Log.e("keysLinewithNUMBER","="+keysLine);
			}
			
			Log.e("threshold==keysLine",threshold+"=="+keysLine.length());
			if (keysLine.length()>threshold){
				keysLine = keysLine.substring(0,threshold-3)+"...";
			} else {
				if (!keysLine.equals("")&&keysLine.length()>1){
					keysLine = keysLine.substring(0,keysLine.length()-1);	
				}				
			}
			Log.e("keysLine2","=="+keysLine);
			
			//fetching details and their values
			List<NodeDefinition> detailNodeDefsList = entityDef.getChildDefinitions();
			for (NodeDefinition nodeDef : detailNodeDefsList){
				List<String> detail = new ArrayList<String>();
				Value attrValue = null;
				if (nodeDef instanceof EntityDefinition){
					attrValue = new TextValue("entitydefinitionnode");
				} else {
					attrValue = (Value)currentEntity.getValue(nodeDef.getName(),0);	
				}
				detail.add(nodeDef.getName());
				String stringValue = convertValueToString(attrValue, nodeDef);
				if (stringValue!=null)
					detail.add(stringValue);				
				//detailsList.add(detail);
			}
			
			/*String detailsLine = "";
			for (List<String> detail : detailsList){
				if (detail.size()==1){
					detailsLine += detail.get(0) + getResources().getString(R.string.valuesSeparator1);
				} else {
					if (detail.get(1).equals("entitydefinitionnode")){
						detailsLine += "["+detail.get(0)+"]" + getResources().getString(R.string.valuesSeparator1);
					} else {
						detailsLine += detail.get(0) + getResources().getString(R.string.valuesEqualsTo) + detail.get(1) + getResources().getString(R.string.valuesSeparator1);	
					}						
				}
				
				if (detailsLine.length()>threshold){
					break;
				}
			}
			if (detailsLine.length()>threshold){
				String visibleDetails = detailsLine.substring(0,threshold-3);
				if (visibleDetails.substring(visibleDetails.length()-1, visibleDetails.length()).equals(getResources().getString(R.string.valuesSeparator1))){
					visibleDetails = visibleDetails.substring(0,visibleDetails.length()-1);
				}
				detailsLine = visibleDetails;
				detailsLine += getResources().getString(R.string.valuesNotVisibleSign);
			} else {
				detailsLine = detailsLine.substring(0,detailsLine.length()-1);
			}*/
			
			Log.e("keysLine==","=="+keysLine);
			TextView titleView = new TextView(context);
			if (this.entityDefinition.isMultiple())
				//titleView.setText(this.label.getText()+" "+(this.instanceNo+1));
				titleView.setText(this.label.getText()+" "+keysLine);
			else
				titleView.setText(this.label.getText()+" "+keysLine);
			titleView.setOnClickListener(this.context);
			this.tableLayout.addView(titleView);
			
			/*TextView tv = new TextView(context);
			tv.setText(keysLine/*+"\r\n"+detailsLine*///);
			/*tv.setId(entityInstanceNo);
			tv.setOnClickListener(listener);
			final Entity entityToRemove = currentEntity;
			final Entity parent = parentEntity;*/
			/*tv.setOnLongClickListener(new OnLongClickListener() {
		        @Override
		        public boolean onLongClick(View v) {		        
		        	AlertMessage.createPositiveNegativeDialog(SummaryList.this.context, false, getResources().getDrawable(R.drawable.warningsign),
		 					getResources().getString(R.string.deleteEntityTitle), getResources().getString(R.string.deleteEntity),
		 					getResources().getString(R.string.yes), getResources().getString(R.string.no),
		 		    		new DialogInterface.OnClickListener() {
		 						@Override
		 						public void onClick(DialogInterface dialog, int which) {
		 							//Log.i("SummaryList", "Yes-button has been pressed from " + entityToRemove.getName());
		 							Log.e("entityToRemove",SummaryList.this.instanceNo+"=="+entityToRemove.getName());
		 							ServiceFactory.getRecordManager().deleteNode(entityToRemove);
		 							
		 							//ApplicationManager.isToBeScrolled = true;
		 							ViewBacktrack viewBacktrack = null;
		 							if (SummaryList.this.form!=null){
		 								viewBacktrack = new ViewBacktrack(SummaryList.this,SummaryList.this.form.getFormScreenId());	
		 							} else {
		 								//Log.e("SummaryList.currentInstance",SummaryList.this.entityInstances.getFormScreenId(SummaryList.this.currentInstanceNo)+"=="+SummaryList.this.currentInstanceNo);
		 								viewBacktrack = new ViewBacktrack(SummaryList.this,SummaryList.this.entityInstances.getFormScreenId());
		 							}		 							
		 							ApplicationManager.selectedViewsBacktrackList.add(viewBacktrack);
		 							Toast.makeText(SummaryList.this.context, getResources().getString(R.string.entityDeletedToast), Toast.LENGTH_SHORT).show();
		 							((EntityInstancesScreen)SummaryList.this.context).onResume();
		 							//TODO: Validation. What exactly to validate if entityToRemove was deleted?
		 						}
		 					},
		 		    		new DialogInterface.OnClickListener() {
		 						@Override
		 						public void onClick(DialogInterface dialog, int which) {
		 							//Log.i("SummaryList", "No-button has been pressed from " + entityToRemove.getName());
		 							//Log.i("SummaryList", "No-button has been pressed. Parent entity is:  " + parent.getName());
		 						}
		 					},
		 					null).show();
		            return true;
		        }
		    });*/
			//tv.setOnClickListener(this.context);

			/*TableRow tr = new TableRow(context);
			tr.addView(tv);
			this.tableLayout.addView(tr);*/
		//}
		
		this.container.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		this.container.addView(this.tableLayout);
		this.addView(this.container);
	}	
	
	public void changeBackgroundColor(int backgroundColor){
		TextView titleView = (TextView)this.tableLayout.getChildAt(0);
		titleView.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		int childrenNo = this.tableLayout.getChildCount();
		for (int i=1;i<childrenNo;i++){		
			TableRow tableRowView = (TableRow)this.tableLayout.getChildAt(i);
			TextView rowView = (TextView) tableRowView.getChildAt(0);
			rowView.setBackgroundDrawable((backgroundColor!=Color.WHITE)?getResources().getDrawable(R.drawable.cellshape_white):getResources().getDrawable(R.drawable.cellshape_black));
			rowView.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		}
	}
	
	public String getTitle(){
		return this.label.getText().toString();
	}
	
	public EntityDefinition getEntityDefinition(){
		return this.entityDefinition;
	}
	
	public void setTitle(EntityDefinition entityDef){
		this.entityDefinition = entityDef;
	}
	
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
	
	public int getInstanceNo(){
		return this.instanceNo;
	}
}
