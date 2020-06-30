package com.wanandroid.zhangtianzhu.tinkertestdemo.gps;


import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.wanandroid.zhangtianzhu.tinkertestdemo.BaseActivity;
import com.wanandroid.zhangtianzhu.tinkertestdemo.R;
import com.wanandroid.zhangtianzhu.tinkertestdemo.utils.AssistStatic;

import java.util.Iterator;
import java.util.List;

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
    private LocationManager manager;
    private String locationProvider;
    private Location mLocation;
    private String gpsSignal = "无";

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
        getLocation();
    }

    private void getLocation() {
        //获取位置管理器
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取位置提供器 gps或网络 LocationManger.getProvider(String name)获取LocationProvider对象，LocationProvider位置源的提供者，
        //用于提供位置信息，使用方法或者最佳者信息
        List<String> providers = manager.getProviders(true);
        //在室内只能使用网络定位
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //网络定位
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
            locationProvider = LocationManager.GPS_PROVIDER;
        } else {
            AssistStatic.showToast(this, "没有可以的定位服务");
            return;
        }
        //获取上一次定位的位置，一般第一次运行，值为空
        Location location = manager.getLastKnownLocation(locationProvider);
        mLocation = location;
        //添加监听
        manager.addGpsStatusListener(listener);
        // 为获取地理位置信息时设置查询条件
//        String bestProvider = manager.getBestProvider(getCriteria(), true);
        // 获取位置信息
        // 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
//        Location location = manager.getLastKnownLocation(bestProvider);
        if (location != null) {
            showLocation();
        } else {
            // 绑定监听，有4个参数
            // 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
            // 参数2，位置信息更新周期，单位毫秒
            // 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
            // 参数4，监听
            // 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新

            // 1秒更新一次，或最小位移变化超过1米更新一次；
            // 注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1, locationListener);
        }
    }

    private LocationListener locationListener = new LocationListener() {
        //位置信息变化时候触发
        @Override
        public void onLocationChanged(Location location) {
            mLocation = location;

            Log.i(TAG, "时间：" + location.getTime());
            Log.i(TAG, "经度：" + location.getLongitude());
            Log.i(TAG, "纬度：" + location.getLatitude());
            Log.i(TAG, "海拔：" + location.getAltitude());
            Log.i(TAG, "方位：" + location.getBearing());
        }

        //GPS 状态发生变化时候触发
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                // GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    Log.i(TAG, "当前GPS状态为可见状态");
                    gpsSignal = "强";
                    break;
                // GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    Log.i(TAG, "当前GPS状态为服务区外状态");
                    gpsSignal = "无";
                    break;
                // GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.i(TAG, "当前GPS状态为暂停服务状态");
                    gpsSignal = "弱";
                    break;
                default:
                    break;
            }
        }

        //Gps 开启时候触发
        @Override
        public void onProviderEnabled(String provider) {
            mLocation = manager.getLastKnownLocation(provider);
        }

        //GPs 禁用时候触发
        @Override
        public void onProviderDisabled(String provider) {
            mLocation = null;
        }
    };

    private void showLocation() {
        StringBuilder sb = new StringBuilder();
        sb.append("GPS设备：");
        sb.append(mLocation.getProvider());
        sb.append("\n卫星数：");
        sb.append(satellitesCount);
        sb.append("\n信号：");
        sb.append(gpsSignal);
        sb.append("\n纬度：");
        sb.append(mLocation.getLatitude());
        sb.append("\n经度：");
        sb.append(mLocation.getLongitude());
        sb.append("\n方位：");
        sb.append(getFWAngle(mLocation.getBearing()));
        tvGps.setText(sb.toString());
    }

    /**
     * 返回查询条件
     * Criteria 用于选择位置信息提供者的辅助类
     *
     * @return
     */
    private static Criteria getCriteria() {

        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(true);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(true);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        return criteria;
    }

    private Integer satellitesCount = 0;
    private GpsStatus.Listener listener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            switch (event) {
                //第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.i(TAG, "onGpsStatusChanged: 第一次定位");
                    break;
                //卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    GpsStatus gpsStatus = manager.getGpsStatus(null);
                    //获取卫星颗数的默认最大值
                    int maxStatellites = gpsStatus.getMaxSatellites();
                    //创建一个迭代器保存所有卫星
                    Iterator<GpsSatellite> iterator = gpsStatus.getSatellites().iterator();
                    int count = 0;
                    while (iterator.hasNext() && count <= maxStatellites) {
                        count++;
                    }
                    satellitesCount = count;
                    Log.i(TAG, "搜索到多少颗卫星: " + count);
                    break;
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.i(TAG, "onGpsStatusChanged: 定位启动");
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.i(TAG, "onGpsStatusChanged: 定位关闭");
                    break;
                default:
                    break;
            }
        }
    };

    private String getFWAngle(float angle) {

        if (angle == 0) {

            return "正北";
        }

        if (angle > 0 && angle < 90) {

            return "东北";
        }

        if (angle == 90) {

            return "正东";
        }

        if (angle > 90 && angle < 180) {

            return "东南";
        }

        if (angle == 180) {

            return "正南";
        }

        if (angle > -180 && angle < -90) {

            return "西南";
        }

        if (angle == -90) {

            return "正西";
        }

        if (angle > -90 && angle < -0) {

            return "西北";
        }

        return "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //使用完释放监听
        manager.removeUpdates(locationListener);
        manager.removeGpsStatusListener(listener);
    }
}
