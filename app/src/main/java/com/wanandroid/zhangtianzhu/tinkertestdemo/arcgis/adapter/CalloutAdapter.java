package com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wanandroid.zhangtianzhu.tinkertestdemo.R;
import com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis.LengthData;

import java.util.List;

public class CalloutAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<LengthData> dataList;
    public CalloutAdapter(Context context, List<LengthData> dataList){
        inflater = LayoutInflater.from(context);
        mContext = context;
        this.dataList = dataList;
    }
    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(view==null){
            view = inflater.inflate(R.layout.item_data_length,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tvNumber = view.findViewById(R.id.txt_number);
            viewHolder.tvLength = view.findViewById(R.id.txt_length);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tvNumber.setText(String.valueOf(dataList.get(i).getNumber()));
        viewHolder.tvLength.setText(dataList.get(i).getLength()+" 米");
        return view;
    }

    class ViewHolder{
        private TextView tvNumber;
        private TextView tvLength;
    }
}
