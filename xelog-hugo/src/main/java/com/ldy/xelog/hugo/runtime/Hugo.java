package com.ldy.xelog.hugo.runtime;



import com.ldy.xelog.hugo.annotations.HugoXELog;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.concurrent.TimeUnit;

/**
 * Created by ldy on 2017/5/23.
 */
@Aspect
public class Hugo {
    private static volatile boolean enabled = true;

    @Pointcut("within(@com.ldy.xelog.hugo.annotations.HugoXELog *)")
    public void withinAnnotatedClass() {
    }


    @Pointcut("execution(!synthetic * *(..)) && withinAnnotatedClass()")
    public void methodInsideAnnotatedType() {
    }

    @Pointcut("execution(!synthetic *.new(..)) && withinAnnotatedClass()")
    public void constructorInsideAnnotatedType() {
    }

    @Pointcut("execution(@com.ldy.xelog.hugo.annotations.HugoXELog * *(..)) || methodInsideAnnotatedType()")
    public void method() {
    }

    @Pointcut("execution(@com.ldy.xelog.hugo.annotations.HugoXELog *.new(..)) || constructorInsideAnnotatedType()")
    public void constructor() {
    }

    @Around("method() || constructor()")
    public Object logAndExecute(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        HugoXELog hugoXELog = null;
        if (signature instanceof MethodSignature) {
            Method method = ((MethodSignature) signature).getMethod();
            hugoXELog = method.getAnnotation(HugoXELog.class);
            if (hugoXELog == null) {
                hugoXELog = (HugoXELog) method.getDeclaringClass().getAnnotation(HugoXELog.class);
            }
        } else if (signature instanceof ConstructorSignature) {
            Constructor constructor = ((ConstructorSignature) signature).getConstructor();
            hugoXELog = (HugoXELog) constructor.getAnnotation(HugoXELog.class);
            if (hugoXELog == null) {
                hugoXELog = (HugoXELog) constructor.getDeclaringClass().getAnnotation(HugoXELog.class);
            }
        }
        if (hugoXELog == null) {
            throw new InvalidParameterException("error");
        }

        IHugoLogHandler hugoLogHandler;
        if (hugoXELog.handler().equals(DefaultHugoLogHandler.class)) {
            hugoLogHandler = new DefaultHugoLogHandler();
        } else {
            hugoLogHandler = hugoXELog.handler().newInstance();
        }

        enterMethod(hugoXELog, hugoLogHandler, joinPoint);

        long startNanos = System.nanoTime();
        Object result = joinPoint.proceed();
        long stopNanos = System.nanoTime();
        long lengthMillis = TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos);

        exitMethod(hugoXELog, hugoLogHandler, joinPoint, result, lengthMillis);

        return result;
    }

    private static void enterMethod(HugoXELog hugoXELog, IHugoLogHandler hugoLogHandler, JoinPoint joinPoint) {
        if (!enabled) return;
        hugoLogHandler.enterMethod(hugoXELog, joinPoint);
    }

    private static void exitMethod(HugoXELog hugoXELog, IHugoLogHandler hugoLogHandler, JoinPoint joinPoint, Object result, long lengthMillis) {
        if (!enabled) return;
        hugoLogHandler.exitMethod(hugoXELog, joinPoint, result, lengthMillis);

    }
}
