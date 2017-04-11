package com.ldy.xelog.common;

import android.content.Context;

/**
 * Created by ldy on 2017/4/7.
 */

public class XELogCommon {
    private static final String DB_NAME = "xelog";

    public static String xelogDirPath;
    public static Context context;

    public static String getDBDirPath(){
        if (xelogDirPath==null){
            return DB_NAME;
        }
        return xelogDirPath+"/"+DB_NAME;
    }
}
