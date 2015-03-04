package org.openforis.collect.android.management;

import static org.openforis.collect.persistence.jooq.Tables.OFC_RECORD;
import static org.openforis.collect.persistence.jooq.tables.OfcUser.OFC_USER;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.openforis.collect.android.database.DatabaseHelper;
import org.openforis.collect.android.database.MobileRecordDao;
import org.openforis.collect.metamodel.ui.UIOptions;
import org.openforis.collect.metamodel.ui.UIOptions.Layout;
import org.openforis.collect.model.CollectRecord;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.model.RecordSummarySortField;
import org.openforis.collect.model.User;
import org.openforis.collect.model.CollectRecord.Step;
import org.openforis.collect.persistence.RecordPersistenceException;
import org.openforis.idm.metamodel.AttributeDefinition;
import org.openforis.idm.metamodel.CodeAttributeDefinition;
import org.openforis.idm.metamodel.CodeList;
import org.openforis.idm.metamodel.CodeListItem;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.ModelVersion;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.Schema;
import org.openforis.idm.model.Code;
import org.openforis.idm.model.CodeAttribute;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.Record;
import org.springframework.transaction.annotation.Transactional;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MobileRecordManager extends org.openforis.collect.manager.RecordManager {
	
	private MobileRecordDao recordDao;
	
	private MobileCodeListManager codeListManager;
	
	private boolean lockingEnabled;
	
	public MobileRecordManager(boolean lockingEnabled) {
		super(lockingEnabled);
		this.lockingEnabled = lockingEnabled;
	}
	
	/*public MobileRecordManager(boolean lockingEnabled, RecordDao recordDao) {
		super(lockingEnabled);
		super.setRecordDao(recordDao);
		this.setRecordDao(recordDao);
	}*/
	
	/*public RecordManager(RecordDao recordDao){
		super();
		super.setRecordDao(recordDao);
		this.setRecordDao(recordDao);
	}*/
	
	public MobileRecordDao getRecordDao() {
		return recordDao;
	}

	public void setRecordDao(MobileRecordDao recordDao) {
		this.recordDao = recordDao;
	}

	public MobileCodeListManager getCodeListManager() {
		return codeListManager;
	}

	public void setCodeListManager(MobileCodeListManager codeListManager) {
		this.codeListManager = codeListManager;
		Log.e("codelistManager!!!!!!!!!!!!!!","=="+(this.codeListManager==null));
	}
	
	@Override
	@Transactional
	public List<CollectRecord> loadSummaries(CollectSurvey survey, String rootEntity) {
		return this.loadSummaries(survey, rootEntity, (String[]) null);
	}
	
	
	public List<CollectRecord> loadSummariesLocal(CollectSurvey survey, String rootEntity, Step step, int offset, int maxRecords, 
			List<RecordSummarySortField> sortFields, String... keyValues) {
		Log.e("MobileRecordDao","LoadSummaries");
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
				Date creationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse(cursor.getString(cursor.getColumnIndex(OFC_RECORD.DATE_CREATED.getName())));
				collectRecord.setCreationDate(creationDate);
				Date modificationDate = null;
				if (cursor.getString(cursor.getColumnIndex(OFC_RECORD.DATE_MODIFIED.getName()))!=null){
					modificationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse(cursor.getString(cursor.getColumnIndex(OFC_RECORD.DATE_MODIFIED.getName())));
				}
				collectRecord.setModifiedDate(modificationDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}			
			collectRecord.setId(cursor.getInt(cursor.getColumnIndex(OFC_RECORD.ID.getName())));

			result.add(collectRecord);
		}	
		cursor.close();
		db.close();
		return result;
	}	
	
	protected void addEmptyEnumeratedEntities(Entity parentEntity, EntityDefinition enumerableEntityDefn) {
		Record record = parentEntity.getRecord();
		ModelVersion version = record.getVersion();
		CodeAttributeDefinition enumeratingCodeDefn = enumerableEntityDefn.getEnumeratingKeyCodeAttribute(version);
		if(enumeratingCodeDefn != null) {
			String enumeratedEntityName = enumerableEntityDefn.getName();
			CodeList list = enumeratingCodeDefn.getList();
			System.err.println("codeListManage==null "+(codeListManager==null));
			List<CodeListItem> items = codeListManager.loadRootItems(list);
			for (int i = 0; i < items.size(); i++) {
				CodeListItem item = items.get(i);
				if(version == null || version.isApplicable(item)) {
					String code = item.getCode();
					Entity enumeratedEntity = getEnumeratedEntity(parentEntity, enumerableEntityDefn, enumeratingCodeDefn, code);
					if( enumeratedEntity == null ) {
						Entity addedEntity = performEntityAdd(parentEntity, enumeratedEntityName, i);
						//set the value of the key CodeAttribute
						CodeAttribute addedCode = (CodeAttribute) addedEntity.get(enumeratingCodeDefn.getName(), 0);
						addedCode.setValue(new Code(code));
					} else {
						parentEntity.move(enumeratedEntityName, enumeratedEntity.getIndex(), i);
					}
				}
			}
		}
	}
	
	protected void addEmptyEnumeratedEntities(Entity parentEntity) {
		Record record = parentEntity.getRecord();
		CollectSurvey survey = (CollectSurvey) parentEntity.getSurvey();
		UIOptions uiOptions = survey.getUIOptions();
		ModelVersion version = record.getVersion();
		EntityDefinition parentEntityDefn = parentEntity.getDefinition();
		List<NodeDefinition> childDefinitions = parentEntityDefn.getChildDefinitions();
		for (NodeDefinition childDefn : childDefinitions) {
			if ( childDefn instanceof EntityDefinition && (version == null || version.isApplicable(childDefn)) ) {
				EntityDefinition childEntityDefn = (EntityDefinition) childDefn;
				boolean tableLayout = uiOptions == null || uiOptions.getLayout(childEntityDefn) == Layout.TABLE;
				if(childEntityDefn.isMultiple() && childEntityDefn.isEnumerable() && tableLayout) {
					addEmptyEnumeratedEntities(parentEntity, childEntityDefn);
				}
			}
		}
	}
	
	protected void addEmptyNodes(Entity entity) {
		Record record = entity.getRecord();
		ModelVersion version = record.getVersion();
		addEmptyEnumeratedEntities(entity);
		EntityDefinition entityDefn = entity.getDefinition();
		List<NodeDefinition> childDefinitions = entityDefn.getChildDefinitions();
		for (NodeDefinition childDefn : childDefinitions) {
			if(version == null || version.isApplicable(childDefn)) {
				String childName = childDefn.getName();
				if(entity.getCount(childName) == 0) {
					int toBeInserted = entity.getEffectiveMinCount(childName);
					if ( toBeInserted <= 0 && childDefn instanceof AttributeDefinition || ! childDefn.isMultiple() ) {
						//insert at least one node
						toBeInserted = 1;
					}
					addEmptyChildren(entity, childDefn, toBeInserted);
				} else {
					List<Node<?>> children = entity.getAll(childName);
					for (Node<?> child : children) {
						if(child instanceof Entity) {
							addEmptyNodes((Entity) child);
						}
					}
				}
			}
		}
	}
	
	protected void addEmptyNodes(CollectRecord record) {
		Entity rootEntity = record.getRootEntity();
		addEmptyNodes(rootEntity);
	}
	
	public CollectRecord create(CollectSurvey survey, String rootEntityName, User user, String modelVersionName, String sessionId) throws RecordPersistenceException {
		if ( lockingEnabled && sessionId == null ) {
			throw new IllegalArgumentException("Lock session id not specified");
		}
		CollectRecord record = new CollectRecord(survey, modelVersionName);
		record.createRootEntity(rootEntityName);
		record.setCreationDate(new Date());
		record.setCreatedBy(user);
		addEmptyNodes(record);
		return record;
	}
	
	public CollectRecord create(CollectSurvey survey, String rootEntityName, User user, String modelVersionName) throws RecordPersistenceException {
		return create(survey, rootEntityName, user, modelVersionName, (String) null);
	}
}
