package com.ldy.xelog_read.activity;

import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jakewharton.rxbinding2.widget.RxSeekBar;
import com.ldy.xelog_read.R;
import com.ldy.xelog_read.control.XELogReadControl;
import com.ldy.xelog_read.widget.LogDetailItem;
import com.ldy.xelog_read.widget.dateSelect.MsSelect;
import com.ldy.xelog_read.widget.dropGroup.DropGroup;
import com.ldy.xelog_read.widget.multiSelect.MultiSelect;
import com.ldy.xelog_read.widget.tagtree.TagTree;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    private Button btnFiletrate;
    private TagTree tagTree;


    @Override
    protected void attachView(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_xelog_read);
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        bindView();
        xeLogReadControl = new XELogReadControl(this);
        xeLogReadControl.init()
                .subscribe(jsonFileBeen -> {
                    Log.d("XELogReadActivity", "loadfinish");
                    if (adapter == null) {
                        adapter = new LogListAdapter(XELogReadActivity.this, jsonFileBeen);
                        lvLog.setAdapter(adapter);
                    } else {
                        adapter.setData(jsonFileBeen);
                    }
                    initTimeFiltrate();
                    initMultiSelFiltrate();
                    initTagTree();
                });

        lvLog.setOnItemClickListener((parent, view, position, id) ->
                LogDetailActivity.navigation(XELogReadActivity.this, adapter.getJsonFileBean(position)));
    }

    private void initTagTree() {
        tagTree.setTag(xeLogReadControl.getTag());
    }

    private void initMultiSelFiltrate() {
        multiSelLevel.setContent("level:", xeLogReadControl.getLevels(), xeLogReadControl.getCheckedLevels());
        multiSelAuthor.setContent("author:", xeLogReadControl.getAuthors(), xeLogReadControl.getCheckedAuthors());
        multiSelThread.setContent("thread:", xeLogReadControl.getThreads(), xeLogReadControl.getCheckedThreads());
    }

    private void initTimeFiltrate() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd\nHH:mm:ss.SSS");

        startTime = xeLogReadControl.getStartTime();
        endTime = xeLogReadControl.getEndTime();
        unitTime = (endTime - startTime) / 100;

        tvStartTime.setText(simpleDateFormat.format(endTime));
        tvEndTime.setText(simpleDateFormat.format(startTime));
        msSelect.setTime(xeLogReadControl.getCheckedTime());
        seekBarTime.setProgress(0);
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
        btnFiletrate = (Button) findViewById(R.id.btn_xelog_read_filtrate_certain);
        tagTree = (TagTree) findViewById(R.id.tagTree_xelog_read);

        RxSeekBar.changes(seekBarTime).subscribe((progress) -> {
            long time = endTime - unitTime * progress;
            if (time < startTime) {
                time = startTime;
            }
            msSelect.setTime(time);
        });

        btnTimeCertain.setOnClickListener(v -> {
            long time = msSelect.getTime();
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Log.d("XELogReadActivity", simpleDateFormat.format(new Date(time)));
            xeLogReadControl.jumpTime(time)
                    .subscribe(position -> {
                        msSelect.setTime(xeLogReadControl.getCheckedTime());
                        lvLog.setSelection(adapter.getRealPosition(position));
                        dropGroup.fold(true);
                    });
        });

        btnFiletrate.setOnClickListener((view) -> filtrate());

        tagTree.setTagChangeListener(this::filtrate);

        dropGroup.setCheckBox(chkFiltrate);
    }

    private void filtrate() {
        xeLogReadControl.filtrate(multiSelLevel.getSelect(), multiSelThread.getSelect(), multiSelAuthor.getSelect())
                .subscribe(jsonFileBeen -> {
                    adapter.setData(jsonFileBeen);
                    initTimeFiltrate();
                });
    }

    @Override
    protected int getToolbarType() {
        return TOOLBAR_TYPE_NORMAL;
    }
}
