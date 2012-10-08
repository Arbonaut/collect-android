package org.openforis.collect.android.fields;

import android.content.Context;
import android.widget.TextView;

public abstract class Field extends UiElement{
	
	protected TextView label;
	//public ImageView scrollLeft;
	//public ImageView scrollRight;
	
	public boolean isMultiple;
	public boolean hasMultipleParent;
	
	public Field(Context context, boolean isMultiple) {
		super(context, isMultiple);
		
		this.label = new TextView(context);
		this.label.setMaxLines(1);
		
		//this.scrollLeft = new ImageView(context);			
		//this.scrollRight = new ImageView(context);				

	}

	public String getLabelText(){
		return this.label.getText().toString();
	}
	
	public void setLabelText(String label){
		this.label.setText(label);
	}
}
