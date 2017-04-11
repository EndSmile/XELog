package com.ldy.xelog_read.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ldy.xelog.common.bean.LogBean;
import com.ldy.xelog_read.R;
import com.ldy.xelog_read.widget.TimeShowView;

import java.util.List;

/**
 * Created by ldy on 2017/3/7.
 */
class LogListAdapter extends BaseAdapter {

    private Context context;
    private List<LogBean> list;

    public LogListAdapter(Context context, List<LogBean> list) {
        this.context = context;
        this.list = list;
    }

    public void setData(List<LogBean> list) {
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

        LogBean logBean = getJsonFileBean(position);
        viewHolder.tvTime.setDate((logBean.getTime()));
        viewHolder.tvSummary.setText(logBean.getSummary());
        if (logBean.getLevel().equals("ERROR")){
            viewHolder.tvTime.setTextColor(context.getResources().getColor(R.color.xelog_read_error));
            viewHolder.tvSummary.setTextColor(context.getResources().getColor(R.color.xelog_read_error));
        }else {
            viewHolder.tvTime.setTextColor(context.getResources().getColor(R.color.xelog_read_deep_grey));
            viewHolder.tvSummary.setTextColor(context.getResources().getColor(R.color.xelog_read_base_black_100));
        }

        return convertView;
    }

    public LogBean getJsonFileBean(int position) {
        return list.get(getRealPosition(position));
    }

    public int getRealPosition(int position){
//        return getCount() - 1 - position;
        return position;
    }

    public List<LogBean> getList() {
        return list;
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
