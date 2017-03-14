package com.ldy.xelog.jsonFile;

import java.util.List;

/**
 * Created by ldy on 2017/3/3.
 */

public class JsonFileBean {

    /**
     * time : 121231231
     * package : com.xdja.logger
     * level : verbose
     * tag : ["net","login"]
     * thread : thread-1
     * stackTrace :
     * author : ldy@xdja.com
     * remarks : 这是一个登录的网络操作
     * summary : method:login
     * content :
     * tagSelect:如果为null则默认为选中
     */
    private long time;
    private String packageName;
    private String level;
    private String thread;
    private List<String> stackTrace;
    private String author;
    private String remarks;
    private String summary;
    private String content;
    private List<String> tag;
    private Boolean tagSelect;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageX) {
        this.packageName = packageX;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    public List<String> getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(List<String> stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public Boolean isTagSelect() {
        return tagSelect;
    }

    public void setTagSelect(Boolean tagSelect) {
        this.tagSelect = tagSelect;
    }
}
