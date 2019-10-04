package org.lightspring.beans.factory.annotation;

import org.lightspring.beans.factory.config.AutowireCapableBeanFactory;

import java.lang.reflect.Member;

public abstract class InjectionElement {
    protected Member member;
    protected AutowireCapableBeanFactory factory;

    InjectionElement(Member member, AutowireCapableBeanFactory factory) {
        this.member = member;
        this.factory = factory;
    }

    public abstract void inject(Object target);
}
