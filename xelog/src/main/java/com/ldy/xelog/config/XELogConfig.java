package com.ldy.xelog.config;

import android.content.Context;
import android.os.Environment;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.Logger;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.formatter.thread.DefaultThreadFormatter;
import com.elvishew.xlog.internal.util.StackTraceUtil;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.ldy.xelog.jsonFile.JsonFileFlattener;
import com.ldy.xelog.jsonFile.JsonFilePrinter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldy on 2017/3/3.
 */
public class XELogConfig {
    public static final String DEFAULT_TAG = "XELog";
    protected Context context;
    private StackTraceElement[] stackTrace;
    private String thread;
    private Logger androidLogger;
    private Logger jsonFileLogger;

    public XELogConfig(Context context) {
        this.context = context;
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

    public List<String> getTag() {
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

    //// TODO: 2017/3/6 增加map,保证异步线程读取时数据不错乱
    public StackTraceElement[] getStackTrace() {
        return stackTrace;
    }

    public String getThread() {
        return thread;
    }

    /**
     * {@link LogConfiguration#tag 字段设置无效,必须使用{@link #getTag()}设置}
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

    public String getFileDirPath() {
        return new File(Environment.getExternalStorageDirectory(), "xelog").getPath();
    }

    public void v(String message) {
        if (withStackTrace()) {
            stackTrace = StackTraceUtil.getCroppedRealStackTrack(new Throwable().getStackTrace(), 0);
        } else {
            stackTrace = null;
        }
        if (withThread()) {
            thread = new DefaultThreadFormatter().format(Thread.currentThread());
        } else {
            thread = null;
        }

        if (isPrintJsonFile()){
            if (jsonFileLogger == null) {
                JsonFilePrinter jsonFilePrinter = new JsonFilePrinter.Builder(getFileDirPath())
                        .logFlattener(new JsonFileFlattener(this))
                        .build();
                jsonFileLogger = XLog.tag(DEFAULT_TAG)
                        .printers(jsonFilePrinter).build();
            }
            jsonFileLogger.v(message);
        }

        if (isPrintConsole()){
            if (androidLogger == null) {
                androidLogger = buildLogger(getXLogConfiguration(), getLogTagStr(), new AndroidPrinter());
            }
            androidLogger.v(message);
        }
    }

    private Logger buildLogger(LogConfiguration logConfiguration, String tag, Printer... printer) {
        if (logConfiguration == null) {
            logConfiguration = new LogConfiguration.Builder().build();
        }

        Logger.Builder builder = new Logger.Builder();
        builder.logLevel(logConfiguration.logLevel)
                .tag(logConfiguration.tag)
                .jsonFormatter(logConfiguration.jsonFormatter)
                .xmlFormatter(logConfiguration.xmlFormatter)
                .throwableFormatter(logConfiguration.throwableFormatter)
                .threadFormatter(logConfiguration.threadFormatter)
                .stackTraceFormatter(logConfiguration.stackTraceFormatter)
                .borderFormatter(logConfiguration.borderFormatter);
        if (logConfiguration.withStackTrace) {
            builder.st(logConfiguration.stackTraceDepth);
        }
        if (logConfiguration.withThread) {
            builder.t();
        }
        if (printer != null) {
            builder.printers(printer);
        }
        if (tag == null) {
            tag = DEFAULT_TAG;
        }
        builder.tag(tag);
        return builder.build();
    }

    private String getLogTagStr() {
        StringBuilder builder = new StringBuilder();
        List<String> tag = getTag();
        for (int i = 0, length = tag.size(); i < length; i++) {
            builder.append(tag.get(i));
            if (i < length - 1) {
                builder.append("_");
            }
        }
        return builder.toString();
    }

}
