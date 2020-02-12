package org.lightspring.aop.aspectj;

import org.aopalliance.intercept.MethodInvocation;
import org.lightspring.aop.config.AspectInstanceFactory;

import java.lang.reflect.Method;

public class AspectJAfterThrowingAdvice extends AbstractAspectJAdvice {


    public AspectJAfterThrowingAdvice(Method adviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory adviceObjectFactory) {
        super(adviceMethod, pointcut, adviceObjectFactory);
    }


    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        } catch (Throwable t) {
            invokeAdviceMethod();
            throw t;
        }
    }

}
