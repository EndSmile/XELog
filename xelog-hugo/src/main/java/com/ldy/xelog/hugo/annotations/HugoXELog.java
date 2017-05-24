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

    /**
     * @return This class must have a empty constructor
     */
    Class<? extends IHugoLogHandler> handler() default DefaultHugoLogHandler.class;
}
