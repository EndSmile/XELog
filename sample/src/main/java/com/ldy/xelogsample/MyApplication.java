package com.ldy.xelogsample;

import android.app.Application;

import com.ldy.xelog.XELog;
import com.ldy.xelog.config.auto.ActivityLog;
import com.ldy.xelog.config.auto.CrashCatchLog;
import com.ldy.xelog_read.XELogRead;

/**
 * Created by ldy on 2017/3/8.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        XELog.init(this);
        XELog.activateAutoLog(ActivityLog.getInstance("ldy"));
    }
}
