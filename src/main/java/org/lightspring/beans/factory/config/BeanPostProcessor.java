package org.lightspring.beans.factory.config;

import org.lightspring.beans.BeansException;

public interface BeanPostProcessor {

    Object beforeInitialization(Object bean, String beanName) throws BeansException;


    Object afterInitialization(Object bean, String beanName) throws BeansException;

}
