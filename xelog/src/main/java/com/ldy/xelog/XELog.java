package com.ldy.xelog;

import android.content.Context;

import com.ldy.xelog.config.auto.IAutoLog;

import java.util.List;

/**
 * Created by ldy on 2017/3/13.
 */

public class XELog {
    public static Context context;
    static InitParams initParams;

    public static void init(Context context) {
        init(context, null);
    }

    public static void init(Context context, InitParams initParams) {
        if (context == null) {
            throw new IllegalStateException("Context can't be null");
        }
        if (XELog.context != null) {
            throw new IllegalStateException("XLog is already initialized, do not initialize again");
        }
        XELog.context = context.getApplicationContext();
        if (initParams == null) {
            initParams = new InitParams();
        }
        XELog.initParams = initParams;
        activateAutoLog(initParams.autoLogs);
    }

    public static void activateAutoLog(IAutoLog... autoLogs) {
        if (autoLogs != null) {
            for (IAutoLog autoLog : autoLogs) {
                autoLog.activate();
            }
        }
    }


    public static void assertInitialization() {
        if (context == null) {
            throw new IllegalStateException("Do you forget to initialize XELog?");
        }
    }

    public static class InitParams {
        boolean printConsole = true;
        boolean printJsonFile = true;
        IAutoLog[] autoLogs;

        public InitParams disPrintConsole() {
            printConsole = false;
            return this;
        }

        public InitParams disPrintJsonFile() {
            printJsonFile = false;
            return this;
        }

        public InitParams activateAutoLog(IAutoLog... autoLogs) {
            autoLogs = autoLogs;
            return this;
        }
    }
}
