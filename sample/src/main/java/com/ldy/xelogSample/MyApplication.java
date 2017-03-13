package com.ldy.xelogSample;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.ldy.xelog.XELog;
import com.ldy.xelog.config.auto.ActivityLog;

/**
 * Created by ldy on 2017/3/8.
 */

public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        XELog.init(this,true);
        XELog.activeAutoLog(ActivityLog.getInstance("ldy"));
    }
}
