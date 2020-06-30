package com.wanandroid.zhangtianzhu.tinkertestdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import cn.com.superLei.aoparms.annotation.Permission;
import cn.com.superLei.aoparms.annotation.PermissionDenied;
import cn.com.superLei.aoparms.annotation.PermissionNoAskDenied;
import cn.com.superLei.aoparms.common.permission.AopPermissionUtils;

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    private static final int REQUEST_PERMISSION_LOCATION = 0;
    private static final int REQUEST_PERMISSION_WRITE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        requestLocationPermission();
        initViews();
        initData();
    }

    @Permission(value = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
            requestCode = REQUEST_PERMISSION_LOCATION,
            rationale = "需要定位权限")
    public void requestLocationPermission() {
        requestWritePermission();
    }

    @Permission(value = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
            requestCode = REQUEST_PERMISSION_WRITE,
            rationale = "需要读写权限")
    public void requestWritePermission() {

    }

    @PermissionDenied
    public void permissionDenied(int requestCode, List<String> denyList) {
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            Log.e(TAG, "permissionDenied>>>:定位权限被拒 " + denyList.toString());
        } else if (requestCode == REQUEST_PERMISSION_WRITE) {
            Log.e(TAG, "permissionDenied>>>:读写权限被拒 " + denyList.toString());
        }
    }

    @PermissionNoAskDenied
    public void permissionNoAskDenied(int requestCode, List<String> denyNoAskList) {
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            Log.e(TAG, "permissionNoAskDenied 定位权限被拒>>>: " + denyNoAskList.toString());
        } else if (requestCode == REQUEST_PERMISSION_WRITE) {
            Log.e(TAG, "permissionDenied>>>:读写权限被拒>>> " + denyNoAskList.toString());
        }
        AopPermissionUtils.showGoSetting(this, "为了更好的体验，建议前往设置页面打开权限");
    }

    public abstract void initViews();

    public abstract int getLayoutId();

    public abstract void initData();
}
