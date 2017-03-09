package com.ldy.xelog_read.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.ldy.xelog_read.R;
import com.ldy.xelog_read.bean.JsonFileBean;
import com.ldy.xelog_read.widget.LogDetailItem;

import java.text.SimpleDateFormat;

public class LogDetailActivity extends XELogReadActivity {

    public static final String LOG_ITEM = "log_item";
    private LogDetailItem liTime;
    private LogDetailItem liTag;
    private LogDetailItem liLevel;
    private LogDetailItem liThread;
    private LogDetailItem liRemarks;
    private LogDetailItem liSummary;
    private TextView tvStackTrace;
    private LogDetailItem liContent;
    private LogDetailItem liAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_detail);

        liTime = ((LogDetailItem) findViewById(R.id.logDetailItem_time));
        liAuthor = ((LogDetailItem) findViewById(R.id.logDetailItem_author));
        liTag = ((LogDetailItem) findViewById(R.id.logDetailItem_tag));
        liLevel = ((LogDetailItem) findViewById(R.id.logDetailItem_level));
        liThread = ((LogDetailItem) findViewById(R.id.logDetailItem_thread));
        liRemarks = ((LogDetailItem) findViewById(R.id.logDetailItem_remarks));
        liSummary = ((LogDetailItem) findViewById(R.id.logDetailItem_summary));
        liContent = ((LogDetailItem) findViewById(R.id.logDetailItem_content));
        tvStackTrace = ((TextView) findViewById(R.id.tv_stackTrace));

        JsonFileBean jsonFileBean = fromIntent();
        liTime.setContent(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(jsonFileBean.getTime()));
        liAuthor.setContent(jsonFileBean.getAuthor());
        liTag.setContent(jsonFileBean.getLogStr());
        liLevel.setContent(jsonFileBean.getLevel());
        liThread.setContent(jsonFileBean.getThread());
        liRemarks.setContent(jsonFileBean.getRemarks());
        liSummary.setContent(jsonFileBean.getSummary());
        liContent.setContent(jsonFileBean.getContent());
        tvStackTrace.setText(jsonFileBean.getStackTraceStr());
    }

    public static void navigation(Activity activity, JsonFileBean jsonFileBean) {
        Intent intent = new Intent(activity, LogDetailActivity.class);
        intent.putExtra(LOG_ITEM, jsonFileBean);
        activity.startActivity(intent);
    }

    private JsonFileBean fromIntent() {
        return getIntent().getParcelableExtra(LOG_ITEM);
    }
}
