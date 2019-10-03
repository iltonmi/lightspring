package org.lightspring.test.v4;

import org.junit.Assert;
import org.junit.Test;
import org.lightspring.core.annotation.AnnotationAttributes;
import org.lightspring.core.io.ClassPathResource;
import org.lightspring.core.type.classreading.AnnotationMetadataReadingVisitor;
import org.lightspring.core.type.classreading.ClassMetadataReadingVisitor;
import org.springframework.asm.ClassReader;

import java.io.IOException;


public class ClassReaderTest {

	@Test
	public void testGetClasMetaData() throws IOException {
		ClassPathResource resource = new ClassPathResource("org/lightspring/service/v4/PetStoreService.class");
		ClassReader reader = new ClassReader(resource.getInputStream());
		
		ClassMetadataReadingVisitor visitor = new ClassMetadataReadingVisitor();
		
		reader.accept(visitor, ClassReader.SKIP_DEBUG);
		
		Assert.assertFalse(visitor.isAbstract());
		Assert.assertFalse(visitor.isInterface());
		Assert.assertFalse(visitor.isFinal());		
		Assert.assertEquals("org.lightspring.service.v4.PetStoreService", visitor.getClassName());
		Assert.assertEquals("java.lang.Object", visitor.getSuperClassName());
		Assert.assertEquals(0, visitor.getInterfaceNames().length);
	}
	
	@Test
	public void testGetAnnonation() throws Exception{
		ClassPathResource resource = new ClassPathResource("org/lightspring/service/v4/PetStoreService.class");
		ClassReader reader = new ClassReader(resource.getInputStream());

		AnnotationMetadataReadingVisitor visitor = new AnnotationMetadataReadingVisitor();

		reader.accept(visitor, ClassReader.SKIP_DEBUG);

		String annotation = "org.lightspring.stereotype.Component";
		Assert.assertTrue(visitor.hasAnnotation(annotation));

		AnnotationAttributes attributes = visitor.getAnnotationAttributes(annotation);

		Assert.assertEquals("petStore", attributes.get("value"));

	}


}
