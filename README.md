实现了AOP的Spring IOC容器。
5000+行的源码，麻雀虽小，但五脏俱全。
开发过程使用TDD开发模式，提供了6个版本的测试用例。
想要了解这个容器，最好结合测试用例和github提交记录，一步步抽丝剥茧。

功能简介如下：
* 实现了构造器函数注入和Autowired注解式注入。
* Xml配置AOP和自动扫描指定包下Component。
* 通过JDK Proxy和CGLIB实现AOP增强。
