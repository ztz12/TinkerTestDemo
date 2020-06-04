package com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.wanandroid.zhangtianzhu.tinkertestdemo.R;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 查询图层
 * MapView（ GeoView ） - identifyLayerAsync方法：查询指定图层
 * MapView（ GeoView ） - identifyLayersAsync方法：查询显示的所有图层
 * GeodatabaseFeatureTable（ FeatureTable ） - queryFeaturesAsync方法
 * ServiceFeatureTable （ FeatureTable ） - queryFeaturesAsync方法
 * FeatureLayer - selectFeaturesAsync方法
 */
public class IdentifyLayerAsync extends AppCompatActivity {
    private String world_cities = " https://sampleserver6.arcgisonline.com/arcgis/rest/services/SampleWorldCities/MapServer";

    private String damage_assessment = " https://sampleserver6.arcgisonline.com/arcgis/rest/services/DamageAssessment/FeatureServer/0";
    private String sample_service_url = "https://sampleserver6.arcgisonline.com/arcgis/rest/services/USA/MapServer/2";

    private MapView mMapView;
    /**
     * ArcGISMapImageLayer是从包含一个或多个子图层的动态地图服务创建的。
     * ArcGISMapImageLayer调用的地图服务会根据源数据动态渲染地图图像，并按照服务中指定的方式应用渲染信息。 这些服务还可以支持修改图层渲染，
     * 从而允许在运行时更改图层的外观。 所做的任何更改都是临时的，不会持久化回服务。
     * 地图服务的URL传递到构造函数中。
     */
    private ArcGISMapImageLayer mapImageLayer;
    private FeatureLayer featureLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_layer_async);
        mMapView = findViewById(R.id.identify_mapView);
//        initMap();
        initFeatureMap();
    }

    private void initMap() {
        ArcGISMap map = new ArcGISMap(Basemap.createTopographic());
        mapImageLayer = new ArcGISMapImageLayer(world_cities);
        mapImageLayer.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                mapImageLayer.getSubLayerContents().get(1).setVisible(false);
                mapImageLayer.getSubLayerContents().get(2).setVisible(false);
            }
        });
        map.getOperationalLayers().add(mapImageLayer);
        FeatureTable featureTable = new ServiceFeatureTable(damage_assessment);
        featureLayer = new FeatureLayer(featureTable);
        map.getOperationalLayers().add(featureLayer);
        map.setInitialViewpoint(new Viewpoint(new Point(-10977012.785807, 4514257.550369, SpatialReference.create(3857)), 68015210));
        mMapView.setMap(map);

        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                android.graphics.Point screenPoint = new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY()));
                //查询FeatureLayer点击范围内所有地图要素
                //identifyLayerAsync ：参数：要查询图层，屏幕指向识别 ，以screenPoint为中心的圆的密度无关像素（dp）中的半径，用于识别GeoElements。值0将仅识别screenPoint处物理像素处的GeoElements。允许的最大值为100dp
                // 参数：如果包含弹出窗口，但识别结果中没有地理元素，则为true; false表示包含地理元素和弹出窗口
                // 参数：要返回的地理元素和/或弹出窗口的最大数量; 必须大于零，但-1可用于表示无限结果
                ListenableFuture<IdentifyLayerResult> identifyLayerResultListenableFuture = mMapView.
                        identifyLayerAsync(featureLayer, screenPoint, 12, false, 10);
                identifyLayerResultListenableFuture.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        IdentifyLayerResult result = null;
                        try {
                            result = identifyLayerResultListenableFuture.get();
                            String name = result.getLayerContent().getName();
                            Toast.makeText(IdentifyLayerAsync.this, name, Toast.LENGTH_SHORT).show();
                            //GeoElement 同时用于几何表与属性表的实体类集合
                            List<GeoElement> geoElements = result.getElements();
                            for (GeoElement geoElement : geoElements) {
                                Map<String, Object> attributes = geoElement.getAttributes();
                                for (String key : attributes.keySet()) {
                                    Log.i("ztz", key + String.valueOf(attributes.get(key)));
                                }
                                //Geometry 提供代表不同类型几何图形（例如点，线或折线）的类，并提供诸如缓冲，简化和计算面积和长度之类的几何计算的类。
                                Geometry geometry = geoElement.getGeometry();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                });
                return super.onSingleTapConfirmed(e);
            }
        });
    }

    /**
     * 空间查询 - FeatureLayer
     */
    private void initFeatureMap() {
        ArcGISMap arcGISMap = new ArcGISMap(Basemap.createTopographic());
        mMapView.setMap(arcGISMap);
        final ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(sample_service_url);
        final FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);

        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLACK, 1);
        SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.YELLOW, lineSymbol);
        featureLayer.setRenderer(new SimpleRenderer(fillSymbol));
        featureLayer.setSelectionColor(Color.RED);
        featureLayer.setSelectionWidth(5);

        arcGISMap.getOperationalLayers().add(featureLayer);
        mMapView.setViewpointCenterAsync(new Point(-11000000, 5000000, SpatialReferences.getWebMercator()), 100000000);

        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Point clickPoint = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                featureLayer.clearSelection();

                int torleance = 1;
                double mapTorlance = torleance * mMapView.getUnitsPerDensityIndependentPixel();
                SpatialReference spatialReference = mMapView.getSpatialReference();
                Envelope envelope = new Envelope(clickPoint.getX() - mapTorlance, clickPoint.getX() - mapTorlance,
                        clickPoint.getY() + mapTorlance, clickPoint.getY() + mapTorlance, spatialReference);
                //空间查询参数，表示查询输入的参数
                QueryParameters queryParameters = new QueryParameters();
                queryParameters.setGeometry(envelope);
                queryParameters.setSpatialRelationship(QueryParameters.SpatialRelationship.WITHIN);
                //ListenableFuture：一种特殊的Future 允许在异步计算完成后将监听设置为运行
                //FeatureQueryResult：表示对FutureTable的查询结果，该迭代器遍历找到的功能
                ListenableFuture<FeatureQueryResult> featureQueryResultListenableFuture = featureLayer.selectFeaturesAsync(queryParameters, FeatureLayer.SelectionMode.NEW);
                featureQueryResultListenableFuture.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FeatureQueryResult queryResult = featureQueryResultListenableFuture.get();
                            Iterator<Feature> iterator = queryResult.iterator();
                            while (iterator.hasNext()) {
                                //Feature 地图上真实对象的表示。 要素保留在数据存储区（例如数据库或服务）或地图的FeatureTable中。
                                // 同一数据存储区或要素层中的要素具有共同的属性架构。
                                Feature feature = iterator.next();
                                featureLayer.selectFeature(feature);
                                Geometry geometry = feature.getGeometry();
                                Envelope envelope1 = geometry.getExtent();
                                mMapView.setViewpointGeometryAsync(envelope1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return super.onSingleTapConfirmed(e);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.dispose();
    }
}
