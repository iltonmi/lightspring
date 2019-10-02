package org.lightspring.test.v3;

import org.junit.Assert;
import org.junit.Test;
import org.lightspring.context.ApplicationContext;
import org.lightspring.context.support.ClassPathXmlApplicationContext;
import org.lightspring.service.v3.PetStoreService;

public class ApplicationContextTestV3 {

	@Test
	public void testGetBeanProperty() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("petstore-v3.xml");
		PetStoreService petStore = (PetStoreService)ctx.getBean("petStore");
		
		Assert.assertNotNull(petStore.getAccountDao());
		Assert.assertNotNull(petStore.getItemDao());		
		Assert.assertEquals(1, petStore.getVersion());
		
	}

}
