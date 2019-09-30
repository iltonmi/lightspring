package org.lightspring.beans;

public interface BeanDefinition {
    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";
    String SCOPE_DEFAULT = "";

    String getBeanClassName();

    public boolean isSingleton();
    public boolean isPrototype();
    String getScope();
    void setScope(String scope);
}
