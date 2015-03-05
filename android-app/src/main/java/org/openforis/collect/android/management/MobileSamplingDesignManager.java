package org.openforis.collect.android.management;

import org.openforis.collect.manager.SamplingDesignManager;
import org.openforis.collect.persistence.SamplingDesignDao;

public class MobileSamplingDesignManager extends SamplingDesignManager {
	
	private SamplingDesignDao samplingDesignDao;
	
	public void deleteBySurvey(int surveyId) {
		samplingDesignDao.deleteBySurvey(surveyId);
	}
	
	public void setSamplingDesignDao (SamplingDesignDao samplingDesignDao){
		this.samplingDesignDao = samplingDesignDao;
	}
}
