package org.lightspring.beans.factory.support;

import org.lightspring.beans.factory.BeanFactory;
import org.lightspring.beans.factory.config.RuntimeBeanReference;
import org.lightspring.beans.factory.config.TypedStringValue;

public class BeanDefinitionValueResolver {
    private final BeanFactory factory;

    public BeanDefinitionValueResolver(BeanFactory factory) {
        this.factory = factory;
    }

    public Object resolveValueIfNecessary(Object value){
        if (value instanceof RuntimeBeanReference){
            String refName = ((RuntimeBeanReference) value).getBeanName();
            return this.factory.getBean(refName);
        } else if (value instanceof TypedStringValue){
            return ((TypedStringValue) value).getValue();
        } else {
            throw new RuntimeException("the value " + value +" has not implemented");
        }
    }
}
