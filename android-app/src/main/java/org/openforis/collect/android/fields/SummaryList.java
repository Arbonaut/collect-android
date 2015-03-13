package org.openforis.collect.android.fields;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.screens.EntityInstancesScreen;
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
import org.openforis.idm.model.NumberValue;
import org.openforis.idm.model.RealRange;
import org.openforis.idm.model.TaxonOccurrence;
import org.openforis.idm.model.TextValue;
import org.openforis.idm.model.Time;
import org.openforis.idm.model.Value;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * 
 * @author K. Waga
 *
 */
public class SummaryList extends UIElement {
	
	public TableLayout tableLayout;
	
	private EntityDefinition entityDefinition;
	
	//private FormScreen context;
	private EntityInstancesScreen context;

	private int instanceNo;
	
	public int plotNo;
	
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
		this.plotNo = -1;
		
		ArrayList<List<String>> keysList = new ArrayList<List<String>>();
		
		Entity parentEntity = this.findParentEntity(this.context.getFormScreenId()).getParent();		
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
		List<AttributeDefinition> keyAttrDefsList = entityDef.getKeyAttributeDefinitions();
		for (AttributeDefinition attrDef : keyAttrDefsList){
			List<String> key = new ArrayList<String>();
			Value attrValue = null;			
			attrValue = (Value)currentEntity.getValue(attrDef.getName(),0);
			String label = ApplicationManager.getLabel(attrDef);
			key.add(label);
			String stringValue = convertValueToString(attrValue, (NodeDefinition)attrDef);
			if (stringValue!=null && !stringValue.equals("")){
				key.add(stringValue);
				isAtLeastOneKeyValue = true;
			}
			keysList.add(key);
			if (stringValue!=null)
				this.plotNo = Integer.valueOf(stringValue);
			else 
				this.plotNo = -1;
		}
			
			String keysLine = "";
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
				if (entityDef.isMultiple())
					keysLine += (entityInstanceNo+1);
			}
			
			if (keysLine.length()>threshold){
				keysLine = keysLine.substring(0,threshold-3)+"...";
			}
			
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
			}
		
			
			TextView titleView = new TextView(context);
			if (this.entityDefinition.isMultiple())
				//titleView.setText(this.label.getText()+" "+(this.instanceNo+1));
				titleView.setText(this.label.getText()+" "+keysLine);
			else
				titleView.setText(this.label.getText()+" "+keysLine);
			titleView.setOnClickListener(this.context);
			this.tableLayout.addView(titleView);
		
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
	
	public int getInstanceNo(){
		return this.instanceNo;
	}
}
