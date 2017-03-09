# XELog
目前所有的日志工具本质上使日志更方便的打印及更好的在控制台上展示，但是并没有拓展日志系统本身的功能，输入的信息量有限，导致其在应对数量庞大的日志时往往显得很无力。

`XELog`通过拓展日志需要配置的字段以完成更为复杂的筛选和更方便的查看，生成的日志会以`json`格式打印到文件中，客户端通过依赖`XELog-read`的方式在客户端读取这些日志，通过使用`xxx`的方式在PC端读取日志

### 实现简介
默认会将需要打印的日志分别在控制台和`json`中各打印一份，在控制台中的打印依赖于[xlog](https://github.com/elvishew/xLog)实现，所以[xlog](https://github.com/elvishew/xLog)的实现全部可以正常使用。

通过继承[XELogConfig](https://github.com/EndSmile/XELog/blob/master/xelog/src/main/java/com/ldy/xelog/config/XELogConfig.java)并覆写其配置方法，新建对象，通过调用`v(String msg)`等即可完成打印。
