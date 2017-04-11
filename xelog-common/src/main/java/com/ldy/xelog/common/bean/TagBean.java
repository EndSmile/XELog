package com.ldy.xelog.common.bean;

import java.util.List;

/**
 * Created by ldy on 2017/4/11.
 */

public class TagBean extends ChildTabBean{
    private boolean tagSelect;

    public TagBean(int id, String text, boolean tagSelect) {
        super(id, text);
        this.tagSelect = tagSelect;
    }

    public boolean isTagSelect() {
        return tagSelect;
    }

    public List<String> getTagList(){
        return LogBean.getListByString(text,LogBean.TAG_SEPARATOR);
    }
}
