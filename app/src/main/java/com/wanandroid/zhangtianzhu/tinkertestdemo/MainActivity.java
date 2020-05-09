package com.wanandroid.zhangtianzhu.tinkertestdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

/**
 * tinker 热修复接入教程
 * 教程：https://www.jianshu.com/p/3227dfa56eac
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "人生如戏，戏里戏外都是人生，人生如梦，梦中自有颜如玉，哈哈哈哈哈", Toast.LENGTH_SHORT).show();
    }
}
