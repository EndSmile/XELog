package com.ldy.xelog.common.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ldy on 2017/3/3.
 */

public class LogBean implements Parcelable {
    public static final String TAG_SEPARATOR = "_";
    public static final String STACK_TRACE_SEPARATOR = "\n";
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
    private String stackTrace;
    private String author;
    private String remarks;
    private String summary;
    private String content;
    private String tag;
    private Boolean tagSelect;

    private String extra1;
    private String extra2;

    /**
     * 数据库自动生成，无需手动设置
     */
    private long id;

    public String getTag() {
        return tag;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public String getExtra1() {
        return extra1;
    }

    public void setExtra1(String extra1) {
        this.extra1 = extra1;
    }

    public String getExtra2() {
        return extra2;
    }

    public void setExtra2(String extra2) {
        this.extra2 = extra2;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getPackageName() {
        if (packageName == null) {
            return "";
        }
        return packageName;
    }

    public void setPackageName(String packageX) {
        this.packageName = packageX;
    }

    public String getLevel() {
        if (level == null) {
            return "";
        }
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getThread() {
        if (thread == null) {
            return "";
        }
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    public List<String> getStackTraceList() {
        return getListByString(stackTrace,STACK_TRACE_SEPARATOR);
    }

    public void setStackTrace(List<String> stackTrace) {
        this.stackTrace = getStrByList(stackTrace,TAG_SEPARATOR);
    }

    public String getAuthor() {
        if (author == null) {
            return "";
        }
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

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getTagList() {
        return getListByString(tag,TAG_SEPARATOR);
    }

    public static List<String> getListByString(String s,String separator) {
        if (TextUtils.isEmpty(s)) {
            return new ArrayList<>();
        }
        try {
            String[] split = s.split(separator);
            return Arrays.asList(split);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void setTag(List<String> tag) {
        this.tag = getStrByList(tag,TAG_SEPARATOR);
    }

    private String getStrByList(List<String> list,String separator) {
        if (list==null){
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0, length = list.size(); i < length; i++) {
            builder.append(list.get(i));
            if (i < length - 1) {
                builder.append(separator);
            }
        }
        return builder.toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * 从数据库写入时并没有更新这个字段，所以读取时上层不应该从这里获取
     */
    public boolean isTagSelect() {
        if (tagSelect == null) {
            return true;
        }
        return tagSelect;
    }

    public void setTagSelect(Boolean tagSelect) {
        this.tagSelect = tagSelect;
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
        dest.writeString(this.stackTrace);
        dest.writeString(this.author);
        dest.writeString(this.remarks);
        dest.writeString(this.summary);
        dest.writeString(this.content);
        dest.writeString(this.tag);
        dest.writeValue(this.tagSelect);
        dest.writeString(this.extra1);
        dest.writeString(this.extra2);
        dest.writeLong(this.id);
    }

    public LogBean() {
    }

    protected LogBean(Parcel in) {
        this.time = in.readLong();
        this.packageName = in.readString();
        this.level = in.readString();
        this.thread = in.readString();
        this.stackTrace = in.readString();
        this.author = in.readString();
        this.remarks = in.readString();
        this.summary = in.readString();
        this.content = in.readString();
        this.tag = in.readString();
        this.tagSelect = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.extra1 = in.readString();
        this.extra2 = in.readString();
        this.id = in.readLong();
    }

    public static final Creator<LogBean> CREATOR = new Creator<LogBean>() {
        @Override
        public LogBean createFromParcel(Parcel source) {
            return new LogBean(source);
        }

        @Override
        public LogBean[] newArray(int size) {
            return new LogBean[size];
        }
    };
}
