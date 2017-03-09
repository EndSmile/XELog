package com.ldy.xelog.config.auto;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.elvishew.xlog.LogConfiguration;
import com.ldy.xelog.config.XELogConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldy on 2017/3/3.
 */

public class ActivityLog extends AbstractAutoLog {
    private static ActivityLog activityLog;

    private ActivityLog(Context context, String author) {
        super(context, author);
    }

    private void initInner() {
        ((Application) context.getApplicationContext()).registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                v(activity.getClass().getName() + ":onCreate");
            }

            @Override
            public void onActivityStarted(Activity activity) {
                v(activity.getClass().getName() + ":onStart");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                v(activity.getClass().getName() + ":onResume");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                v(activity.getClass().getName() + ":onPause");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                v(activity.getClass().getName() + ":onStop");
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                v(activity.getClass().getName() + ":onSaveInstanceState");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                v(activity.getClass().getName() + ":onDestroy");
            }
        });
    }

    @Override
    public String getSummary(String msg) {
        try {
            String[] split = msg.split(":");
            String[] split1 = split[0].split("\\.");
            return split1[split1.length - 1] + ":" + split[1];
        } catch (Exception e) {
            return super.getSummary(msg);
        }
    }

    @Override
    public String getRemarks(String message) {
        return "activity生命周期";
    }

    @Override
    public List<String> getTag() {
        List<String> tags = super.getTag();
        tags.add("activity");
        tags.add("lifeCircle");
        return tags;
    }

    public static void init(Context context, String author) {
        if (activityLog == null) {
            activityLog = new ActivityLog(context.getApplicationContext(), author);
            activityLog.initInner();
        } else {
            throw new RuntimeException("always init");
        }
    }

    public static void init(Context context) {
        init(context, "");
    }
}
