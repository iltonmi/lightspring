package org.lightspring.beans.factory.support;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lightspring.beans.BeanDefinition;
import org.lightspring.beans.BeansException;
import org.lightspring.beans.PropertyValue;
import org.lightspring.beans.SimpleTypeConverter;
import org.lightspring.beans.factory.BeanCreationException;
import org.lightspring.beans.factory.BeanFactoryAware;
import org.lightspring.beans.factory.NoSuchBeanDefinitionException;
import org.lightspring.beans.factory.config.BeanPostProcessor;
import org.lightspring.beans.factory.config.ConfigurableBeanFactory;
import org.lightspring.beans.factory.config.DependencyDescriptor;
import org.lightspring.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.lightspring.context.support.AbstractApplicationContext;
import org.lightspring.util.ClassUtils;


import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultBeanFactory extends AbstractBeanFactory
        implements BeanDefinitionRegistry {
    private static final Log logger = LogFactory.getLog(DefaultBeanFactory.class);
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();
    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<BeanPostProcessor>();

    private ClassLoader beanClassLoader;

    public DefaultBeanFactory() {

    }
    public void registerBeanDefinition(String beanID,BeanDefinition bd){
        this.beanDefinitionMap.put(beanID, bd);
    }


    public BeanDefinition getBeanDefinition(String beanID) {
        return this.beanDefinitionMap.get(beanID);
    }

    public void addBeanPostProcessor(BeanPostProcessor postProcessor) {
        this.beanPostProcessors.add(postProcessor);
    }

    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }

    public Object getBean(String beanID) {
        BeanDefinition bd = this.beanDefinitionMap.get(beanID);
        if (bd == null){
            throw new BeanCreationException("Bean Definition does not exist");
        }
        if (bd.isSingleton()) {
            Object bean = this.getSingleton(beanID);
            if (bean == null){
                bean = this.createBean(bd);
                this.registerSingleton(beanID, bean);
            }
            return bean;
        }
        return this.createBean(bd);
    }

    @Override
    protected Object createBean(BeanDefinition bd) {
        Object bean = instantiateBean(bd);
        //set up the properties of the bean
        populateBean(bd, bean);
        bean = initializeBean(bd, bean);
        return bean;
    }

    private Object instantiateBean(BeanDefinition bd){
        if (bd.hasConstructorArgumentValues()){
            ConstructorResolver resolver = new ConstructorResolver(this);
            return resolver.autowireConstructor(bd);
        }else {
            ClassLoader cl = this.getBeanClassLoader();
            String beanClassName = bd.getBeanClassName();
            try {
                Class<?> clz = cl.loadClass(beanClassName);
                return clz.getDeclaredConstructor().newInstance();
            } catch (Exception e){
                throw new BeanCreationException("create bean for " + beanClassName + " failed", e);
            }
        }
    }

    private void populateBean(BeanDefinition bd, Object bean){
        for (BeanPostProcessor processor : this.getBeanPostProcessors()) {
            if (processor instanceof InstantiationAwareBeanPostProcessor) {
                ((InstantiationAwareBeanPostProcessor) processor).postProcessPropertyValues(bean, bd.getID());
            }
        }
        List<PropertyValue> pvs = bd.getPropertyValues();
        if (pvs == null || pvs.isEmpty()){
            return;
        }
        BeanDefinitionValueResolver resolver = new BeanDefinitionValueResolver(this);
        SimpleTypeConverter converter = new SimpleTypeConverter();
        try{
            for (PropertyValue pv : pvs){
                String propName = pv.getName();
                Object originalValue = pv.getValue();
                Object resolvedValue = resolver.resolveValueIfNecessary(originalValue);
                BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
                PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor pd : pds){
                    if (pd.getName().equals(propName)){
                        Object convertedValue = converter.convertIfNecessary(resolvedValue, pd.getPropertyType());
                        pd.getWriteMethod().invoke(bean, convertedValue);
                        break;
                    }
                }
            }
        } catch (Exception e){
            throw new BeanCreationException("Fail to obtain BeanInfo for class {" + bd.getBeanClassName() + "}", e);
        }
    }

    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    public ClassLoader getBeanClassLoader() {
        return (this.beanClassLoader != null) ? this.beanClassLoader: ClassUtils.getDefaultClassLoader();
    }

    public Object resolveDependency(DependencyDescriptor descriptor) {
        Class<?> typeToMatch = descriptor.getDependencyType();
        for (BeanDefinition bd : this.beanDefinitionMap.values()) {
            resolveBeanClass(bd);
            Class<?> beanClass = bd.getBeanClass();
            if (beanClass.isAssignableFrom(typeToMatch)) {
                return this.getBean(bd.getID());
            }
        }
        return null;
    }

    public void resolveBeanClass(BeanDefinition bd) {
        if (bd.hasBeanClass()) {
            return;
        } else {
            try {
                bd.resolveBeanClass(this.getBeanClassLoader());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("can't load class:" + bd.getBeanClassName());
            }
        }
    }

    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        BeanDefinition bd = this.getBeanDefinition(name);
        if (bd == null) {
            throw new NoSuchBeanDefinitionException(name);
        }
        resolveBeanClass(bd);
        return bd.getBeanClass();
    }

    public List<Object> getBeansByType(Class<?> type) {
        List<Object> result = new ArrayList<Object>();
        List<String> beanIDs = this.getBeanIDsByType(type);
        for (String beanID : beanIDs) {
            result.add(this.getBean(beanID));
        }
        return result;
    }

    private List<String> getBeanIDsByType(Class<?> type) {
        List<String> result = new ArrayList<String>();
        for (String beanName : this.beanDefinitionMap.keySet()) {
            Class<?> beanClass = null;
            try {
                beanClass = this.getType(beanName);
            } catch (Exception e) {
                logger.warn("can't load class for bean :" + beanName + ", skip it.");
                continue;
            }
            if ((beanClass != null) && type.isAssignableFrom(this.getType(beanName))) {
                result.add(beanName);
            }
        }
        return result;
    }

    protected Object initializeBean(BeanDefinition bd, Object bean) {
        invokeAwareMethods(bean);
        //Todo，调用Bean的init方法，暂不实现
        if (!bd.isSynthetic()) {
            return applyBeanPostProcessorsAfterInitialization(bean, bd.getID());
        }
        return bean;
    }

    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
            throws BeansException {

        Object result = existingBean;
        for (BeanPostProcessor beanProcessor : getBeanPostProcessors()) {
            result = beanProcessor.afterInitialization(result, beanName);
            if (result == null) {
                return result;
            }
        }
        return result;
    }

    private void invokeAwareMethods(final Object bean) {
        if (bean instanceof BeanFactoryAware) {
            ((BeanFactoryAware) bean).setBeanFactory(this);
        }
    }
}
