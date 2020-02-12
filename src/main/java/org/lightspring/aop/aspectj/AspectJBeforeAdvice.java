package org.lightspring.aop.aspectj;

import org.aopalliance.intercept.MethodInvocation;
import org.lightspring.aop.config.AspectInstanceFactory;

import java.lang.reflect.Method;


public class AspectJBeforeAdvice extends AbstractAspectJAdvice {

    public AspectJBeforeAdvice(Method adviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory adviceObjectFactory) {
        super(adviceMethod, pointcut, adviceObjectFactory);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        //例如： 调用TransactionManager的start方法
        this.invokeAdviceMethod();
        Object o = mi.proceed();
        return o;
    }


}