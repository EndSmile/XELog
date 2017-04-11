package com.ldy.xelog.common.bean;

import java.util.List;
import java.util.List;

/**
 * Created by ldy on 2017/4/11.
 */

public class FiltrateParamsBean {
    public static final int pageSize = 50;

    /**
     * 初始为0
     */
    private int pageNo = 0;

    private List<ChildTabBean> packageNames;
    private List<ChildTabBean> levels;
    private List<ChildTabBean> authors;
    private List<ChildTabBean> threads;
    private List<ChildTabBean> extra1s;
    private List<ChildTabBean> extra2s;

    private List<TagBean> tagBeans;

    public static int getPageSize() {
        return pageSize;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public List<ChildTabBean> getPackageNames() {
        return packageNames;
    }

    public void setPackageNames(List<ChildTabBean> packageNames) {
        this.packageNames = packageNames;
    }

    public List<ChildTabBean> getLevels() {
        return levels;
    }

    public void setLevels(List<ChildTabBean> levels) {
        this.levels = levels;
    }

    public List<ChildTabBean> getAuthors() {
        return authors;
    }

    public void setAuthors(List<ChildTabBean> authors) {
        this.authors = authors;
    }

    public List<ChildTabBean> getThreads() {
        return threads;
    }

    public void setThreads(List<ChildTabBean> threads) {
        this.threads = threads;
    }

    public List<ChildTabBean> getExtra1s() {
        return extra1s;
    }

    public void setExtra1s(List<ChildTabBean> extra1s) {
        this.extra1s = extra1s;
    }

    public List<ChildTabBean> getExtra2s() {
        return extra2s;
    }

    public void setExtra2s(List<ChildTabBean> extra2s) {
        this.extra2s = extra2s;
    }

    public List<TagBean> getTagBeans() {
        return tagBeans;
    }

    public void setTagBeans(List<TagBean> tagBeans) {
        this.tagBeans = tagBeans;
    }
}
