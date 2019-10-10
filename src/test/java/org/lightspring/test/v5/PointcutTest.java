package org.lightspring.test.v5;

import org.junit.Assert;
import org.junit.Test;
import org.lightspring.aop.MethodMatcher;
import org.lightspring.aop.aspectj.AspectJExpressionPointcut;
import org.lightspring.service.v5.PetStoreService;

import java.lang.reflect.Method;


public class PointcutTest {
    @Test
    public void testPointcut() throws Exception {

        String expression = "execution(* org.lightspring.service.v5.*.placeOrder(..))";

        AspectJExpressionPointcut pc = new AspectJExpressionPointcut();
        pc.setExpression(expression);

        MethodMatcher mm = pc.getMethodMatcher();

        {
            Class<?> targetClass = PetStoreService.class;

            Method method1 = targetClass.getMethod("placeOrder");
            Assert.assertTrue(mm.matches(method1));

            Method method2 = targetClass.getMethod("getAccountDao");
            Assert.assertFalse(mm.matches(method2));
        }

        {
            Class<?> targetClass = org.lightspring.service.v4.PetStoreService.class;

            Method method = targetClass.getMethod("getAccountDao");
            Assert.assertFalse(mm.matches(method));
        }

    }
}
