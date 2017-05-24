package com.ldy.xelog.hugo.runtime;

import com.ldy.xelog.hugo.annotations.HugoXELog;

import org.aspectj.lang.JoinPoint;

/**
 * Created by ldy on 2017/5/23.
 */

public interface IHugoLogHandler {
    void enterMethod(HugoXELog hugoXELog, JoinPoint joinPoint);
    void exitMethod(HugoXELog hugoXELog, JoinPoint joinPoint, Object result, long lengthMillis);
}
