package com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.ArcGISVectorTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.BackgroundGrid;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.wanandroid.zhangtianzhu.tinkertestdemo.R;

/**
 * 1.MapView作为屏幕展示容器，接收ArcGISMap。
 *
 * 2.ArcGISMap可由Web Map、MMPK文件直接填充也可由Basemap和OperationLayers组合填充。
 *
 * 3.绘制图层（GraphicsOverLay）依托于MapView展示。
 *
 * 4.基础底图（Basemap）可由在线的、离线的切片地图服务（ArcGISTiledLayer、ArcGISVectorTiledLayer）填充。
 *
 * 5.业务图层（OperationLayers）可由在线的要素服务（FeatureLayer）、离线的本地文件（MMPK、.geodatabase）填充。
 */
public class ArcGisActivity extends AppCompatActivity {
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arc_gis);
        initMapView();
    }

    private void initMapView(){
        mapView = findViewById(R.id.mapView);
        /**
         * MapView 屏幕展示容器 包含 ArcGISMap与GraphicOverlay,而 ArcGISMap 包含 BaseMap 与OperationalLayers
         * ArcGISMap 地图内容 图层数据加载 管理 删除等操作交给此类，包含一个底图与多个业务图层，底图永远在最下面
         * ArcGISMap.getOperationalLayers的方法获取到一个包含当前加载图层的集合类LayerList，再通过这个类进行控制。另外整个地图的空间参考将由ArcGISMap类加载的第一个图层来定，也就是说这个空间参考不一定是根据底图来确定。
         * BaeMap 基础地图，也就是底图，如果需要切换底图，重新赋值一个底图即可
         * OperationalLayers 业务图层
         * GraphicOverlay 绘制图层
         * * 参数1：BaseMap.Type：底图类型
         *          * 参数2：形成地图中心点的初始视点的纬度
         *          * 参数3：形成地图中心点的初始视点的经度
         *          * 参数4：转换为初始Viewpoint的比例的详细程度。0是缩小最多的级别。只能设置不能获取
         *
         */
        ArcGISMap arcGisMap = new ArcGISMap(Basemap.Type.TOPOGRAPHIC,30.671475859566514,
                104.07567785156248,16);
        //设置地图背景颜色
        BackgroundGrid backgroundGrid = new BackgroundGrid();
//        //设置背景颜色
//        backgroundGrid.setColor(Color.GREEN);
//        //设置背景格子线颜色
//        backgroundGrid.setGridLineColor(Color.BLUE);
//        mapView.setBackgroundGrid(backgroundGrid);
//        mapView.setMap(arcGisMap);

        ArcGISMap arcGISMap1 = new ArcGISMap(Basemap.createStreets());
        ArcGISMap arcGISMap2 = new ArcGISMap(Basemap.createImagery());
        ArcGISMap arcGISMap3 = new ArcGISMap(Basemap.createStreetsVector());

        ArcGISMap arcGISMap4 = new ArcGISMap(Basemap.createTopographic());

        //初始化可见区域
        Envelope targetExtent = new Envelope(-13639984.0, 4537387.0, -13606734.0, 4558866.0,
                SpatialReferences.getWebMercator());
        Viewpoint initViewpoint = new Viewpoint(targetExtent);
        arcGISMap4.setInitialViewpoint(initViewpoint);

//        mapView.setMap(arcGISMap4);
//        String url = "http://services.arcgisonline.com/arcgis/rest/services/World_Topo_Map/MapServer";
//        //ArcGISTiledLayer 切片数据主要用来做底图展示，包含渲染后的地图与地图空间参考信息，离线数据格式为tpk
//        ArcGISTiledLayer arcGISTiledLayer = new ArcGISTiledLayer(url);
//        Basemap basemap = new Basemap(arcGISTiledLayer);
//        ArcGISMap arcGISMap = new ArcGISMap(basemap);
//        mapView.setMap(arcGISMap);

        //ArcGISVectorTiledLayer 矢量切片数据，除了提供展示，还提供查询功能
        String mid_century_url = "https://www.arcgis.com/home/item.html?id=7675d44bb1e4428aa2c30a9b68f97822";
        ArcGISVectorTiledLayer arcGISVectorTiledLayer = new ArcGISVectorTiledLayer(mid_century_url);
        Basemap basemap = new Basemap(arcGISVectorTiledLayer);
        ArcGISMap map = new ArcGISMap(basemap);
        Viewpoint vp = new Viewpoint(47.606726, -122.335564, 72223.819286);
        map.setInitialViewpoint(vp);
        mapView.setMap(map);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.dispose();
    }
}
