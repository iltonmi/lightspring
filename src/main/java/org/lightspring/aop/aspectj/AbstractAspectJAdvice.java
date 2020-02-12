package org.lightspring.aop.aspectj;

import org.lightspring.aop.Advice;
import org.lightspring.aop.Pointcut;
import org.lightspring.aop.config.AspectInstanceFactory;

import java.lang.reflect.Method;

/**
 * @author weili
 */
public abstract class AbstractAspectJAdvice implements Advice {

    /**
     * 被增强的方法
     */
    protected Method adviceMethod;

    /**
     * 拦截哪个方法
     */
    protected AspectJExpressionPointcut pointcut;

    /**
     * 储存包含增强方法的对象的工厂
     */
    protected AspectInstanceFactory adviceObjectFactory;


    public AbstractAspectJAdvice(Method adviceMethod,
                                 AspectJExpressionPointcut pointcut,
                                 AspectInstanceFactory adviceObjectFactory) {

        this.adviceMethod = adviceMethod;
        this.pointcut = pointcut;
        this.adviceObjectFactory = adviceObjectFactory;
    }


    public void invokeAdviceMethod() throws Throwable {

        adviceMethod.invoke(adviceObjectFactory.getAspectInstance());
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    public Method getAdviceMethod() {
        return adviceMethod;
    }

    public Object getAdviceInstance() throws Exception {
        return adviceObjectFactory.getAspectInstance();
    }
}