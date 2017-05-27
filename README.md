# XELog
目前所有的日志工具本质上使日志更方便的打印及更好的在控制台上展示，但是并没有拓展日志系统本身的功能，输入的信息量有限，导致其在应对数量庞大的日志时往往显得很无力。

`XELog`通过拓展日志需要配置的字段以完成更为复杂的筛选和更方便的查看，生成的日志会打印到数据库中，客户端通过依赖`XELog-read`的方式在客户端读取这些日志，通过使用`xxx`的方式在PC端读取日志

### 实现简介
默认会将需要打印的日志分别在控制台和数据库中各打印一份，在控制台中的打印依赖于[xlog](https://github.com/elvishew/xLog)实现，所以[xlog](https://github.com/elvishew/xLog)的实现全部可以正常使用。

通过继承[XELogConfig](https://github.com/EndSmile/XELog/blob/master/xelog/src/main/java/com/ldy/xelog/config/XELogConfig.java)并覆写其配置方法，新建对象，通过调用`v(String msg)`等即可完成打印。

### 使用
```
//日志打印库
compile 'com.ldy:xelog:0.5.2'
//日志读取库
compile 'com.ldy:xelog-read:0.5.2'
//使用aop的形式将{@link com.ldy.xelog.hugo.annotations.HugoXELog}注解加在类，方法上可实现日志打印，参考hugo
compile 'com.ldy:xelog-hugo:0.5.2'
//okhttp日志拦截器，使用xelog实现
compile 'com.ldy:xelog-okhttp-interceptor:0.5.2'
```

### todoList

 1. 根据不同level改变颜色（现已完成error颜色改变）
 2. 根据调用栈信息解析出调用者的模块信息
 3. 程序异常时打印更多的信息（包括修复现有异常捕捉bug）
 4. 时间筛选（时间筛选方式更新为选定开始和结束时间，而不是跳转到某个时间）
 5. 日志删除，提供多选，全选的方式删除日志
 6. 右下角增加辅助按钮
 7. ...

 



