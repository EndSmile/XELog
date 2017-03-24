package com.ldy.xelog_read.activity;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ldy.xelog.common.JsonFileBean;
import com.ldy.xelog_read.R;
import com.ldy.xelog_read.widget.TimeShowView;

import java.util.List;
import java.util.Objects;

/**
 * Created by ldy on 2017/3/7.
 */
class LogListAdapter extends BaseAdapter {

    private Context context;
    private List<JsonFileBean> list;

    public LogListAdapter(Context context, List<JsonFileBean> list) {
        this.context = context;
        this.list = list;
    }

    public void setData(List<JsonFileBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_log, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        JsonFileBean jsonFileBean = getJsonFileBean(position);
        viewHolder.tvTime.setDate((jsonFileBean.getTime()));
        viewHolder.tvSummary.setText(jsonFileBean.getSummary());
        if (jsonFileBean.getLevel().equals("ERROR")){
            viewHolder.tvTime.setTextColor(context.getResources().getColor(R.color.xelog_read_error));
            viewHolder.tvSummary.setTextColor(context.getResources().getColor(R.color.xelog_read_error));
        }else {
            viewHolder.tvTime.setTextColor(context.getResources().getColor(R.color.xelog_read_deep_grey));
            viewHolder.tvSummary.setTextColor(context.getResources().getColor(R.color.xelog_read_base_black_100));
        }

        return convertView;
    }

    public JsonFileBean getJsonFileBean(int position) {
        return list.get(getRealPosition(position));
    }

    public int getRealPosition(int position){
        return getCount() - 1 - position;
    }

    static class ViewHolder {
        TimeShowView tvTime;
        TextView tvSummary;

        ViewHolder(View view) {
            tvTime = (TimeShowView) view.findViewById(R.id.tv_item_log_time);
            tvSummary = (TextView) view.findViewById(R.id.tv_item_log_summary);
        }
    }
}
