package org.lightspring.beans.factory;

import org.lightspring.beans.BeanDefinition;

public interface BeanFactory {
    Object getBean(String beanID);
}
