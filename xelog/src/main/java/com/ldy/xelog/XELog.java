package com.ldy.xelog;

import android.content.Context;

import com.ldy.xelog.config.auto.IAutoLog;

/**
 * Created by ldy on 2017/3/13.
 */

public class XELog {
    private static boolean defaultShowConsole = true;
    public static Context context;

    public static void init(Context context) {
        init(context, defaultShowConsole);
    }

    public static void init(Context context, boolean defaultShowConsole) {
        if (context==null){
            throw new IllegalStateException("Context can't be null");
        }
        if (XELog.context != null) {
            throw new IllegalStateException("XLog is already initialized, do not initialize again");
        }
        XELog.context = context.getApplicationContext();
        XELog.defaultShowConsole = defaultShowConsole;
    }

    public static void activeAutoLog(IAutoLog... autoLogs){
        if (autoLogs!=null){
            for (IAutoLog autoLog:autoLogs){
                autoLog.active();
            }
        }
    }

    public static boolean isShowConsole() {
        return defaultShowConsole;
    }

    public static void assertInitialization() {
        if (context == null) {
            throw new IllegalStateException("Do you forget to initialize XELog?");
        }
    }
}
