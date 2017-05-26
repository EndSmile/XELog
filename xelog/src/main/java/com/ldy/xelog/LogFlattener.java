package com.ldy.xelog;

import com.elvishew.xlog.LogLevel;
import com.ldy.xelog.common.bean.LogBean;
import com.ldy.xelog.config.XELogConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldy on 2017/3/3.
 */
public class LogFlattener{

    private final XELogConfig xeLogConfig;
    private final StackTraceElement[] stackTrace;
    private final String thread;
    private final List<String> tag;

    public LogFlattener(XELogConfig xeLogConfig, StackTraceElement[] stackTrace, String thread, List<String> tag) {
        this.xeLogConfig = xeLogConfig;
        this.stackTrace = stackTrace;
        this.thread = thread;
        if (tag==null){
            this.tag = XELogConfig.DEFAULT_TAG_LIST;
        }else {
            this.tag = tag;
        }
    }

    /**
     * Flatten the log.
     *
     * @param logLevel the level of log
     * @param tag      the tag of log
     * @param message  the message of log
     * @return the formatted final log Charsequence
     */
    public LogBean flatten(int logLevel, String tag, String message) {

        LogBean logBean = new LogBean();
        logBean.setLevel(LogLevel.getLevelName(logLevel));
        logBean.setTag(this.tag);
        logBean.setAuthor(xeLogConfig.getAuthor());
        logBean.setPackageName(xeLogConfig.getPackageName());
        logBean.setRemarks(xeLogConfig.getRemarks(message));
        logBean.setThread(thread);
        logBean.setSummary(xeLogConfig.getSummary(message));
        logBean.setContent(message);
        logBean.setTime(xeLogConfig.getTime());
        logBean.setExtra1(xeLogConfig.getExtra1());
        logBean.setExtra2(xeLogConfig.getExtra2());
        if (!xeLogConfig.tagSelect()){
            logBean.setTagSelect(false);
        }

        ArrayList<String> stackTraceStrList = null;
        if (stackTrace != null) {
            stackTraceStrList = new ArrayList<>();
            for (StackTraceElement aStackTrace : stackTrace) {
                stackTraceStrList.add(aStackTrace.toString());
            }
        }
        logBean.setStackTrace(stackTraceStrList);
        return logBean;
    }
}
