package com.ldy.xelog_read.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ldy.xelog_read.R;


/**
 * Created by ldy on 2016/11/2.
 */

public abstract class XELogReadBaseActivity extends AppCompatActivity{
    protected static final int TOOLBAR_TYPE_BACK = 0;
    protected static final int TOOLBAR_TYPE_NORMAL = 1;

    protected Toolbar toolbar;
    private boolean isSuccess;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attachView(savedInstanceState);
        initToolbar();
        initView(savedInstanceState);
    }



    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar == null)
            return;
        if (getSupportActionBar() == null) {
            setSupportActionBar(toolbar);
            switch (getToolbarType()) {
                case TOOLBAR_TYPE_BACK:
                    toolbar.setNavigationIcon(R.drawable.ic_back);
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                    break;
            }
        }
    }

    protected int getToolbarType() {
        return TOOLBAR_TYPE_BACK;
    }

    protected boolean loadData(Bundle savedInstanceState) {
        return false;
    }

    protected void isVisible(View view, boolean isVisible) {
        if (isVisible) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    protected void gone(View view) {
        view.setVisibility(View.GONE);
    }

    protected void visible(View view) {
        view.setVisibility(View.VISIBLE);
    }



    protected abstract void attachView(@Nullable Bundle savedInstanceState);

    protected void initView(@Nullable Bundle savedInstanceState) {
    }

    public void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}
