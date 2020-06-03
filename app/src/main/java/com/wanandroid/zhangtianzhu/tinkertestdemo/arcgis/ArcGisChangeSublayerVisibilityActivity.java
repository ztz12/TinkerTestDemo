package com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISMapImageSublayer;
import com.esri.arcgisruntime.layers.SublayerList;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.wanandroid.zhangtianzhu.tinkertestdemo.R;
import com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis.adapter.DrawerLayoutAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态地图图层ArcGISMapImageLayer是通过访问动态地图服务MapService获取的，里面包含的是一个子图层集合SublayerList，
 * 通过这个子图层集合可以构造得到每个动态地图图层ArcGISMapImageSublayer。
 */
public class ArcGisChangeSublayerVisibilityActivity extends AppCompatActivity {
    private MapView mapView;
    //创建处理动态生成地图图像的ArcGISMapImageLayer
    private ArcGISMapImageLayer arcGISMapImageLayer;
    private String url = "http://sampleserver6.arcgisonline.com/arcgis/rest/services/SampleWorldCities/MapServer";

    private String url2 = "http://sampleserver6.arcgisonline.com/arcgis/rest/services/SampleWorldCities/MapServer/0";

    private DrawerLayout mDrawerLayout;

    private ListView mListView;

    //子图层集合
    private SublayerList sublayerList;
    private ActionBarDrawerToggle mDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arc_gis_change_sublayer_visibility);
        initMap();
    }

    private void initMap() {
        findView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("加载动态图层的子图层");

        //底图
        ArcGISMap map = new ArcGISMap(Basemap.Type.TOPOGRAPHIC, 48.354406, -99.998267, 2);

        //动态图层
        arcGISMapImageLayer = new ArcGISMapImageLayer(url);
        //添加层加载完成时调用的监听器。
        arcGISMapImageLayer.addDoneLoadingListener(() -> {
            //获取所有图层集合
            sublayerList = arcGISMapImageLayer.getSublayers();
            ArcGISMapImageSublayer mapImageSublayer = (ArcGISMapImageSublayer) sublayerList.get(0);
        });
        //设置动态图层透明度
        arcGISMapImageLayer.setOpacity(0.5f);
        //默认把所有子图层都显示出来
        map.getOperationalLayers().add(arcGISMapImageLayer);
        mapView.setMap(map);

        initDrawerLayout();
    }

    private void findView() {
        mapView = findViewById(R.id.sublayer_mapView);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mListView = findViewById(R.id.listview);

        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
    }

    private void initDrawerLayout() {
        setupDrawer();

        List<String> dataList = new ArrayList<>();
        dataList.add("Cities");
        dataList.add("Continents");
        dataList.add("World");

        DrawerLayoutAdapter drawerLayoutAdapter = new DrawerLayoutAdapter(this, dataList, R.layout.item);
        mListView.setAdapter(drawerLayoutAdapter);

        drawerLayoutAdapter.setCheckedChangeListener((isChecked, position) -> {
            if (isChecked) { //显示
                sublayerList.get(position).setVisible(true);
            } else { //不显示
                sublayerList.get(position).setVisible(false);
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return (mDrawerToggle.onOptionsItemSelected(item)) || super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.dispose();
    }
}
