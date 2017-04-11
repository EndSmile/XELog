package com.ldy.xelog_read.activity;

import android.support.annotation.Nullable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ldy.xelog.common.bean.LogBean;
import com.ldy.xelog_read.R;
import com.ldy.xelog_read.control.XELogReadControl;
import com.ldy.xelog_read.widget.LogDetailItem;
import com.ldy.xelog_read.widget.dateSelect.MsSelect;
import com.ldy.xelog_read.widget.dropGroup.DropGroup;
import com.ldy.xelog_read.widget.multiSelect.MultiSelect;
import com.ldy.xelog_read.widget.tagtree.TagTree;
import com.ldy.xelog_read.widget.xListView.XListView;

import java.text.SimpleDateFormat;
import java.util.List;

public class XELogReadActivity extends XELogReadBaseActivity {

    private XELogReadControl xeLogReadControl;
    private LogListAdapter adapter;
    private XListView xlvLog;
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
    private EditText edtStringFiltrate;
    private XELogReadControl.DataLoadListener dataLoadListener;
    private MultiSelect multiSelPackage;
    private MultiSelect multiSelExtra1;
    private MultiSelect multiSelExtra2;
    private int pageNo;


    @Override
    protected void attachView(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_xelog_read);
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        bindView();
        xeLogReadControl = new XELogReadControl(this);
        dataLoadListener = new XELogReadControl.DataLoadListener() {
            @Override
            public void loadFirst(List<LogBean> dataList) {
                runOnUiThread(() -> {
                    xlvLog.stopRefresh(true);
                    initTimeFiltrate();
                    initMultiSelFiltrate();
                    initTagTree();
                    setData(dataList);
                });
            }

            @Override
            public void loadFinish(List<LogBean> dataList) {
                runOnUiThread(() -> {
                    if (pageNo > 0 && dataList.get(0).getId() == adapter.getList().get(0).getId()) {
                        Toast.makeText(XELogReadActivity.this, "没有更多数据了...", Toast.LENGTH_SHORT).show();
                    }
                    xlvLog.stopLoadMore();
                    setData(dataList);
                    initTimeFiltrate();
                });
            }

            @Override
            public void jumpPosition(int position) {
                runOnUiThread(() -> {
                    msSelect.setTime(xeLogReadControl.getCheckedTime());
                    xlvLog.setSelection(adapter.getRealPosition(position));
                    dropGroup.fold(true);
                });
            }
        };
        xeLogReadControl.init(dataLoadListener);

        xlvLog.setOnItemClickListener((parent, view, position, id) ->
                LogDetailActivity.navigation(XELogReadActivity.this, adapter.getJsonFileBean(position - 1)));
        xlvLog.setPullLoadEnable(true);
    }

    private void setData(List<LogBean> jsonFileBeen) {
        if (adapter == null) {
            adapter = new LogListAdapter(XELogReadActivity.this, jsonFileBeen);
            xlvLog.setAdapter(adapter);
        } else {
            adapter.setData(jsonFileBeen);
        }
    }

    private void initTagTree() {
        tagTree.setTag(xeLogReadControl.getTag());
    }

    private void initMultiSelFiltrate() {
        multiSelLevel.setContent("level:", xeLogReadControl.getLevels(), xeLogReadControl.getCheckedLevels());
        multiSelAuthor.setContent("author:", xeLogReadControl.getAuthors(), xeLogReadControl.getCheckedAuthors());
        multiSelThread.setContent("thread:", xeLogReadControl.getThreads(), xeLogReadControl.getCheckedThreads());
        multiSelPackage.setContent("package:", xeLogReadControl.getPackageNames(), xeLogReadControl.getCheckedPackage());
        multiSelExtra1.setContent("extra1:", xeLogReadControl.getExtra1s(), xeLogReadControl.getExtra1s());
        multiSelExtra2.setContent("extra2:", xeLogReadControl.getExtra2s(), xeLogReadControl.getCheckedExtra2s());
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
        xlvLog = (XListView) findViewById(R.id.xlv_xelog_read);
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
        multiSelPackage = (MultiSelect) findViewById(R.id.multiSel_package);
        multiSelExtra1 = (MultiSelect) findViewById(R.id.multiSel_extra1);
        multiSelExtra2 = (MultiSelect) findViewById(R.id.multiSel_extra2);
        btnFiletrate = (Button) findViewById(R.id.btn_xelog_read_filtrate_certain);
        tagTree = (TagTree) findViewById(R.id.tagTree_xelog_read);
        edtStringFiltrate = (EditText) findViewById(R.id.edt_xelog_read_string_filtrate);

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
        btnTimeCertain.setOnClickListener(v -> {
            long time = msSelect.getTime();
            xeLogReadControl.jump2Time(time);
        });

        btnFiletrate.setOnClickListener((view) -> filtrate(0));
        edtStringFiltrate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filtrate(0);
            }
        });


        tagTree.setTagChangeListener(() -> filtrate(0));

        dropGroup.setCheckBox(chkFiltrate);

        xlvLog.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                xeLogReadControl.init(dataLoadListener);
            }

            @Override
            public void onLoadMore() {
                filtrate(pageNo + 1);
            }
        });
    }

    private void filtrate(int pageNo) {
        this.pageNo = pageNo;
        xeLogReadControl.filtrate(pageNo, multiSelLevel.getSelect()
                , multiSelThread.getSelect(), multiSelAuthor.getSelect(), multiSelPackage.getSelect(), multiSelExtra1.getSelect(),
                multiSelExtra2.getSelect(), edtStringFiltrate.getText().toString());
    }


    @Override
    protected int getToolbarType() {
        return TOOLBAR_TYPE_NORMAL;
    }
}
