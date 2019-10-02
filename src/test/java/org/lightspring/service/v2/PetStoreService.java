package org.lightspring.service.v2;

import org.lightspring.dao.v2.AccountDao;
import org.lightspring.dao.v2.ItemDao;

public class PetStoreService {
	private AccountDao accountDao;
	private ItemDao  itemDao;

	public AccountDao getAccountDao() {
		return accountDao;
	}
	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}
	public ItemDao getItemDao() {
		return itemDao;
	}
	public void setItemDao(ItemDao itemDao) {
		this.itemDao = itemDao;
	}
}
