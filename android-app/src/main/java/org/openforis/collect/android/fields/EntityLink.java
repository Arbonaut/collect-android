package org.openforis.collect.android.fields;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.idm.metamodel.EntityDefinition;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author K. Waga
 *
 */
public class EntityLink extends UIElement {
	
	//private TableLayout tableLayout;
	private RelativeLayout relativeLayout;
	
	private EntityDefinition entityDefinition;
	
	//private FormScreen context;

	//private int instanceNo;
	
	public EntityLink(Context context, EntityDefinition entityDef, int threshold,
			OnClickListener listener) {
		//super(context, entityDef);
		super(context, entityDef/*, (entityDef.isMultiple())?false:(entityDef.getChildDefinitions().size()==1)?(entityDef.getChildDefinitions().get(0) instanceof EntityDefinition)?true:false:false*/);

		this.entityDefinition = entityDef;
		
		this.relativeLayout = new RelativeLayout(context);
		
		TextView titleView = new TextView(context);
		titleView.setText(this.label.getText());
		RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		titleParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		titleParams.addRule(RelativeLayout.CENTER_VERTICAL);
		titleView.setLayoutParams(titleParams);
		
		ImageView linkView = new ImageView(context);
		linkView.setBackgroundResource(R.drawable.multiple_entity_arrow_black);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(64,64);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		linkView.setLayoutParams(params);
		
		this.relativeLayout.addView(titleView);
		this.relativeLayout.addView(linkView);
		this.relativeLayout.addView(ApplicationManager.getDividerLine(context));
		
		this.container.addView(this.relativeLayout);
		this.addView(this.container);		
	}
	
	public void changeBackgroundColor(int backgroundColor){
		TextView titleView = (TextView)this.relativeLayout.getChildAt(0);
		titleView.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		ImageView linkView = (ImageView)this.relativeLayout.getChildAt(1);
		linkView.setBackgroundResource((backgroundColor!=Color.WHITE)?R.drawable.multiple_entity_arrow_black:R.drawable.multiple_entity_arrow_white);
		View dividerView = (View)this.relativeLayout.getChildAt(2);
		dividerView.setBackgroundColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
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
	
	/*private String convertValueToString(Value value, NodeDefinition nodeDef){
		String valueToReturn = null;
		if (value!=null){
			if (value instanceof TextValue){
				TextValue textValue = (TextValue)value;
				valueToReturn = textValue.getValue();
			} else if (value instanceof NumberValue){
				NumberValue<?> numberValue = (NumberValue<?>)value;
				if (((NumberAttributeDefinition) nodeDef).isInteger()){
					valueToReturn = String.valueOf(numberValue.getValue().intValue());	
				} else {
					valueToReturn = String.valueOf(numberValue.getValue().doubleValue());
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
	}*/
	
	/*public int getInstanceNo(){
		return this.instanceNo;
	}*/
}
