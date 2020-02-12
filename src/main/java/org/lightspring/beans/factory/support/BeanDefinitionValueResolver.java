package org.lightspring.beans.factory.support;

import org.lightspring.beans.BeanDefinition;
import org.lightspring.beans.BeansException;
import org.lightspring.beans.factory.BeanCreationException;
import org.lightspring.beans.factory.BeanFactory;
import org.lightspring.beans.factory.FactoryBean;
import org.lightspring.beans.factory.config.RuntimeBeanReference;
import org.lightspring.beans.factory.config.TypedStringValue;

public class BeanDefinitionValueResolver {
    private final AbstractBeanFactory beanFactory;

    public BeanDefinitionValueResolver(AbstractBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public Object resolveValueIfNecessary(Object value){
        if (value instanceof RuntimeBeanReference){
            String refName = ((RuntimeBeanReference) value).getBeanName();
            return this.beanFactory.getBean(refName);
        } else if (value instanceof TypedStringValue){
            return ((TypedStringValue) value).getValue();
        } else if (value instanceof BeanDefinition) {
            //匿名内置Bean
            //Bean定义不会出现在BeanFactory的BeanDefinitionMap
            // Resolve plain BeanDefinition, without contained name: use dummy name.
            BeanDefinition bd = (BeanDefinition) value;

            String innerBeanName = "(inner bean)" + bd.getBeanClassName() + "#" +
                    Integer.toHexString(System.identityHashCode(bd));

            return resolveInnerBean(innerBeanName, bd);

        } else {
            return value;
        }
    }

    private Object resolveInnerBean(String innerBeanName, BeanDefinition innerBd) {

        try {

            Object innerBean = this.beanFactory.createBean(innerBd);

            if (innerBean instanceof FactoryBean) {
                try {
                    return ((FactoryBean<?>) innerBean).getObject();
                } catch (Exception e) {
                    throw new BeanCreationException(innerBeanName, "FactoryBean threw exception on object creation", e);
                }
            } else {
                return innerBean;
            }
        } catch (BeansException ex) {
            throw new BeanCreationException(
                    innerBeanName,
                    "Cannot create inner bean '" + innerBeanName + "' " +
                            (innerBd != null && innerBd.getBeanClassName() != null ? "of type [" + innerBd.getBeanClassName() + "] " : "")
                    , ex);
        }
    }
}
