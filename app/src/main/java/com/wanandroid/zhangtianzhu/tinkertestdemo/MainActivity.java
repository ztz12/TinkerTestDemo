package com.wanandroid.zhangtianzhu.tinkertestdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis.ArcGisActivity;

/**
 * tinker 热修复接入教程
 * 教程：https://www.jianshu.com/p/3227dfa56eac
 */
public class MainActivity extends BaseActivity {


    @Override
    public void initViews() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ArcGisActivity.class));
            }
        });
    }
}
