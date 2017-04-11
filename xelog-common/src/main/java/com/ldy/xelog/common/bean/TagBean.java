package com.ldy.xelog.common.bean;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public static Set<TagBean> getTagTabSet(Set<TagBean> childTabBeen,
                                            List<String> filtrateSet){
        Set<TagBean> result = new HashSet<>();
        for (String s:filtrateSet){
            for (TagBean childTabBean:childTabBeen){
                if (childTabBean.getText().equals(s)){
                    result.add(childTabBean);
                    childTabBean.tagSelect = true;
                    break;
                }
            }
        }
        return result;
    }
}
