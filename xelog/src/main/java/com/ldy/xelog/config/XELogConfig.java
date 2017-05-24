package com.ldy.xelog.config;

import android.content.Context;

import com.elvishew.xlog.LogConfiguration;
import com.ldy.xelog.XELog;
import com.ldy.xelog.XELogger;

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
    protected final XELogger xeLogger;

    public XELogConfig() {
        XELog.assertInitialization();
        this.context = XELog.context;
        xeLogger = XELogger.getInstance();
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

    public String getExtra1() {
        return null;
    }

    public String getExtra2() {
        return null;
    }

    public boolean isPrintConsole() {
        return true;
    }

    public boolean isPrintJsonFile() {
        return true;
    }

    public void println(int level, String message) {
        println(level, null, message);
    }

    public void println(int level, List<String> plusTag, String message) {
        xeLogger.println(this, level, plusTag, message);
    }

    public void v(String message) {
        v(null, message);
    }

    public void v(List<String> plusTag, String message) {
        xeLogger.v(this, plusTag, message);
    }

    public void d(String message) {
        d(null, message);
    }

    public void d(List<String> plusTag, String message) {
        xeLogger.d(this, plusTag, message);
    }

    public void i(String message) {
        i(null, message);
    }

    public void i(List<String> plusTag, String message) {
        xeLogger.i(this, plusTag, message);
    }

    public void w(String message) {
        w(null, message);
    }

    public void w(List<String> plusTag, String message) {
        xeLogger.w(this, plusTag, message);
    }

    public void e(String message) {
        e(null, message);
    }

    public void e(List<String> plusTag, String message) {
        xeLogger.e(this, plusTag, message);
    }
}
