package com.ldy.xelog.config.auto;

import com.elvishew.xlog.LogConfiguration;

import java.util.List;

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
    public String getSummary(String msg) {
        try {
            String[] strings = msg.split(":");
            String[] fullClass = strings[0].split("\\.");
            return fullClass[fullClass.length - 1];
        }catch (Exception ignored){
        }
        return super.getSummary(msg);
    }

    @Override
    public LogConfiguration getXLogConfiguration() {
        if (logConfiguration!=null){
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
        e(getXLogConfiguration().throwableFormatter.format(e));
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
