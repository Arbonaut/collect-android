package org.openforis.collect.android.management;

import java.util.HashMap;
import java.util.List;

import org.openforis.collect.persistence.CodeListItemDao;
import org.openforis.collect.persistence.DatabaseExternalCodeListProvider;
import org.openforis.idm.metamodel.CodeList;
import org.openforis.idm.metamodel.CodeListItem;
import org.openforis.idm.metamodel.ExternalCodeListItem;
import org.openforis.idm.metamodel.PersistedCodeListItem;

import android.util.Log;

public class MobileCodeListManager extends org.openforis.collect.manager.CodeListManager{
		
	private DatabaseExternalCodeListProvider provider;
	private CodeListItemDao codeListItemDao;
	private HashMap<CodeList,List<PersistedCodeListItem>> loadedCodeLists;
	
	public MobileCodeListManager(CodeListItemDao codeListItemDao){
		super();
		super.setCodeListItemDao(codeListItemDao);
		this.setCodeListItemDao(codeListItemDao);
		this.loadedCodeLists = new HashMap<CodeList,List<PersistedCodeListItem>>();
	}
	
	public CodeListItemDao getCodeListItemDao() {
		return codeListItemDao;
	}

	public void setCodeListItemDao(CodeListItemDao codeListItemDao) {
		this.codeListItemDao = codeListItemDao;
	}

	public DatabaseExternalCodeListProvider getProvider() {
		return provider;
	}

	public void setProvider(DatabaseExternalCodeListProvider provider) {
		this.provider = provider;
	}	
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends CodeListItem> List<T> loadChildItems(CodeListItem parent) {
		System.err.println("Load child items from mobile CodeListManager");
		CodeList list = parent.getCodeList();
		if ( list.isExternal() ) {
			return (List<T>) getProvider().getChildItems((ExternalCodeListItem) parent);
		} else if ( list.isEmpty() ) {
			System.err.println("Finish loading child items from mobile CodeListManager from CodeListDao");
			return (List<T>) getCodeListItemDao().loadChildItems((PersistedCodeListItem) parent);
		} else {
			System.err.println("Finish loading child items from mobile CodeListManager from parent.getChildItems");
			return parent.getChildItems();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends CodeListItem> List<T> loadRootItems(CodeList list) {
		if ( list.isExternal() ) {
			return (List<T>) provider.getRootItems(list);
		} else if ( list.isEmpty() ) {
			if (this.loadedCodeLists.containsKey(list)){				
				return (List<T>)this.loadedCodeLists.get(list);
			}
			List<PersistedCodeListItem> loadedCodeListItems = (List<PersistedCodeListItem>) codeListItemDao.loadRootItems(list);
			this.loadedCodeLists.put(list, loadedCodeListItems);			
			return (List<T>)loadedCodeListItems;
		} else {
			return list.getItems();
		}
	}
}
