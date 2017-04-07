package com.ldy.xelog;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

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
        XLog.init();
        XELog.initParams = initParams;
        XELogCommon.xelogDirPath = getFileDir();
    }

    public static void activateAutoLog(IAutoLog... autoLogs) {
        if (autoLogs != null) {
            for (IAutoLog autoLog : autoLogs) {
                autoLog.activate();
            }
        }
    }

    public static String getFileDir() {
        if (dirPath!=null){
            return dirPath;
        }
        dirPath = initParams.dirPath;
        if (dirPath!=null){
            File file = new File(dirPath);
            if (!file.exists()) {
                if (file.mkdirs()) {
                    return dirPath;
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            File dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            if (dir!=null){
                File xelogFile = new File(dir, "xelog");
                if (xelogFile.exists()){
                    dirPath = xelogFile.getPath();
                    return dirPath;
                }else {
                    if (xelogFile.mkdirs()){
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
        boolean printJsonFile = true;
        String dirPath;

        public InitParams disPrintConsole() {
            printConsole = false;
            return this;
        }

        public InitParams disPrintJsonFile() {
            printJsonFile = false;
            return this;
        }

        public InitParams setDirPath(String dirPath) {
            this.dirPath = dirPath;
            return this;
        }
    }
}
