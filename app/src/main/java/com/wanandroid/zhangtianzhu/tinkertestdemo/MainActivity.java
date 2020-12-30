package com.wanandroid.zhangtianzhu.tinkertestdemo;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis.ArcGisActivity;
import com.wanandroid.zhangtianzhu.tinkertestdemo.utils.AssistStatic;
import com.wanandroid.zhangtianzhu.tinkertestdemo.utils.BindView;
import com.wanandroid.zhangtianzhu.tinkertestdemo.utils.Binding;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

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
        NiceSpinner spinner = findViewById(R.id.nice_spinner);
        List<String> markerData = new ArrayList<>();
        String[] gdTypes = getResources().getStringArray(R.array.dilei_type);
        for (int i = 0; i < gdTypes.length; i++) {
            markerData.add(gdTypes[i]);
        }
        spinner.attachDataSource(markerData);
        spinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                String selectedMarker = parent.getItemAtPosition(position).toString();
                AssistStatic.showToast(MainActivity.this, selectedMarker);
            }
        });
    }
}
