package com.ldy.xelog_read.widget.dateSelect;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldy.xelog_read.R;

/**
 * Created by ldy on 2017/3/7.
 */

public class DateItem extends LinearLayout {

    private EditText edtDate;
    private TextView tvDateUnit;

    public DateItem(Context context) {
        this(context, null);
    }

    public DateItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        initView(context);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.xelog_read_DateItem);
        if (a != null) {
            int n = a.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.xelog_read_DateItem_xelog_read_date) {
                    setDateNumber(a.getInt(attr, 0));

                } else if (attr == R.styleable.xelog_read_DateItem_xelog_read_dateUnit) {
                    setDateUnit(a.getString(attr));
                }
            }
            a.recycle();
        }

    }

    private void initView(Context context) {
        edtDate = new EditText(context);
        edtDate.setTextColor(getResources().getColor(R.color.xelog_read_base_black_100));
        edtDate.setInputType(InputType.TYPE_CLASS_NUMBER);
        tvDateUnit = new TextView(context);
        addView(edtDate);
        addView(tvDateUnit);
    }

    public void setDateNumber(int number) {
        edtDate.setText(prefix0(number));
    }

    private String prefix0(int number) {
        if (number < 10) {
            return 0 + String.valueOf(number);
        } else {
            return String.valueOf(number);
        }
    }

    public int getDataNumber() {
        return Integer.valueOf(edtDate.getText().toString());
    }

    public void setDateUnit(String dateUnit) {
        tvDateUnit.setText(dateUnit);
    }
}
