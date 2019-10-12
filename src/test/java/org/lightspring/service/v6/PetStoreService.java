package org.lightspring.service.v6;


import org.lightspring.stereotype.Component;
import org.lightspring.util.MessageTracker;

@Component(value = "petStore")
public class PetStoreService implements IPetStoreService {

    public PetStoreService() {

    }

    public void placeOrder() {
        System.out.println("place order");
        MessageTracker.addMsg("place order");
    }


}
