package com.ldy.xelog_read.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.LongSparseArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ldy.xelog_read.utils.DateFormatUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ldy on 2016/11/28.
 */
public class TimeShowView extends TextView {
    private static LongSparseArray<String> dateFormatCache = new LongSparseArray<>();

    public TimeShowView(Context context) {
        this(context, null);
    }

    public TimeShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDate(long time) {
        setText(putOrGetDateFormat(time));
    }

    @NonNull
    private String putOrGetDateFormat(long time) {
        String dateFormat = dateFormatCache.get(time);
        if (dateFormat == null) {
            if (dateFormatCache.size() > 100) {
                //最多cache100个
                dateFormatCache.clear();
            }
            dateFormat = DateFormatUtils.format(time);
            dateFormatCache.put(time, dateFormat);
        }
        return dateFormat;
    }

}
