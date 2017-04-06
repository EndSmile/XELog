package com.ldy.xelog.config;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.elvishew.xlog.LogConfiguration;
import com.ldy.xelog.XELog;
import com.ldy.xelog.XELogger;

import java.io.File;
import java.util.ArrayList;
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

    /**
     * @return the author of the log
     */
    public String getAuthor() {
        return null;
    }

    /**
     * @return the time of the log
     */
    public long getTime() {
        return System.currentTimeMillis();
    }

    /**
     * @param message the message of the log
     * @return the remarks of the log
     */
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
//        if (context != null) {
//            return context.getPackageName();
//        }
        return null;
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
        return true;
    }

    public boolean tagSelect() {
        return true;
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
        return true;
    }

    public boolean isPrintJsonFile() {
        return true;
    }

    public void v(String message) {
        xeLogger.v(null, message);
    }

    public void e(String string) {
        xeLogger.e(null,string);
    }
}
