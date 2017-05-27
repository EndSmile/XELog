package com.ldy.xelog.config.auto;

import com.ldy.xelog.XELog;
import com.ldy.xelog.common.BuildConfig;
import com.ldy.xelog.common.XELogCommon;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Created by ldy on 2017/5/27.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CrashCatchLogTest {

    private CrashCatchLog crashCatchLog;

    @Before
    public void setUp() throws Exception {
        XELog.init(RuntimeEnvironment.application);
        crashCatchLog = new CrashCatchLog(null);
    }

    @Test
    public void getSummary() throws Exception {
        String msg = "java.lang.reflect.InvocationTargetException\n" +
                "\tat java.lang.reflect.Method.invoke(Native Method)\n" +
                "\tat android.support.v7.app.AppCompatViewInflater$DeclaredOnClickListener.onClick(AppCompatViewInflater.java:288)\n" +
                "\tat android.view.View.performClick(View.java:5646)\n" +
                "\tat android.view.View$PerformClick.run(View.java:22458)\n" +
                "\tat android.os.Handler.handleCallback(Handler.java:761)\n" +
                "\tat android.os.Handler.dispatchMessage(Handler.java:98)\n" +
                "\tat android.os.Looper.loop(Looper.java:156)\n" +
                "\tat android.app.ActivityThread.main(ActivityThread.java:6524)\n" +
                "\tat java.lang.reflect.Method.invoke(Native Method)\n" +
                "\tat com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:941)\n" +
                "\tat com.android.internal.os.ZygoteInit.main(ZygoteInit.java:831)\n" +
                "Caused by: java.lang.NullPointerException: 实验\n" +
                "\tat com.ldy.xelogsample.MainActivity.errorLog(MainActivity.java:66)\n" +
                "\t... 11 more\n" +
                "java.lang.NullPointerException: 实验\n" +
                "\tat com.ldy.xelogsample.MainActivity.errorLog(MainActivity.java:66)\n" +
                "\tat java.lang.reflect.Method.invoke(Native Method)\n" +
                "\tat android.support.v7.app.AppCompatViewInflater$DeclaredOnClickListener.onClick(AppCompatViewInflater.java:288)\n" +
                "\tat android.view.View.performClick(View.java:5646)\n" +
                "\tat android.view.View$PerformClick.run(View.java:22458)\n" +
                "\tat android.os.Handler.handleCallback(Handler.java:761)\n" +
                "\tat android.os.Handler.dispatchMessage(Handler.java:98)\n" +
                "\tat android.os.Looper.loop(Looper.java:156)\n" +
                "\tat android.app.ActivityThread.main(ActivityThread.java:6524)\n" +
                "\tat java.lang.reflect.Method.invoke(Native Method)\n" +
                "\tat com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:941)\n" +
                "\tat com.android.internal.os.ZygoteInit.main(ZygoteInit.java:831)";
        String summary = crashCatchLog.getSummary(msg);

        assertEquals("NullPointerException",summary);
    }

}