package org.openforis.collect.android.service;

import java.util.List;

import org.openforis.collect.android.config.Configuration;
import org.openforis.collect.android.database.DatabaseHelper;
import org.openforis.collect.android.database.SQLDroidDataSource;
import org.openforis.collect.manager.CodeListManager;
import org.openforis.collect.manager.RecordManager;
import org.openforis.collect.manager.SurveyManager;
import org.openforis.collect.manager.UserManager;
import org.openforis.collect.model.CollectSurveyContext;
import org.openforis.collect.persistence.CodeListItemDao;
import org.openforis.collect.persistence.RecordDao;
import org.openforis.collect.persistence.SurveyDao;
import org.openforis.collect.persistence.SurveyWorkDao;
import org.openforis.collect.persistence.UserDao;
import org.openforis.collect.service.CollectCodeListService;
import org.openforis.idm.metamodel.CodeList;
import org.openforis.idm.metamodel.CodeListItem;
import org.openforis.idm.metamodel.CodeListService;
import org.openforis.idm.metamodel.ModelVersion;
import org.openforis.idm.metamodel.validation.Validator;
import org.openforis.idm.model.CodeAttribute;
import org.openforis.idm.model.expression.ExpressionFactory;

/**
 * 
 * @author S. Ricci
 *
 */
public class ServiceFactory {

	private static RecordManager recordManager;
	private static SurveyManager surveyManager;
	private static UserManager userManager;
	private static SQLDroidDataSource dataSource;
	private static CodeListManager codeListManager;

	public static void init(Configuration config) {
		init(config, true);
	}
	
	public static void init(Configuration config, boolean updateDBSchema) {
		try {
			dataSource = new SQLDroidDataSource();
	    	dataSource.setUrl(config.getDbConnectionUrl());
	    	if ( updateDBSchema ) {
	    		DatabaseHelper.updateDBSchema();
	    	}
	    	codeListManager = new CodeListManager();
	    	CodeListItemDao codeListItemDao = new CodeListItemDao();
	    	codeListItemDao.setDataSource(dataSource);
			codeListManager.setCodeListItemDao(codeListItemDao);
			CollectCodeListService codeListService = new CollectCodeListService();
			codeListService.setCodeListManager(codeListManager);
			
		    ExpressionFactory expressionFactory = new ExpressionFactory();
	    	Validator validator = new Validator();
	    	CollectSurveyContext collectSurveyContext = new CollectSurveyContext(expressionFactory, validator);
			collectSurveyContext.setCodeListService(codeListService);
	    	
	    	surveyManager = new SurveyManager();
	    	surveyManager.setCollectSurveyContext(collectSurveyContext);
	    	SurveyDao surveyDao = new SurveyDao();
	    	surveyDao.setSurveyContext(collectSurveyContext);
	    	surveyDao.setDataSource(dataSource);
	    	surveyManager.setSurveyWorkDao(new SurveyWorkDao());
	    	surveyManager.setSurveyDao(surveyDao);
	    	surveyManager.setCodeListManager(codeListManager);
	    	
	    	recordManager = new RecordManager(false);
	    	RecordDao recordDao = new RecordDao();
	    	recordDao.setDataSource(dataSource);
	    	recordManager.setRecordDao(recordDao);
	    	
			userManager = new UserManager();
	    	UserDao userDao = new UserDao();
	    	userDao.setDataSource(dataSource);
			userManager.setUserDao(userDao);
			userManager.setRecordDao(recordDao);
	
			surveyManager.init();
		} finally {
			DatabaseHelper.closeConnection();
		}
	}
	
	public static SQLDroidDataSource getDataSource() {
		return dataSource;
	}
	
	public static RecordManager getRecordManager() {
		return recordManager;
	}
	
	public static CodeListManager getCodeListManager() {
		return codeListManager;
	}
	
	public static SurveyManager getSurveyManager() {
		return surveyManager;
	}
	
	public static UserManager getUserManager() {
		return userManager;
	}

}
