package org.lightspring.service.v4;

import org.lightspring.beans.factory.annotation.Autowired;
import org.lightspring.dao.v3.AccountDao;
import org.lightspring.dao.v3.ItemDao;
import org.lightspring.stereotype.Component;

@Component(value="petStore")

public class PetStoreService {
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private ItemDao  itemDao;
	
	public AccountDao getAccountDao() {
		return accountDao;
	}

	public ItemDao getItemDao() {
		return itemDao;
	}
	
	
}