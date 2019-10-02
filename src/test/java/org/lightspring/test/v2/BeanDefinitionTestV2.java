package org.lightspring.test.v2;

import org.junit.Assert;
import org.junit.Test;
import org.lightspring.beans.BeanDefinition;
import org.lightspring.beans.PropertyValue;
import org.lightspring.beans.factory.config.RuntimeBeanReference;
import org.lightspring.beans.factory.support.DefaultBeanFactory;
import org.lightspring.beans.factory.xml.XmlBeanDefinitionReader;
import org.lightspring.core.io.ClassPathResource;

import java.util.List;

public class BeanDefinitionTestV2 {

	
	@Test
	public void testGetBeanDefinition() {
		
		DefaultBeanFactory factory = new DefaultBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
		
		reader.loadBeanDefinitions(new ClassPathResource("petstore-v2.xml"));
		
		BeanDefinition bd = factory.getBeanDefinition("petStore");
		
		List<PropertyValue> pvs = bd.getPropertyValues();
		for (PropertyValue prop: pvs){
			System.out.println(prop.getName());
		}
		Assert.assertTrue(pvs.size() == 4);
		{
			PropertyValue pv = this.getPropertyValue("accountDao", pvs);
			
			Assert.assertNotNull(pv);
			
			Assert.assertTrue(pv.getValue() instanceof RuntimeBeanReference);
		}
		
		{
			PropertyValue pv = this.getPropertyValue("itemDao", pvs);
			
			Assert.assertNotNull(pv);
			
			Assert.assertTrue(pv.getValue() instanceof RuntimeBeanReference);
		}
		
	}
	
	private PropertyValue getPropertyValue(String name,List<PropertyValue> pvs){
		for(PropertyValue pv : pvs){
			if(pv.getName().equals(name)){
				return pv;
			}
		}
		return null;
	}

}

