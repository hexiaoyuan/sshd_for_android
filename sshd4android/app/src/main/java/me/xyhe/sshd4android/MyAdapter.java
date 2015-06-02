package me.xyhe.sshd4android;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

    List<String> logList;
    Context context;

    public MyAdapter(Context context, List<String> logList) {
        this.context = context;
        this.logList = logList;
    }

    @Override
    public int getCount() {
        return logList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return logList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        View row = arg1;
        Wrapper wrapper = null;
        if (row == null) {
            row = LayoutInflater.from(context).inflate(R.layout.list_item,
                    arg2, false);
            wrapper = new Wrapper(row);
            row.setTag(wrapper);
        } else {
            row = arg1;
            wrapper = (Wrapper) row.getTag();
        }

        TextView tvLog = wrapper.getTvLog();
        String log = logList.get(arg0);
        tvLog.setText(log);
        return row;
    }

    class Wrapper {
        TextView tvLog;
        View row;

        public Wrapper(View row) {
            this.row = row;
        }

        public TextView getTvLog() {
            if (tvLog == null) {
                tvLog = (TextView) row.findViewById(R.id.tv_log);
            }
            return tvLog;
        }
    }
}