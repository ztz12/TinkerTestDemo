package com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.DrawStatus;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedEvent;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedListener;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.wanandroid.zhangtianzhu.tinkertestdemo.R;

public class ArcGisDisplayDrawingStatusActivity extends AppCompatActivity {
    private MapView mapView;
    private ProgressBar progressBar;
    private final String service_feature_table_url = "http://sampleserver6.arcgisonline.com/arcgis/rest/services/DamageAssessment/FeatureServer/0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arc_gis_display_drawing_status);
        initMap();
    }

    private void initMap(){
        mapView = findViewById(R.id.draw_mapView);
        progressBar = findViewById(R.id.progressBar);
        ArcGISMap arcGISMap = new ArcGISMap(Basemap.createTopographic());
        //Envelope 表示由最小以及最大x坐标，以及最小最大y坐标形参的矩形区域，创建新的Envelope实例或使用EnvelopeBuilder，而不是更改现有Envelope的属性。
        Envelope targetExtent = new Envelope(-13639984.0, 4537387.0, -13606734.0, 4558866.0,
                SpatialReferences.getWebMercator());
        //是GeoView 可见区域与视图位置，这是用户在查看地图时看到的内容。 它可用于定义和控制视图的位置，范围，比例和旋转。
        // 它是一个不变的对象，用于在视图上设置位置。
        Viewpoint initViewpoint = new Viewpoint(targetExtent);
        arcGISMap.setInitialViewpoint(initViewpoint);
        //ServiceFeatureTable.FeatureRequestMode，它控制（1）是否在本地缓存要素（以便通过地图和场景层更快地访问），
        // 以及（2）是在本地缓存还是在服务器上执行查询。 您可以使用FeatureRequestMode检索或修改表的功能请求模式。
        ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(service_feature_table_url);
        //表示由要素组成的图层，其中要素包含几何与一组一致的字段，可以显示在地图上查询编辑与选择
//        每个FeatureLayer对应一个FeatureTable，该表传递给FeatureLayer构造函数。
        FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
        arcGISMap.getOperationalLayers().add(featureLayer);
        mapView.setMap(arcGISMap);

        //监听在MapView的DrawStatus更改时调用的DrawStatusChangedListener，当地图完成加载时候，或者在地图导航后完成加载切片和要素
        mapView.addDrawStatusChangedListener(drawStatusChangedEvent -> {
            //当地图加载时显示进度条
            if(drawStatusChangedEvent.getDrawStatus()== DrawStatus.IN_PROGRESS){
                progressBar.setVisibility(View.VISIBLE);
            }else if(drawStatusChangedEvent.getDrawStatus()==DrawStatus.COMPLETED){
                //当地图加载完成后，隐藏进度条
                progressBar.setVisibility(View.GONE);
            }
        });

        /**
         *   addLayerViewStateChangedListener 监听地图中任何图层视图状态发生改变时调用，例如图层进入或者超出可见范围，或者使其可见与不可见
         *         要了解整个地图的绘图状态更改，请改用该 addDrawStatusChangedListener(DrawStatusChangedListener)方法。
         *
         *         如果从UI线程添加该侦听器，则将在UI线程上调用此侦听器，否则无法保证在哪个线程上调用侦听器。
         *
         *  以下为官方案例
         */
//        final ArcGISMap mMap = new ArcGISMap(Basemap.createTopographic());
//        mMap.getOperationalLayers().add(tiledLayer);
//        mMap.getOperationalLayers().add(imageLayer);
//        mMap.getOperationalLayers().add(featureLayer);
//        mMapView.setMap(mMap);
//
//        // zoom to custom ViewPoint
//        mMapView.setViewpoint(new Viewpoint(new Point(-11e6, 45e5, SpatialReferences.getWebMercator()), MIN_SCALE));
//
//        // 监听地图中任何图层的视图状态发生更改时调用的LayerViewStateChangedListener，
//        // 例如，如果图层进入或超出可见比例范围，或者使其可见或不可见。
//        mMapView.addLayerViewStateChangedListener(new LayerViewStateChangedListener() {
//            @Override
//            public void layerViewStateChanged(LayerViewStateChangedEvent layerViewStateChangedEvent) {
//
//                // get the layer which changed it's state
//                Layer layer = layerViewStateChangedEvent.getLayer();
//
//                // get the View Status of the layer
//                // View status will be either of ACTIVE, ERROR, LOADING, NOT_VISIBLE, OUT_OF_SCALE, UNKNOWN
//                String viewStatus = layerViewStateChangedEvent.getLayerViewStatus().iterator().next().toString();
//
//                final int layerIndex = mMap.getOperationalLayers().indexOf(layer);
//
//                // finding and updating status of the layer
//                switch (layerIndex) {
//                    case TILED_LAYER:
//                        timeZoneTextView.setText(viewStatusString(viewStatus));
//                        break;
//                    case IMAGE_LAYER:
//                        worldCensusTextView.setText(viewStatusString(viewStatus));
//                        break;
//                    case FEATURE_LAYER:
//                        recreationTextView.setText(viewStatusString(viewStatus));
//                        break;
//                }
//
//            }
//        });
    }

//    /**
//     * The method looks up the view status of the layer and returns a string which is displayed
//     *
//     * @param status View Status of the layer
//     * @return String equivalent of the status
//     */
//    private String viewStatusString(String status) {
//
//        switch (status) {
//            case "ACTIVE":
//                return getApplication().getString(R.string.active);
//
//            case "ERROR":
//                return getApplication().getString(R.string.error);
//
//            case "LOADING":
//                return getApplication().getString(R.string.loading);
//
//            case "NOT_VISIBLE":
//                return getApplication().getString(R.string.notVisible);
//
//            case "OUT_OF_SCALE":
//                return getApplication().getString(R.string.outOfScale);
//
//        }
//
//        return getApplication().getString(R.string.unknown);
//
//    }

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
