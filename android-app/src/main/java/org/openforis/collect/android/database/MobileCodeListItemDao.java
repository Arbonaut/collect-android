package org.openforis.collect.android.database;

import static org.openforis.collect.persistence.jooq.tables.OfcCodeList.OFC_CODE_LIST;

import java.util.ArrayList;
import java.util.List;

import org.jooq.TableField;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.persistence.jooq.tables.records.OfcCodeListRecord;
import org.openforis.idm.metamodel.CodeList;
import org.openforis.idm.metamodel.ModelVersion;
import org.openforis.idm.metamodel.PersistedCodeListItem;
import org.openforis.idm.metamodel.Survey;
import org.openforis.idm.metamodel.SurveyObject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class MobileCodeListItemDao extends org.openforis.collect.persistence.CodeListItemDao {

	private ArrayList<Object> queriesList;
	
	public MobileCodeListItemDao() {
		super();
		this.queriesList = new ArrayList<Object>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected List<PersistedCodeListItem> loadChildItems(CodeList codeList, Integer parentItemId, ModelVersion version) {
		List<PersistedCodeListItem> result = new ArrayList<PersistedCodeListItem>();
	
		//Prepare query
		CollectSurvey survey = (CollectSurvey) codeList.getSurvey();
		TableField<OfcCodeListRecord, Integer> surveyIdField = getSurveyIdField(survey.isWork());		
		//Check if parent_id is NULL
		String parentIdCondition = "";
		if (parentItemId == null){
			parentIdCondition = OFC_CODE_LIST.PARENT_ID + " is null";
		}else{
			parentIdCondition = OFC_CODE_LIST.PARENT_ID + " = " + parentItemId;
		}
		//Query string
		String query = "select * from " + OFC_CODE_LIST 
				+ " where " + surveyIdField + " = " + survey.getId()
				+ " and " + OFC_CODE_LIST.CODE_LIST_ID + " = " + codeList.getId()
				+ " and " + parentIdCondition
				+ " order by " + OFC_CODE_LIST.SORT_ORDER; 
		//checking if the same query was already executed
		int queryPositionOnTheList = findQueryOnTheList(this.queriesList,query);
		if (queryPositionOnTheList>=0){
			try{
				result = ((List<PersistedCodeListItem>)this.queriesList.get(queryPositionOnTheList+1));	
			} catch (IndexOutOfBoundsException e){
				this.queriesList.add(null);
				result = null;
			}
		} else {
			this.queriesList.add(query);
			SQLiteDatabase db = DatabaseHelper.getDb();
			Cursor cursor = db.rawQuery(query, null);
			int itemId;
			PersistedCodeListItem entity;
			if (cursor.moveToFirst()){
				do{
					itemId = cursor.getInt(cursor.getColumnIndex(OFC_CODE_LIST.ITEM_ID.getName()));
					entity = new PersistedCodeListItem(codeList, itemId);
					entity.setSystemId(cursor.getInt(cursor.getColumnIndex(OFC_CODE_LIST.ID.getName())));
					entity.setSortOrder(cursor.getInt(cursor.getColumnIndex(OFC_CODE_LIST.SORT_ORDER.getName())));
					entity.setCode(cursor.getString(cursor.getColumnIndex(OFC_CODE_LIST.CODE.getName())));
					entity.setParentId(cursor.getInt(cursor.getColumnIndex(OFC_CODE_LIST.PARENT_ID.getName())));
					entity.setQualifiable(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(OFC_CODE_LIST.QUALIFIABLE.getName()))));
					entity.setSinceVersion(extractModelVersion(entity, cursor.getInt(cursor.getColumnIndex(OFC_CODE_LIST.SINCE_VERSION_ID.getName()))));
					entity.setDeprecatedVersion(extractModelVersion(entity, cursor.getInt(cursor.getColumnIndex(OFC_CODE_LIST.DEPRECATED_VERSION_ID.getName()))));
					extractLabels(codeList, cursor, entity);
					extractDescriptions(codeList, cursor, entity);			
					
					result.add(entity);
				}while(cursor.moveToNext());
			}
			cursor.close();
			db.close();
			this.queriesList.add(result);
		}
		return result;
	}
	
	private int findQueryOnTheList(ArrayList<Object> queriesList, String query){
		try {
			for (int i=0;i<queriesList.size();i++){
				if (i%2==0){
					String queryFromList = (String)queriesList.get(i);
					if (queryFromList.equals(query)){
						return i;
					}
				}			
			}	
		} catch (Exception e){
			
		}		
		return -1;		
	}
	
	protected ModelVersion extractModelVersion(SurveyObject surveyObject, Integer versionId) {
		Survey survey = surveyObject.getSurvey();
		ModelVersion version = ((versionId == null)||(versionId == 0)) ? null: survey.getVersionById(versionId);
		return version;
	}
	
	protected void extractLabels(CodeList codeList, Cursor crs, PersistedCodeListItem item) {
		Survey survey = codeList.getSurvey();
		item.removeAllLabels();
		List<String> languages = survey.getLanguages();
		String[] labelColumnNames = {OFC_CODE_LIST.LABEL1.getName(), OFC_CODE_LIST.LABEL2.getName(), OFC_CODE_LIST.LABEL3.getName()};
		for (int i = 0; i < languages.size(); i++) {
			String lang = languages.get(i);
			String label = crs.getString(crs.getColumnIndex(labelColumnNames[i]));
			item.setLabel(lang, label);
			if(i>=3)
				break;
		}
	}
	
	protected void extractDescriptions(CodeList codeList, Cursor crs, PersistedCodeListItem item) {
		Survey survey = codeList.getSurvey();
		item.removeAllDescriptions();
		List<String> languages = survey.getLanguages();
		String[] descrColumnNames = {OFC_CODE_LIST.DESCRIPTION1.getName(), OFC_CODE_LIST.DESCRIPTION2.getName(), OFC_CODE_LIST.DESCRIPTION3.getName()};
		for (int i = 0; i < languages.size(); i++) {
			String lang = languages.get(i);
			String label = crs.getString(crs.getColumnIndex(descrColumnNames[i]));
			item.setDescription(lang, label);
			if(i>=3)
				break;
		}
	}
}
