package com.ldy.xelog_read.activity;

import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ldy.xelog_read.R;
import com.ldy.xelog_read.bean.JsonFileBean;
import com.ldy.xelog_read.control.XELogReadControl;
import com.ldy.xelog_read.widget.LogDetailItem;
import com.ldy.xelog_read.widget.dateSelect.MsSelect;
import com.ldy.xelog_read.widget.dropGroup.DropGroup;
import com.ldy.xelog_read.widget.multiSelect.MultiSelect;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class XELogReadActivity extends XELogReadBaseActivity {

    private XELogReadControl xeLogReadControl;
    private LogListAdapter adapter;
    private ListView lvLog;
    private LogDetailItem liStartTime;
    private LogDetailItem liEndTime;
    private DropGroup dropGroup;
    private CheckBox chkFiltrate;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private SeekBar seekBarTime;
    private MsSelect msSelect;

    private long startTime;
    private long endTime;
    private long unitTime;
    private Button btnTimeCertain;
    private MultiSelect multiSelLevel;
    private MultiSelect multiSelThread;
    private MultiSelect multiSelAuthor;


    @Override
    protected void attachView(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_xelog_read);
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        bindView();
        xeLogReadControl = new XELogReadControl(this);
        xeLogReadControl.init(new XELogReadControl.DataLoadListener() {
            @Override
            public void loadFinish(final List<JsonFileBean> jsonFileBeen) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("XELogReadActivity", "loadfinish");
                        if (adapter == null) {
                            adapter = new LogListAdapter(XELogReadActivity.this, jsonFileBeen);
                            lvLog.setAdapter(adapter);
                        } else {
                            adapter.setData(jsonFileBeen);
                        }
                        initTimeFiltrate();
                        initMultiSelFiltrate();
                    }
                });
            }
        });

        lvLog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogDetailActivity.navigation(XELogReadActivity.this, adapter.getJsonFileBean(position));
            }
        });
    }

    private void initMultiSelFiltrate() {
        multiSelLevel.setContent("level:",xeLogReadControl.getLevels(),xeLogReadControl.getCheckedLevels());
        multiSelAuthor.setContent("author:",xeLogReadControl.getAuthors(),xeLogReadControl.getCheckedAuthors());
        multiSelAuthor.setContent("thread:",xeLogReadControl.getThreads(),xeLogReadControl.getCheckedThreads());
    }

    private void initTimeFiltrate() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd\nHH:mm:ss.SSS");

        startTime = xeLogReadControl.getStartTime();
        endTime = xeLogReadControl.getEndTime();
        unitTime = (endTime - startTime) / 100;

        tvStartTime.setText(simpleDateFormat.format(endTime));
        tvEndTime.setText(simpleDateFormat.format(startTime));
        msSelect.setTime(xeLogReadControl.getCheckedTime());
    }

    private void bindView() {
        lvLog = (ListView) findViewById(R.id.lv_xelog_read);
        dropGroup = (DropGroup) findViewById(R.id.dropGroup_xelog_read);
        chkFiltrate = (CheckBox) findViewById(R.id.chk_xelog_read);
        tvStartTime = (TextView) findViewById(R.id.tv_xelog_start_time);
        tvEndTime = (TextView) findViewById(R.id.tv_xelog_end_time);
        seekBarTime = (SeekBar) findViewById(R.id.seekBar_xelog_read);
        msSelect = (MsSelect) findViewById(R.id.msSelect_xelog_read);
        btnTimeCertain = (Button) findViewById(R.id.btn_xelog_read_time_certain);
        multiSelLevel = (MultiSelect) findViewById(R.id.multiSel_level);
        multiSelAuthor = (MultiSelect) findViewById(R.id.multiSel_author);
        multiSelThread = (MultiSelect) findViewById(R.id.multiSel_thread);

        seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                long time = endTime - unitTime * progress;
                if (time < startTime) {
                    time = startTime;
                }
                msSelect.setTime(time);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btnTimeCertain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long time = msSelect.getTime();
                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                Log.d("XELogReadActivity", simpleDateFormat.format(new Date(time)));
                int position = xeLogReadControl.jumpTime(time);
                msSelect.setTime(xeLogReadControl.getCheckedTime());
                lvLog.setSelection(adapter.getRealPosition(position));
                dropGroup.fold(true);
            }
        });

        dropGroup.setCheckBox(chkFiltrate);
    }

}
