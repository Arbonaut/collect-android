package org.openforis.collect.android.filechooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openforis.collect.android.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

/**
 * 
 * @author K. Waga
 *
 */
public class FileChooser extends ListActivity {
	
    private File currentDir;
    private FileArrayAdapter adapter;
    
    private String selectedFile;
    private int selectedFileType;
    private int fileChosen;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentDir = new File(Environment.getExternalStorageDirectory().toString()+getResources().getString(R.string.application_folder));
        this.selectedFileType = getIntent().getIntExtra(getResources().getString(R.string.fileNameRequestType), -1);
        Log.e("selectedFileType"+this.selectedFileType,"fileChose");
        if (this.selectedFileType==getResources().getInteger(R.integer.chooseFormFile)){
        	this.selectedFile = getResources().getString(R.string.formFileName);
        	this.fileChosen = getResources().getInteger(R.integer.formFileChosen);
        } else if (this.selectedFileType==getResources().getInteger(R.integer.chooseDatabaseFile)){
        	this.selectedFile = getResources().getString(R.string.databaseFileName);
        	this.fileChosen = getResources().getInteger(R.integer.databaseFileChosen);
        } else if (this.selectedFileType==getResources().getInteger(R.integer.chooseSpeciesListFile)){
        	this.selectedFile = getResources().getString(R.string.speciesListFileName);
        	this.fileChosen = getResources().getInteger(R.integer.speciesListFileChosen);
        }
        Log.e("selectedFile"+this.selectedFile,"fileChose"+this.fileChosen);
        fill(currentDir);
    }
    
    private void fill(File f)
    {
		File[]dirs = f.listFiles();
		this.setTitle(getResources().getString(R.string.currentDirectory)+f.getName());
		List<Option>dir = new ArrayList<Option>();
		List<Option>fls = new ArrayList<Option>();
		try{
			for(File ff: dirs)
			{
				if(ff.isDirectory())
					dir.add(new Option(ff.getName(),"Folder",ff.getAbsolutePath()));
				else {
					fls.add(new Option(ff.getName(),"File Size: "+ff.length(),ff.getAbsolutePath()));
				}
			}
		} catch (Exception e)
		{
			 
		}
		Collections.sort(dir);
		Collections.sort(fls);
		dir.addAll(fls);
		if(!f.getName().equalsIgnoreCase("sdcard"))
			dir.add(0,new Option("..","Parent Directory",f.getParent()));
		adapter = new FileArrayAdapter(FileChooser.this,R.layout.file_view,dir);
		this.setListAdapter(adapter);
	}
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Option o = adapter.getItem(position);
		if(o.getData().equalsIgnoreCase("folder")||o.getData().equalsIgnoreCase("parent directory")){
				currentDir = new File(o.getPath());
				fill(currentDir);
		}
		else
		{
			onFileClick(o);
		}
	}
    
    private void onFileClick(Option o)
    {
		Intent resultHolder = new Intent();
		resultHolder.putExtra(this.selectedFile, o.getPath());		
		setResult(this.fileChosen,resultHolder);
		FileChooser.this.finish();	
    }
}