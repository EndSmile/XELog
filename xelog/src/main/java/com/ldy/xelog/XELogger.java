package com.ldy.xelog;

import android.util.Log;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.Logger;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.formatter.thread.DefaultThreadFormatter;
import com.elvishew.xlog.internal.util.StackTraceUtil;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.ldy.xelog.config.XELogConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldy on 2017/3/10.
 */
public class XELogger {

    private static XELogger instance = new XELogger();

    private DBPrinter dbPrinter;
    private Logger dbLogger;

    private XELogger() {
        dbPrinter = DBPrinter.instance();
    }

    public static XELogger getInstance() {
        return instance;
    }

    public void v(XELogConfig config, List<String> plusTag, String message) {
        println(config, Log.VERBOSE, plusTag, message);
    }

    public void d(XELogConfig config, List<String> plusTag, String message) {
        println(config, Log.DEBUG, plusTag, message);
    }

    public void i(XELogConfig config, List<String> plusTag, String message) {
        println(config, Log.INFO, plusTag, message);
    }

    public void w(XELogConfig config, List<String> plusTag, String message) {
        println(config, Log.WARN, plusTag, message);
    }

    public void e(XELogConfig config, List<String> plusTag, String message) {
        println(config, Log.ERROR, plusTag, message);
    }

    public void println(XELogConfig config, int level, List<String> plusTag, Object message) {
        if (isIntercept(config)) {
            return;
        }

        StackTraceElement[] stackTrace = null;
        String thread = null;
        if (config.withStackTrace()) {
            stackTrace = StackTraceUtil.getCroppedRealStackTrack(new Throwable().getStackTrace(), 0);
        }
        if (config.withThread()) {
            thread = new DefaultThreadFormatter().format(Thread.currentThread());
        }

        if (config.isPrintFile() && XELog.initParams.printFile) {
//            if (jsonFileLogger == null) {
//                jsonFilePrinter = new JsonFilePrinter.Builder(XELog.getFileDir())
//                        .logFlattener(new JsonFileFlattener(config, stackTrace, thread, getLogTag(plusTag)))
//                        .build();
//                jsonFileLogger = XLog.tag("")
//                        .printers(jsonFilePrinter).build();
//            } else {
//                jsonFilePrinter.setFlattener(new JsonFileFlattener(config, stackTrace, thread, getLogTag(plusTag)));
//            }
//            println(level, jsonFileLogger, message);

            dbPrinter.setInfo(new LogFlattener(config, stackTrace, thread, getLogTag(plusTag, config)));
            if (dbLogger == null) {
                dbLogger = XLog.tag("")
                        .printers(dbPrinter).build();
            }
            println(level, dbLogger, message);
        }

        if (config.isPrintConsole() && XELog.initParams.printConsole) {

            Logger androidLogger = buildAndroidLogger(config.getXLogConfiguration(), getLogTagStr(plusTag, config));
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

    private Logger.Builder builder;

    private Logger buildAndroidLogger(LogConfiguration logConfiguration, String tag) {
        if (builder == null) {
            if (logConfiguration == null) {
                logConfiguration = new LogConfiguration.Builder().build();
            }

            builder = new Logger.Builder();
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
            builder.printers(new AndroidPrinter());
            if (tag == null) {
                tag = XELogConfig.DEFAULT_TAG;
            }
        }
        builder.tag(tag);
        return builder.build();
    }

    private String getLogTagStr(List<String> plusTag, XELogConfig config) {
        List<String> fullTag = getLogTag(plusTag, config);
        StringBuilder builder = new StringBuilder();
        for (int i = 0, length = fullTag.size(); i < length; i++) {
            builder.append(fullTag.get(i));
            if (i < length - 1) {
                builder.append("_");
            }
        }
        return builder.toString();
    }

    private List<String> getLogTag(List<String> plusTag, XELogConfig config) {
        List<String> fullTag;
        if (plusTag != null) {
            fullTag = new ArrayList<>(config.getBaseTag());
            fullTag.addAll(plusTag);
        } else {
            fullTag = config.getBaseTag();
        }
        return fullTag;
    }

    private boolean isIntercept(XELogConfig config) {
        return !(config.isPrintConsole() && XELog.initParams.printConsole
                || config.isPrintFile() && XELog.initParams.printFile);
    }

}
