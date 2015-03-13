package org.openforis.collect.android.service;

import org.openforis.collect.android.config.Configuration;
import org.openforis.collect.android.database.DatabaseHelper;
import org.openforis.collect.android.database.MobileRecordDao;
import org.openforis.collect.android.database.SQLDroidDataSource;
import org.openforis.collect.android.management.MobileCodeListManager;
import org.openforis.collect.android.management.MobileRecordManager;
import org.openforis.collect.android.management.MobileSamplingDesignManager;
import org.openforis.collect.android.management.MobileSpeciesManager;
import org.openforis.collect.android.management.MobileSurveyManager;
import org.openforis.collect.android.management.TaxonManager;
import org.openforis.collect.manager.UserManager;
import org.openforis.collect.model.CollectSurveyContext;
import org.openforis.collect.persistence.CodeListItemDao;
import org.openforis.collect.persistence.SamplingDesignDao;
import org.openforis.collect.persistence.SurveyDao;
import org.openforis.collect.persistence.SurveyWorkDao;
import org.openforis.collect.persistence.TaxonDao;
import org.openforis.collect.persistence.TaxonVernacularNameDao;
import org.openforis.collect.persistence.TaxonomyDao;
import org.openforis.collect.persistence.UserDao;
import org.openforis.collect.service.CollectCodeListService;
import org.openforis.idm.metamodel.validation.Validator;
import org.openforis.idm.model.expression.ExpressionFactory;
//import org.openforis.collect.manager.RecordFileManager;

/**
 * 
 * @author S. Ricci
 * @author K. Waga
 *
 */
public class ServiceFactory {

	private static MobileRecordManager recordManager;
	//private static RecordFileManager recordFileManager;
	private static MobileSurveyManager surveyManager;
	private static UserManager userManager;
	private static TaxonManager taxonManager;
	private static SQLDroidDataSource dataSource;
	private static MobileCodeListManager codeListManager;

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
	    	
	    	CodeListItemDao codeListItemDao = new CodeListItemDao();
			codeListItemDao.setDataSource(dataSource);
	    	codeListManager = new org.openforis.collect.android.management.MobileCodeListManager(codeListItemDao);	    	
			
			
			CollectCodeListService codeListService = new CollectCodeListService();
			codeListService.setCodeListManager(codeListManager);
			
		    ExpressionFactory expressionFactory = new ExpressionFactory();
	    	Validator validator = new Validator();
	    	CollectSurveyContext collectSurveyContext = new CollectSurveyContext(expressionFactory, validator);
			collectSurveyContext.setCodeListService(codeListService);
	    	
	    	surveyManager = new MobileSurveyManager();
	    	surveyManager.setCollectSurveyContext(collectSurveyContext);
	    	SurveyDao surveyDao = new SurveyDao();
	    	surveyDao.setSurveyContext(collectSurveyContext);
	    	surveyDao.setDataSource(dataSource);
	    	surveyManager.setSurveyWorkDao(new SurveyWorkDao());
	    	surveyManager.setSurveyDao(surveyDao);
	    	surveyManager.setCodeListManager(codeListManager);
	    	
	    	MobileRecordDao recordDao = new MobileRecordDao();
	    	recordManager = new MobileRecordManager(false);	    	
	    	recordDao.setDataSource(dataSource);
	    	recordManager.setRecordDao(recordDao);
	    	recordManager.setCodeListManager(codeListManager);
	    	
	    	//recordFileManager = new RecordFileManager();

	    	
			userManager = new UserManager();
	    	UserDao userDao = new UserDao();
	    	userDao.setDataSource(dataSource);
			userManager.setUserDao(userDao);
			userManager.setRecordDao(recordDao);
			
			taxonManager = new TaxonManager();
	    	TaxonDao taxonDao = new TaxonDao();
	    	taxonDao.setDataSource(dataSource);
	    	taxonManager.setTaxonDao(taxonDao);
	    	TaxonomyDao taxonomyDao = new TaxonomyDao();
	    	taxonomyDao.setDataSource(dataSource);
	    	taxonManager.setTaxonomyDao(taxonomyDao);
	    	TaxonVernacularNameDao taxonVernNameDao = new TaxonVernacularNameDao();
	    	taxonVernNameDao.setDataSource(dataSource);
	    	taxonManager.setTaxonVernacularNameDao(taxonVernNameDao);
	    	
			surveyManager.init();
	    	surveyManager.setRecordDao(recordDao);
	    	
	    	MobileSpeciesManager speciesManager = new MobileSpeciesManager();
	    	speciesManager.setTaxonDao(taxonDao);
	    	speciesManager.setTaxonomyDao(taxonomyDao);
	    	speciesManager.setTaxonVernacularNameDao(taxonVernNameDao);
	    	surveyManager.setSpeciesManager(speciesManager);
	    	
	    	MobileSamplingDesignManager samplingDesignManager = new MobileSamplingDesignManager();
	    	samplingDesignManager.setSamplingDesignDao(new SamplingDesignDao());
	    	surveyManager.setSamplingDesignManager(samplingDesignManager);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DatabaseHelper.closeConnection();
		}
	}
	
	public static SQLDroidDataSource getDataSource() {
		return dataSource;
	}
	
	public static MobileRecordManager getRecordManager() {
		return recordManager;
	}
	
	/*public static RecordFileManager getRecordFileManager() {
		return recordFileManager;
	}*/
	
	public static MobileCodeListManager getCodeListManager() {
		return codeListManager;
	}
	
	public static MobileSurveyManager getSurveyManager() {
		return surveyManager;
	}
	
	public static UserManager getUserManager() {
		return userManager;
	}

	public static TaxonManager getTaxonManager() {
		return taxonManager;
	}
}