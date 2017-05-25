package com.ldy.xelog.hugo.runtime;

import android.os.Build;
import android.os.Looper;
import android.os.Trace;

import com.ldy.xelog.config.XELogConfig;
import com.ldy.xelog.hugo.annotations.HugoXELog;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ldy on 2017/5/23.
 */

public class DefaultHugoLogHandler extends XELogConfig implements IHugoLogHandler {

    public static final String EXIT_METHOD_STR_START = "\u21E0 ";
    public static final String ENTER_METHOD_STR_START = "\u21E2 ";
    public static final String DEFAULT_ROOT_TAG = "hugo";
    private HugoXELog hugoXELog;

    @Override
    public List<String> getBaseTag() {
        ArrayList<String> list = new ArrayList<>();
        list.add(DEFAULT_ROOT_TAG);
        return list;
    }

    @Override
    public boolean withStackTrace() {
        return hugoXELog.withStackTrace();
    }

    @Override
    public boolean withThread() {
        return hugoXELog.withThread();
    }

    @Override
    public boolean isPrintConsole() {
        return hugoXELog.printConsole();
    }

    @Override
    public boolean isPrintFile() {
        return hugoXELog.printFile();
    }

    @Override
    public String getSummary(String msg) {
        if (msg.startsWith(ENTER_METHOD_STR_START)) {
            int index = msg.indexOf("(");
            if (index != -1){
                return msg.substring(0, index);
            }
        } else {
            int index = msg.indexOf("[");
            if (index != -1){
                return msg.substring(0, index);
            }
        }
        return super.getSummary(msg);
    }

    @Override
    public void enterMethod(HugoXELog hugoXELog, JoinPoint joinPoint) {
        this.hugoXELog = hugoXELog;

        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();

        Class<?> cls = codeSignature.getDeclaringType();
        String methodName = codeSignature.getName();
        String[] parameterNames = codeSignature.getParameterNames();
        Object[] parameterValues = joinPoint.getArgs();

        StringBuilder builder = new StringBuilder(ENTER_METHOD_STR_START);
        builder.append(methodName).append('(');
        for (int i = 0; i < parameterValues.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(parameterNames[i]).append('=');
            builder.append(Strings.toString(parameterValues[i]));
        }
        builder.append(')');

        if (Looper.myLooper() != Looper.getMainLooper()) {
            builder.append(" [Thread:\"").append(Thread.currentThread().getName()).append("\"]");
        }

        println(hugoXELog, cls, methodName, builder);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final String section = builder.toString().substring(2);
            Trace.beginSection(section);
        }
    }

    @Override
    public void exitMethod(HugoXELog hugoXELog, JoinPoint joinPoint, Object result, long lengthMillis) {
        this.hugoXELog = hugoXELog;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Trace.endSection();
        }

        Signature signature = joinPoint.getSignature();

        Class<?> cls = signature.getDeclaringType();
        String methodName = signature.getName();
        boolean hasReturnType = signature instanceof MethodSignature
                && ((MethodSignature) signature).getReturnType() != void.class;

        StringBuilder builder = new StringBuilder(EXIT_METHOD_STR_START)
                .append(methodName)
                .append(" [")
                .append(lengthMillis)
                .append("ms]");

        if (hasReturnType) {
            builder.append(" = ");
            builder.append(Strings.toString(result));
        }

        println(hugoXELog, cls, methodName, builder);
    }

    private void println(HugoXELog hugoXELog, Class<?> cls, String methodName, StringBuilder builder) {
        List<String> plusTag;
        if (hugoXELog.tag().length == 0) {
            plusTag = Arrays.asList(asTag(cls), methodName);
        } else {
            plusTag = Arrays.asList(hugoXELog.tag());
        }
        println(hugoXELog.value(), plusTag, builder.toString());
    }

    private static String asTag(Class<?> cls) {
        if (cls.isAnonymousClass()) {
            return asTag(cls.getEnclosingClass());
        }
        return cls.getSimpleName();
    }
}
