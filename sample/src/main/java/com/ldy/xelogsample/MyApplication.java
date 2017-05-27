package com.ldy.xelogsample;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.os.Looper;
import android.widget.Toast;

import com.ldy.xelog.XELog;
import com.ldy.xelog.config.auto.ActivityLog;
import com.ldy.xelog.config.auto.CrashCatchLog;


/**
 * Created by ldy on 2017/3/8.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        XELog.init(this);
        XELog.activateAutoLog(ActivityLog.getInstance("ldy")
                ,new CrashCatchLog("ldy", new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread t, Throwable e) {
                        Toast.makeText(MyApplication.this, "程序发生意外，即将退出...", Toast.LENGTH_SHORT).show();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        System.exit(0);
                    }
                })
        );
    }
}
