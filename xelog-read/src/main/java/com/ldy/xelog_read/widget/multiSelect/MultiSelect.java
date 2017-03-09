package com.ldy.xelog_read.widget.multiSelect;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by ldy on 2017/3/8.
 */
public class MultiSelect extends LinearLayout {
    private List<CheckBox> contentList;
    private TextView tvTitle;

    public MultiSelect(Context context) {
        this(context,null);
    }

    public MultiSelect(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public void setContent(String title, Set<String> content, Set<String> checkedContent){
        contentList = new ArrayList<>();
        tvTitle = new TextView(getContext());
        tvTitle.setText(title);
        addView(tvTitle);
        for (String s:content){
            if (TextUtils.isEmpty(s)){
                continue;
            }
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(s);
            checkBox.setChecked(checkedContent.contains(s));
            contentList.add(checkBox);
            addView(checkBox);
        }
    }
}
