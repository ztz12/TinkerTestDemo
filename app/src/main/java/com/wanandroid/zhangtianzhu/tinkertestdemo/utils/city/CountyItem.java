package com.wanandroid.zhangtianzhu.tinkertestdemo.utils.city;

import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.baozi.treerecyclerview.base.ViewHolder;
import com.baozi.treerecyclerview.factory.ItemHelperFactory;
import com.baozi.treerecyclerview.item.TreeItem;
import com.baozi.treerecyclerview.item.TreeItemGroup;
import com.wanandroid.zhangtianzhu.tinkertestdemo.R;

import java.util.List;

public class CountyItem extends TreeItemGroup<ProvinceBean.CityBean> {

    @Nullable
    @Override
    protected List<TreeItem> initChild(ProvinceBean.CityBean data) {
        return ItemHelperFactory.createItems(data.areas,this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_two;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder) {
        viewHolder.setText(R.id.tv_content,data.cityName);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, RecyclerView.LayoutParams layoutParams, int position) {
        super.getItemOffsets(outRect, layoutParams, position);
        outRect.top = 2;
        outRect.left = 5;
    }
}
