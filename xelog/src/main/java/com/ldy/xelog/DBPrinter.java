package com.ldy.xelog;


import com.elvishew.xlog.printer.Printer;
import com.ldy.xelog.common.bean.LogBean;
import com.ldy.xelog.common.db.LogDao;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ldy on 2017/4/10.
 */

public class DBPrinter implements Printer{

    private final LogDao logDao;
    private volatile Worker worker;
    private LogFlattener logFlattener;

    public static DBPrinter instance() {
        return Instance.logDao;
    }

    private DBPrinter() {
        logDao = LogDao.instance();
        worker = new Worker();
    }

    @Override
    public void println(int logLevel, String tag, String msg) {
        if (logFlattener==null){
            return;
        }

        if (!worker.isStarted()) {
            worker.start();
        }
        worker.enqueue(logFlattener.flatten(logLevel, tag, msg));
    }

    private static class Instance {
        private static final DBPrinter logDao = new DBPrinter();
    }

    public void setInfo(LogFlattener logFlattener) {
        this.logFlattener = logFlattener;
    }



    /**
     * Work in background, we can enqueue the logs, and the worker will dispatch them.
     */
    private class Worker implements Runnable {

        private BlockingQueue<LogBean> logs = new LinkedBlockingQueue<>();

        private volatile boolean started;

        /**
         * Enqueue the log.
         *
         * @param log the log to be written to file
         */
        void enqueue(LogBean log) {
            try {
                logs.put(log);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * Whether the worker is started.
         *
         * @return true if started, false otherwise
         */
        boolean isStarted() {
            synchronized (this) {
                return started;
            }
        }

        /**
         * Start the worker.
         */
        void start() {
            synchronized (this) {
                new Thread(this).start();
                started = true;
            }
        }

        @Override
        public void run() {
            LogBean log;
            try {
                while ((log = logs.take()) != null) {
                    logDao.addData(log);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                synchronized (this) {
                    started = false;
                }
            }
        }
    }
}
