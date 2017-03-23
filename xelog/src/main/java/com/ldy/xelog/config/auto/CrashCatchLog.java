package com.ldy.xelog.config.auto;

/**
 * Created by ldy on 2017/3/14.
 */

public class CrashCatchLog extends AbstractAutoLog implements Thread.UncaughtExceptionHandler {

    private final Thread.UncaughtExceptionHandler handler;
    private Thread.UncaughtExceptionHandler defaultHandler;
    private boolean isActive;

    @Override
    public boolean withStackTrace() {
        return true;
    }

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
            String[] fullClass = strings[0].split(".");
            return fullClass[fullClass.length - 1];
        }catch (Exception ignored){
        }
        return super.getSummary(msg);
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
        e(e);
        if (handler == null) {
            if (defaultHandler != null)
                defaultHandler.uncaughtException(t, e);
        } else {
            handler.uncaughtException(t, e);
        }
    }

}
