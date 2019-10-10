package org.lightspring.service.v5;


import org.lightspring.beans.factory.annotation.Autowired;
import org.lightspring.dao.v5.AccountDao;
import org.lightspring.dao.v5.ItemDao;
import org.lightspring.stereotype.Component;
import org.lightspring.util.MessageTracker;

@Component(value = "petStore")
public class PetStoreService {
    @Autowired
    AccountDao accountDao;
    @Autowired
    ItemDao itemDao;

    public PetStoreService() {

    }

    public ItemDao getItemDao() {
        return itemDao;
    }

    public AccountDao getAccountDao() {
        return accountDao;
    }

    public void placeOrder() {
        System.out.println("place order");
        MessageTracker.addMsg("place order");

    }
}
