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

public class ProvinceItem extends TreeItemGroup<ProvinceBean> {
    @Nullable
    @Override
    protected List<TreeItem> initChild(ProvinceBean data) {
        List<TreeItem> items = ItemHelperFactory.createItems(data.citys,this);
        for(int i=0;i<items.size();i++){
            TreeItemGroup treeItemGroup = (TreeItemGroup) items.get(i);
            treeItemGroup.setExpand(false);
        }
        return items;
    }

    @Override
    public int getLayoutId() {
        return R.layout.itme_one;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder) {
        viewHolder.setText(R.id.tv_content,data.provinceName);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, RecyclerView.LayoutParams layoutParams, int position) {
        super.getItemOffsets(outRect, layoutParams, position);
        outRect.bottom = 2;
    }

    @Override
    public boolean isCanExpand() {
        return true;
    }
}
