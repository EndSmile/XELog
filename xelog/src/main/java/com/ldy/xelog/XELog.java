package com.ldy.xelog;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.elvishew.xlog.XLog;
import com.ldy.xelog.common.XELogCommon;
import com.ldy.xelog.config.auto.IAutoLog;

import java.io.File;

/**
 * Created by ldy on 2017/3/13.
 */
public class XELog {
    public static Context context;
    static InitParams initParams;
    private static String dirPath;

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
        if (!initParams.printConsole) {
            //这样直接使用XLog就不会显示任何log了
            XLog.init(Log.ERROR + 3);
        }else {
            XLog.init();
        }

        XELogCommon.dirPath = getFileDir();
        XELogCommon.context = context;
    }

    public static void activateAutoLog(IAutoLog... autoLogs) {
        if (autoLogs != null) {
            for (IAutoLog autoLog : autoLogs) {
                autoLog.activate();
            }
        }
    }

    public static String getFileDir() {
        if (dirPath != null) {
            return dirPath;
        }
        dirPath = initParams.dirPath;
        if (dirPath != null) {
            File file = new File(dirPath);
            if (!file.exists()) {
                if (file.mkdirs()) {
                    return dirPath;
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            File dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            if (dir != null) {
                File xelogFile = new File(dir, "xelog");
                if (xelogFile.exists()) {
                    dirPath = xelogFile.getPath();
                    return dirPath;
                } else {
                    if (xelogFile.mkdirs()) {
                        dirPath = xelogFile.getPath();
                        return dirPath;
                    }
                }
            }
        }
        dirPath = new File(Environment.getExternalStorageDirectory(), "xelog").getPath();
        return dirPath;
    }

    public static void assertInitialization() {
        if (context == null) {
            throw new IllegalStateException("Do you forget to initialize XELog?");
        }
    }


    public static class InitParams {
        boolean printConsole = true;
        boolean printFile = true;
        String dirPath;

        public InitParams disPrintConsole() {
            printConsole = false;
            return this;
        }

        public InitParams disPrintFile(){
            printFile = false;
            return this;
        }

        /**
         * Use {@link #disPrintFile()} instead
         */
        @Deprecated
        public InitParams disPrintJsonFile() {
            return disPrintFile();
        }

        public InitParams setDirPath(String dirPath) {
            this.dirPath = dirPath;
            return this;
        }
    }
}
