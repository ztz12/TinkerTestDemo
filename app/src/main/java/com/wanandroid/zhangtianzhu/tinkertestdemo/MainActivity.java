package com.wanandroid.zhangtianzhu.tinkertestdemo;

import android.content.Intent;
import android.widget.Button;

import com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis.ArcGisActivity;
import com.wanandroid.zhangtianzhu.tinkertestdemo.utils.BindView;
import com.wanandroid.zhangtianzhu.tinkertestdemo.utils.Binding;

/**
 * tinker 热修复接入教程
 * 教程：https://www.jianshu.com/p/3227dfa56eac
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.btn)
    Button btn;

    @Override
    public void initViews() {
        Binding.bind(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        btn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ArcGisActivity.class)));
    }
}
