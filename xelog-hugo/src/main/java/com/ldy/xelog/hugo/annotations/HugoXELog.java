package com.ldy.xelog.hugo.annotations;

import android.util.Log;

import com.ldy.xelog.hugo.runtime.DefaultHugoLogHandler;
import com.ldy.xelog.hugo.runtime.IHugoLogHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by ldy on 2017/5/23.
 */
@Target({TYPE, METHOD, CONSTRUCTOR}) @Retention(RUNTIME)
public @interface HugoXELog {
    /**
     * @return log level
     */
    int value() default Log.VERBOSE;

    boolean withStackTrace() default false;

    boolean withThread() default true;

    boolean printConsole() default true;

    boolean printFile() default true;

    /**
     * @return 如果返回{},则tag为{$className,$methodName}</p>
     * 注意：所有tag会增加默认根tag{@link DefaultHugoLogHandler#DEFAULT_ROOT_TAG}
     */
    String[] tag() default {};

    /**
     * @return This class must have a empty constructor
     */
    Class<? extends IHugoLogHandler> handler() default DefaultHugoLogHandler.class;
}
