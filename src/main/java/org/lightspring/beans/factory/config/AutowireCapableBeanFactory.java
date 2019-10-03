package org.lightspring.beans.factory.config;

import org.lightspring.beans.factory.BeanFactory;

public interface AutowireCapableBeanFactory extends BeanFactory {
    public Object resolveDependency(DependencyDescriptor descriptor);
}
