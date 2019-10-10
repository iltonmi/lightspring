package org.lightspring.aop;


public interface Pointcut {
    MethodMatcher getMethodMatcher();

    String getExpression();
}
