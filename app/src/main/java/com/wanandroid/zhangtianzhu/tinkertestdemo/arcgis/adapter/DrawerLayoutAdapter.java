package com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.wanandroid.zhangtianzhu.tinkertestdemo.R;

import java.util.List;

public class DrawerLayoutAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> dataList;
    private int layoutId;

    public DrawerLayoutAdapter(Context context, List<String> dataList, int layoutId) {
        mContext = context;
        this.dataList = dataList;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList == null ? null : dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
            holder = new ViewHolder();
            holder.checkBox = convertView.findViewById(R.id.checkBox);
            holder.tv = convertView.findViewById(R.id.tv_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv.setText(dataList.get(position));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkedChangeListener != null) {
                    checkedChangeListener.onCheckedChanged(isChecked, position);
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        private CheckBox checkBox;
        private TextView tv;
    }

    private MyOnCheckedChangeListener checkedChangeListener;

    public void setCheckedChangeListener(MyOnCheckedChangeListener checkedChangeListener) {
        this.checkedChangeListener = checkedChangeListener;
    }

    public interface MyOnCheckedChangeListener {
        void onCheckedChanged(boolean isChecked, int position);
    }
}
