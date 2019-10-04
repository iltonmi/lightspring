package org.lightspring.beans.factory.annotation;

import org.lightspring.beans.factory.BeanCreationException;
import org.lightspring.beans.factory.config.AutowireCapableBeanFactory;
import org.lightspring.beans.factory.config.DependencyDescriptor;
import org.lightspring.util.ReflectionUtils;

import java.lang.reflect.Field;

public class AutowiredFieldElement extends InjectionElement {
    boolean required;

    public AutowiredFieldElement(Field f, boolean required, AutowireCapableBeanFactory factory) {
        super(f, factory);
        this.required = required;
    }

    public Field getField() {
        return (Field) this.member;
    }

    @Override
    public void inject(Object target) {
        Field field = this.getField();
        try {
            DependencyDescriptor descriptor = new DependencyDescriptor(field, this.required);

            Object value = this.factory.resolveDependency(descriptor);
            if (value != null) {
                ReflectionUtils.makeAccessible(field);
                field.set(target, value);
            }
        } catch (Throwable ex) {
            throw new BeanCreationException("Could not autowire field: " + field, ex);
        }
    }
}
