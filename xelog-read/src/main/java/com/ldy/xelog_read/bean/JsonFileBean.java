package com.ldy.xelog_read.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by ldy on 2017/3/3.
 */

public class JsonFileBean implements Parcelable {

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

    public String getLogStr() {
        StringBuilder builder = new StringBuilder();
        List<String> tag = getTag();
        for (int i = 0, length = tag.size(); i < length; i++) {
            builder.append(tag.get(i));
            if (i < length - 1) {
                builder.append("_");
            }
        }
        return builder.toString();
    }

    public String getStackTraceStr(){
        if (stackTrace==null){
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String s:stackTrace){
            stringBuilder.append(s);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return "JsonFileBean{" +
                "time=" + time +
                ", packageName='" + packageName + '\'' +
                ", level='" + level + '\'' +
                ", thread='" + thread + '\'' +
                ", stackTrace=" + stackTrace +
                ", author='" + author + '\'' +
                ", remarks='" + remarks + '\'' +
                ", summary='" + summary + '\'' +
                ", content='" + content + '\'' +
                ", tag=" + tag +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.time);
        dest.writeString(this.packageName);
        dest.writeString(this.level);
        dest.writeString(this.thread);
        dest.writeStringList(this.stackTrace);
        dest.writeString(this.author);
        dest.writeString(this.remarks);
        dest.writeString(this.summary);
        dest.writeString(this.content);
        dest.writeStringList(this.tag);
    }

    public JsonFileBean() {
    }

    protected JsonFileBean(Parcel in) {
        this.time = in.readLong();
        this.packageName = in.readString();
        this.level = in.readString();
        this.thread = in.readString();
        this.stackTrace = in.createStringArrayList();
        this.author = in.readString();
        this.remarks = in.readString();
        this.summary = in.readString();
        this.content = in.readString();
        this.tag = in.createStringArrayList();
    }

    public static final Parcelable.Creator<JsonFileBean> CREATOR = new Parcelable.Creator<JsonFileBean>() {
        @Override
        public JsonFileBean createFromParcel(Parcel source) {
            return new JsonFileBean(source);
        }

        @Override
        public JsonFileBean[] newArray(int size) {
            return new JsonFileBean[size];
        }
    };
}
