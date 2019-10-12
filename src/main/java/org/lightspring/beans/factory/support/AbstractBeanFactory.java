package org.lightspring.beans.factory.support;

import org.lightspring.beans.BeanDefinition;
import org.lightspring.beans.factory.BeanCreationException;
import org.lightspring.beans.factory.config.ConfigurableBeanFactory;

public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements ConfigurableBeanFactory {
    protected abstract Object createBean(BeanDefinition bd) throws BeanCreationException;
}
