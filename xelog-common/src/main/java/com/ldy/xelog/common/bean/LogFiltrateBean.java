package com.ldy.xelog.common.bean;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Set;

/**
 * Created by ldy on 2017/4/11.
 */

public class LogFiltrateBean {
    private Set<ChildTabBean> packageNames;
    private Set<ChildTabBean> levels;
    private Set<ChildTabBean> authors;
    private Set<ChildTabBean> threads;
    private Set<ChildTabBean> extra1s;
    private Set<ChildTabBean> extra2s;

    private Set<TagBean> tagBeans;

    private long startTime;
    private long endTime;

    public static Set<String> getTextSet(Set<? extends ChildTabBean> childTabBeen){
        Set<String> strings = new HashSet<>();
        for (ChildTabBean childTabBean:childTabBeen){
            strings.add(childTabBean.getText());
        }
        return strings;
    }

    public static<T extends ChildTabBean> Set<T> getChildTabSet(Set<T> childTabBeen,
                                                     List<String> filtrateSet){
        Set<T> result = new HashSet<>();
        for (String s:filtrateSet){
            for (T childTabBean:childTabBeen){
                if (childTabBean.getText().equals(s)){
                    result.add(childTabBean);
                    break;
                }
            }
        }
        return result;
    }

    public Set<ChildTabBean> getPackageNames() {
        return packageNames;
    }

    public void setPackageNames(Set<ChildTabBean> packageNames) {
        this.packageNames = packageNames;
    }

    public Set<ChildTabBean> getLevels() {
        return levels;
    }

    public void setLevels(Set<ChildTabBean> levels) {
        this.levels = levels;
    }

    public Set<ChildTabBean> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<ChildTabBean> authors) {
        this.authors = authors;
    }

    public Set<ChildTabBean> getThreads() {
        return threads;
    }

    public void setThreads(Set<ChildTabBean> threads) {
        this.threads = threads;
    }

    public Set<ChildTabBean> getExtra1s() {
        return extra1s;
    }

    public void setExtra1s(Set<ChildTabBean> extra1s) {
        this.extra1s = extra1s;
    }

    public Set<ChildTabBean> getExtra2s() {
        return extra2s;
    }

    public void setExtra2s(Set<ChildTabBean> extra2s) {
        this.extra2s = extra2s;
    }

    public Set<TagBean> getTagBeans() {
        return tagBeans;
    }

    public void setTagBeans(Set<TagBean> tagBeans) {
        this.tagBeans = tagBeans;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
