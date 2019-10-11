package org.lightspring.test.v5;

import org.aopalliance.intercept.MethodInterceptor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lightspring.aop.aspectj.AspectJAfterReturningAdvice;
import org.lightspring.aop.aspectj.AspectJAfterThrowingAdvice;
import org.lightspring.aop.aspectj.AspectJBeforeAdvice;
import org.lightspring.aop.framework.ReflectiveMethodInvocation;
import org.lightspring.service.v5.PetStoreService;
import org.lightspring.tx.TransactionManager;
import org.lightspring.util.MessageTracker;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectiveMethodInvocationTest {


    private AspectJBeforeAdvice beforeAdvice = null;
    private AspectJAfterReturningAdvice afterAdvice = null;
    private AspectJAfterThrowingAdvice afterThrowingAdvice = null;
    private PetStoreService petStoreService = null;
    private TransactionManager tx;


    @Before
    public void setUp() throws Exception {
        petStoreService = new PetStoreService();
        tx = new TransactionManager();

        MessageTracker.clearMsgs();
        beforeAdvice = new AspectJBeforeAdvice(
                TransactionManager.class.getMethod("start"),
                null,
                tx);

        afterAdvice = new AspectJAfterReturningAdvice(
                TransactionManager.class.getMethod("commit"),
                null,
                tx);

        afterThrowingAdvice = new AspectJAfterThrowingAdvice(
                TransactionManager.class.getMethod("rollback"),
                null,
                tx
        );

    }


    @Test
    public void testMethodInvocation() throws Throwable {


        Method targetMethod = PetStoreService.class.getMethod("placeOrder");

        List<MethodInterceptor> interceptors = new ArrayList<MethodInterceptor>();
        interceptors.add(afterAdvice);
        interceptors.add(beforeAdvice);


        ReflectiveMethodInvocation mi = new ReflectiveMethodInvocation(petStoreService, targetMethod, new Object[0], interceptors);

        mi.proceed();


        List<String> msgs = MessageTracker.getMsgs();
        Assert.assertEquals(3, msgs.size());
        Assert.assertEquals("start tx", msgs.get(0));
        Assert.assertEquals("place order", msgs.get(1));
        Assert.assertEquals("commit tx", msgs.get(2));

    }

    //	@Test
//	public void testMethodInvocation2() throws Throwable{
//
//
//		Method targetMethod = PetStoreService.class.getMethod("placeOrder");
//
//		List<MethodInterceptor> interceptors = new ArrayList<MethodInterceptor>();
//		interceptors.add(afterAdvice);
//		interceptors.add(beforeAdvice);
//
//
//
//		ReflectiveMethodInvocation mi = new ReflectiveMethodInvocation(petStoreService,targetMethod,new Object[0],interceptors);
//
//		mi.proceed();
//
//
//		List<String> msgs = MessageTracker.getMsgs();
//		Assert.assertEquals(3, msgs.size());
//		Assert.assertEquals("start tx", msgs.get(0));
//		Assert.assertEquals("place order", msgs.get(1));
//		Assert.assertEquals("commit tx", msgs.get(2));
//
//	}
    @Test
    public void testAfterThrowing() throws Throwable {


        Method targetMethod = PetStoreService.class.getMethod("placeOrderWithException");

        List<MethodInterceptor> interceptors = new ArrayList<MethodInterceptor>();
        interceptors.add(afterThrowingAdvice);
        interceptors.add(beforeAdvice);


        ReflectiveMethodInvocation mi = new ReflectiveMethodInvocation(petStoreService, targetMethod, new Object[0], interceptors);
        try {
            mi.proceed();

        } catch (Throwable t) {
            List<String> msgs = MessageTracker.getMsgs();
            Assert.assertEquals(2, msgs.size());
            Assert.assertEquals("start tx", msgs.get(0));
            Assert.assertEquals("rollback tx", msgs.get(1));
            return;
        }


        Assert.fail("No Exception thrown");


    }

}
