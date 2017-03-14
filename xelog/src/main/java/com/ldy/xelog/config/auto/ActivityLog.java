package com.ldy.xelog.config.auto;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.List;

/**
 * Created by ldy on 2017/3/3.
 */

public class ActivityLog extends AbstractAutoLog {
    protected static ActivityLog instance;
    private boolean active;
    private Application.ActivityLifecycleCallbacks callback;

    protected ActivityLog(String author) {
        super(author);
    }

    public static ActivityLog getInstance(String author) {
        if (instance == null) {
            synchronized (ActivityLog.class) {
                if (instance == null) {
                    instance = new ActivityLog(author);
                }
            }
        }
        return instance;
    }

    public void activate() {
        if (active) {
            return;
        }
        this.active = true;
        if (callback==null){
            callback = new Application.ActivityLifecycleCallbacks() {
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
            };
        }
        ((Application) context.getApplicationContext()).registerActivityLifecycleCallbacks(callback);
    }

    @Override
    public void stop() {
        if (callback == null || !active) {
            return;
        }
        ((Application) context.getApplicationContext()).unregisterActivityLifecycleCallbacks(callback);
        active = false;
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
    public List<String> getBaseTag() {
        List<String> tags = super.getBaseTag();
        tags.add("activity");
        tags.add("lifeCircle");
        return tags;
    }

}
