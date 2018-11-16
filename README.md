说明<br/>
* 模拟spring的方法监视，在函数执行前后调用指定的方法。<br\>
* @MyBefore和@MyAfter一级@MyAspect在监视对象使用，对应方法执行前后调用方法。
* MyRegister用于模拟spring的加载xml操作，只是这里没有使用xml，提供一个init方法达到相同作用。 handleBean用于构建bean以及要执行的方法，handleAspect用于构建@MyBefore和@MyAfter，具体执行是在handle中。init重载两个方法，bean传包名每次都构建新的对象，传bean的引用则只有第一次调用时创建对象，后面使用第一次创建的对象。


