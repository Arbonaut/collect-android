package org.openforis.collect.android.management;

import java.util.List;

import org.openforis.collect.manager.SpeciesManager;
import org.openforis.collect.model.CollectTaxonomy;
import org.openforis.collect.persistence.SurveyDao;
import org.openforis.collect.persistence.TaxonomyDao;
import org.springframework.transaction.annotation.Transactional;

public class MobileSpeciesManager extends SpeciesManager {

	private TaxonomyDao taxonomyDao;
	
	@Transactional
	public void deleteTaxonomiesBySurvey(int surveyId) {
		List<CollectTaxonomy> taxonomies = taxonomyDao.loadAllBySurvey(surveyId);
		for (CollectTaxonomy taxonomy : taxonomies) {
			delete(taxonomy);
		}
	}
	
	
	@Transactional
	public void delete(CollectTaxonomy taxonomy) {
		Integer id = taxonomy.getId();
		deleteTaxonsByTaxonomy(id);
		taxonomyDao.delete(id);
	}
	
	public void setTaxonomyDao(TaxonomyDao taxonomyDao) {
		this.taxonomyDao = taxonomyDao;
	}
}
