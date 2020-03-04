# 简介：
实现了AOP的Spring IOC容器。
5000+行的源码，麻雀虽小，但五脏俱全。
开发过程使用TDD开发模式，提供了6个版本的测试用例。
想要了解这个容器，最好结合测试用例和github提交记录，一步步抽丝剥茧。

功能简介如下：
* 实现了setter注入、constructor注入和Autowired注解式注入。
* Xml配置component-scan便签，使用ASM字节码框架发现指定包下的@Component。
* 通过JDK Proxy和CGLIB实现AOP增强。

# IOC部分：

## Xml配置文件的封装：Resource
   输入流的获取：
   1. ClassPathResource，使用ClassLoader的getResourceAsStream()
   2. FileSystemResource，创建File实例，new一个FileInputStream


## BeanDefinition的读取
通过XmlBeanDefinitionReader实现，它依赖BeanDefinitionRegistry
将封装了配置文件的Resource传入它的loadBeanDefinition方法
利用dom4j的第三方库解析xml结构树，将解析出来的BeanDefinition注册到Registry

## 单例或者原型
xml中用scope字段指定，为空则默认单例。

## 不同的依赖注入方式，对应的BeanDefinition
### 1.setter注入
#### BeanDefinition的表示：
1. PropertyValue表示属性，其中：ref的bean id使用RuntimeBeanReference表示，value的值用TypedStringValue表示
2. name属性也保存在PV
3. BeanDefinition用ArrayList保存PropertyValue
#### 具体注入过程：
populateBean的时候，BeanDefinitionValueResolver将
1. 对RuntimeBeanReference类型，获取bean id并递归调用BeanFactory的getBean
2. 对TypedStringValue调用getValue获取字面值
java.beans包的Introspector反射获取BeanInfo，然后获取PropertyDescriptor，去匹配成员变量名称，利用BeanDefinitionValueResolver和TypeConverter转换bean id对应的对象或数值。
3. 利用PropertydDesctiptor的getWriteMethod()反射获取setter，再反射调用setter，完成属性注入。

### 2. 构造器注入
#### BeanDefinition的表示：
1. ConstructorArgument用ArrayList保存参数。
2. ConstructorArgument的内部类ValueHolder表示参数的引用或者值。

#### 具体注入过程：
instansiateBean的时候，
1. ConstructorResolver通过bean class对象的getConstructors方法反射获取所有可能的构造器。
2. 通过Constructor的getParamaterTypes()获取参数对应的class对象的数组，判断参数的长度，利用TypeConverter判断类型转换是否合法。
3. 若找到了符合的Constructor，利用BeanDefinitionValueResolver和TypeConverter转换bean id对应的对象或数值。
4. 最后Constructor调用newInstance()，传入转换过的参数数组，通过反射创建对象。

### 3. 声明式组件和声明式注入（@Component和@Autowired）
#### BeanDefinition的表示：
ScannedGenericBeanDefinition保存了AnnotationMetaData

#### 辅助类：
1. InjectionElement保存需要注入的Field
2. InjectionMetaData封装注入过程，它用List保存InjectionElement。
3. DependencyDescriptor依赖描述符，描述需要注入的Field，是BeanFactory和InjectionElement的桥梁
4. AutowiredAnnotationProcessor把一个Class变成InjectionMetaData，是InstantiationAwareBeanPostProcessor，实例化之后的钩子函数。

#### 具体注入过程：
前置条件： ApplicationContext把AutowiredAnnotationProcessor注册到DefaultBeanFactory
注入过程：populateBean的时候，
1. AutowiredAnnotationProcessor使用Class对象将@Autowired的Field封装进InjectionElement，
2. 然后把InjectionElement封装进InjectionMetaData
3. InjectionMetaData的inject方法调用InjectionElement的inject()方法

### component-scan的自动扫描@Component实现
1. XmlBeanDefinitionReader解析出context:component-scan的base-package指定的包名
2. ClassPathBeanDefinitionScanner的doScan()被调用：使用PackageResourceLoader加载指定包下的Resource数组
3. 将封装好的Resource传入MetaDataReader构造器：构造器中发生的事情，获取Resource的输入流传入ClassReader的构造器，创建ClassVisitor传入ClassReader的accept()方法
，ClassVistor中就有了类和注解的元数据。
4. 判断类是否包含@Component注解
5. 将BeanDefinition注册到Registry

#### 为什么需要ASM，反射不可以吗？
不知道类名，无法直接通过反射获取类的注解。
#### 使用ASM，Annotation读取的实现(Visitor模式)：
1. ASM提供的接口：
    1. 将InputStream传入asm框架的ClassReader。
    2. 自己继承ClassVistor重写有需要的visitXXX()方法。
    3. 将ClassVisitor传入ClassReader的accept()方法。
2. 具体实现：
    1. AnnotationMetaDataVisitor继承ClassMetaDataVisitor，而ClassMetaDataVisitor继承ClassVisitor。
    2. AnnotationMetaDataVisitor和ClassMetaDataVisitor分别实现了AnnotationMetaData和ClassMetaData。
    
# AOP部分：
## Advice链式调用
ReflectiveMethodInvocation用List保存Interceptor，链式调用3种Advice：before、after-returning、after-throwing
## BeanFactory生成代理的过程
1. AspectJAutoProxyCreator获取BeanFactory中的Advice。
2. 使用Advice的MethodMatcher比较Bean的方法和PointCut。
3. 若存在候选的Advice，将Advice、需要被增强Bean和Bean实现的接口，保存到AOPConfig（包含生成代理的必须信息）。
4. 根据实现接口的数量，选用JDK动态代理或CGLIB实现代理，将AOPConfig传入构造器。

## CGLIB生成代理前的处理：
1. 设置目标类为enhancer的父类
2. 设置Callbacks的为包含MethodInterceptor的数组
3. 设置CallbackFilter(accept()根据Method参数决定使用的Callback在数组中的索引，暂时默认返回0，只采用唯一的方法拦截器)
3. enhancer.create()
### CGLIB生成代理如何调用代理方法：
1. 拦截器的方法签名：public Object intercept(Object obj, Method method, Object[] params,MethodProxy proxy)
2. 具体实现：intercept方法获取AOPConfig筛选出当前Method对应的Advice
若存在Advice，创建ReflectiveMethodInvocation，对Advice链式调用。

## JDK Proxy生成代理前的处理：
1. 3个参数：类加载器，从AOPConfig取出实现的接口，传入InvocationHandler
2. Proxy.newInstance()
### JDK Proxy生成代理如何调用代理方法：
1. InvocationHandler需要重写的方法签名：invoke(Object proxy, Method method, Object[] args)，
2. 获取AOPConfig筛选出当前Method对应的Advice若存在Advice，创建ReflectiveMethodInvocation，对Advice链式调用。

## AOP相关Bean的说明：
1. Advice合成Bean（通过Xml配置，人工合成）的属性：
    1. 引用的point cut的RuntimeBeanReference
    2. AspectFactoty，包含AspectName和BeanFactory
    3. MethodLocatingFactory包含AspectName和Advice内容方法名，是个FactoryBean可以获得增强内容Method
2. PointCut对象本身实现了MethodMatcher接口。
3. Advice是增强的内容，就要知道增强内容所在的方法，还要知道方法所在的切面。