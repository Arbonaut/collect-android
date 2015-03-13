package org.openforis.collect.android.fields;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.service.ServiceFactory;
import org.openforis.collect.model.NodeChangeSet;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.TextAttribute;
import org.openforis.idm.model.TextValue;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.QwertyKeyListener;
import android.text.method.TextKeyListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 
 * @author K. Waga
 *
 */
public class TextField extends InputField {

	public TextField(Context context, NodeDefinition nodeDef) {
		super(context, nodeDef);
		
		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	String descr = TextField.this.nodeDefinition.getDescription(ApplicationManager.selectedLanguage);
	        	if (descr==null){
	        		descr="";
	        	}
	        	ToastMessage.displayToastMessage(TextField.this.getContext(), TextField.this.getLabelText()+descr, Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		this.txtBox = new EditText(context);
		this.txtBox.addTextChangedListener(this);
		this.txtBox.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
		
		this.addView(this.txtBox);
		
		// When TextField gets focus
		this.txtBox.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	// Get current settings about software keyboard for text fields
		    	if(hasFocus){
			    	if(this.getClass().toString().contains("TextField")){			    
				    	boolean valueForText = false;				   
				    	if (ApplicationManager.appPreferences!=null){
				    		valueForText = ApplicationManager.appPreferences.getBoolean(getResources().getString(R.string.showSoftKeyboardOnTextField), false);
				    	}
				    	// Switch on or off Software keyboard depend of settings
				    	if(valueForText){
				    		TextField.this.setKeyboardType(new QwertyKeyListener(TextKeyListener.Capitalize.NONE, false));
				        }
				    	else {
				    		txtBox.setInputType(InputType.TYPE_NULL);
				    	}				    	
			    	}
		    	}
		    }
	    });
	}
	
	
	public void setValue(Integer position, String value, String path, boolean isTextChanged)
	{
		if (!isTextChanged)
			this.txtBox.setText(value);
		
		Entity parentEntity = this.findParentEntity(path);
		Node<? extends NodeDefinition> node = this.findParentEntity(path).get(this.nodeDefinition.getName(), position);
		NodeChangeSet nodeChangeSet = null;
		if (node!=null){
			nodeChangeSet = ServiceFactory.getRecordManager().updateAttribute((TextAttribute)node, new TextValue(value));
		} else {
			nodeChangeSet = ServiceFactory.getRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new TextValue(value), null, null);
		}
		validateField(nodeChangeSet);
	}
}
