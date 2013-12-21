package org.openforis.collect.android.database;

import static org.openforis.collect.persistence.jooq.Tables.OFC_RECORD;
import static org.openforis.collect.persistence.jooq.tables.OfcUser.OFC_USER;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.openforis.collect.model.CollectRecord;
import org.openforis.collect.model.CollectRecord.Step;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.model.RecordSummarySortField;
import org.openforis.collect.model.User;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.Schema;
import org.springframework.transaction.annotation.Transactional;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MobileRecordDao extends org.openforis.collect.persistence.RecordDao {

	public MobileRecordDao() {
		super();
	}
	
	@Override
	@Transactional
	public List<CollectRecord> loadSummaries(CollectSurvey survey, String rootEntity, Step step, int offset, int maxRecords, 
			List<RecordSummarySortField> sortFields, String... keyValues) {

		List<CollectRecord> result = new ArrayList<CollectRecord>();
		//preparing data for query
		Schema schema = survey.getSchema();
		EntityDefinition rootEntityDefn = schema.getRootEntityDefinition(rootEntity);
		Integer rootEntityDefnId = rootEntityDefn.getId();
		
		String query = "SELECT " + 
				OFC_RECORD.DATE_CREATED + "," + OFC_RECORD.CREATED_BY_ID + "," + OFC_RECORD.DATE_MODIFIED + "," + OFC_RECORD.ERRORS + "," + OFC_RECORD.ID + "," + 
			     OFC_RECORD.MISSING + "," + OFC_RECORD.MODEL_VERSION + "," + OFC_RECORD.MODIFIED_BY_ID + "," + 
			     OFC_RECORD.ROOT_ENTITY_DEFINITION_ID + "," + OFC_RECORD.SKIPPED + "," + OFC_RECORD.STATE + "," + OFC_RECORD.STEP + "," + OFC_RECORD.SURVEY_ID + "," + 
			     OFC_RECORD.WARNINGS + "," + OFC_RECORD.KEY1 + "," + OFC_RECORD.KEY2 + "," + OFC_RECORD.KEY3 + "," + 
			     OFC_RECORD.COUNT1 + "," + OFC_RECORD.COUNT2 + "," + OFC_RECORD.COUNT3 + "," + OFC_RECORD.COUNT4 + "," + OFC_RECORD.COUNT5
			     + " FROM " + OFC_RECORD
			     + " WHERE " + OFC_RECORD.SURVEY_ID + " = " + survey.getId()
			     + " AND " + OFC_RECORD.ROOT_ENTITY_DEFINITION_ID + " = " + rootEntityDefnId;
		
		if ( step != null ) {
			query += " AND " + OFC_RECORD.STEP + " = " + step.getStepNumber();
		}
		/*Log.e("keyValues!=null","=="+(keyValues!=null));
		if ( keyValues != null ) {
			String key = keyValues[1];
			Log.e("keyValues[1]","=="+keyValues[1]);
		}*/

		query += " ORDER BY " + OFC_RECORD.ID; 
		query += " LIMIT " + maxRecords;
		
		//executing query
		SQLiteDatabase db = DatabaseHelper.getDb();
		Cursor cursor = db.rawQuery(query, null);
		
		//preparing result
		while (cursor.moveToNext()) {
			CollectRecord collectRecord = new CollectRecord(survey, survey.getVersion(cursor.getString(cursor.getColumnIndex(OFC_RECORD.MODEL_VERSION.getName()))).getName());
			
			query = "SELECT " + OFC_USER.USERNAME + " FROM " + OFC_USER
					+ " WHERE " + OFC_USER.ID + " = " + cursor.getColumnIndex(OFC_RECORD.CREATED_BY_ID.getName());

			Cursor userCursor = db.rawQuery(query, null);
			if (userCursor.moveToFirst()){
				User user = new User();
	        	user.setName(userCursor.getString(0));
	        	collectRecord.setCreatedBy(user);	
			}
			try {
				Date creationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH).parse(cursor.getString(cursor.getColumnIndex(OFC_RECORD.DATE_CREATED.getName())));
				collectRecord.setCreationDate(creationDate);
				Date modificationDate = null;
				if (cursor.getString(cursor.getColumnIndex(OFC_RECORD.DATE_CREATED.getName()))!=null){
					modificationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH).parse(cursor.getString(cursor.getColumnIndex(OFC_RECORD.DATE_CREATED.getName())));
				}
				collectRecord.setModifiedDate(modificationDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}			
			collectRecord.setId(cursor.getInt(cursor.getColumnIndex(OFC_RECORD.ID.getName())));

			result.add(collectRecord);
		}	
		db.close();
		return result;
	}	
}