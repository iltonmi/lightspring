package org.lightspring.aop;

import org.aopalliance.intercept.MethodInterceptor;

/**
 * @author weili
 */
public interface Advice extends MethodInterceptor {
    public Pointcut getPointcut();
}

