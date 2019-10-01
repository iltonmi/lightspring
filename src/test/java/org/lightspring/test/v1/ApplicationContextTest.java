package org.lightspring.test.v1;

import org.junit.Assert;
import org.junit.Test;
import org.lightspring.context.support.FileSystemXmlApplicationContext;
import org.lightspring.service.v1.PetStoreService;
import org.lightspring.context.ApplicationContext;
import org.lightspring.context.support.ClassPathXmlApplicationContext;

public class ApplicationContextTest {

	@Test
	public void testGetBean() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("petstore-v1.xml");
		PetStoreService petStore = (PetStoreService)ctx.getBean("petStore");
		Assert.assertNotNull(petStore);
	}
    @Test 
	public void testGetBeanFromFileSystemContext(){
	    //注意啊，这里仍然是hardcode了一个本地路径，这是不好的实践!! 如何处理，留作作业
		//同样，修改绝对路径为想相对路径
		ApplicationContext ctx = new FileSystemXmlApplicationContext("src\\test\\resources\\petstore-v1.xml");
		PetStoreService petStore = (PetStoreService)ctx.getBean("petStore");
		Assert.assertNotNull(petStore);
		
	}

}
