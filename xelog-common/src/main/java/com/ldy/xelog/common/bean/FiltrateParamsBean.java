package com.ldy.xelog.common.bean;

import java.util.Set;

/**
 * Created by ldy on 2017/4/11.
 * set为null则代表匹配全部，为empty则代表都不匹配
 */
public class FiltrateParamsBean {
    public static final int pageSize = 100;

    /**
     * 初始为0
     */
    private int pageNo = 0;
    private String matchText;

    //为null则代表匹配全部
    private Set<ChildTabBean> packageNames;
    private Set<ChildTabBean> levels;
    private Set<ChildTabBean> authors;
    private Set<ChildTabBean> threads;
    private Set<ChildTabBean> extra1s;
    private Set<ChildTabBean> extra2s;

    private Set<TagBean> tagBeans;


    public boolean isValidFiltrate() {
        return isValid(packageNames)
                && isValid(levels)
                && isValid(authors)
                && isValid(threads)
                && isValid(extra1s)
                && isValid(extra2s)
                && isValidTag()
                && pageNo >= 0
                ;
    }

    private boolean isValid(Set set) {
        return set == null || !set.isEmpty();
    }

    private boolean isValidTag(){
        for (TagBean tagBean:tagBeans){
            if (tagBean.isTagSelect()){
                return true;
            }
        }
        return false;
    }

    public static int getPageSize() {
        return pageSize;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
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

    public String getMatchText() {
        return matchText;
    }

    public void setMatchText(String matchText) {
        this.matchText = matchText;
    }
}
