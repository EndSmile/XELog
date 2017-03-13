package com.ldy.xelog;

import android.util.Log;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.Logger;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.formatter.thread.DefaultThreadFormatter;
import com.elvishew.xlog.internal.util.StackTraceUtil;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.ldy.xelog.config.XELogConfig;
import com.ldy.xelog.jsonFile.JsonFileFlattener;
import com.ldy.xelog.jsonFile.JsonFilePrinter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldy on 2017/3/10.
 */
public class XELogger {
    private XELogConfig config;

    private Logger androidLogger;
    private Logger jsonFileLogger;
    private JsonFilePrinter jsonFilePrinter;

    public XELogger(XELogConfig config) {
        this.config = config;
    }

    public void v(List<String> plusTag, String message) {
        println(Log.VERBOSE,plusTag,message);
    }

    public void e(List<String> plusTag, Throwable throwable){
        println(Log.ERROR,plusTag,throwable);
    }

    private void println(int level, List<String> plusTag, Object message) {
        StackTraceElement[] stackTrace = null;
        String thread = null;
        if (config.withStackTrace()) {
            stackTrace = StackTraceUtil.getCroppedRealStackTrack(new Throwable().getStackTrace(), 0);
        }
        if (config.withThread()) {
            thread = new DefaultThreadFormatter().format(Thread.currentThread());
        }

        if (config.isPrintJsonFile()) {
            if (jsonFileLogger == null) {
                jsonFilePrinter = new JsonFilePrinter.Builder(config.getFileDirPath())
                        .logFlattener(new JsonFileFlattener(config, stackTrace, thread, getLogTag(plusTag)))
                        .build();
                jsonFileLogger = XLog.tag("")
                        .printers(jsonFilePrinter).build();
            } else {
                jsonFilePrinter.setFlattener(new JsonFileFlattener(config, stackTrace, thread, getLogTag(plusTag)));
            }
            println(level, jsonFileLogger, message);
        }

        if (config.isPrintConsole()) {
            if (androidLogger == null) {
                androidLogger = buildAndroidLogger(config.getXLogConfiguration(), getLogTagStr(plusTag), new AndroidPrinter());
            }
            println(level, androidLogger, message);
        }
    }

    private void println(int level, Logger logger, Object msg) {
        switch (level) {
            case Log.VERBOSE:
                logger.v(msg);
                break;
            case Log.DEBUG:
                logger.d(msg);
                break;
            case Log.INFO:
                logger.i(msg);
                break;
            case Log.WARN:
                logger.w(msg);
                break;
            case Log.ERROR:
                logger.e(msg);
                break;
        }
    }

    private Logger buildAndroidLogger(LogConfiguration logConfiguration, String tag, Printer... printer) {

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
            tag = XELogConfig.DEFAULT_TAG;
        }
        builder.tag(tag);
        return builder.build();
    }

    private String getLogTagStr(List<String> plusTag) {
        List<String> fullTag = getLogTag(plusTag);
        StringBuilder builder = new StringBuilder();
        for (int i = 0, length = fullTag.size(); i < length; i++) {
            builder.append(fullTag.get(i));
            if (i < length - 1) {
                builder.append("_");
            }
        }
        return builder.toString();
    }

    private List<String> getLogTag(List<String> plusTag){
        List<String> fullTag;
        if (plusTag != null) {
            fullTag = new ArrayList<>(config.getBaseTag());
            fullTag.addAll(plusTag);
        }else {
            fullTag = config.getBaseTag();
        }
        return fullTag;
    }
}
