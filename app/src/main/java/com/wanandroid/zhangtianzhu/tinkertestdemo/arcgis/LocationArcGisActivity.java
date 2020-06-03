package com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.location.LocationDataSource;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.wanandroid.zhangtianzhu.tinkertestdemo.R;
import com.wanandroid.zhangtianzhu.tinkertestdemo.bluetooth.BluetoothActivityTwo;

import java.util.ArrayList;
import java.util.List;

/**
 * 地图GPS 定位
 */
public class LocationArcGisActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSION_ACCESS_LOCATION = 0;
    private List<String> permissions = new ArrayList<>();
    private int mPermissionRequestCount = 0;
    private int MAX_NUMBER_REQUEST_PERMISSIONS = 4;
    private MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_arc_gis);
        mMapView = findViewById(R.id.location_mapView);
        initMap();
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.READ_CONTACTS);
        permissions.add(Manifest.permission.READ_SMS);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        requestPermissionIfNecessary();
    }

    private void initMap() {
        String theURLString = "http://map.geoq.cn/arcgis/rest/services/ChinaOnlineCommunity/MapServer";
        /**
         * 此类的实例使您可以使用预先生成的切片显示来自ArcGIS Map服务的数据。
         * 该服务可以托管在ArcGIS Online上的云中，也可以托管在ArcGIS Server的本地中。
         * ArcGIS切片图层使用预先生成的切片的缓存来创建地图，而不是动态生成地图图像。 与图层相同。 更改类型只是为了清楚起见，您会获得派生类型。
         * 您需要将此对象传递给所有ArcGIS平铺图层功能。 ArcGIS平铺图层类是从图层类派生的。
         */
        ArcGISTiledLayer arcGISTiledLayer = new ArcGISTiledLayer(theURLString);
        Basemap basemap = new Basemap(arcGISTiledLayer);
        ArcGISMap arcGISMap = new ArcGISMap(basemap);
        mMapView.setMap(arcGISMap);
    }

    private void startLocation() {
        /**
         * 管理当前位置在显示地图里的展示，包括当前位置的信息，符号，以及随地图的平移、旋转、缩放等进行自动变化。
         * 也就是有了这个类，不仅可以获取当前位置信息进行定位，也可以将位置信息展示出来。
         */
        LocationDisplay locationDisplay = mMapView.getLocationDisplay();
        //位置监听的自动扫描模式，当自己的位置信息发送改变的时候，地图上将会展现出来
        //模式一共有四种：
        //COMPASS_NAVIGATION 和NAVIGATION 分别最适用于步行导航和车载导航，用户的位置符号会固定显示在屏幕的某个点上，
        // 并且指向设备的顶部（也就是地图会随着用户移动而平移，随用户转弯而旋转）；
        //OFF 模式，用户位置符号会随位置变化而移动，但地图不会动；
        //RECENTER模式，当用户位置处于当前地图范围内时候，用户位置符号会随位置变化而移动，但地图不会动；当用户位置处于地图边缘时候，
        // 地图会自动平移是用户的当前位置重新居于显示地图中心。
        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
//        // 当我们执行LocationDisplay.startAsync()方法时候，会在地图上显示出我们当前位置
//        locationDisplay.startAsync();

        //改变符号样式
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        BitmapDrawable drawable = new BitmapDrawable(bitmap);
        PictureMarkerSymbol pictureMarkerSymbol = new PictureMarkerSymbol(drawable);
        pictureMarkerSymbol.loadAsync();
        pictureMarkerSymbol.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                //设置默认的符号
                locationDisplay.setDefaultSymbol(pictureMarkerSymbol);
                //隐藏位置信息
//                locationDisplay.setShowLocation(false);
                //隐藏符号缓存区域
                locationDisplay.setShowAccuracy(false);
                //隐藏位置更新动画
//                locationDisplay.setShowPingAnimation(false);
            }
        });
        locationDisplay.startAsync();

        //获取的点是基于当前地图坐标系的点
        Point point = locationDisplay.getMapLocation();
        Log.i("ztz", "Point" + point.toString());

        //获取基于GPS的位置信息
        LocationDataSource.Location location = locationDisplay.getLocation();
        //基于WGS84的经纬度坐标
        Point point1 = location.getPosition();
        if (point1 != null) {
            Log.i("ztz", "Point1" + point1.toString());
        }

        // 如果要在LocationDisplay里进行位置信息的自动监听，方法也很简单，只需要LocationDisplay.addLocationChangedListener即可
        locationDisplay.addLocationChangedListener(new LocationDisplay.LocationChangedListener() {
            @Override
            public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {
                LocationDataSource.Location location1 = locationChangedEvent.getLocation();
                Log.i("ztz", "onLocationChanged" + location1.getPosition().toString());
            }
        });
    }

    private void requestPermissionIfNecessary() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkAllPermission()) {
                if (mPermissionRequestCount < MAX_NUMBER_REQUEST_PERMISSIONS) {
                    mPermissionRequestCount += 1;
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.READ_SMS,
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_ACCESS_LOCATION);
                } else {
                    Toast.makeText(this, "缺失权限", Toast.LENGTH_LONG).show();
                }
            } else {
                startLocation();
            }
        } else {
            startLocation();
        }
    }

    private boolean checkAllPermission() {
        boolean hasPermission = true;
        for (String permission : permissions) {
            hasPermission = hasPermission && ContextCompat.checkSelfPermission(this, permission)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return hasPermission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_LOCATION: {
                if (checkAllPermission()) {
                    startLocation();
                } else {
                    showSettingDialog();
                }
            }
            break;
            default:
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("帮助");
        builder.setMessage("当前应用缺少权限。\n \n 请点击 \"设置\"-\"权限\"-打开所需权限。");
        builder.setNegativeButton("取消", (dialog, which) -> {

        });
        builder.setPositiveButton("设置", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + LocationArcGisActivity.this.getPackageName()));
            startActivity(intent);
        });
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.dispose();
    }
}
