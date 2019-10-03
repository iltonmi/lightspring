package org.lightspring.test.v4;

import org.junit.Assert;
import org.junit.Test;
import org.lightspring.beans.BeanDefinition;
import org.lightspring.beans.factory.support.DefaultBeanFactory;
import org.lightspring.context.annotation.ClassPathBeanDefinitionScanner;
import org.lightspring.context.annotation.ScannedGenericBeanDefinition;
import org.lightspring.core.annotation.AnnotationAttributes;
import org.lightspring.core.type.AnnotationMetadata;
import org.lightspring.stereotype.Component;

public class ClassPathBeanDefinitionScannerTest {
	
	@Test
	public void testParseScanedBean(){
		
		DefaultBeanFactory factory = new DefaultBeanFactory();
		
		String basePackages = "org.lightspring.service.v4,org.lightspring.dao.v4";
		
		ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(factory);
		scanner.doScan(basePackages);
		
		
		String annotation = Component.class.getName();
		
		{
			BeanDefinition bd = factory.getBeanDefinition("petStore");
			Assert.assertTrue(bd instanceof ScannedGenericBeanDefinition);
			ScannedGenericBeanDefinition sbd = (ScannedGenericBeanDefinition)bd;
			AnnotationMetadata amd = sbd.getMetadata();
			
			
			Assert.assertTrue(amd.hasAnnotation(annotation));		
			AnnotationAttributes attributes = amd.getAnnotationAttributes(annotation);		
			Assert.assertEquals("petStore", attributes.get("value"));
		}
		{
			BeanDefinition bd = factory.getBeanDefinition("accountDao");
			Assert.assertTrue(bd instanceof ScannedGenericBeanDefinition);
			ScannedGenericBeanDefinition sbd = (ScannedGenericBeanDefinition)bd;			
			AnnotationMetadata amd = sbd.getMetadata();
			Assert.assertTrue(amd.hasAnnotation(annotation));
		}
		{
			BeanDefinition bd = factory.getBeanDefinition("itemDao");
			Assert.assertTrue(bd instanceof ScannedGenericBeanDefinition);
			ScannedGenericBeanDefinition sbd = (ScannedGenericBeanDefinition)bd;			
			AnnotationMetadata amd = sbd.getMetadata();
			Assert.assertTrue(amd.hasAnnotation(annotation));
		}
	}
}
