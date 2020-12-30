package com.wanandroid.zhangtianzhu.tinkertestdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.baozi.treerecyclerview.adpater.TreeRecyclerAdapter;
import com.baozi.treerecyclerview.adpater.TreeRecyclerType;
import com.baozi.treerecyclerview.base.BaseRecyclerAdapter;
import com.baozi.treerecyclerview.base.ViewHolder;
import com.baozi.treerecyclerview.factory.ItemHelperFactory;
import com.baozi.treerecyclerview.item.TreeItem;
import com.baozi.treerecyclerview.item.TreeItemGroup;
import com.wanandroid.zhangtianzhu.tinkertestdemo.utils.city.ProvinceBean;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class TreeRecyclerViewActivity extends AppCompatActivity {

    TreeRecyclerAdapter treeRecyclerAdapter = new TreeRecyclerAdapter(TreeRecyclerType.SHOW_EXPAND);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_recycler_view);
        RecyclerView recyclerView = findViewById(R.id.rv_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(treeRecyclerAdapter);
        new Thread() {
            @Override
            public void run() {
                super.run();
                String string = getFromAssets("city.txt");
                Log.i("json", string);
                List<ProvinceBean> cityBeen = JSON.parseArray(string, ProvinceBean.class);
                refresh(cityBeen);
            }
        }.start();
    }

    private String getFromAssets(String fileName) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open(fileName));
            BufferedReader bf = new BufferedReader(inputReader);
            String line;
            while ((line = bf.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private void refresh(final List<ProvinceBean> cityBeen) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<TreeItem> items = ItemHelperFactory.createItems(cityBeen);
                for (int i = 0; i < items.size(); i++) {
                    TreeItemGroup treeItemGroup = (TreeItemGroup) items.get(i);
                    treeItemGroup.setExpand(false);
                }
                //添加到adapter中
                treeRecyclerAdapter.getItemManager().replaceAllItem(items);
            }
        });
    }
}
