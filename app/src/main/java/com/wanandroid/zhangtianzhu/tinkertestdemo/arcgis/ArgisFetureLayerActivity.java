package com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.wanandroid.zhangtianzhu.tinkertestdemo.R;

/**
 * FeatureLayer 表示由要素显示的图层，要素包括几何或者一组一致的字段，可以显示在地图查询，编辑与选择上面
 * FeatureTable 表示要素表，FeatureTable提供queryFeaturesAsync(QueryParameters)空间和属性查询，以及添加，更新和删除功能的方法。
 * 使用构造函数创建FeatureTable子类的实例。FeatureTable用于创建FeatureLayer。FeatureLayer添加到地图后，将在地图中显示表格的要素。
 */
public class ArgisFetureLayerActivity extends AppCompatActivity {
    private final String sample_service_url = "http://sampleserver6.arcgisonline.com/arcgis/rest/services/Energy/Geology/FeatureServer/9";
    private MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_argis_feture_layer);
        initMapView();
    }

    private void initMapView(){
        mMapView = findViewById(R.id.feature_mapView);
        ArcGISMap map = new ArcGISMap(Basemap.createTerrainWithLabels());
        //scale ：比例尺-比例尺在地图上的距离与地面上的相应距离之间的比率
        map.setInitialViewpoint(new Viewpoint(new Point(-13176752, 4090404, SpatialReferences.getWebMercator()), 500000));
        ServiceFeatureTable table = new ServiceFeatureTable(sample_service_url);
        FeatureLayer featureLayer = new FeatureLayer(table);
        map.getOperationalLayers().add(featureLayer);
        mMapView.setMap(map);

    }
}
