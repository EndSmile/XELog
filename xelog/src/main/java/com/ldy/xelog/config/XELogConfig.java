package com.ldy.xelog.config;

import android.content.Context;
import android.os.Environment;

import com.elvishew.xlog.LogConfiguration;
import com.ldy.xelog.XELog;
import com.ldy.xelog.XELogger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by ldy on 2017/3/3.
 */
public class XELogConfig {
    public static final String DEFAULT_TAG = "XELog";
    public static final List<String> DEFAULT_TAG_LIST = Collections.singletonList(DEFAULT_TAG);
    protected Context context;
    private final XELogger xeLogger;

    public XELogConfig() {
        XELog.assertInitialization();
        this.context = XELog.context;
        xeLogger = new XELogger(this);
    }

    public String getAuthor() {
        return null;
    }

    public long getTime() {
        return System.currentTimeMillis();
    }

    public String getRemarks(String message) {
        return null;
    }

    public String getSummary(String msg) {
        if (msg.length() > 10) {
            return msg.substring(0, 10);
        } else {
            return msg;
        }
    }

    public String getPackageName() {
        if (context != null) {
            return context.getPackageName();
        }
        return "";
    }

    public List<String> getBaseTag() {
        ArrayList<String> tags = new ArrayList<>();
        tags.add(DEFAULT_TAG);
        return tags;
    }

    public boolean withStackTrace() {
        return true;
    }

    public boolean withThread() {
        return false;
    }



    /**
     * {@link LogConfiguration#tag 字段设置无效,必须使用{@link #getBaseTag()}设置}
     *
     * @return
     */
    public LogConfiguration getXLogConfiguration() {
        return null;
    }

    public boolean isPrintConsole() {
        return XELog.isShowConsole();
    }

    public boolean isPrintJsonFile() {
        return true;
    }

    public String getFileDirPath() {
        return new File(Environment.getExternalStorageDirectory(), "xelog").getPath();
    }

    public void v(String message) {
        xeLogger.v(null,message);
    }

    public void e(Throwable throwable){
        xeLogger.e(null,throwable);
    }
}
