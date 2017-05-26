package com.ldy.xelogsample;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.elvishew.xlog.XLog;
import com.ldy.xelog.hugo.annotations.HugoXELog;
import com.ldy.xelog.okhttp_interceptor.OkHttpLogInterceptor;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void netLog(View view) {
        OkHttpLogInterceptor interceptor = new OkHttpLogInterceptor();
        interceptor.setLevel(OkHttpLogInterceptor.Level.BODY);
        interceptor.jsonBody();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor)
                .addInterceptor(httpLoggingInterceptor)
                .build();
        Request request = new Request.Builder().url("http://www.weather.com.cn/data/sk/101010100.html").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showToast(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                showToast(response.body().toString());
            }
        });
    }

    private void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void errorLog(View view) {
        throw new NullPointerException("实验");
    }

    public void hugoLog(View view){
        printArgs("Hugo", "XELog", "ldy");

        Greeter greeter = new Greeter("Jake");
        Log.d("Greeting", greeter.sayHello());

        startSleepyThread();
    }

    @HugoXELog
    private void printArgs(String... args) {
        for (String arg : args) {
            Log.i("Args", arg);
        }
    }

    @HugoXELog(Log.DEBUG)
    static class Greeter {
        private final String name;

        Greeter(String name) {
            this.name = name;
        }

        private String sayHello() {
            return "Hello, " + name;
        }
    }


    private void startSleepyThread() {
        new Thread(new Runnable() {
            private static final long SOME_POINTLESS_AMOUNT_OF_TIME = 50;

            @Override public void run() {
                sleepyMethod(SOME_POINTLESS_AMOUNT_OF_TIME);
            }

            @HugoXELog
            private void sleepyMethod(long milliseconds) {
                SystemClock.sleep(milliseconds);
            }
        }, "Im a lazy thr.. bah! whatever!").start();
    }
}
