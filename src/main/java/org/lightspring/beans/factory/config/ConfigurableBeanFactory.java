package org.lightspring.beans.factory.config;

import org.lightspring.beans.factory.BeanFactory;

public interface ConfigurableBeanFactory extends AutowireCapableBeanFactory {
    void setBeanClassLoader(ClassLoader beanClassLoader);
    ClassLoader getBeanClassLoader();
}
