package com.ldy.xelog_read.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ldy on 2017/3/8.
 */

public class DateFormatUtils {
    public static String format(long time) {
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(time);
        calendar.setTime(date);
        int dateYear = calendar.get(Calendar.YEAR);
        SimpleDateFormat simpleDateFormat;
        if (dateYear == thisYear) {
            simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
        } else {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        }
        return simpleDateFormat.format(date);
    }
}
