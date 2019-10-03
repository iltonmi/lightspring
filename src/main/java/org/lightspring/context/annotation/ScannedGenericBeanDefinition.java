package org.lightspring.context.annotation;

import org.lightspring.beans.factory.annotation.AnnotatedBeanDefinition;
import org.lightspring.beans.factory.support.GenericBeanDefinition;
import org.lightspring.core.type.AnnotationMetadata;

public class ScannedGenericBeanDefinition extends GenericBeanDefinition implements AnnotatedBeanDefinition {

	private final AnnotationMetadata metadata;


	public ScannedGenericBeanDefinition(AnnotationMetadata metadata) {
		super();
		
		this.metadata = metadata;
		
		setBeanClassName(this.metadata.getClassName());
	}


	public final AnnotationMetadata getMetadata() {
		return this.metadata;
	}

}