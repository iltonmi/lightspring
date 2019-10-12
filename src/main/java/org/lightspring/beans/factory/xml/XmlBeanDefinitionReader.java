package org.lightspring.beans.factory.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.lightspring.aop.config.ConfigBeanDefinitionParser;
import org.lightspring.beans.BeanDefinition;
import org.lightspring.beans.ConstructorArgument;
import org.lightspring.beans.PropertyValue;
import org.lightspring.beans.factory.BeanDefinitionStoreException;
import org.lightspring.beans.factory.config.RuntimeBeanReference;
import org.lightspring.beans.factory.config.TypedStringValue;
import org.lightspring.beans.factory.support.BeanDefinitionRegistry;
import org.lightspring.beans.factory.support.GenericBeanDefinition;
import org.lightspring.context.annotation.ClassPathBeanDefinitionScanner;
import org.lightspring.core.io.Resource;
import org.lightspring.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class XmlBeanDefinitionReader {

    public static final String ID_ATTRIBUTE = "id";

    public static final String CLASS_ATTRIBUTE = "class";

    public static final String SCOPE_ATTRIBUTE = "scope";

    public static final String PROPERTY_ELEMENT = "property";

    public static final String REF_ATTRIBUTE = "ref";

    public static final String VALUE_ATTRIBUTE = "value";

    public static final String NAME_ATTRIBUTE = "name";

    public static final String CONSTRUCTOR_ARG_ELEMENT = "constructor-arg";

    public static final String TYPE_ATTRIBUTE = "type";

    public static final String BEANS_NAMESPACE_URI = "http://www.springframework.org/schema/beans";

    public static final String CONTEXT_NAMESPACE_URI = "http://www.springframework.org/schema/context";

    public static final String AOP_NAMESPACE_URI = "http://www.springframework.org/schema/aop";

    private static final String BASE_PACKAGE_ATTRIBUTE = "base-package";

    protected final Log logger = LogFactory.getLog(getClass());

    BeanDefinitionRegistry registry;

    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry){
        this.registry = registry;
    }

    public void loadBeanDefinitions(Resource resource){
        InputStream is = null;
        try{
            is = resource.getInputStream();
            SAXReader reader = new SAXReader();
            Document doc = reader.read(is);
            Element root = doc.getRootElement();
            Iterator itr = root.elementIterator();
            while (itr.hasNext()) {
                Element ele = (Element) itr.next();
                String namespaceUri = ele.getNamespaceURI();
                if (isContextNamespace(namespaceUri)) {
                    parseComponentElement(ele); //例如<context:component-scan>
                }else if (isDefaultNamespace(namespaceUri)){
                    parseDefaultElement(ele); //例如普通的Bean
                } else if (this.isAOPNamespace(namespaceUri)) {
                    parseAOPElement(ele);  //例如 <aop:config>
                }
            }
        } catch (Exception e) {
            throw new BeanDefinitionStoreException("IOException parsing XML document", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void parseComponentElement(Element ele){
        String basePackages = ele.attributeValue(BASE_PACKAGE_ATTRIBUTE);
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(this.registry);
        scanner.doScan(basePackages);
    }

    private void parseAOPElement(Element ele) {
        ConfigBeanDefinitionParser parser = new ConfigBeanDefinitionParser();
        parser.parse(ele, this.registry);
    }

    private void parseDefaultElement(Element ele){
        String id = ele.attributeValue(ID_ATTRIBUTE);
        String beanClassName = ele.attributeValue(CLASS_ATTRIBUTE);
        BeanDefinition bd = new GenericBeanDefinition(id, beanClassName);
        if (ele.attribute(SCOPE_ATTRIBUTE) != null){
            bd.setScope(ele.attributeValue(SCOPE_ATTRIBUTE));
        }
        parseConstructorArgElements(ele,bd);
        parsePropertyElement(ele,bd);
        this.registry.registerBeanDefinition(id, bd);
    }

    public void parseConstructorArgElements(Element beanEle, BeanDefinition bd){
        Iterator itr = beanEle.elementIterator(CONSTRUCTOR_ARG_ELEMENT);
        while (itr.hasNext()){
            Element ele = (Element)itr.next();
            parseConstructorArgElement(ele, bd);
        }
    }

    public void parseConstructorArgElement(Element ele, BeanDefinition bd){
        String typeAttr = ele.attributeValue(TYPE_ATTRIBUTE);
        String nameAttr = ele.attributeValue(NAME_ATTRIBUTE);
        Object value = parsePropertyValue(ele, bd, null);
        ConstructorArgument.ValueHolder valueHolder = new ConstructorArgument.ValueHolder(value);
        if (StringUtils.hasLength(typeAttr)) {
            valueHolder.setType(typeAttr);
        }
        if (StringUtils.hasLength(nameAttr)) {
            valueHolder.setName(nameAttr);
        }

        bd.getConstructorArgument().addArgumentValue(valueHolder);
    }

    public void parsePropertyElement(Element beanElem, BeanDefinition bd){
        Iterator itr = beanElem.elementIterator(PROPERTY_ELEMENT);
        while (itr.hasNext()){
            Element propElem = (Element)itr.next();
            String propName = propElem.attributeValue(NAME_ATTRIBUTE);
            if (!StringUtils.hasLength(propName)){
                logger.fatal("Tag 'property' must have a 'name' attribute");
                return;
            }
            Object val = parsePropertyValue(propElem, bd, propName);
            PropertyValue pv = new PropertyValue(propName, val);
            bd.getPropertyValues().add(pv);
        }
    }

    public Object parsePropertyValue(Element ele, BeanDefinition bd, String propertyName) {
        String elementName = (propertyName != null) ?
                "<property> element for property '" + propertyName + "'" :
                "<constructor-arg> element";


        boolean hasRefAttribute = (ele.attribute(REF_ATTRIBUTE)!=null);
        boolean hasValueAttribute = (ele.attribute(VALUE_ATTRIBUTE) !=null);

        if (hasRefAttribute) {
            String refName = ele.attributeValue(REF_ATTRIBUTE);
            if (!StringUtils.hasText(refName)) {
                logger.error(elementName + " contains empty 'ref' attribute");
            }
            RuntimeBeanReference ref = new RuntimeBeanReference(refName);
            return ref;
        }else if (hasValueAttribute) {
            TypedStringValue valueHolder = new TypedStringValue(ele.attributeValue(VALUE_ATTRIBUTE));

            return valueHolder;
        }
        else {

            throw new RuntimeException(elementName + " must specify a ref or value");
        }
    }

    public boolean isDefaultNamespace(String namespaceUri) {
        return (!StringUtils.hasLength(namespaceUri) || BEANS_NAMESPACE_URI.equals(namespaceUri));
    }
    public boolean isContextNamespace(String namespaceUri){
        return (!StringUtils.hasLength(namespaceUri) || CONTEXT_NAMESPACE_URI.equals(namespaceUri));
    }

    public boolean isAOPNamespace(String namespaceUri) {
        return (!StringUtils.hasLength(namespaceUri) || AOP_NAMESPACE_URI.equals(namespaceUri));
    }
}
