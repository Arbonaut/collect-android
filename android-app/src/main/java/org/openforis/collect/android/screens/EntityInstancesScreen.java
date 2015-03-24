package org.openforis.collect.android.screens;

import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.fields.BooleanField;
import org.openforis.collect.android.fields.CoordinateField;
import org.openforis.collect.android.fields.Field;
import org.openforis.collect.android.fields.SummaryList;
import org.openforis.collect.android.fields.SummaryTable;
import org.openforis.collect.android.fields.TaxonField;
import org.openforis.collect.android.fields.UIElement;
import org.openforis.collect.android.logs.RunnableHandler;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.misc.ViewBacktrack;
import org.openforis.collect.android.service.ServiceFactory;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.EntityBuilder;
import org.openforis.idm.model.Node;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
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
public class EntityInstancesScreen extends BaseActivity implements OnClickListener {
	
	private static final String TAG = "EntityInstancesScreen";

	private ScrollView sv;			
    private LinearLayout ll;
    private LinearLayout mainLayout;
	
	private Intent startingIntent;
	private String parentFormScreenId;
	private String breadcrumb;
	private String screenTitle;
	private int intentType;
	//private int fieldsNo;
	private int idmlId;
	//public int currInstanceNo;
	public int plotId;
	
	public Entity parentEntity;
	public Entity parentEntitySingleAttribute;
	public Entity parentEntityMultipleAttribute;
	
	private boolean isTextViewClicked = false;
	
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
    		//this.currInstanceNo = this.startingIntent.getIntExtra(getResources().getString(R.string.instanceNo),-1);
    		//this.numberOfInstances = this.startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances),-1);
    		this.parentFormScreenId = this.startingIntent.getStringExtra(getResources().getString(R.string.parentFormScreenId));;
    		this.plotId = this.startingIntent.getIntExtra(getResources().getString(R.string.plotId),-1);
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
			EntityInstancesScreen.this.sv = new ScrollView(EntityInstancesScreen.this);
			EntityInstancesScreen.this.ll = new LinearLayout(EntityInstancesScreen.this);
			EntityInstancesScreen.this.ll.setOrientation(android.widget.LinearLayout.VERTICAL);
			EntityInstancesScreen.this.sv.addView(ll);
			
			EntityInstancesScreen.this.mainLayout = new LinearLayout(EntityInstancesScreen.this);
			EntityInstancesScreen.this.mainLayout.setOrientation(android.widget.LinearLayout.VERTICAL);

			if (!EntityInstancesScreen.this.breadcrumb.equals("")){				
				TextView breadcrumb = new TextView(EntityInstancesScreen.this);
				if (EntityInstancesScreen.this.intentType != getResources().getInteger(R.integer.singleEntityIntent)){
					if (EntityInstancesScreen.this.intentType == getResources().getInteger(R.integer.multipleEntityIntent)){
						breadcrumb.setText(EntityInstancesScreen.this.breadcrumb/*.substring(0, EntityInstancesScreen.this.breadcrumb.lastIndexOf(" "))/*+" "+(EntityInstancesScreen.this.currInstanceNo+1)*/);	
					} else{
						breadcrumb.setText(EntityInstancesScreen.this.breadcrumb);//+" "+(EntityInstancesScreen.this.currInstanceNo+1));	
					}
				}
				else
					breadcrumb.setText(EntityInstancesScreen.this.breadcrumb);
				int pixels = (int) (getResources().getInteger(R.integer.breadcrumbFontSize) * ApplicationManager.dpiScale + 0.5f);
	    		breadcrumb.setTextSize(pixels/*getResources().getInteger(R.integer.breadcrumbFontSize)*/);
	    		breadcrumb.setSingleLine();
	    		HorizontalScrollView scroller = new HorizontalScrollView(EntityInstancesScreen.this);
	    		scroller.addView(breadcrumb);
	    		//EntityInstancesScreen.this.ll.addView(scroller);
	    		EntityInstancesScreen.this.mainLayout.addView(scroller);
	    		//EntityInstancesScreen.this.ll.addView(ApplicationManager.getDividerLine(this));
	    		EntityInstancesScreen.this.mainLayout.addView(ApplicationManager.getDividerLine(this));
	    		
	    		TextView screenTitle = new TextView(EntityInstancesScreen.this);
	    		screenTitle.setText(EntityInstancesScreen.this.screenTitle);
	    		pixels = (int) (getResources().getInteger(R.integer.screenTitleFontSize) * ApplicationManager.dpiScale + 0.5f);
	    		screenTitle.setTextSize(pixels/*getResources().getInteger(R.integer.screenTitleFontSize)*/);
	    		//EntityInstancesScreen.this.ll.addView(screenTitle);
	    		EntityInstancesScreen.this.mainLayout.addView(screenTitle);
	    		//EntityInstancesScreen.this.ll.addView(ApplicationManager.getDividerLine(this));
	    		EntityInstancesScreen.this.mainLayout.addView(ApplicationManager.getDividerLine(this));
			}
			
			NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(EntityInstancesScreen.this.startingIntent.getIntExtra(getResources().getString(R.string.idmlId), -1));
			if (nodeDef.isMultiple()){
				//this.ll.addView(arrangeButtonsInLine(new Button(this), getResources().getString(R.string.addInstanceButton), this, true));
				this.mainLayout.addView(arrangeButtonsInLine(new Button(this), getResources().getString(R.string.addInstanceButton), this, true));
				//this.ll.addView(ApplicationManager.getDividerLine(this));
				this.mainLayout.addView(ApplicationManager.getDividerLine(this));
			}
			
			EntityInstancesScreen.this.parentEntitySingleAttribute = EntityInstancesScreen.this.findParentEntity(this.getFormScreenId());
			
			if (EntityInstancesScreen.this.parentEntitySingleAttribute==null){
				EntityInstancesScreen.this.parentEntitySingleAttribute = EntityInstancesScreen.this.findParentEntity2(this.getFormScreenId());				
			}

			if (ApplicationManager.currentRecord.getRootEntity().getId()!=nodeDef.getId()){
				try{
					Node<?> foundNode = EntityInstancesScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
					
					if (foundNode==null){
						//EntityBuilder.addEntity(EntityInstancesScreen.this.parentEntitySingleAttribute, ApplicationManager.getSurvey().getSchema().getDefinitionById(nodeDef.getId()).getName(), 0);
					}
				} catch (IllegalArgumentException e){					
					EntityInstancesScreen.this.parentEntitySingleAttribute = EntityInstancesScreen.this.parentEntitySingleAttribute.getParent();
				} catch (NullPointerException e){
					e.printStackTrace();
				}
			}
			Entity tempEntity = EntityInstancesScreen.this.parentEntitySingleAttribute;
			boolean error = false;
			try {
				EntityInstancesScreen.this.parentEntitySingleAttribute = (Entity) EntityInstancesScreen.this.parentEntitySingleAttribute.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(nodeDef.getId()).getName(), 0);
				error = false;
			} catch (IllegalArgumentException e){
				error = true;
				EntityInstancesScreen.this.parentEntitySingleAttribute = EntityInstancesScreen.this.parentEntitySingleAttribute.getParent();
			} catch (ClassCastException e){
				error = true;
				EntityInstancesScreen.this.parentEntitySingleAttribute = EntityInstancesScreen.this.parentEntitySingleAttribute.getParent();
			} catch (NullPointerException e){
				error = true;
				e.printStackTrace();				
			}
			if (!error){
				EntityInstancesScreen.this.parentEntitySingleAttribute = tempEntity;
			}

			EntityDefinition entityDef = (EntityDefinition)nodeDef;
			int instanceNo = EntityInstancesScreen.this.parentEntitySingleAttribute.getCount(entityDef.getName());
			for (int e=0;e<instanceNo;e++){
				SummaryList summaryListView = new SummaryList(EntityInstancesScreen.this, entityDef, 45, EntityInstancesScreen.this,e);
				summaryListView.setOnClickListener(EntityInstancesScreen.this);
				summaryListView.setId(nodeDef.getId());
				//summaryListView.setId(e);
				EntityInstancesScreen.this.ll.addView(summaryListView);
				EntityInstancesScreen.this.ll.addView(ApplicationManager.getDividerLine(EntityInstancesScreen.this));
				registerForContextMenu(summaryListView);
				TextView titleView = (TextView)summaryListView.tableLayout.getChildAt(0);
				registerForContextMenu(titleView);
			}
			
			EntityInstancesScreen.this.mainLayout.addView(sv);	
			setContentView(EntityInstancesScreen.this.mainLayout);
			
			int backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);		
			changeBackgroundColor(backgroundColor);
	
			ApplicationManager.pd.dismiss();
			
	    	if (this.plotId>-1){
	    		//opening specific plot	    		
	    		ApplicationManager.pd = ProgressDialog.show(this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.loadingMultipleEntitiesList));
	    		SummaryList summaryList = null;
	    		int childNo = EntityInstancesScreen.this.ll.getChildCount();
	    		for (int i=0;i<childNo;i++){
	    			View view = EntityInstancesScreen.this.ll.getChildAt(i);
	    			if (view instanceof SummaryList){
	    				summaryList = (SummaryList)view;
	    				int plotNo = summaryList.plotNo;
	    				if (plotNo==this.plotId){
	    					break;
	    				}
	    			}
	    		}
	    		this.startActivity(this.prepareIntentForNewScreen(summaryList));
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
	}
    
    @Override
    public void onPause(){    
		Log.i(getResources().getString(R.string.app_name),TAG+":onPause");
		/*if (ApplicationManager.selectedView instanceof SummaryTable){
			SummaryTable temp = (SummaryTable)ApplicationManager.selectedView;
			if (this.idmlId==temp.nodeDefinition.getId()){
				ApplicationManager.isToBeScrolled = true;	
			}
		} else if (ApplicationManager.selectedView instanceof SummaryList){
			SummaryList temp = (SummaryList)ApplicationManager.selectedView;
			if (this.idmlId==temp.nodeDefinition.getId()){
				ApplicationManager.isToBeScrolled = true;	
			}
		}*/
		super.onPause();
    }

	@Override
	public void onClick(View arg0) {
		/*if (arg0 instanceof Button){
			Button btn = (Button)arg0;
			if (btn.getId()==getResources().getInteger(R.integer.leftButtonMultipleAttribute)){
				refreshMultipleAttributeScreen(true);
			} else if (btn.getId()==getResources().getInteger(R.integer.rightButtonMultipleAttribute)){
				refreshMultipleAttributeScreen(false);				
			} else if (btn.getId()==getResources().getInteger(R.integer.leftButtonMultipleEntity)){
				refreshEntityScreen(true);
			} else if (btn.getId()==getResources().getInteger(R.integer.rightButtonMultipleEntity)){
				refreshEntityScreen(false);
			}
		} else if (arg0 instanceof TextView){
			TextView tv = (TextView)arg0;
			Object parentView = arg0.getParent().getParent().getParent().getParent();
			if (parentView instanceof SummaryList){
				SummaryList temp = (SummaryList)arg0.getParent().getParent().getParent().getParent();
				ApplicationManager.selectedView = temp;
				ApplicationManager.isToBeScrolled = false;
				this.startActivity(this.prepareIntentForNewScreen(temp));				
			} else if (parentView instanceof SummaryTable){
				SummaryTable temp = (SummaryTable)parentView;
				ApplicationManager.selectedView = temp;
				ApplicationManager.isToBeScrolled = false;
				this.startActivity(this.prepareIntentForMultipleField(temp, tv.getId(), temp.getValues()));
			} else if (parentView instanceof EntityLink){
				EntityLink temp = (EntityLink)parentView;
				ApplicationManager.selectedView = temp;
				ApplicationManager.isToBeScrolled = false;
				this.startActivity(this.prepareIntentForEntityInstancesList(temp));
			}
			
		}*/

		if (arg0 instanceof Button){			
			Button btn = (Button)arg0;
			if (btn.getId()==getResources().getInteger(R.integer.addButtonMultipleEntity)){		
				addNewEntity();
			}
		} else if (arg0 instanceof TextView){
			Object parentView = arg0.getParent().getParent().getParent();
			if (parentView instanceof SummaryList){
				ApplicationManager.pd = ProgressDialog.show(this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.loadingSavedEntity));
				SummaryList temp = (SummaryList)parentView;
				ViewBacktrack viewBacktrack = new ViewBacktrack(temp,EntityInstancesScreen.this.getFormScreenId(temp.getInstanceNo()));
				ApplicationManager.selectedViewsBacktrackList.add(viewBacktrack);
				this.startActivity(this.prepareIntentForNewScreen(temp));
			}	
		} else if (arg0 instanceof SummaryList){
			SummaryList tempList = (SummaryList)arg0;
			ViewBacktrack viewBacktrack = new ViewBacktrack(tempList,EntityInstancesScreen.this.getFormScreenId(tempList.getInstanceNo()));
			ApplicationManager.selectedViewsBacktrackList.add(viewBacktrack);				
			this.startActivity(this.prepareIntentForNewScreen(tempList));
		}
	}
	
	private void addNewEntity(){
		NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+0, -1)).getParentDefinition();
		if (!nodeDef.isMultiple()){	
			AlertMessage.createPositiveNegativeDialog(EntityInstancesScreen.this, true, null,
					getResources().getString(R.string.addNewEntityTitle), 
					getResources().getString(R.string.addNewEntityMessage),
						getResources().getString(R.string.yes),
						getResources().getString(R.string.no),
			    		new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//ApplicationManager.pd = ProgressDialog.show(EntityInstancesScreen.this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.loadingNewEntity));
								/*Node<?> foundNode = EntityInstancesScreen.this.parentEntitySingleAttribute.getParent().get(FormScreen.this.parentEntitySingleAttribute.getName(), FormScreen.this.currInstanceNo+1);
								while (foundNode!=null){
									EntityInstancesScreen.this.currInstanceNo++;
									foundNode = EntityInstancesScreen.this.parentEntitySingleAttribute.getParent().get(FormScreen.this.parentEntitySingleAttribute.getName(), FormScreen.this.currInstanceNo+1);
								}
								EntityInstancesScreen.this.currInstanceNo++;	
								refreshEntityScreenFields();*/
							}
						},
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
							}
						},
						null).show();				
		} else {
			AlertMessage.createPositiveNegativeDialog(EntityInstancesScreen.this, true, null,
					getResources().getString(R.string.addNewEntityTitle), 
					getResources().getString(R.string.addNewEntityMessage),
						getResources().getString(R.string.yes),
						getResources().getString(R.string.no),
			    		new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {								
								//refreshing values of fields in the entity
								ApplicationManager.pd = ProgressDialog.show(EntityInstancesScreen.this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.loadingNewEntity));
								Entity parentEntity = EntityInstancesScreen.this.findParentEntity(EntityInstancesScreen.this.getFormScreenId());
								
								int currentInstanceNo = 0;
								while (parentEntity!=null){
									currentInstanceNo++;
									parentEntity = EntityInstancesScreen.this.findParentEntity(EntityInstancesScreen.this.getFormScreenId(currentInstanceNo));
								}
								if (parentEntity==null){
									String path = EntityInstancesScreen.this.getFormScreenId().substring(0,EntityInstancesScreen.this.getFormScreenId().lastIndexOf(getResources().getString(R.string.valuesSeparator2)));
									parentEntity = EntityInstancesScreen.this.findParentEntity(path);
									try{
										EntityBuilder.addEntity(parentEntity, ApplicationManager.getSurvey().getSchema().getDefinitionById(EntityInstancesScreen.this.idmlId).getName());
									} catch (IllegalArgumentException e){
										parentEntity = parentEntity.getParent();								
										EntityBuilder.addEntity(parentEntity, ApplicationManager.getSurvey().getSchema().getDefinitionById(EntityInstancesScreen.this.idmlId).getName());
									}
									parentEntity = EntityInstancesScreen.this.findParentEntity(EntityInstancesScreen.this.getFormScreenId());
								}
								EntityInstancesScreen.this.parentEntitySingleAttribute = EntityInstancesScreen.this.findParentEntity(EntityInstancesScreen.this.getFormScreenId());
								EntityInstancesScreen.this.parentEntityMultipleAttribute = EntityInstancesScreen.this.findParentEntity(EntityInstancesScreen.this.parentFormScreenId);
								EntityInstancesScreen.this.onResume();
								ApplicationManager.pd.dismiss();
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
	
	private Intent prepareIntentForNewScreen(SummaryList summaryList){
		Intent intent = new Intent(this,FormScreen.class);
		if (!this.breadcrumb.equals("")){
			String title = "";
			String entityTitle = "";
			if (summaryList.getEntityDefinition().isMultiple()){
				title = this.breadcrumb+getResources().getString(R.string.breadcrumbSeparator)+summaryList.getTitle()+" "+0/*(this.currInstanceNo+1)*/;
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
		intent.putExtra(getResources().getString(R.string.parentFormScreenId), this.getFormScreenId(summaryList.getInstanceNo()));
        List<NodeDefinition> entityAttributes = summaryList.getEntityDefinition().getChildDefinitions();
        int counter = 0;
        for (NodeDefinition formField : entityAttributes){
			intent.putExtra(getResources().getString(R.string.attributeId)+counter, formField.getId());
			counter++;
        }
		return intent;
	}

	@Override
    protected void changeBackgroundColor(int backgroundColor){
    	try{
    		super.changeBackgroundColor(backgroundColor);
    		//getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));

    		boolean hasBreadcrumb = !this.breadcrumb.equals("");
    		if (hasBreadcrumb){
    			//ViewGroup scrollbarViews = ((ViewGroup)this.ll.getChildAt(0));
    			ViewGroup scrollbarViews = ((ViewGroup)this.mainLayout.getChildAt(0));
    			TextView breadcrumb = (TextView)scrollbarViews.getChildAt(0);    			
    			breadcrumb.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);	
    		}
    		
    		boolean hasTitle = !this.screenTitle.equals("");
    		if (hasTitle){
    			//View dividerLine = (View)this.ll.getChildAt(1);
    			View dividerLine = (View)this.mainLayout.getChildAt(1);
    			dividerLine.setBackgroundColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
    			//TextView screenTitle = (TextView)this.ll.getChildAt(2);
    			TextView screenTitle = (TextView)this.mainLayout.getChildAt(2);
    			screenTitle.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
    			//dividerLine = (View)this.ll.getChildAt(3);
    			dividerLine = (View)this.mainLayout.getChildAt(3);
    			dividerLine.setBackgroundColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);			
    		}
    		    		
    		//View dividerLine = (View)this.ll.getChildAt(5);
    		View dividerLine = (View)this.mainLayout.getChildAt(5);
    		if (dividerLine!=null){
    			dividerLine.setBackgroundColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);	
    		}
    		
    		//int viewsNo = this.ll.getChildCount();
    		//int viewsNo = this.mainLayout.getChildCount();
    		
    		LinearLayout fieldsLayout= (LinearLayout)this.sv.getChildAt(0);
    		int viewsNo = fieldsLayout.getChildCount();
    		//int start = (hasBreadcrumb)?1:0;
    		int start = 0;
    		for (int i=start;i<viewsNo;i++){
    			//View tempView = this.ll.getChildAt(i);
    			//View tempView = this.mainLayout.getChildAt(i);
    			View tempView = fieldsLayout.getChildAt(i);
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
    				}
    			} else if (tempView instanceof RelativeLayout){
    				RelativeLayout rLayout = (RelativeLayout)tempView;
    				LinearLayout lLayout = (LinearLayout)rLayout.getChildAt(0);
    				Button btn = (Button)lLayout.getChildAt(0);
    				btn.setBackgroundResource((backgroundColor!=Color.WHITE)?R.drawable.add_new_black:R.drawable.add_new_white);
    			} else if ((tempView instanceof View)&&(!(tempView instanceof TextView))){
    				dividerLine = (View)tempView;
    				dividerLine.setBackgroundColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
    			}
    		}
    		
    		if (!(this.mainLayout.getChildAt(4) instanceof ScrollView)){
    			RelativeLayout rLayout = (RelativeLayout)this.mainLayout.getChildAt(4);
    			LinearLayout ll = (LinearLayout)rLayout.getChildAt(0);
    			Button addBtn = (Button)ll.getChildAt(0);
				addBtn.setBackgroundResource((backgroundColor!=Color.WHITE)?R.drawable.add_new_black:R.drawable.add_new_white);
    		}
    	} catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":changeBackgroundColor",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
    	}
		
    }
    
    /*private RelativeLayout arrangeButtonsInLine(Button btnLeft, String btnLeftLabel, Button btnRight, String btnRightLabel, OnClickListener listener, boolean isForEntity){
		RelativeLayout relativeButtonsLayout = new RelativeLayout(this);
	    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
	            RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	    relativeButtonsLayout.setLayoutParams(lp);
		btnLeft.setText(btnLeftLabel);
		btnRight.setText(btnRightLabel);
		
		btnLeft.setOnClickListener(listener);
		btnRight.setOnClickListener(listener);
		
		RelativeLayout.LayoutParams lpBtnLeft = new RelativeLayout.LayoutParams(
	            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lpBtnLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		btnLeft.setLayoutParams(lpBtnLeft);
		relativeButtonsLayout.addView(btnLeft);
		
		RelativeLayout.LayoutParams lpBtnRight = new RelativeLayout.LayoutParams(
	            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lpBtnRight.addRule(RelativeLayout.RIGHT_OF,btnLeft.getId());
		lpBtnRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		btnRight.setLayoutParams(lpBtnRight);
		relativeButtonsLayout.addView(btnRight);
		
		if (!isForEntity){
			btnLeft.setId(getResources().getInteger(R.integer.leftButtonMultipleAttribute));
			btnRight.setId(getResources().getInteger(R.integer.rightButtonMultipleAttribute));	
		} else {
			btnLeft.setId(getResources().getInteger(R.integer.leftButtonMultipleEntity));
			btnRight.setId(getResources().getInteger(R.integer.rightButtonMultipleEntity));
		}
		
		return relativeButtonsLayout;
    }*/
    
    /*public String getFormScreenId(){
    	if (this.parentFormScreenId.equals("")){
    		return this.idmlId+getResources().getString(R.string.valuesSeparator1)+this.currInstanceNo;
    	} else 
    		return this.parentFormScreenId+getResources().getString(R.string.valuesSeparator2)+this.idmlId+getResources().getString(R.string.valuesSeparator1)+this.currInstanceNo;
    }*/
	
	private Entity findParentEntity(String path){
		Entity parentEntity = ApplicationManager.currentRecord.getRootEntity();
		String screenPath = path;
		String[] entityPath = screenPath.split(getResources().getString(R.string.valuesSeparator2));
		try{
			for (int m=1;m<entityPath.length;m++){
				String[] instancePath = entityPath[m].split(getResources().getString(R.string.valuesSeparator1));				
				int id = Integer.valueOf(instancePath[0]);
				int instanceNo = Integer.valueOf(instancePath[1]);
				/*try{
					parentEntity = (Entity) parentEntity.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(id).getName(), instanceNo);
				} catch (IllegalArgumentException e){
					parentEntity = parentEntity.getParent();
					parentEntity = (Entity) parentEntity.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(id).getName(), instanceNo);
				}*/

				for (int y=0;y<parentEntity.getDefinition().getChildDefinitions().size();y++){
					NodeDefinition tempNodeDef = parentEntity.getDefinition().getChildDefinitions().get(y);
					if (tempNodeDef instanceof EntityDefinition){
						if (parentEntity.getAll(tempNodeDef.getName()).size()==0){
							EntityBuilder.addEntity(parentEntity, tempNodeDef.getName(), 0);	
						}					
						
					}
				}
				parentEntity = (Entity) parentEntity.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(id).getName(), instanceNo);
			}			
		} catch (ClassCastException e){
			
		} catch (IllegalArgumentException e){
			
		}
		return parentEntity;
	}
	
	private Entity findParentEntity2(String path){
		Entity parentEntity = ApplicationManager.currentRecord.getRootEntity();
		String screenPath = path;
		String[] entityPath = screenPath.split(getResources().getString(R.string.valuesSeparator2));
		try{
			for (int m=0;m<entityPath.length-1;m++){
				String[] instancePath = entityPath[m].split(getResources().getString(R.string.valuesSeparator1));				
				int id = Integer.valueOf(instancePath[0]);
				int instanceNo = Integer.valueOf(instancePath[1]);
				
				parentEntity = (Entity) parentEntity.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(id).getName(), instanceNo);
			}			
		} catch (ClassCastException e){
			
		} catch (IllegalArgumentException e){
			
		}
		return parentEntity;
	}

    public String getFormScreenId(){
    	if (this.parentFormScreenId.equals("")){
    		return removeDuplicates(this.idmlId+getResources().getString(R.string.valuesSeparator1)+"0"/*this.currInstanceNo*/);    		
    	} else {
    		return removeDuplicates(this.parentFormScreenId+getResources().getString(R.string.valuesSeparator2)+this.idmlId+getResources().getString(R.string.valuesSeparator1)+"0"/*this.currInstanceNo*/);
    	}    		
    }
    
    public String getFormScreenId(int instanceNo){
    	if (this.parentFormScreenId.equals("")){
    		return removeDuplicates(this.idmlId+getResources().getString(R.string.valuesSeparator1)+instanceNo/*this.currInstanceNo*/);    		
    	} else {
    		return removeDuplicates(this.parentFormScreenId+getResources().getString(R.string.valuesSeparator2)+this.idmlId+getResources().getString(R.string.valuesSeparator1)+instanceNo/*this.currInstanceNo*/);
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
    
    private RelativeLayout arrangeButtonsInLine(Button btnAdd, String btnAddLabel, OnClickListener listener, boolean isForEntity){
		RelativeLayout relativeButtonsLayout = new RelativeLayout(this);
	    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
	            RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	    relativeButtonsLayout.setLayoutParams(lp);
		//btnAdd.setText(btnAddLabel);
	    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(getResources().getInteger(R.integer.addButtonWidth),getResources().getInteger(R.integer.addButtonHeight));
	    btnAdd.setLayoutParams(params);
	    btnAdd.setBackgroundResource(R.drawable.add_new_white);
		
		btnAdd.setOnClickListener(listener);
		
		LinearLayout ll = new LinearLayout(this);
		ll.addView(btnAdd);		
		relativeButtonsLayout.addView(ll);;
		
		if (!isForEntity){
			btnAdd.setId(getResources().getInteger(R.integer.addButtonMultipleAttribute));
		} else {
			btnAdd.setId(getResources().getInteger(R.integer.addButtonMultipleEntity));
		}
		
		
		return relativeButtonsLayout;
    }
	
    public void setScreenOrientation(){
		String screenOrientation = ApplicationManager.appPreferences.getString(getResources().getString(R.string.screenOrientation), getResources().getString(R.string.defaultScreenOrientation)); 
		if (screenOrientation.equals("vertical")){
    		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	} else if (screenOrientation.equals("horizontal")){
    		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);	
    	} else {
    		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    	}
	}
    
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
    	final View clickedView = v;
        if (v instanceof TextView){
        	this.isTextViewClicked = true;
    	} else {
    		this.isTextViewClicked = false;
    	}
        if (!this.isTextViewClicked){
            getMenuInflater().inflate(R.menu.context_menu, menu);
            MenuItem viewItem = menu.findItem(R.id.view);
            viewItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    //Object parentView = clickedView;//arg0.getParent().getParent().getParent();
        			if (clickedView instanceof SummaryList){
        				ApplicationManager.pd = ProgressDialog.show(EntityInstancesScreen.this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.loadingSavedEntity));
        				SummaryList temp = (SummaryList)clickedView;
        				ViewBacktrack viewBacktrack = new ViewBacktrack(temp,EntityInstancesScreen.this.getFormScreenId(temp.getInstanceNo()));
        				ApplicationManager.selectedViewsBacktrackList.add(viewBacktrack);				
        				EntityInstancesScreen.this.startActivity(EntityInstancesScreen.this.prepareIntentForNewScreen(temp));
        			}
                    return true;
                }
            });
            MenuItem deleteItem = menu.findItem(R.id.delete);
            deleteItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                	AlertMessage.createPositiveNegativeDialog(EntityInstancesScreen.this, false, getResources().getDrawable(R.drawable.warningsign),
            				getResources().getString(R.string.deleteEntityTitle), getResources().getString(R.string.deleteEntity),
            				getResources().getString(R.string.yes), getResources().getString(R.string.no),
            	    		new DialogInterface.OnClickListener() {
            					@Override
            					public void onClick(DialogInterface dialog, int which) {
            						int position = -1;
            						for (int i=0;i<EntityInstancesScreen.this.ll.getChildCount();i++){
            							if (clickedView.equals(EntityInstancesScreen.this.ll.getChildAt(i))){
            								position = i;
            								break;
            							}
            						}
            						position /= 2;
            						NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(EntityInstancesScreen.this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+"0", -1));
		 							NodeDefinition parentNodeDefinition = nodeDef.getParentDefinition();
		 							Node<?> foundNode = EntityInstancesScreen.this.parentEntitySingleAttribute/*.getParent()*/.get(parentNodeDefinition.getName(), position);		 							
		 							if (foundNode!=null){
		 								ServiceFactory.getMobileRecordManager().deleteNode(foundNode);
		 								Node<?> tempNode = EntityInstancesScreen.this.parentEntitySingleAttribute.get(parentNodeDefinition.getName(), position);
		 								if ((tempNode==null) && (position==0)){
		 									//EntityBuilder.addEntity(EntityInstancesScreen.this.parentEntitySingleAttribute/*tempEntity*/, parentNodeDefinition.getName());
		 								}
		 											 								
		 							}
	 								//refreshEntityScreen(2);
	 								Toast.makeText(EntityInstancesScreen.this, getResources().getString(R.string.entityDeletedToast), Toast.LENGTH_SHORT).show();
	 								EntityInstancesScreen.this.onResume();
            					}
            				},
            	    		new DialogInterface.OnClickListener() {
            					@Override
            					public void onClick(DialogInterface dialog, int which) {
            						
            					}
            				},
            				null).show();
                    return true;
                }
            });
            this.isTextViewClicked = false;
        }
    }

    public boolean onContextItemSelected(android.view.MenuItem item) {

        /*AdapterContextMenuInfo adapInfo = (AdapterContextMenuInfo) item
                .getMenuInfo();
        final int position = (int)adapInfo.id;*/
        switch (item.getItemId()) {
        case R.id.view:
            return true;
        case R.id.delete:        	
            return true;
        }
        return false;
    }
}