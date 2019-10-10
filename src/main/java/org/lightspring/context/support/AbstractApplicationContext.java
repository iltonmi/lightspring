package org.lightspring.context.support;

import org.lightspring.beans.factory.NoSuchBeanDefinitionException;
import org.lightspring.beans.factory.annotation.AutowiredAnnotationProcessor;
import org.lightspring.beans.factory.config.ConfigurableBeanFactory;
import org.lightspring.beans.factory.support.DefaultBeanFactory;
import org.lightspring.beans.factory.xml.XmlBeanDefinitionReader;
import org.lightspring.context.ApplicationContext;
import org.lightspring.core.io.Resource;
import org.lightspring.util.ClassUtils;

public abstract class AbstractApplicationContext implements ApplicationContext {
    private DefaultBeanFactory factory;
    private ClassLoader beanClassLoader;

    public AbstractApplicationContext(String configFile) {
        factory = new DefaultBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        Resource resource = this.getResourceByPath(configFile);
        reader.loadBeanDefinitions(resource);
        factory.setBeanClassLoader(this.getBeanClassLoader());
        registerBeanPostProcessors(factory);
    }

    public Object getBean(String beanID) {
        return this.factory.getBean(beanID);
    }

    protected abstract Resource getResourceByPath(String path);

    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    public ClassLoader getBeanClassLoader() {
        return (this.beanClassLoader != null) ? this.beanClassLoader: ClassUtils.getDefaultClassLoader();
    }

    protected void registerBeanPostProcessors(ConfigurableBeanFactory beanFactory) {

        AutowiredAnnotationProcessor postProcessor = new AutowiredAnnotationProcessor();
        postProcessor.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(postProcessor);

    }

    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return this.factory.getType(name);
    }
}
