package org.lightspring.test.v2;

import org.junit.Test;
import org.lightspring.context.ApplicationContext;
import org.lightspring.context.support.ClassPathXmlApplicationContext;
import org.lightspring.dao.v2.AccountDao;
import org.lightspring.dao.v2.ItemDao;
import org.lightspring.service.v2.PetStoreService;
import static org.junit.Assert.*;
public class ApplicationContextTestV2 {

    @Test
    public void testGetBeanProperty(){
        ApplicationContext ctx = new ClassPathXmlApplicationContext("petstore-v2.xml");
        PetStoreService petStore = (PetStoreService)ctx.getBean("petStore");

        assertNotNull(petStore.getAccountDao());
        assertNotNull(petStore.getItemDao());

        assertTrue(petStore.getAccountDao() instanceof AccountDao);
        assertTrue(petStore.getItemDao() instanceof ItemDao);

        assertEquals("liuxin",petStore.getOwner());
        assertEquals(2, petStore.getVersion());
    }
}
