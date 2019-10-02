package org.lightspring.beans.factory.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lightspring.beans.BeanDefinition;
import org.lightspring.beans.ConstructorArgument;
import org.lightspring.beans.SimpleTypeConverter;
import org.lightspring.beans.factory.BeanCreationException;
import org.lightspring.beans.factory.config.ConfigurableBeanFactory;

import java.lang.reflect.Constructor;
import java.util.List;


public class ConstructorResolver {

	protected final Log logger = LogFactory.getLog(getClass());
	
	
	private final ConfigurableBeanFactory beanFactory;


	
	public ConstructorResolver(ConfigurableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	
	public Object autowireConstructor(final BeanDefinition bd) {
		Constructor<?> constructorToUse = null;
		Object[] argsToUse = null;

		Class<?> beanClass = null;
		try {
			beanClass = this.beanFactory.getBeanClassLoader().loadClass(bd.getBeanClassName());

		} catch (ClassNotFoundException e) {
			throw new BeanCreationException( bd.getID(), "Instantiation of bean failed, can't resolve class", e);
		}

		Constructor<?>[] candidates = beanClass.getConstructors();


		BeanDefinitionValueResolver valueResolver =
				new BeanDefinitionValueResolver(this.beanFactory);

		ConstructorArgument cargs = bd.getConstructorArgument();
		SimpleTypeConverter typeConverter = new SimpleTypeConverter();
		for(int i =0; i < candidates.length; ++i){
			Class<?> [] parameterTypes = candidates[i].getParameterTypes();
			//判断参数数量是否一致
			if(parameterTypes.length != cargs.getArgumentCount()){
				continue;
			}
			//初始化数组用作保存注入参数
			argsToUse = new Object[parameterTypes.length];
			//判断参数类型和顺序是否一致并返回布尔值
			//保存注入参数
			boolean result = this.valuesMatchTypes(parameterTypes,
					cargs.getArgumentValues(),
					argsToUse,
					valueResolver,
					typeConverter);
			//若为真则保存当前遍历的构造函数并退出循环
			if (result) {
				constructorToUse = candidates[i];
				break;
			}
		}
		//若没有可用构造函数则抛出异常
		if (constructorToUse == null){
			throw new BeanCreationException( bd.getID(), "can't find a apporiate constructor");
		}

		try {
			return constructorToUse.newInstance(argsToUse);
		} catch (Exception e) {
			throw new BeanCreationException( bd.getID(), "can't find a create instance using "+constructorToUse);
		}
	}

	private boolean valuesMatchTypes(Class<?> [] parameterTypes,
			List<ConstructorArgument.ValueHolder> valueHolders,
			Object[] argsToUse,
			BeanDefinitionValueResolver valueResolver,
			SimpleTypeConverter typeConverter ){
		//通过BeanDefinitionResolver获得传入参数对应的值
		//通过TypeConverter的转换类型功能判断值类型和参数类型是否相同
		for (int i = 0; i < parameterTypes.length; ++i){
			ConstructorArgument.ValueHolder valueHolder
					= valueHolders.get(i);
			//获取参数的值，可能是TypedStringValue, 也可能是RuntimeBeanReference
			Object originalValue = valueHolder.getValue();

			try{
				//获得真正的值
				Object resolvedValue = valueResolver.resolveValueIfNecessary(originalValue);
				//如果参数类型是 int, 但是值是字符串,例如"3",还需要转型
				//如果转型失败，则抛出异常。说明这个构造函数不可用
				Object convertedValue = typeConverter.convertIfNecessary(resolvedValue, parameterTypes[i]);
				//转型成功，记录下来
				argsToUse[i] = convertedValue;
			} catch (Exception e) {
				logger.error(e);
				return false;
			}
		}
		return true;
	}
	

}
