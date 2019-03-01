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


