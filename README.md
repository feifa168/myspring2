说明<br/>
* 模拟spring的方法监视，在函数执行前后调用指定的方法。
* @MyBefore和@MyAfter以及@MyAspect在监视对象使用，对应方法执行前后调用方法。
* MyRegister用于模拟spring的加载xml操作，只是这里没有使用xml，提供一个init方法达到相同作用。 handleBean用于构建bean以及要执行的方法，handleAspect用于构建@MyBefore和@MyAfter，具体执行是在handle中。init重载两个方法，bean传包名每次都构建新的对象，传bean的引用则使用的是外部引用，不创建对象。

## 模拟Spring getBean功能，可以配置xml
> Spring的ApplicationContext其实就是把xml文件解析出来，根据ben构造出一个个对象，放到map里
* MyApplicationContext可以读取xml配置文件，解析出的bean会构造对应的对象，放在map<id, object>里
* MyApplicationContext.getBean(String id)，从map里查找得到的bean返回

## 模拟jackson指定构造函数和参数
* @MyCreator 功能类似 @JackCreator
* @MyParam 功能类似 @JacksonParam

## 技术点
* 静态代理
* JDK动态代理
* CGLIB(Code Generation Library 代码生成库)
### 基本概念
```text
首先，我们知道Spring AOP的底层实现有两种方式：一种是JDK动态代理，另一种是CGLib的方式。
自Java 1.3以后，Java提供了动态代理技术，允许开发者在运行期创建接口的代理实例，后来这项技术被用到了Spring的很多地方。
JDK动态代理主要涉及java.lang.reflect包下边的两个类：Proxy和InvocationHandler。其中，InvocationHandler是一个接口，可以通过实现该接口定义横切逻辑，并通过反射机制调用目标类的代码，动态地将横切逻辑和业务逻辑贬值在一起。
JDK动态代理的话，他有一个限制，就是它只能为接口创建代理实例，而对于没有通过接口定义业务方法的类，如何创建动态代理实例哪？答案就是CGLib。
CGLib采用底层的字节码技术，全称是：Code Generation Library，CGLib可以为一个类创建一个子类，在子类中采用方法拦截的技术拦截所有父类方法的调用并顺势织入横切逻辑。
```

### JDK动态代理 和 CGLib动态代理区别
* Spring默认采取动态代理机制实现AOP，当动态代理不可用时（代理类无接口）会使用cglib机制
* 动态代理在Java中有着广泛的应用，比如Spring AOP，Hibernate数据查询、测试框架的后端mock、RPC，Java注解对象获取等。静态代理的代理关系在编译时就确定了，而动态代理的代理关系是在运行时确定的。静态代理实现简单，适合于代理类较少且确定的情况，而动态代理则给我们提供了更大的灵活性。今天我们来探讨Java中两种常见的动态代理方式：JDK原生动态代理和CGLIB动态代理。

#### JDK动态代理具体实现原理：
```text
通过实现InvocationHandlet接口创建自己的调用处理器；

通过为Proxy类指定ClassLoader对象和一组interface来创建动态代理；

通过反射机制获取动态代理类的构造函数，其唯一参数类型就是调用处理器接口类型；

通过构造函数创建动态代理类实例，构造时调用处理器对象作为参数参入；

JDK动态代理是面向接口的代理模式，如果被代理目标没有接口那么Spring也无能为力，Spring通过Java的反射机制生产被代理接口的新的匿名实现类，重写了其中AOP的增强方法。
```
#### CGLib动态代理：
```text
CGLib是一个强大、高性能的Code生产类库，可以实现运行期动态扩展java类，Spring在运行期间通过 CGlib继承要被动态代理的类，重写父类的方法，实现AOP面向切面编程呢。
```

#### 两者对比：
* JDK动态代理是面向接口的。
* CGLib动态代理是通过字节码底层继承要代理类来实现（如果被代理类被final关键字所修饰，那么抱歉会失败）。

#### 使用注意：
* 如果要被代理的对象是个实现类，那么Spring会使用JDK动态代理来完成操作（Spirng默认采用JDK动态代理实现机制）；
* 如果要被代理的对象不是个实现类那么，Spring会强制使用CGLib来实现动态代理。

#### 参考文档
* [Java Proxy 和 CGLIB 动态代理原理](http://www.importnew.com/27772.html)
* [Java静态代理&动态代理笔记](https://www.jianshu.com/p/e2917b0b9614)
* [AOP与JAVA动态代理](https://www.cnblogs.com/xiaoxiao7/p/6057724.html)
* [Spring AOP中的JDK和CGLib动态代理哪个效率更高？](https://blog.csdn.net/xlgen157387/article/details/82497594)

#### 代理模式性能比较
* [Cglib 与 JDK动态代理的运行性能比较](https://www.cnblogs.com/haiq/p/4304615.html)



