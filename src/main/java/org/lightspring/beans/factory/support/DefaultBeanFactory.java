package org.lightspring.beans.factory.support;



import org.lightspring.beans.BeanDefinition;
import org.lightspring.beans.PropertyValue;
import org.lightspring.beans.SimpleTypeConverter;
import org.lightspring.beans.factory.BeanCreationException;
import org.lightspring.beans.factory.config.ConfigurableBeanFactory;
import org.lightspring.util.ClassUtils;


import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultBeanFactory extends DefaultSingletonBeanRegistry
        implements ConfigurableBeanFactory, BeanDefinitionRegistry{

    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

    private ClassLoader beanClassLoader;

    public DefaultBeanFactory() {

    }
    public void registerBeanDefinition(String beanID,BeanDefinition bd){
        this.beanDefinitionMap.put(beanID, bd);
    }


    public BeanDefinition getBeanDefinition(String beanID) {
        return this.beanDefinitionMap.get(beanID);
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

    private Object createBean(BeanDefinition bd){
        Object bean = instantiateBean(bd);
        //set up the properties of the bean
        populateBean(bd, bean);

        return bean;
    }

    private Object instantiateBean(BeanDefinition bd){
        ClassLoader cl = this.getBeanClassLoader();
        String beanClassName = bd.getBeanClassName();
        try {
            Class<?> clz = cl.loadClass(beanClassName);
            return clz.getDeclaredConstructor().newInstance();
        } catch (Exception e){
            throw new BeanCreationException("create bean for" + beanClassName + "failed", e);
        }
    }

    private void populateBean(BeanDefinition bd, Object bean){
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
}
