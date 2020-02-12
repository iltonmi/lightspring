package org.lightspring.beans.factory.config;

/**
 * @author weili
 */
public class RuntimeBeanReference {
	private final String beanName;
	public RuntimeBeanReference(String beanName) {
		this.beanName = beanName;
	}
	public String getBeanName() {
		return this.beanName;
	}
}
