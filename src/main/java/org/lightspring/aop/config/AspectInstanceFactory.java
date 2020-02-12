package org.lightspring.aop.config;


import org.lightspring.beans.factory.BeanFactory;
import org.lightspring.beans.factory.BeanFactoryAware;
import org.lightspring.util.StringUtils;


/**
 * Implementation of {@link AspectInstanceFactory} that locates the aspect from the
 * {@link org.springframework.beans.factory.BeanFactory} using a configured bean name.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
public class AspectInstanceFactory implements BeanFactoryAware {

    /**
     * 包含增强功能的切面
     */
    private String aspectBeanName;

    private BeanFactory beanFactory;

    public void setAspectBeanName(String aspectBeanName) {
        this.aspectBeanName = aspectBeanName;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        if (!StringUtils.hasText(this.aspectBeanName)) {
            throw new IllegalArgumentException("'aspectBeanName' is required");
        }
    }

    public Object getAspectInstance() throws Exception {
        return this.beanFactory.getBean(this.aspectBeanName);
    }
}
