package com.ldy.xelog_read.widget.dateSelect;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ldy on 2017/3/7.
 */

public class MsSelect extends LinearLayout {
    private DateItem year;
    private DateItem month;
    private DateItem day;
    private DateItem hour;
    private DateItem min;
    private DateItem second;
    private DateItem ms;

    public MsSelect(Context context) {
        this(context, null);
    }

    public MsSelect(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        LinearLayout row1 = generateRow();
        year = generateItem("年");
        row1.addView(year);
        month = generateItem("月");
        row1.addView(month);
        day = generateItem("日");
        row1.addView(day);
        addView(row1);
        LinearLayout row2 = generateRow();
        hour = generateItem("时");
        row2.addView(hour);
        min = generateItem("分");
        row2.addView(min);
        second = generateItem("秒");
        row2.addView(second);
        ms = generateItem("毫秒");
        row2.addView(ms);
        addView(row2);
    }

    public void setTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        year.setDateNumber(calendar.get(Calendar.YEAR));
        month.setDateNumber(calendar.get(Calendar.MONTH)+1);
        day.setDateNumber(calendar.get(Calendar.DAY_OF_MONTH));
        hour.setDateNumber(calendar.get(Calendar.HOUR_OF_DAY));
        min.setDateNumber(calendar.get(Calendar.MINUTE));
        second.setDateNumber(calendar.get(Calendar.SECOND));
        ms.setDateNumber(calendar.get(Calendar.MILLISECOND));
    }

    public long getTime(){
        return new GregorianCalendar(year.getDataNumber(), month.getDataNumber()-1, day.getDataNumber(),
                hour.getDataNumber(), min.getDataNumber(), second.getDataNumber()).getTimeInMillis() + ms.getDataNumber();
    }

    private LinearLayout generateRow() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        return linearLayout;
    }

    private DateItem generateItem(String dateUnit) {
        DateItem dateItem = new DateItem(getContext());
        dateItem.setDateUnit(dateUnit);
        dateItem.setDateNumber(20);
        return dateItem;
    }
}
