package com.ldy.xelog.common;

import android.content.Context;

/**
 * Created by ldy on 2017/4/7.
 */

public class XELogCommon {
    private static final String DB_NAME = "xelog";

    public static String dirPath;
    public static Context context;

    public static String getDBPath(){
        if (dirPath ==null){
            return DB_NAME;
        }
        return dirPath +"/"+DB_NAME;
    }
}
