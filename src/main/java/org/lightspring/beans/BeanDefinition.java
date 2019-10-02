package org.lightspring.beans;

import java.util.List;

public interface BeanDefinition {
    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";
    String SCOPE_DEFAULT = "";

    String getBeanClassName();
    List<PropertyValue> getPropertyValues();

    public boolean isSingleton();
    public boolean isPrototype();
    String getScope();
    void setScope(String scope);
}
