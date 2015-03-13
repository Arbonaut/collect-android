package org.openforis.collect.android.lists;

import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.logs.RunnableHandler;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.screens.BaseListActivity;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.idm.metamodel.EntityDefinition;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author K. Waga
 *
 */
public class RootEntityChoiceActivity extends BaseListActivity{
	
	private static final String TAG = "RootEntityChoiceActivity";

	private TextView activityLabel;
	
	private List<EntityDefinition> rootEntitiesList;
	private ArrayAdapter<String> adapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
        setContentView(R.layout.clusterchoiceactivity);
        try{
        	ApplicationManager.rootEntitySelectionActivity = this;
        	
    		this.rootEntitiesList = ApplicationManager.getSurvey().getSchema().getRootEntityDefinitions();
        	if (this.rootEntitiesList.size()==1){
        		Intent resultHolder = new Intent();
        		resultHolder.putExtra(getResources().getString(R.string.rootEntityId), this.rootEntitiesList.get(0).getId());
        		setResult(getResources().getInteger(R.integer.rootEntityChoiceSuccessful),resultHolder);
        		RootEntityChoiceActivity.this.finish();
        	}
        	
        	this.activityLabel = (TextView)findViewById(R.id.lblList);
        	this.activityLabel.setText(getResources().getString(R.string.rootEntityChoiceListLabel));
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
		
		ApplicationManager.isRecordListUpToDate = false;
		ApplicationManager.recordsList = null;
		
		int backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);	
		changeBackgroundColor(backgroundColor);
		CollectSurvey collectSurvey = (CollectSurvey)ApplicationManager.getSurvey();
		this.rootEntitiesList = collectSurvey.getSchema().getRootEntityDefinitions();
		String[] clusterList = new String[rootEntitiesList.size()];
		for (int i=0;i<rootEntitiesList.size();i++){
			EntityDefinition rootEntity = rootEntitiesList.get(i);
			clusterList[i] = ApplicationManager.getLabel(rootEntity);
		}
		int layout = (backgroundColor!=Color.WHITE)?R.layout.localclusterrow_white:R.layout.localclusterrow_black;
        this.adapter = new ArrayAdapter<String>(this, layout, R.id.plotlabel, clusterList);
		this.setListAdapter(this.adapter);
    }
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.i(getResources().getString(R.string.app_name),TAG+":onListItemClick");
		Intent resultHolder = new Intent();
		resultHolder.putExtra(getResources().getString(R.string.rootEntityId), this.rootEntitiesList.get(position).getId());
		setResult(getResources().getInteger(R.integer.rootEntityChoiceSuccessful),resultHolder);
		RootEntityChoiceActivity.this.finish();
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
		/*AlertMessage.createPositiveNegativeDialog(RootEntityChoiceActivity.this, false, getResources().getDrawable(R.drawable.warningsign),
				getResources().getString(R.string.exitAppTitle), getResources().getString(R.string.exitAppMessage),
				getResources().getString(R.string.yes), getResources().getString(R.string.no),
	    		new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						setResult(getResources().getInteger(R.integer.backButtonPressed), new Intent());
						RootEntityChoiceActivity.this.finish();
					}
				},
	    		new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				},
				null).show();*/
		setResult(getResources().getInteger(R.integer.backButtonPressed), new Intent());
		RootEntityChoiceActivity.this.finish();
	}
	
    private void changeBackgroundColor(int backgroundColor){
		getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));
		this.activityLabel.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
    }
}