package org.openforis.collect.android.lists;

import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.logs.RunnableHandler;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.management.DataManager;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.screens.BaseListActivity;
import org.openforis.collect.model.CollectRecord;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.idm.metamodel.AttributeDefinition;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.NodeLabel.Type;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 
 * @author K. Waga
 *
 */
public class RecordChoiceActivity extends BaseListActivity implements OnClickListener/*implements OnItemLongClickListener*/{
	
	private static final String TAG = "RecordChoiceActivity";

	//private TextView activityLabel;
	
	private List<CollectRecord> recordsList;
	private ArrayAdapter<String> adapter;
	
	private EntityDefinition rootEntityDef;
	
	private String[] clusterList;

    private ListView lv;
    private LinearLayout mainLayout;      
    
    private static ProgressDialog pd;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //setContentView(R.layout.clusterchoiceactivity);
        try{
        	ApplicationManager.recordSelectionActivity = this;
        } catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onCreate",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
        }
    }
    
    public void onResume(){
		super.onResume();
		Log.i(getResources().getString(R.string.app_name),TAG+":onResume");
		try{
			RecordChoiceActivity.pd = ProgressDialog.show(RecordChoiceActivity.this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.loadingSavedRecordsList));
			LayoutParams lp = new LayoutParams(
		            LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			RecordChoiceActivity.this.lv = new ListView(RecordChoiceActivity.this);
			RecordChoiceActivity.this.lv.setLayoutParams(lp);
			RecordChoiceActivity.this.lv.setId(android.R.id.list);
			registerForContextMenu(lv);
			
			RecordChoiceActivity.this.mainLayout = new LinearLayout(RecordChoiceActivity.this);
			RecordChoiceActivity.this.mainLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
			
			TextView screenTitle = new TextView(RecordChoiceActivity.this);
			screenTitle.setText(getResources().getString(R.string.clusterChoiceListLabel));
			
			int pixels = (int) (getResources().getInteger(R.integer.screenTitleFontSize) * ApplicationManager.dpiScale + 0.5f);
			screenTitle.setTextSize(pixels/*getResources().getInteger(R.integer.screenTitleFontSize)*/);
			RecordChoiceActivity.this.mainLayout.addView(screenTitle);
			RecordChoiceActivity.this.mainLayout.addView(ApplicationManager.getDividerLine(this));
			
			this.mainLayout.addView(arrangeButtonsInLine(new Button(this), getResources().getString(R.string.addInstanceButton), this));
			this.mainLayout.addView(ApplicationManager.getDividerLine(this));
			
			RecordChoiceActivity.this.mainLayout.addView(lv);	
			setContentView(RecordChoiceActivity.this.mainLayout);
			
			int backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);	
			changeBackgroundColor(backgroundColor);
			
			refreshRecordsList();
			/*ApplicationManager.isRecordListUpToDate = false;
			Intent resultHolder = new Intent();
			resultHolder.putExtra(getResources().getString(R.string.recordId), 1);			
			setResult(getResources().getInteger(R.integer.clusterChoiceSuccessful),resultHolder);
			RecordChoiceActivity.this.finish();*/
		} catch (Exception e){
			RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":on",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
		}		
    }
    
    @Override
	public void onClick(View arg0) {
		if (arg0 instanceof Button){
			ApplicationManager.pd = ProgressDialog.show(this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.loadingNewRecord));
			ApplicationManager.isRecordListUpToDate = false;
			Intent resultHolder = new Intent();
			resultHolder.putExtra(getResources().getString(R.string.recordId), getResources().getInteger(R.integer.unsavedRecordId));			
			setResult(getResources().getInteger(R.integer.clusterChoiceSuccessful),resultHolder);
			RecordChoiceActivity.this.finish();	
		}
	}
    
    private RelativeLayout arrangeButtonsInLine(Button btnAdd, String btnAddLabel, OnClickListener listener){
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
		
		return relativeButtonsLayout;
    }
    
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {    	
        AdapterContextMenuInfo adapInfo = (AdapterContextMenuInfo) item
                .getMenuInfo();
        
        //String selectedName = clusterList[(int)adapInfo.id];
        final int position = (int)adapInfo.id;
        switch (item.getItemId()) {
        case R.id.view:
        	/*if (this.recordsList.size()==0){
        		ApplicationManager.pd = ProgressDialog.show(this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.loadingNewRecord));
    			Intent resultHolder = new Intent();
    			resultHolder.putExtra(getResources().getString(R.string.recordId), getResources().getInteger(R.integer.unsavedRecordId));	
    			setResult(getResources().getInteger(R.integer.clusterChoiceSuccessful),resultHolder);
    			RecordChoiceActivity.this.finish();		
    		} else {*/
    			//if (position!=recordsList.size()){
    			ApplicationManager.pd = ProgressDialog.show(this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.loadingSavedRecord));
				Intent resultHolder = new Intent();
				if (position<recordsList.size()){
					resultHolder.putExtra(getResources().getString(R.string.recordId), this.recordsList.get(position).getId());	
				} else {
					resultHolder.putExtra(getResources().getString(R.string.recordId), getResources().getInteger(R.integer.unsavedRecordId));	
				}			
				setResult(getResources().getInteger(R.integer.clusterChoiceSuccessful),resultHolder);
				RecordChoiceActivity.this.finish();	
    			//}
    		//}
            return true;
        case R.id.delete:
        	AlertMessage.createPositiveNegativeDialog(RecordChoiceActivity.this, false, getResources().getDrawable(R.drawable.warningsign),
    				getResources().getString(R.string.deleteRecordTitle), getResources().getString(R.string.deleteRecord),
    				getResources().getString(R.string.yes), getResources().getString(R.string.no),
    	    		new DialogInterface.OnClickListener() {
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    						RecordChoiceActivity.pd = ProgressDialog.show(RecordChoiceActivity.this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.deletingRecord));
    						deleteRecord(position);
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
        return false;
    }
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.i(getResources().getString(R.string.app_name),TAG+":onListItemClick");
		ApplicationManager.pd = ProgressDialog.show(this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.loadingSavedRecord));
		/*if (this.recordsList.size()==0){
			ApplicationManager.isRecordListUpToDate = false;
			Intent resultHolder = new Intent();
			resultHolder.putExtra(getResources().getString(R.string.recordId), getResources().getInteger(R.integer.unsavedRecordId));	
			setResult(getResources().getInteger(R.integer.clusterChoiceSuccessful),resultHolder);
			RecordChoiceActivity.this.finish();					
		} else {*/
			//if (position!=recordsList.size()){
			ApplicationManager.isRecordListUpToDate = false;
			Intent resultHolder = new Intent();
			if (position<recordsList.size()){
				resultHolder.putExtra(getResources().getString(R.string.recordId), this.recordsList.get(position).getId());	
			} else {
				resultHolder.putExtra(getResources().getString(R.string.recordId), getResources().getInteger(R.integer.unsavedRecordId));	
			}			
			setResult(getResources().getInteger(R.integer.clusterChoiceSuccessful),resultHolder);
			RecordChoiceActivity.this.finish();	
			//}
		//}		
	}
    
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }
	
	@Override
	public void onBackPressed() { 
		setResult(getResources().getInteger(R.integer.backButtonPressed), new Intent());
		RecordChoiceActivity.this.finish();
	}
	
    private void changeBackgroundColor(int backgroundColor){
		//this.activityLabel.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));

		TextView screenTitle = (TextView)this.mainLayout.getChildAt(0);
		screenTitle.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		View dividerLine = (View)this.mainLayout.getChildAt(1);
		dividerLine.setBackgroundColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);			
	
		    		
		//View dividerLine = (View)this.ll.getChildAt(5);
		dividerLine = (View)this.mainLayout.getChildAt(3);
		if (dividerLine!=null){
			dividerLine.setBackgroundColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);	
		}
		
		if (!(this.mainLayout.getChildAt(2) instanceof ScrollView)){
			RelativeLayout rLayout = (RelativeLayout)this.mainLayout.getChildAt(2);
			LinearLayout ll = (LinearLayout)rLayout.getChildAt(0);
			Button addBtn = (Button)ll.getChildAt(0);
			addBtn.setBackgroundResource((backgroundColor!=Color.WHITE)?R.drawable.add_new_black:R.drawable.add_new_white);
		}
    }

	/*@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
			long id) {
		if ((recordsList.size()!=0) && (position<recordsList.size())){
			final int number = position;
			AlertMessage.createPositiveNegativeDialog(RecordChoiceActivity.this, false, getResources().getDrawable(R.drawable.warningsign),
					getResources().getString(R.string.deleteRecordTitle), getResources().getString(R.string.deleteRecord),
					getResources().getString(R.string.yes), getResources().getString(R.string.no),
		    		new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {							
							ApplicationManager.dataManager.deleteRecord(number);
							refreshRecordsList();
						}
					},
		    		new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					},
					null).show();
		}		
		return false;
	}*/
	
	public void refreshRecordsList(){
		 final Handler handler = new Handler(){
			    @Override
			    public void handleMessage(Message msg) {
			    	int layout = (backgroundColor!=Color.WHITE)?R.layout.localclusterrow_white:R.layout.localclusterrow_black;
					RecordChoiceActivity.this.adapter = new ArrayAdapter<String>(RecordChoiceActivity.this, layout, R.id.plotlabel, clusterList);
				    RecordChoiceActivity.this.lv.setAdapter(RecordChoiceActivity.this.adapter);
					//RecordChoiceActivity.this.setListAdapter(RecordChoiceActivity.this.adapter);
				    if (clusterList[0].equals("")){
				    	RecordChoiceActivity.this.lv.setVisibility(View.GONE);
				    } else {
				    	RecordChoiceActivity.this.lv.setVisibility(View.VISIBLE);
				    }
				    RecordChoiceActivity.pd.dismiss();
			    }};
		  new Thread(new Runnable() {
			    public void run() {	    	
			    	RecordChoiceActivity.this.rootEntityDef = ApplicationManager.getSurvey().getSchema().getRootEntityDefinition(getIntent().getIntExtra(getResources().getString(R.string.rootEntityId),1));					
					CollectSurvey collectSurvey = (CollectSurvey)ApplicationManager.getSurvey();	        	
			    	DataManager dataManager = new DataManager(RecordChoiceActivity.this,collectSurvey,RecordChoiceActivity.this.rootEntityDef.getName(),ApplicationManager.getLoggedInUser());
			    	if (!ApplicationManager.isRecordListUpToDate){
			    		ApplicationManager.recordsList = dataManager.loadSummaries();
			    	}
			    	RecordChoiceActivity.this.recordsList = ApplicationManager.recordsList;
			    	/*if (RecordChoiceActivity.this.recordsList.size()==0){
			    		Intent resultHolder = new Intent();
						resultHolder.putExtra(getResources().getString(R.string.recordId), getResources().getInteger(R.integer.unsavedRecordId));	
						setResult(getResources().getInteger(R.integer.clusterChoiceSuccessful),resultHolder);
						RecordChoiceActivity.this.finish();	
			    	}*/
					
					if (RecordChoiceActivity.this.recordsList.size()==0){
						clusterList = new String[1];
						clusterList[0]="";
					} else {
						clusterList = new String[recordsList.size()/*+2*/];
					}									
					for (int i=0;i<recordsList.size();i++){
						CollectRecord record = recordsList.get(i);
						List<String> keyValues = record.getRootEntityKeyValues();
						Log.e("keyValuesSIZE","=="+keyValues.size());
						for (int g=0;g<keyValues.size();g++){
							//Log.e("attr"+g,"=="+keyAttr.get(g).getName());
							Log.e("value"+g,"=="+keyValues.get(g));
						}
						clusterList[i] = /*record.getId()+"=="+*/(i+1)//+" "+record.getCreatedBy().getName()
								+"\r\n"+record.getCreationDate();
						if (record.getModifiedDate()!=null){
							clusterList[i] += "\r\n"+record.getModifiedDate();
						}
						CollectRecord currentRecord = null;
						List<AttributeDefinition> attrDefs = null;
						if (keyValues.size()>0){
							Log.e("moreKEYS","=="+keyValues.size());
							currentRecord = dataManager.loadRecord(record.getId());
							attrDefs = currentRecord.getRootEntity().getDefinition().getKeyAttributeDefinitions();
							Log.e("attrDefs.size","=="+attrDefs.size());
						}
						for (int j=0;j<keyValues.size();j++){
							String key = keyValues.get(j);
							Log.e("value","=="+key);
							if (key!=null){
								String label = attrDefs.get(j).getLabel(Type.INSTANCE, ApplicationManager.selectedLanguage);
								Log.e("label","=="+label);
								if (label!=null)
									clusterList[i] += "\r\n"+label+": "+key;
							}				
						}
						
					}
					/*if (RecordChoiceActivity.this.recordsList.size()==0){		
						clusterList[0]=getResources().getString(R.string.addNewRecord)+" "+ApplicationManager.getLabel(RecordChoiceActivity.this.rootEntityDef);;
					} else {
						clusterList[recordsList.size()]="";
						clusterList[recordsList.size()+1]=getResources().getString(R.string.addNewRecord)+" "+ApplicationManager.getLabel(RecordChoiceActivity.this.rootEntityDef);;
					}*/
					
					Message msg = Message.obtain();
			        msg.what = 1;
					handler.sendMessage(msg);
			    }
			  }).start();
	}
	
	private void deleteRecord(final int position){
		 final Handler handler = new Handler(){
			    @Override
			    public void handleMessage(Message msg) {
			    	refreshRecordsList();
			    }};
		  new Thread(new Runnable() {
			    public void run() {	    	
					ApplicationManager.dataManager.deleteRecord(position);
					ApplicationManager.recordsList.remove(position);					
					Message msg = Message.obtain();
			        msg.what = 1;
					handler.sendMessage(msg);
			    }
			  }).start();
	}
}