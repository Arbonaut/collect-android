package org.openforis.collect.android.management;

import static org.openforis.collect.persistence.jooq.Tables.OFC_RECORD;
import static org.openforis.collect.persistence.jooq.tables.OfcTaxon.OFC_TAXON;
import static org.openforis.collect.persistence.jooq.tables.OfcTaxonVernacularName.OFC_TAXON_VERNACULAR_NAME;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jooq.TableField;
import org.openforis.collect.android.database.DatabaseHelper;
import org.openforis.collect.android.misc.Pair;
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
import org.osmdroid.util.GeoPoint;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataManager {

	private CollectSurvey survey;
	private String rootEntity;
	private User user;
	//private Context context;
	
	private DataMarshaller dataMarshaller;
	private DataUnmarshaller dataUnmarshaller;
	
	public DataManager(Context ctx, CollectSurvey survey, String rootEntity, User loggedInUser){
		this.survey = survey;
		this.rootEntity = rootEntity;
		this.user = loggedInUser;
		//this.context = ctx;
		
		this.dataMarshaller = new DataMarshaller();
		HashMap<String,User> users = new HashMap<String, User>();
		users.put(loggedInUser.getName(), loggedInUser);
		DataHandler dataHandler = new DataHandler(survey, users);
		this.dataUnmarshaller = new DataUnmarshaller(dataHandler);
	}
	
	public User getUser(){
		return this.user;
	}
	
	public boolean saveRecord() {
		boolean isSuccess = true;
		try {
			CollectRecord recordToSave = ApplicationManager.currentRecord;
			
			if (recordToSave.getId()==null){
				recordToSave.setCreatedBy(this.user);
				recordToSave.setCreationDate(new Date());
				recordToSave.setStep(Step.ENTRY);			
			} else {
				recordToSave.setModifiedDate(new Date());
			}
			ServiceFactory.getRecordManager().save(recordToSave, ApplicationManager.getSessionId());
			this.assignShapesToRecord(recordToSave.getId());
			ApplicationManager.isRecordListUpToDate = false;
		} catch (RecordUnlockedException e) {
			e.printStackTrace();
			isSuccess = false;
			ApplicationManager.isRecordListUpToDate = true;
		} catch (RecordPersistenceException e) {
			e.printStackTrace();
			isSuccess = false;
			ApplicationManager.isRecordListUpToDate = true;
		} catch (NullPointerException e){
			e.printStackTrace();
			isSuccess = false;
			ApplicationManager.isRecordListUpToDate = true;
		} catch (Exception e){
			e.printStackTrace();
			isSuccess = false;
			ApplicationManager.isRecordListUpToDate = true;
		} finally {
			
		}
		return isSuccess;
	}
	
	public int saveRecord(CollectRecord recordToSave) {
		try {
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
		try {
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
	
	public CollectRecord loadRecordFromXml(String filename) throws DataUnmarshallerException{
		//filename = Environment.getExternalStorageDirectory().toString()+"/ofcm/data/imported/"+filename;
		CollectRecord loadedRecord = null;
		ParseRecordResult result = this.dataUnmarshaller.parse(filename);
		loadedRecord = result.getRecord();
		if (loadedRecord==null)
			throw new DataUnmarshallerException();
		this.saveRecord(loadedRecord);
				
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
	
	public void deleteForm(int position){
		try {
			List<CollectSurvey> formsList = ServiceFactory.getSurveyManager().getAll();
			Log.e("ServiceFactory.getSurveyManager()==null","=="+(ServiceFactory.getSurveyManager()==null));
			Log.e("formID","=="+formsList.get(position).getId());
			ApplicationManager.setSurvey(formsList.get(position));
			if (ApplicationManager.getSurvey()!=null){
				Log.e("survey","!isNUULL");
			} else {
				Log.e("survey","isNUULL");
			}
			ServiceFactory.getSurveyManager().deleteSurvey(formsList.get(position).getId());
			//List<CollectRecord> recordsList = ServiceFactory.getRecordManager().loadSummaries(survey, rootEntity);
			//ServiceFactory.getRecordManager().delete(recordsList.get(position).getId());	
		} catch (Exception e) {
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
		//List<CollectRecord> recordsList = this.loadSummaries(survey, rootEntity, (Step) null, 0, Integer.MAX_VALUE, (List<RecordSummarySortField>) null, (String[]) null);
		//List<CollectRecord> recordsList = ServiceFactory.getRecordManager().loadSummaries(survey, rootEntity);
		//MobileRecordManager recordManager = (MobileRecordManager) ServiceFactory.getRecordManager();
		List<CollectRecord> recordsList = ServiceFactory.getRecordManager().getRecordDao().loadSummaries(survey, rootEntity);
		//android.os.Debug.stopMethodTracing();
		System.err.println("LOADING SUMMARIES");
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
		searchString = /*"%" +*/ searchString.toUpperCase() + "%";
		String query = "SELECT *"
				+ " FROM " + OFC_TAXON_VERNACULAR_NAME
				+ " JOIN " + OFC_TAXON
				+ " ON " + OFC_TAXON.ID + "=" + OFC_TAXON_VERNACULAR_NAME.TAXON_ID
				+ " WHERE " + OFC_TAXON.TAXONOMY_ID + "=" + taxonomyId
				+ " AND " + OFC_TAXON_VERNACULAR_NAME.VERNACULAR_NAME + " LIKE '" + searchString + "'"
				+ " LIMIT " + maxResults;
		SQLiteDatabase db = DatabaseHelper.getDb();
		Cursor cursor = db.rawQuery(query, null);		
		
		//prepare results
		TaxonVernacularName entity;
		if (cursor.moveToFirst()){
			do {
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
	
	public List<Taxon> findByCode(int taxonomyId, String searchString, int maxResults) {
		return findStartingWith(OFC_TAXON.CODE, taxonomyId, searchString, maxResults);
	}
	
	public List<Taxon> findByScientificName(int taxonomyId, String searchString, int maxResults) {
		return findStartingWith(OFC_TAXON.SCIENTIFIC_NAME, taxonomyId, searchString, maxResults);
	}
	
	public List<Taxon> findStartingWith(TableField<?,String> field, int taxonomyId, String searchString, int maxResults) {
		searchString = searchString.toUpperCase() + "%";
		String query = "SELECT *"
				+ " FROM " + OFC_TAXON
				+ " WHERE " + OFC_TAXON.TAXONOMY_ID + "=" + taxonomyId
				+ " AND " + field + " LIKE '" + searchString +"'"
				+ " LIMIT " + maxResults;
		
		SQLiteDatabase db = DatabaseHelper.getDb();
		Cursor cursor = db.rawQuery(query, null);
		
		List<Taxon> entitiesList = new ArrayList<Taxon>();
		Taxon taxon;
		if (cursor.moveToFirst()){
			do {
				taxon = new Taxon();
				taxon.setCode(cursor.getString(cursor.getColumnIndex(OFC_TAXON.CODE.getName())));
				taxon.setParentId(cursor.getInt(cursor.getColumnIndex(OFC_TAXON.PARENT_ID.getName())));
				taxon.setScientificName(cursor.getString(cursor.getColumnIndex(OFC_TAXON.SCIENTIFIC_NAME.getName())));
				taxon.setStep(cursor.getInt(cursor.getColumnIndex(OFC_TAXON.STEP.getName())));
				taxon.setSystemId(cursor.getInt(cursor.getColumnIndex(OFC_TAXON.ID.getName())));
				taxon.setTaxonId(cursor.getInt(cursor.getColumnIndex(OFC_TAXON.TAXON_ID.getName())));
				taxon.setTaxonomyId(cursor.getInt(cursor.getColumnIndex(OFC_TAXON.TAXONOMY_ID.getName())));
				taxon.setTaxonRank(TaxonRank.fromName(cursor.getString(cursor.getColumnIndex(OFC_TAXON.TAXON_RANK.getName()))));
				entitiesList.add(taxon);
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		db.close();
		return entitiesList;
	}
	
	private static final TableField[] SUMMARY_FIELDS = 
		{OFC_RECORD.DATE_CREATED, OFC_RECORD.CREATED_BY_ID, OFC_RECORD.DATE_MODIFIED, OFC_RECORD.ERRORS, OFC_RECORD.ID, 
	     OFC_RECORD.MISSING, OFC_RECORD.MODEL_VERSION, OFC_RECORD.MODIFIED_BY_ID, OFC_RECORD.OWNER_ID, 
	     OFC_RECORD.ROOT_ENTITY_DEFINITION_ID, OFC_RECORD.SKIPPED, OFC_RECORD.STATE, OFC_RECORD.STEP, OFC_RECORD.SURVEY_ID, 
	     OFC_RECORD.WARNINGS, OFC_RECORD.KEY1, OFC_RECORD.KEY2, OFC_RECORD.KEY3, 
	     OFC_RECORD.COUNT1, OFC_RECORD.COUNT2, OFC_RECORD.COUNT3, OFC_RECORD.COUNT4, OFC_RECORD.COUNT5};
	
	/*@Transactional
	public List<CollectRecord> loadSummaries(CollectSurvey survey, String rootEntity, Step step, int offset, int maxRecords, 
			List<RecordSummarySortField> sortFields, String... keyValues) {
		List<CollectRecord> summariesList = new ArrayList<CollectRecord>();
		Log.e("DataManager","==========loadSummaries");
		String summaryFields = "";
		for (int i=0;i<SUMMARY_FIELDS.length;i++){
			if (i<SUMMARY_FIELDS.length-1)
				summaryFields += SUMMARY_FIELDS[i] + ", ";
			else 
				summaryFields += SUMMARY_FIELDS[i];
		}
		String query = "SELECT "+ summaryFields
				+ " FROM " + OFC_RECORD;
		
		Schema schema = survey.getSchema();
		EntityDefinition rootEntityDefn = schema.getRootEntityDefinition(rootEntity);
		Integer rootEntityDefnId = rootEntityDefn.getId();
		
		query += " WHERE " + OFC_RECORD.SURVEY_ID + "=" + survey.getId()
				+ " AND " + OFC_RECORD.ROOT_ENTITY_DEFINITION_ID + "=" + rootEntityDefnId;
		
		if ( step != null ) {
			query += " AND " + OFC_RECORD.STEP + "=" + step.getStepNumber();
		}
		
		query += " ORDER BY " + OFC_RECORD.ID;
		
		SQLiteDatabase db = DatabaseHelper.getDb();
		Cursor cursor = db.rawQuery(query, null);
		Log.e("cursor.size","=="+cursor.getCount());
		CollectRecord summary;
		if (cursor.moveToFirst()){
			do {
				summary = new CollectRecord();
				
				summariesList.add(summary);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return summariesList;
		/*JooqFactory jf = getMappingJooqFactory(survey);
		SelectQuery q = jf.selectQuery();	
		q.addSelect(SUMMARY_FIELDS);
		Field<String> ownerNameField = OFC_USER.USERNAME.as(RecordSummarySortField.Sortable.OWNER_NAME.name());
		q.addSelect(ownerNameField);
		q.addFrom(OFC_RECORD);
		q.addJoin(OFC_USER, JoinType.LEFT_OUTER_JOIN, OFC_RECORD.OWNER_ID.equal(OFC_USER.ID));

		Schema schema = survey.getSchema();
		EntityDefinition rootEntityDefn = schema.getRootEntityDefinition(rootEntity);
		Integer rootEntityDefnId = rootEntityDefn.getId();
		q.addConditions(OFC_RECORD.SURVEY_ID.equal(survey.getId()));
		q.addConditions(OFC_RECORD.ROOT_ENTITY_DEFINITION_ID.equal(rootEntityDefnId));
		if ( step != null ) {
			q.addConditions(OFC_RECORD.STEP.equal(step.getStepNumber()));
		}
		addFilterByKeyConditions(q, keyValues);
		
		if ( sortFields != null ) {
			for (RecordSummarySortField sortField : sortFields) {
				addOrderBy(q, sortField, ownerNameField);
			}
		}
		
		//always order by ID to avoid pagination issues
		q.addOrderBy(OFC_RECORD.ID);
		
		//add limit
		q.addLimit(offset, maxRecords);
		
		//fetch results
		Result<Record> result = q.fetch();
		
		return jf.fromResult(result);*/
	//}
	
	private void assignShapesToRecord(int recordId){
		int pointsNo = ApplicationManager.points.size();
		int linesNo = ApplicationManager.lineEnds.size();
		int plotsNo = ApplicationManager.plots.size();		
		Log.e("recordId to be set","=="+recordId);
		if ((pointsNo+linesNo+plotsNo)>0){
			for (int i=0;i<pointsNo;i++){
				if ((ApplicationManager.points.get(i).getLeft()==null)){					
					ApplicationManager.points.get(i).setLeft(recordId);
				}
			}
			for (int i=0;i<linesNo;i++){
				if (ApplicationManager.lineEnds.get(i).getLeft()==null){
					ApplicationManager.lineEnds.get(i).setLeft(recordId);
				}
			}
			for (int i=0;i<plotsNo;i++){
				if (ApplicationManager.plots.get(i).get(0).getLeft()==null){
					List<Pair<Integer,GeoPoint>> cornersList = ApplicationManager.plots.get(i);
					for (Pair<Integer,GeoPoint> pair : cornersList){
						pair.setLeft(recordId);
					}
				}
			}
		}
	}	
}