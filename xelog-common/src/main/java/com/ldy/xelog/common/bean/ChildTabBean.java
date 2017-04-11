package com.ldy.xelog.common.bean;

/**
 * Created by ldy on 2017/4/11.
 */

public class ChildTabBean {
    protected int id;
    protected String text;

    public ChildTabBean(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChildTabBean that = (ChildTabBean) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
