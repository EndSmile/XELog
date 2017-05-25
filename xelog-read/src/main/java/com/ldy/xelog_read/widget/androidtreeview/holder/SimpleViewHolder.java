package com.ldy.xelog_read.widget.androidtreeview.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.ldy.xelog_read.widget.androidtreeview.model.TreeNode;


/**
 * Created by Bogdan Melnychuk on 2/11/15.
 */
public class SimpleViewHolder extends BaseNodeViewHolder<Object> {

    public SimpleViewHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, Object value) {
        final TextView tv = new TextView(context);
        tv.setText(String.valueOf(value));
        return tv;
    }

    @Override
    public void toggle(boolean active) {

    }
}
