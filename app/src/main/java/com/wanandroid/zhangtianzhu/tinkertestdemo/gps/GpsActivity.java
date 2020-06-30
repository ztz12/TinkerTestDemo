package com.wanandroid.zhangtianzhu.tinkertestdemo.gps;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

import com.wanandroid.zhangtianzhu.tinkertestdemo.BaseActivity;
import com.wanandroid.zhangtianzhu.tinkertestdemo.R;

/**
 * GPS 定位特点
 * 1、比较耗电；
 * <p>
 * 2、绝大部分用户默认不开启GPS模块；
 * <p>
 * 3、从GPS模块启动到获取第一次定位数据，可能需要比较长的时间；
 * <p>
 * 4、室内几乎无法使用。
 */
@SuppressLint("MissingPermission")
public class GpsActivity extends BaseActivity {
    private static final String TAG = "GpsActivity";
    private TextView tvGps;
    private GpsService.MyBinder mBinder;

    @Override
    public void initViews() {
        tvGps = findViewById(R.id.tv_gps);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_gps;
    }

    @Override
    public void initData() {
        Intent intent = new Intent(this, GpsService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        showLocationInfo();
//        findViewById(R.id.btn_showLocation).setOnClickListener(v -> showLocationInfo());
    }

    private void showLocationInfo() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.wanandroid.zhangtianzhu.tinkertestdemo.gps.GpsService");
        registerReceiver(receiver, intentFilter);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (GpsService.MyBinder) service;
            mBinder.postShowLocation();
            mBinder.linkToDeath(deathRecipient, 0);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String provider = bundle.getString("gpsDevice");
                int count = bundle.getInt("satellites");
                String gpsSignal = bundle.getString("gpsSignal");
                double latitude = bundle.getDouble("la");
                double longitude = bundle.getDouble("long");
                String fwAngle = bundle.getString("fw");
                StringBuilder sb = new StringBuilder();
                sb.append("GPS设备：");
                sb.append(provider);
                sb.append("\n卫星数：");
                sb.append(count);
                sb.append("\n信号：");
                sb.append(gpsSignal);
                sb.append("\n纬度：");
                sb.append(latitude);
                sb.append("\n经度：");
                sb.append(longitude);
                sb.append("\n方位：");
                sb.append(fwAngle);
                tvGps.setText(sb.toString());
            }
        }
    };

    /**
     * 设置死亡代理
     */
    private IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (mBinder == null) {
                return;
            }
            mBinder.unlinkToDeath(deathRecipient, 0);
            mBinder = null;
            Intent intent = new Intent(GpsActivity.this, GpsService.class);
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinder.releaseListener();
        unregisterReceiver(receiver);
        unbindService(serviceConnection);
    }
}
