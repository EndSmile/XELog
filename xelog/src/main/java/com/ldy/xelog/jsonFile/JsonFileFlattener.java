package com.ldy.xelog.jsonFile;

import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.flattener.Flattener;
import com.google.gson.Gson;
import com.ldy.xelog.config.XELogConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldy on 2017/3/3.
 */
public class JsonFileFlattener implements Flattener {

    private static Gson gson = new Gson();
    private final XELogConfig xeLogConfig;
    private final StackTraceElement[] stackTrace;
    private final String thread;
    private final List<String> tag;

    public JsonFileFlattener(XELogConfig xeLogConfig, StackTraceElement[] stackTrace, String thread, List<String> tag) {
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
    @Override
    public CharSequence flatten(int logLevel, String tag, String message) {

        JsonFileBean jsonFileBean = new JsonFileBean();
        jsonFileBean.setLevel(LogLevel.getLevelName(logLevel));
        jsonFileBean.setTag(this.tag);
        jsonFileBean.setTag(xeLogConfig.getBaseTag());
        jsonFileBean.setAuthor(xeLogConfig.getAuthor());
        jsonFileBean.setPackageName(xeLogConfig.getPackageName());
        jsonFileBean.setRemarks(xeLogConfig.getRemarks(message));
        jsonFileBean.setThread(thread);
        jsonFileBean.setSummary(xeLogConfig.getSummary(message));
        jsonFileBean.setContent(message);
        jsonFileBean.setTime(xeLogConfig.getTime());
        if (!xeLogConfig.tagSelect()){
            jsonFileBean.setTagSelect(false);
        }

        ArrayList<String> stackTraceStrList = null;
        if (stackTrace != null) {
            stackTraceStrList = new ArrayList<>();
            for (StackTraceElement aStackTrace : stackTrace) {
                stackTraceStrList.add(aStackTrace.toString());
            }
        }
        jsonFileBean.setStackTrace(stackTraceStrList);
        return gson.toJson(jsonFileBean);
    }
}
