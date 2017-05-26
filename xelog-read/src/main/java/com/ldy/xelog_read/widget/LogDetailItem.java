package com.ldy.xelog_read.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ldy.xelog_read.R;

/**
 * Created by ldy on 2017/3/6.
 */
public class LogDetailItem extends FrameLayout {

    private final TextView tvContent;
    private String content;
    private String title;
    private TextView tvTitele;

    public LogDetailItem(Context context) {
        this(context,null);
    }

    public LogDetailItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.xelog_read_LogDetailItem);
        if (a != null) {
            int n = a.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.xelog_read_LogDetailItem_android_title) {
                    title = a.getString(attr);

                } else if (attr == R.styleable.xelog_read_LogDetailItem_android_text) {
                    content = a.getString(attr);
                }
            }
            a.recycle();
        }
        LayoutInflater.from(context).inflate(R.layout.xelog_read_item_log_detail,this);

        tvTitele = ((TextView) findViewById(R.id.tv_log_detail_title));
        tvContent = ((TextView) findViewById(R.id.tv_log_detail_content));

        tvTitele.setText(title);
        tvContent.setText(content);
    }

    public void setContent(String content) {
        if (TextUtils.isEmpty(content)){
            setVisibility(GONE);
            return;
        }
        tvContent.setText(content);
    }
}
