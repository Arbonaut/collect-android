package org.openforis.collect.android.management;

import static org.openforis.collect.persistence.jooq.tables.OfcTaxon.OFC_TAXON;
import static org.openforis.collect.persistence.jooq.tables.OfcTaxonVernacularName.OFC_TAXON_VERNACULAR_NAME;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.openforis.collect.android.database.DatabaseHelper;
import org.openforis.collect.android.service.ServiceFactory;
import org.openforis.collect.manager.dataexport.BackupProcess;
import org.openforis.collect.model.CollectRecord;
import org.openforis.collect.model.CollectRecord.Step;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.model.User;
import org.openforis.collect.persistence.RecordPersistenceException;
import org.openforis.collect.persistence.RecordUnlockedException;
import org.openforis.collect.persistence.xml.DataHandler;
import org.openforis.collect.persistence.xml.DataMarshaller;
import org.openforis.collect.persistence.xml.DataUnmarshaller;
import org.openforis.collect.persistence.xml.DataUnmarshaller.ParseRecordResult;
import org.openforis.collect.persistence.xml.DataUnmarshallerException;
import org.openforis.idm.model.species.Taxon;
import org.openforis.idm.model.species.Taxon.TaxonRank;
import org.openforis.idm.model.species.TaxonVernacularName;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataManager {

	private CollectSurvey survey;
	private String rootEntity;
	private User user;
	
	private DataMarshaller dataMarshaller;
	private DataUnmarshaller dataUnmarshaller;
	
	public DataManager(CollectSurvey survey, String rootEntity, User loggedInUser){
		this.survey = survey;
		this.rootEntity = rootEntity;
		this.user = loggedInUser;
		
		this.dataMarshaller = new DataMarshaller();
		HashMap<String,User> users = new HashMap<String, User>();
		users.put(loggedInUser.getName(), loggedInUser);
		DataHandler dataHandler = new DataHandler(survey, users);
		this.dataUnmarshaller = new DataUnmarshaller(dataHandler);
	}
	
	public boolean saveRecord(Context ctx) {
		boolean isSuccess = true;
		try {
//			JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
//			jdbcDao.getConnection();
			CollectRecord recordToSave = ApplicationManager.currentRecord;
			
			if (recordToSave.getId()==null){
				recordToSave.setCreatedBy(this.user);
				recordToSave.setCreationDate(new Date());
				recordToSave.setStep(Step.ENTRY);			
			} else {
				recordToSave.setModifiedDate(new Date());
			}
			ServiceFactory.getRecordManager().save(recordToSave, ApplicationManager.getSessionId());
		} catch (RecordUnlockedException e) {
			e.printStackTrace();
			isSuccess = false;
		} catch (RecordPersistenceException e) {
			e.printStackTrace();
			isSuccess = false;
		} catch (NullPointerException e){
			e.printStackTrace();
			isSuccess = false;
		} catch (Exception e){
			e.printStackTrace();
			isSuccess = false;
		} finally {
			
		}
		return isSuccess;
	}
	
	public int saveRecord(CollectRecord recordToSave) {
		try {
//			JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
//			jdbcDao.getConnection();
			Log.e("recordToSave==null","=="+(recordToSave==null));
			if (recordToSave.getId()==null){
				recordToSave.setCreatedBy(this.user);
				recordToSave.setCreationDate(new Date());
				recordToSave.setStep(Step.ENTRY);			
			} else {
				recordToSave.setModifiedDate(new Date());
			}
			ServiceFactory.getRecordManager().save(recordToSave, ApplicationManager.getSessionId());
		} catch (RecordUnlockedException e) {
			e.printStackTrace();
		} catch (RecordPersistenceException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			e.printStackTrace();
		}
		return 0;
	}
	
	public void saveAllRecordsToFile(String folderToSave){
		try{
			/*BackupProcess backup = new BackupProcess(ServiceFactory.getSurveyManager(), ServiceFactory.getRecordManager(), 
					this.dataMarshaller, new File(folderToSave),
					this.survey, this.survey.getSchema().getDefinitionById(ApplicationManager.currRootEntityId).getName(), new int[]{1,2,3});*/
			BackupProcess backup = new BackupProcess(ServiceFactory.getSurveyManager(),
					ServiceFactory.getRecordManager(),ServiceFactory.getRecordFileManager(),
					this.dataMarshaller, new File(folderToSave),
					this.survey, this.survey.getSchema().getDefinitionById(ApplicationManager.currRootEntityId).getName());
			backup.init();
			backup.setIncludeIdm(false);
			backup.call();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public int saveRecordToXml(CollectRecord recordToSave, String folderToSave) {
		long startTime = System.currentTimeMillis();
		try {
			//CollectRecord recordToSave = ApplicationManager.currentRecord;
			
			if (recordToSave.getId()==null){
				recordToSave.setCreatedBy(this.user);
				recordToSave.setCreationDate(new Date());
				recordToSave.setStep(Step.ENTRY);			
			} else {
				recordToSave.setModifiedDate(new Date());
			}
			List<String> rootEntityKeyValuesList = ApplicationManager.currentRecord.getRootEntityKeyValues();
			FileWriter fwr;
			String fileName = folderToSave+"/";
			if (rootEntityKeyValuesList!=null){
				for (String rootEntityKeyValue : rootEntityKeyValuesList){
					fileName += rootEntityKeyValue + "_";
				}
			}
			fileName += ApplicationManager.currentRecord.getId()+"_"+ApplicationManager.currRootEntityId+"_"+ApplicationManager.currentRecord.getCreationDate().getDay()+"_"+ApplicationManager.currentRecord.getCreationDate().getMonth()+"_"+ApplicationManager.currentRecord.getCreationDate().getYear()+"_"+ApplicationManager.currentRecord.getCreationDate().getHours()+"_"+ApplicationManager.currentRecord.getCreationDate().getMinutes()+"_"+ApplicationManager.currentRecord.getCreatedBy().getName();
			fileName += ".xml";
			fwr = new FileWriter(fileName);
			this.dataMarshaller.write(recordToSave, fwr);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} finally {

		}
		return 0;
	}
	
	public CollectRecord loadRecordFromXml(String filename) {
		//filename = Environment.getExternalStorageDirectory().toString()+"/ofcm/data/imported/"+filename;
		long startTime = System.currentTimeMillis();
		CollectRecord loadedRecord = null;
		try {
			ParseRecordResult result = this.dataUnmarshaller.parse(filename);
			loadedRecord = result.getRecord();
			this.saveRecord(loadedRecord);
		} catch (NullPointerException e){
			e.printStackTrace();
		} catch (DataUnmarshallerException e) {
			e.printStackTrace();
		}			
		return loadedRecord;
	}
	
	public void deleteRecord(int position){
		try {
//			JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
//			jdbcDao.getConnection();
			List<CollectRecord> recordsList = ServiceFactory.getRecordManager().loadSummaries(survey, rootEntity);
			ServiceFactory.getRecordManager().delete(recordsList.get(position).getId());			
		} catch (RecordPersistenceException e) {
			e.printStackTrace();
		} finally {
			DatabaseHelper.closeConnection();
		}		
	}
	
	public List<CollectRecord> loadSummaries(){
		Log.e("loading","SUMMARIES");
		long startTime = System.currentTimeMillis();
		//JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
		//jdbcDao.getConnection();
		//android.os.Debug.startMethodTracing("slad");
		List<CollectRecord> recordsList = ServiceFactory.getRecordManager().loadSummaries(survey, rootEntity);
		//android.os.Debug.stopMethodTracing();
		Log.e("loadSummaries","=="+((System.currentTimeMillis()-startTime)));
		//JdbcDaoSupport.close();
		ApplicationManager.isRecordListUpToDate = true;
		return recordsList;
	}
	
	public CollectRecord loadRecord(int recordId){
		long startTime = System.currentTimeMillis();
		CollectRecord loadedRecord = null;
		try {
//			JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
//			jdbcDao.getConnection();
			loadedRecord = ServiceFactory.getRecordManager().load(survey, recordId, Step.ENTRY);
//			JdbcDaoSupport.close();
			DatabaseHelper.closeConnection();
		} catch (NullPointerException e){
			e.printStackTrace();
		} /*catch (RecordPersistenceException e) {
			e.printStackTrace();
		}*/
		Log.e("record"+recordId,"LOADED IN "+(System.currentTimeMillis()-startTime)+"ms");
		return loadedRecord;
	}
	
	public List<TaxonVernacularName> findByVernacularName(int taxonomyId, String searchString, int maxResults){
		List<TaxonVernacularName> entitiesList = new ArrayList<TaxonVernacularName>();
		searchString = "%" + searchString.toUpperCase() + "%";
		String query = "SELECT *"
				+ " FROM " + OFC_TAXON_VERNACULAR_NAME
				+ " JOIN " + OFC_TAXON
				+ " ON " + OFC_TAXON.ID + "=" + OFC_TAXON_VERNACULAR_NAME.TAXON_ID
				+ " WHERE " + OFC_TAXON.TAXONOMY_ID + "=" + taxonomyId
				+ " AND " + OFC_TAXON_VERNACULAR_NAME.VERNACULAR_NAME + " LIKE '" + searchString + "'"
				+ " LIMIT " + maxResults;
		SQLiteDatabase db = DatabaseHelper.getDb();
		Cursor cursor = db.rawQuery(query, null);
		Log.e("cursor","size=="+cursor.getCount());
		
		
		//prepare results
		TaxonVernacularName entity;
		if (cursor.moveToFirst()){
			do{
				entity = new TaxonVernacularName();
				entity.setId(cursor.getInt(cursor.getColumnIndex(OFC_TAXON_VERNACULAR_NAME.ID.getName())));
				entity.setLanguageCode(cursor.getString(cursor.getColumnIndex(OFC_TAXON_VERNACULAR_NAME.LANGUAGE_CODE.getName())));
				entity.setLanguageVariety(cursor.getString(cursor.getColumnIndex(OFC_TAXON_VERNACULAR_NAME.LANGUAGE_VARIETY.getName())));
				List<String> qualifiers = new ArrayList<String>();
				String qualifier = cursor.getString(cursor.getColumnIndex(OFC_TAXON_VERNACULAR_NAME.QUALIFIER1.getName()));
				if (qualifier!=null){
					qualifiers.add(qualifier);	
				}
				qualifier = cursor.getString(cursor.getColumnIndex(OFC_TAXON_VERNACULAR_NAME.QUALIFIER2.getName()));
				if (qualifier!=null){
					qualifiers.add(qualifier);	
				}
				qualifier = cursor.getString(cursor.getColumnIndex(OFC_TAXON_VERNACULAR_NAME.QUALIFIER3.getName()));
				if (qualifier!=null){
					qualifiers.add(qualifier);	
				}
				entity.setQualifiers(qualifiers);
				entity.setStep(cursor.getInt(cursor.getColumnIndex(OFC_TAXON_VERNACULAR_NAME.STEP.getName())));
				entity.setTaxonSystemId(cursor.getInt(cursor.getColumnIndex(OFC_TAXON_VERNACULAR_NAME./*TAXON_*/ID.getName())));
				entity.setVernacularName(cursor.getString(cursor.getColumnIndex(OFC_TAXON_VERNACULAR_NAME.VERNACULAR_NAME.getName())));	
				entitiesList.add(entity);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return entitiesList;
	}
	
	public Taxon loadById(int id){
		Taxon taxon = new Taxon();
		String query = "SELECT *"
				+ " FROM " + OFC_TAXON
				+ " WHERE " + OFC_TAXON.ID + "=" + id
				+ " LIMIT 1";
		
		SQLiteDatabase db = DatabaseHelper.getDb();
		Cursor cursor = db.rawQuery(query, null);
		
		Log.e("cursor","size=="+cursor.getCount());
		if (cursor.moveToFirst()){
			taxon.setCode(cursor.getString(cursor.getColumnIndex(OFC_TAXON.CODE.getName())));
			taxon.setParentId(cursor.getInt(cursor.getColumnIndex(OFC_TAXON.PARENT_ID.getName())));
			taxon.setScientificName(cursor.getString(cursor.getColumnIndex(OFC_TAXON.SCIENTIFIC_NAME.getName())));
			taxon.setStep(cursor.getInt(cursor.getColumnIndex(OFC_TAXON.STEP.getName())));
			taxon.setSystemId(cursor.getInt(cursor.getColumnIndex(OFC_TAXON.ID.getName())));
			taxon.setTaxonId(cursor.getInt(cursor.getColumnIndex(OFC_TAXON.TAXON_ID.getName())));
			taxon.setTaxonomyId(cursor.getInt(cursor.getColumnIndex(OFC_TAXON.TAXONOMY_ID.getName())));
			taxon.setTaxonRank(TaxonRank.fromName(cursor.getString(cursor.getColumnIndex(OFC_TAXON.TAXON_RANK.getName()))));
		}
		cursor.close();
		db.close();
		return taxon;
	}
}