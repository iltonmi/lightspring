package org.lightspring.beans.factory.annotation;

import org.lightspring.beans.BeanDefinition;
import org.lightspring.core.type.AnnotationMetadata;

public interface AnnotatedBeanDefinition extends BeanDefinition {
	AnnotationMetadata getMetadata();
}
