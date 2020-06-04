package com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.wanandroid.zhangtianzhu.tinkertestdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义一个标注内容显示在地图上面
 */
public class CalloutActivity extends AppCompatActivity {
    private MapView calloutMapView;
    private List<String> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callout);
        initMap();
    }

    private void initMap() {
        calloutMapView = findViewById(R.id.callout_mapView);
        final ArcGISMap mMap = new ArcGISMap(Basemap.Type.TOPOGRAPHIC, 34.056295, -117.195800, 10);
        calloutMapView.setMap(mMap);
        initData();
        calloutMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, calloutMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Point mapPoint = calloutMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                showCallOutView(mapPoint);
                return super.onSingleTapConfirmed(e);
            }
        });
    }

    private void showCallOutView(Point point) {
        View calloutView = LayoutInflater.from(this).inflate(R.layout.callout_view, null);
        ListView lv = calloutView.findViewById(R.id.callout_listView);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, dataList);
        lv.setAdapter(arrayAdapter);

        //在MapView上绘制标注并管理其行为。 标注会显示一个包含文本和/或其他内容的Android视图。 它的领导者指向标注所指的位置。
        // 标注的主体是一个带有弯曲拐角的矩形区域，其中包含应用程序提供的内容视图。 在整个标注周围绘制了一条细边框。
        //应用程序必须通过调用MapView.getCallout（）从MapView获取一个Callout对象。 对于特定的MapView，这始终返回相同的对象。
        // 然后，有四种方法可以设置内容和位置并显示标注：
        Callout callout = calloutMapView.getCallout();
        //设置callout 的样式
        Callout.Style style = new Callout.Style(this);
        style.setMaxWidth(400); //设置最大宽度
        style.setMaxHeight(300);  //设置最大高度
        style.setMinWidth(200);  //设置最小宽度
        style.setMinHeight(100);  //设置最小高度
        style.setBorderWidth(2); //设置边框宽度
        style.setBorderColor(Color.BLUE); //设置边框颜色
        style.setBackgroundColor(Color.WHITE); //设置背景颜色
        style.setCornerRadius(8); //设置圆角半径
        //style.setLeaderLength(50); //设置指示性长度
        //style.setLeaderWidth(5); //设置指示性宽度
        style.setLeaderPosition(Callout.Style.LeaderPosition.LOWER_MIDDLE); //设置指示性位置
        callout.setStyle(style);
        callout.setContent(calloutView);
        //通过地图中指定Point来设置callout位置
        callout.setLocation(point);
        callout.show();

        calloutMapView.setViewpointCenterAsync(point);
    }

    private void initData() {
        dataList = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            dataList.add("条目:" + i);
        }
    }
}
