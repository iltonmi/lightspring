package org.lightspring.beans.factory.annotation;

import java.util.List;

public class InjectionMetadata {
    private Class<?> targetClass;
    private List<InjectionElement> injectionElements;

    public InjectionMetadata(Class<?> targetClass, List<InjectionElement> injectionElements) {
        this.targetClass = targetClass;
        this.injectionElements = injectionElements;
    }

    public List<InjectionElement> getInjectionElements() {
        return injectionElements;
    }

    public void inject(Object target) {
        if (injectionElements == null || injectionElements.isEmpty()) {
            return;
        }
        for (InjectionElement ie : this.injectionElements) {
            ie.inject(target);
        }
    }
}
