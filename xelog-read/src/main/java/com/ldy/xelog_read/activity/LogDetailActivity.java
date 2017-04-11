package com.ldy.xelog_read.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.widget.TextView;

import com.ldy.xelog.common.bean.LogBean;
import com.ldy.xelog_read.R;
import com.ldy.xelog_read.widget.LogDetailItem;

import java.text.SimpleDateFormat;

public class LogDetailActivity extends XELogReadBaseActivity {

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
    protected void attachView(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_log_detail);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        liTime = ((LogDetailItem) findViewById(R.id.logDetailItem_time));
        liAuthor = ((LogDetailItem) findViewById(R.id.logDetailItem_author));
        liTag = ((LogDetailItem) findViewById(R.id.logDetailItem_tag));
        liLevel = ((LogDetailItem) findViewById(R.id.logDetailItem_level));
        liThread = ((LogDetailItem) findViewById(R.id.logDetailItem_thread));
        liRemarks = ((LogDetailItem) findViewById(R.id.logDetailItem_remarks));
        liSummary = ((LogDetailItem) findViewById(R.id.logDetailItem_summary));
        liContent = ((LogDetailItem) findViewById(R.id.logDetailItem_content));
        tvStackTrace = ((TextView) findViewById(R.id.tv_stackTrace));

        LogBean logBean = fromIntent();
        liTime.setContent(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(logBean.getTime()));
        liAuthor.setContent(logBean.getAuthor());
        liTag.setContent(logBean.getTag());
        liLevel.setContent(logBean.getLevel());
        liThread.setContent(logBean.getThread());
        liRemarks.setContent(logBean.getRemarks());
        liSummary.setContent(logBean.getSummary());
        liContent.setContent(logBean.getContent());
        tvStackTrace.setText(logBean.getStackTrace());
    }

    public static void navigation(Activity activity, LogBean logBean) {
        Intent intent = new Intent(activity, LogDetailActivity.class);
        intent.putExtra(LOG_ITEM, logBean);
        activity.startActivity(intent);
    }

    private LogBean fromIntent() {
        return getIntent().getParcelableExtra(LOG_ITEM);
    }
}
