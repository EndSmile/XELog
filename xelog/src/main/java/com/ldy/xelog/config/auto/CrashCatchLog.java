package com.ldy.xelog.config.auto;

import com.elvishew.xlog.LogConfiguration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ldy on 2017/3/14.
 */
public class CrashCatchLog extends AbstractAutoLog implements Thread.UncaughtExceptionHandler {

    private final Thread.UncaughtExceptionHandler handler;
    private Thread.UncaughtExceptionHandler defaultHandler;
    private boolean isActive;
    private LogConfiguration logConfiguration;

    public CrashCatchLog(String author) {
        this(author, null);
    }

    public CrashCatchLog(String author, Thread.UncaughtExceptionHandler handler) {
        super(author);
        this.handler = handler;
    }

    @Override
    public boolean isPrintConsole() {
        return true;
    }

    @Override
    public String getSummary(String msg) {
        Pattern pattern = Pattern.compile("[^\\t].*Exception");
        Matcher matcher = pattern.matcher(msg);
        LinkedList<String> list = new LinkedList<>();
        while (matcher.find()) {
            list.addFirst(matcher.group());
        }
        System.out.println(list.toString());
        for (String s : list) {
            try {
                String[] split = s.split("\\.");
                return split[split.length - 1];
            } catch (Exception ignore) {
            }
        }
        return super.getSummary(msg);
    }

    @Override
    public LogConfiguration getXLogConfiguration() {
        if (logConfiguration != null) {
            logConfiguration = new LogConfiguration.Builder().build();
        }
        return logConfiguration;
    }

    @Override
    public boolean tagSelect() {
        return true;
    }

    @Override
    public void activate() {
        if (isActive) {
            return;
        }
        isActive = true;
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);

    }

    @Override
    public void stop() {
        if (!isActive) {
            return;
        }
        isActive = false;
        Thread.setDefaultUncaughtExceptionHandler(defaultHandler);
    }

    @Override
    public boolean withThread() {
        return true;
    }

    public Thread.UncaughtExceptionHandler getDefaultHandler() {
        return defaultHandler;
    }

    /**
     * Method invoked when the given thread terminates due to the
     * given uncaught exception.
     * <p>Any exception thrown by this method will be ignored by the
     * Java Virtual Machine.
     *
     * @param t the thread
     * @param e the exception
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        String result = null;
        if (e != null) {
            Writer writer = new StringWriter();
            PrintWriter pw = new PrintWriter(writer);
            Throwable cause = e.getCause();
            while (cause != null) {
                cause.printStackTrace(pw);
                cause = cause.getCause();
            }
            pw.close();
            result = writer.toString();
        }
        e(result);
        if (handler == null) {
            if (defaultHandler != null)
                defaultHandler.uncaughtException(t, e);
        } else {
            handler.uncaughtException(t, e);
        }
    }

    @Override
    public List<String> getBaseTag() {
        List<String> tags = super.getBaseTag();
        tags.add("crash");
        return tags;
    }
}
