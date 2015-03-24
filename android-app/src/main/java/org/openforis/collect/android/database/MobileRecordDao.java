package org.openforis.collect.android.database;

import static org.openforis.collect.persistence.jooq.Tables.OFC_RECORD;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.jooq.TableField;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.model.CollectRecord;
import org.openforis.collect.model.CollectRecord.State;
import org.openforis.collect.model.CollectRecord.Step;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.model.RecordSummarySortField;
import org.openforis.collect.model.User;
import org.openforis.collect.persistence.DataInconsistencyException;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.Schema;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.ModelSerializer;
import org.springframework.transaction.annotation.Transactional;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class MobileRecordDao extends org.openforis.collect.persistence.RecordDao {

	
	private static final TableField[] KEY_FIELDS = 
		{OFC_RECORD.KEY1, OFC_RECORD.KEY2, OFC_RECORD.KEY3};
	private static final TableField[] COUNT_FIELDS = 
		{OFC_RECORD.COUNT1, OFC_RECORD.COUNT2, OFC_RECORD.COUNT3, OFC_RECORD.COUNT4, OFC_RECORD.COUNT5};
	private static final TableField[] SUMMARY_FIELDS = 
		{OFC_RECORD.DATE_CREATED, OFC_RECORD.CREATED_BY_ID, OFC_RECORD.DATE_MODIFIED, OFC_RECORD.ERRORS, OFC_RECORD.ID, 
	     OFC_RECORD.MISSING, OFC_RECORD.MODEL_VERSION, OFC_RECORD.MODIFIED_BY_ID, 
	     OFC_RECORD.ROOT_ENTITY_DEFINITION_ID, OFC_RECORD.SKIPPED, OFC_RECORD.STATE, OFC_RECORD.STEP, OFC_RECORD.SURVEY_ID, 
	     OFC_RECORD.WARNINGS, OFC_RECORD.KEY1, OFC_RECORD.KEY2, OFC_RECORD.KEY3, 
	     OFC_RECORD.COUNT1, OFC_RECORD.COUNT2, OFC_RECORD.COUNT3, OFC_RECORD.COUNT4, OFC_RECORD.COUNT5};

	private Field<byte[]> dataAlias;
	private static final int SERIALIZATION_BUFFER_SIZE = 50000;
	
	public MobileRecordDao() {
		super();
	}
	
	@Override
	@Transactional
	public List<CollectRecord> loadSummaries(CollectSurvey survey, String rootEntity) {
		return this.loadSummaries(survey, rootEntity, (String[]) null);
	}
	
	@Transactional
	public List<CollectRecord> loadSummaries(CollectSurvey survey, String rootEntity, String... keys) {	
		return loadSummariesLocal(survey, rootEntity, (Step) null, 0, Integer.MAX_VALUE, (List<RecordSummarySortField>) null, keys);
	}
	
	private JooqFactory getMappingJooqFactory(CollectSurvey survey) {
		try {
			return new JooqFactory(getConnection(), survey);
		} catch (SQLException e) {			
			e.printStackTrace();
			return null;
		}		
	}
	
	private JooqFactory getMappingJooqFactory(CollectSurvey survey, int step) {
		try {
			return new JooqFactory(getConnection(), survey, step);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	@Transactional
	public List<CollectRecord> loadSummariesLocal(CollectSurvey survey, String rootEntity, Step step, int offset, int maxRecords, 
			List<RecordSummarySortField> sortFields, String... keyValues) {		
		if (step!=null)
			this.dataAlias = (step.getStepNumber() == 1 ? OFC_RECORD.DATA1 : OFC_RECORD.DATA2).as("DATA");
		else
			this.dataAlias = (OFC_RECORD.DATA1).as("DATA");
		JooqFactory jf = getMappingJooqFactory(survey);
		SelectQuery q = jf.selectQuery();	
		q.addFrom(OFC_RECORD);
		q.addSelect(SUMMARY_FIELDS);

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
				addOrderBy(q, sortField);
			}
		}
		
		//always order by ID to avoid pagination issues
		q.addOrderBy(OFC_RECORD.ID);
		
		//add limit
		q.addLimit(offset, maxRecords);
		//executing query
		List<CollectRecord> result = new ArrayList<CollectRecord>();
		SQLiteDatabase db = DatabaseHelper.getDb();
		Cursor cursor = db.rawQuery(q.toString(), null);
				
		//preparing result
		while (cursor.moveToNext()) {
			CollectRecord collectRecord = new CollectRecord(survey, (cursor.getString(cursor.getColumnIndex(OFC_RECORD.MODEL_VERSION.getName()))==null)?null:survey.getVersion(cursor.getString(cursor.getColumnIndex(OFC_RECORD.MODEL_VERSION.getName()))).getName());
			fromCursor(cursor, collectRecord);		
			result.add(collectRecord);
		}
		cursor.close();
		db.close();
		return result;
	}
	
	private void fromCursor(Cursor cursor, CollectRecord collectRecord){
		collectRecord.setId(cursor.getInt(cursor.getColumnIndex(OFC_RECORD.ID.getName())));
		Date creationDate = null;
		Date modificationDate = null;
		try {
			creationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse(cursor.getString(cursor.getColumnIndex(OFC_RECORD.DATE_CREATED.getName())));
			collectRecord.setCreationDate(creationDate);
			if (cursor.getString(cursor.getColumnIndex(OFC_RECORD.DATE_MODIFIED.getName()))!=null){
				modificationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse(cursor.getString(cursor.getColumnIndex(OFC_RECORD.DATE_MODIFIED.getName())));
			}
			collectRecord.setModifiedDate(modificationDate);
		} catch (ParseException e) {
			e.printStackTrace();
			if (creationDate==null){
				collectRecord.setCreationDate(null);
			} 
			if (modificationDate==null){
				collectRecord.setModifiedDate(null);
			}
		}
		
		Integer createdById = cursor.getInt(cursor.getColumnIndex(OFC_RECORD.CREATED_BY_ID.getName()));
		if(createdById != null){
			User user = loadUser(createdById);
			collectRecord.setCreatedBy(user);
		}
		Integer modifiedById = cursor.getInt(cursor.getColumnIndex(OFC_RECORD.MODIFIED_BY_ID.getName()));
		if(modifiedById != null){
			User user = loadUser(modifiedById);
			collectRecord.setModifiedBy(user);
		}
		
		collectRecord.setWarnings(cursor.getInt(cursor.getColumnIndex(OFC_RECORD.WARNINGS.getName())));
		collectRecord.setErrors(cursor.getInt(cursor.getColumnIndex(OFC_RECORD.ERRORS.getName())));
		collectRecord.setSkipped(cursor.getInt(cursor.getColumnIndex(OFC_RECORD.SKIPPED.getName())));
		collectRecord.setMissing(cursor.getInt(cursor.getColumnIndex(OFC_RECORD.MISSING.getName())));
		
		Integer loadedStep = cursor.getInt(cursor.getColumnIndex(OFC_RECORD.STEP.getName()));
		if (loadedStep != null) {
			collectRecord.setStep(Step.valueOf(loadedStep));
		}
		String state = cursor.getString(cursor.getColumnIndex(OFC_RECORD.STATE.getName()));
		if (state != null) {
			collectRecord.setState(State.fromCode(state));
		}
		
		List<Integer> counts = new ArrayList<Integer>(COUNT_FIELDS.length);
		for (TableField tableField : COUNT_FIELDS) {
			counts.add(cursor.getInt(cursor.getColumnIndex(tableField.getName())));
		}
		collectRecord.setEntityCounts(counts);
		// create list of keys
		List<String> keys = new ArrayList<String>(KEY_FIELDS.length);
		for (TableField tableField : KEY_FIELDS) {
			keys.add(cursor.getString(cursor.getColumnIndex(tableField.getName())));
		}			
		collectRecord.setRootEntityKeyValues(keys);
		
		int rootEntityId = cursor.getInt(cursor.getColumnIndex(OFC_RECORD.ROOT_ENTITY_DEFINITION_ID.getName()));
		Entity rootEntity = collectRecord.createRootEntity(rootEntityId);
		collectRecord.setRootEntity(rootEntity);
	}
	
	private ModelSerializer getSerializer() {
		return new ModelSerializer(SERIALIZATION_BUFFER_SIZE);
	}
	
	private User loadUser(int userId) {
		SQLiteDatabase db = DatabaseHelper.getDb();
		String query = "SELECT " + org.openforis.collect.persistence.jooq.tables.OfcUser.OFC_USER.USERNAME + ","
				+ org.openforis.collect.persistence.jooq.tables.OfcUser.OFC_USER.PASSWORD
				+ " FROM " + org.openforis.collect.persistence.jooq.tables.OfcUser.OFC_USER
				+ " WHERE " + org.openforis.collect.persistence.jooq.tables.OfcUser.OFC_USER.ID + " = " + userId;

		Cursor userCursor = db.rawQuery(query, null);
		User user = null;
		if (userCursor.moveToFirst()){
			user = new User();
        	user.setName(userCursor.getString(0));
    		user.setPassword(userCursor.getString(1));
		}
		userCursor.close();
		db.close();
		return user;
	}
	
	private void addFilterByKeyConditions(SelectQuery q, String... keyValues) {
		if ( keyValues != null ) {
			for (int i = 0; i < keyValues.length && i < KEY_FIELDS.length; i++) {
				String key = keyValues[i];
				if(StringUtils.isNotBlank(key)) {
					@SuppressWarnings("unchecked")
					Field<String> keyField = (Field<String>) KEY_FIELDS[i];
					q.addConditions(keyField.upper().equal(key.toUpperCase()));
				}
			}
		}
	}

	private void addOrderBy(SelectQuery q, RecordSummarySortField sortField) {
		Field<?> orderBy = null;
		if(sortField != null) {
			switch(sortField.getField()) {
			case KEY1:
				orderBy = OFC_RECORD.KEY1;
				break;
			case KEY2:
				orderBy = OFC_RECORD.KEY2;
				break;
			case KEY3:
				orderBy = OFC_RECORD.KEY3;
				break;
			case COUNT1:
				orderBy = OFC_RECORD.COUNT1;
				break;
			case COUNT2:
				orderBy = OFC_RECORD.COUNT2;
				break;
			case COUNT3:
				orderBy = OFC_RECORD.COUNT3;
				break;
			case DATE_CREATED:
				orderBy = OFC_RECORD.DATE_CREATED;
				break;
			case DATE_MODIFIED:
				orderBy = OFC_RECORD.DATE_MODIFIED;
				break;
			case SKIPPED:
				orderBy = OFC_RECORD.SKIPPED;
				break;
			case MISSING:
				orderBy = OFC_RECORD.MISSING;
				break;
			case ERRORS:
				orderBy = OFC_RECORD.ERRORS;
				break;
			case WARNINGS:
				orderBy = OFC_RECORD.WARNINGS;
				break;
			case STEP:
				orderBy = OFC_RECORD.STEP;
				break;
			}
		}
		if(orderBy != null) {
			if(sortField.isDescending()) {
				q.addOrderBy(orderBy.desc());
			} else {
				q.addOrderBy(orderBy);
			}
		}
	}
	
	public CollectRecord load(CollectSurvey survey, int id, int step) {
		JooqFactory jf = getMappingJooqFactory(survey, step);
		SelectQuery query = jf.selectRecordQuery(id);
		query.addLimit(1);
		SQLiteDatabase db = DatabaseHelper.getDb();
		Cursor cursor = db.rawQuery(query.toString(), null);
		
		if (cursor.getCount()==0){
			return null;
		}
		cursor.moveToNext();
		CollectRecord collectRecord;
		//preparing result
		collectRecord = new CollectRecord(survey, (cursor.getString(cursor.getColumnIndex(OFC_RECORD.MODEL_VERSION.getName()))==null)?null:survey.getVersion(cursor.getString(cursor.getColumnIndex(OFC_RECORD.MODEL_VERSION.getName()))).getName());
		fromCursor2(cursor, query.fetchOne(), collectRecord);		
		
		cursor.close();
		db.close();
		return collectRecord;		
	}
	
	/*public CollectRecord load(CollectSurvey survey, int id, int step) {
		JooqFactory jf = getMappingJooqFactory(survey, step);
		SelectQuery query = jf.selectRecordQuery(id);
		Record r = query.fetchOne();
		if ( r == null ) {
			return null;
		} else {
			return jf.fromRecord(r);
		}
	}*/
	
	/*@Override
	public CollectRecord fromRecord(Record r) {
		int rootEntityId = r.getValueAsInteger(OFC_RECORD.ROOT_ENTITY_DEFINITION_ID);
		String version = r.getValueAsString(OFC_RECORD.MODEL_VERSION);
		Schema schema = survey.getSchema();
		NodeDefinition rootEntityDefn = schema.getDefinitionById(rootEntityId);
		if (rootEntityDefn == null) {
			throw new DataInconsistencyException("Unknown root entity id " + rootEntityId);
		}
		CollectRecord record = new CollectRecord(survey, version);
		c.setId(r.getValue(OFC_RECORD.ID));
		c.setCreationDate(r.getValue(OFC_RECORD.DATE_CREATED));
		c.setModifiedDate(r.getValue(OFC_RECORD.DATE_MODIFIED));
		
		Integer createdById = r.getValue(OFC_RECORD.CREATED_BY_ID);
		if(createdById !=null){
			User user = loadUser(createdById);
			c.setCreatedBy(user);
		}
		Integer modifiedById = r.getValue(OFC_RECORD.MODIFIED_BY_ID);
		if(modifiedById !=null){
			User user = loadUser(modifiedById);
			c.setModifiedBy(user);
		}
		c.setWarnings(r.getValue(OFC_RECORD.WARNINGS));
		c.setErrors(r.getValue(OFC_RECORD.ERRORS));
		c.setSkipped(r.getValue(OFC_RECORD.SKIPPED));
		c.setMissing(r.getValue(OFC_RECORD.MISSING));

		Integer step = r.getValue(OFC_RECORD.STEP);
		if (step != null) {
			c.setStep(Step.valueOf(step));
		}
		String state = r.getValue(OFC_RECORD.STATE);
		if (state != null) {
			c.setState(State.fromCode(state));
		}
		
		// create list of entity counts
		List<Integer> counts = new ArrayList<Integer>(COUNT_FIELDS.length);
		for (TableField tableField : COUNT_FIELDS) {
			counts.add(r.getValueAsInteger(tableField));
		}
		c.setEntityCounts(counts);

		// create list of keys
		List<String> keys = new ArrayList<String>(KEY_FIELDS.length);
		for (TableField tableField : KEY_FIELDS) {
			keys.add(r.getValueAsString(tableField));
		}
		c.setRootEntityKeyValues(keys);

		int rootEntityId = r.getValue(OFC_RECORD.ROOT_ENTITY_DEFINITION_ID);

		if ( dataAlias != null ) {
			byte[] data = r.getValue(dataAlias);
			//System.out.println("r.getValue(dataAlias) = " + r.getValue(dataAlias));
			Entity rootEntity = c.createRootEntity(rootEntityId);
			ModelSerializer modelSerializer = getSerializer();
			modelSerializer.mergeFrom(data, rootEntity);
		}
		return record;
	}
	
	@Override
	protected void fromRecord(Record r, CollectRecord c) {
		c.setId(r.getValue(OFC_RECORD.ID));
		c.setCreationDate(r.getValue(OFC_RECORD.DATE_CREATED));
		c.setModifiedDate(r.getValue(OFC_RECORD.DATE_MODIFIED));
		
		Integer createdById = r.getValue(OFC_RECORD.CREATED_BY_ID);
		if(createdById !=null){
			User user = loadUser(createdById);
			c.setCreatedBy(user);
		}
		Integer modifiedById = r.getValue(OFC_RECORD.MODIFIED_BY_ID);
		if(modifiedById !=null){
			User user = loadUser(modifiedById);
			c.setModifiedBy(user);
		}
		c.setWarnings(r.getValue(OFC_RECORD.WARNINGS));
		c.setErrors(r.getValue(OFC_RECORD.ERRORS));
		c.setSkipped(r.getValue(OFC_RECORD.SKIPPED));
		c.setMissing(r.getValue(OFC_RECORD.MISSING));

		Integer step = r.getValue(OFC_RECORD.STEP);
		if (step != null) {
			c.setStep(Step.valueOf(step));
		}
		String state = r.getValue(OFC_RECORD.STATE);
		if (state != null) {
			c.setState(State.fromCode(state));
		}
		
		// create list of entity counts
		List<Integer> counts = new ArrayList<Integer>(COUNT_FIELDS.length);
		for (TableField tableField : COUNT_FIELDS) {
			counts.add(r.getValueAsInteger(tableField));
		}
		c.setEntityCounts(counts);

		// create list of keys
		List<String> keys = new ArrayList<String>(KEY_FIELDS.length);
		for (TableField tableField : KEY_FIELDS) {
			keys.add(r.getValueAsString(tableField));
		}
		c.setRootEntityKeyValues(keys);

		int rootEntityId = r.getValue(OFC_RECORD.ROOT_ENTITY_DEFINITION_ID);

		if ( dataAlias != null ) {
			byte[] data = r.getValue(dataAlias);
			//System.out.println("r.getValue(dataAlias) = " + r.getValue(dataAlias));
			Entity rootEntity = c.createRootEntity(rootEntityId);
			ModelSerializer modelSerializer = getSerializer();
			modelSerializer.mergeFrom(data, rootEntity);
		}
	}*/
	public CollectRecord fromCursor2(Cursor cursor, Record r, CollectRecord collectRecord) {
		int rootEntityId = cursor.getInt(cursor.getColumnIndex(OFC_RECORD.ROOT_ENTITY_DEFINITION_ID.getName()));
		
		String version = cursor.getString(cursor.getColumnIndex(OFC_RECORD.MODEL_VERSION.getName()));		
		Schema schema = ApplicationManager.getSurvey().getSchema();
		NodeDefinition rootEntityDefn = schema.getDefinitionById(rootEntityId);
		if (rootEntityDefn == null) {
			throw new DataInconsistencyException("Unknown root entity id " + rootEntityId);
		}
		//CollectRecord record = new CollectRecord((CollectSurvey)ApplicationManager.getSurvey(), version);
		
		collectRecord.setId(cursor.getInt(cursor.getColumnIndex(OFC_RECORD.ID.getName())));
		Date creationDate = null;
		Date modificationDate = null;
		try {
			creationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse(cursor.getString(cursor.getColumnIndex(OFC_RECORD.DATE_CREATED.getName())));
			collectRecord.setCreationDate(creationDate);
			if (cursor.getString(cursor.getColumnIndex(OFC_RECORD.DATE_MODIFIED.getName()))!=null){
				modificationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse(cursor.getString(cursor.getColumnIndex(OFC_RECORD.DATE_MODIFIED.getName())));
			}
			collectRecord.setModifiedDate(modificationDate);
		} catch (ParseException e) {
			e.printStackTrace();
			if (creationDate==null){
				collectRecord.setCreationDate(null);
			} 
			if (modificationDate==null){
				collectRecord.setModifiedDate(null);
			}
		}
		
		Integer createdById = cursor.getInt(cursor.getColumnIndex(OFC_RECORD.CREATED_BY_ID.getName()));
		if(createdById != null){
			User user = loadUser(createdById);
			collectRecord.setCreatedBy(user);
		}
		Integer modifiedById = cursor.getInt(cursor.getColumnIndex(OFC_RECORD.MODIFIED_BY_ID.getName()));
		if(modifiedById != null){
			User user = loadUser(modifiedById);
			collectRecord.setModifiedBy(user);
		}
		
		collectRecord.setWarnings(cursor.getInt(cursor.getColumnIndex(OFC_RECORD.WARNINGS.getName())));
		collectRecord.setErrors(cursor.getInt(cursor.getColumnIndex(OFC_RECORD.ERRORS.getName())));
		collectRecord.setSkipped(cursor.getInt(cursor.getColumnIndex(OFC_RECORD.SKIPPED.getName())));
		collectRecord.setMissing(cursor.getInt(cursor.getColumnIndex(OFC_RECORD.MISSING.getName())));
		
		Integer loadedStep = cursor.getInt(cursor.getColumnIndex(OFC_RECORD.STEP.getName()));
		if (loadedStep != null) {
			collectRecord.setStep(Step.valueOf(loadedStep));
		}
		String state = cursor.getString(cursor.getColumnIndex(OFC_RECORD.STATE.getName()));
		if (state != null) {
			collectRecord.setState(State.fromCode(state));
		}
		
		List<Integer> counts = new ArrayList<Integer>(COUNT_FIELDS.length);
		for (TableField tableField : COUNT_FIELDS) {
			counts.add(cursor.getInt(cursor.getColumnIndex(tableField.getName())));
		}
		collectRecord.setEntityCounts(counts);
		// create list of keys
		List<String> keys = new ArrayList<String>(KEY_FIELDS.length);
		for (TableField tableField : KEY_FIELDS) {
			keys.add(cursor.getString(cursor.getColumnIndex(tableField.getName())));
		}			
		collectRecord.setRootEntityKeyValues(keys);
		
		Entity rootEntity = collectRecord.createRootEntity(rootEntityId);
		collectRecord.setRootEntity(rootEntity);

		if ( dataAlias != null ) {
			byte[] data = r.getValue(dataAlias);
			//System.out.println("r.getValue(dataAlias) = " + r.getValue(dataAlias));
			//Entity rootEntity = c.createRootEntity(rootEntityId);
			ModelSerializer modelSerializer = getSerializer();
			modelSerializer.mergeFrom(data, rootEntity);
		}
		/*if ( dataAlias != null ) {
			byte[] data = r.getValue(dataAlias);
			//System.out.println("r.getValue(dataAlias) = " + r.getValue(dataAlias));
			//Entity rootEntity = c.createRootEntity(rootEntityId);
			ModelSerializer modelSerializer = getSerializer();
			modelSerializer.mergeFrom(data, rootEntity);
		}*/
		return collectRecord;
	}
}