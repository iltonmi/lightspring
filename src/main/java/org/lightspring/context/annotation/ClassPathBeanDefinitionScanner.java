package org.lightspring.context.annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lightspring.beans.BeanDefinition;
import org.lightspring.beans.factory.BeanDefinitionStoreException;
import org.lightspring.beans.factory.support.BeanDefinitionRegistry;
import org.lightspring.beans.factory.support.BeanNameGenerator;
import org.lightspring.core.io.Resource;
import org.lightspring.core.io.support.PackageResourceLoader;
import org.lightspring.core.type.classreading.MetadataReader;
import org.lightspring.core.type.classreading.SimpleMetadataReader;
import org.lightspring.stereotype.Component;
import org.lightspring.util.StringUtils;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class ClassPathBeanDefinitionScanner {
	

	private final BeanDefinitionRegistry registry;
	
	private PackageResourceLoader resourceLoader = new PackageResourceLoader();
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

	public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {		
		this.registry = registry;		
	}
	
	public Set<BeanDefinition> doScan(String packagesToScan) {
		//得到需要扫描的包名
		String[] basePackages = StringUtils.tokenizeToStringArray(packagesToScan,",");
		
		Set<BeanDefinition> beanDefinitions = new LinkedHashSet<BeanDefinition>();
		//遍历所有需要扫描的包名
		for (String basePackage : basePackages) {
			//扫描当前包
			Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
			//将当前包下得到的所有BeanDefinition注册到BeanFactory
			for (BeanDefinition candidate : candidates) {
				beanDefinitions.add(candidate);
				registry.registerBeanDefinition(candidate.getID(),candidate);
				
			}
		}
		return beanDefinitions;
	}
	
	
	
	public Set<BeanDefinition> findCandidateComponents(String basePackage) {
		Set<BeanDefinition> candidates = new LinkedHashSet<BeanDefinition>();
		try {
			
			Resource[] resources = this.resourceLoader.getResources(basePackage);
			
			for (Resource resource : resources) {
				try {
					
					MetadataReader metadataReader = new SimpleMetadataReader(resource);
					//判断当前类文件元数据是否包含注解
					if(metadataReader.getAnnotationMetadata().hasAnnotation(Component.class.getName())){
						ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader.getAnnotationMetadata());
						//根据注AnnotationBeanDefinition生成BeanID
						String beanName = this.beanNameGenerator.generateBeanName(sbd, this.registry);
						sbd.setId(beanName);
						candidates.add(sbd);					
					}
				}
				catch (Throwable ex) {
					throw new BeanDefinitionStoreException(
							"Failed to read candidate component class: " + resource, ex);
				}
				
			}
		}
		catch (IOException ex) {
			throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
		}
		return candidates;
	}
	
}
