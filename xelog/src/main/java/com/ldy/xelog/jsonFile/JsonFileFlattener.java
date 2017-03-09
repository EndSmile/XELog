package com.ldy.xelog.jsonFile;

import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.flattener.Flattener;
import com.google.gson.Gson;
import com.ldy.xelog.config.XELogConfig;

import java.util.ArrayList;

/**
 * Created by ldy on 2017/3/3.
 */

public class JsonFileFlattener implements Flattener {


    private final Gson gson;
    private XELogConfig xeLogConfig;

    public JsonFileFlattener(XELogConfig xeLogConfig) {
        this.xeLogConfig = xeLogConfig;
        gson = new Gson();
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
        jsonFileBean.setTag(xeLogConfig.getTag());
        jsonFileBean.setAuthor(xeLogConfig.getAuthor());
        jsonFileBean.setPackageName(xeLogConfig.getPackageName());
        jsonFileBean.setRemarks(xeLogConfig.getRemarks(message));
        ArrayList<String> stackTraceStrList = null;
        StackTraceElement[] stackTrace = xeLogConfig.getStackTrace();
        if (stackTrace != null) {
            stackTraceStrList = new ArrayList<>();
            for (int i = 0, length = stackTrace.length; i < length; i++) {
                stackTraceStrList.add(stackTrace[i].toString());
            }
        }
        jsonFileBean.setStackTrace(stackTraceStrList);
        jsonFileBean.setThread(xeLogConfig.getThread());
        jsonFileBean.setSummary(xeLogConfig.getSummary(message));
        jsonFileBean.setContent(message);
        jsonFileBean.setTime(xeLogConfig.getTime());
        return gson.toJson(jsonFileBean);
    }
}
