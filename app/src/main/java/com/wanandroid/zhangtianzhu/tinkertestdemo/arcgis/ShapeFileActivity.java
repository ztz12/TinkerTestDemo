package com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.data.TileCache;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SketchCreationMode;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.mapping.view.SketchStyle;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.wanandroid.zhangtianzhu.tinkertestdemo.R;
import com.wanandroid.zhangtianzhu.tinkertestdemo.utils.AssistStatic;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * ShapeFile 是一种存储地理要素的几何位置和属性信息的非拓扑简单格式，shapeFile中的地理要素可以通过点、线、面区域来表示
 * 加载shap文件的时候，如果数据量过大，就会加载缓慢
 * shp文件 必须有least three files (.shp, .shx, .dbf) 如果有prj文件那就更好。位置准确。
 * 在安卓端加载Shapefile文件的关键是ShapefileFeatureTable（com.esri.arcgisruntime.data.ShapefileFeatureTable）。
 * 相比于.geodatabase文件，Shapefile文件的缺点在于只是单图层，且没有符号化，当然可以通过移动端的可视化API进行处理。
 */
public class ShapeFileActivity extends AppCompatActivity implements View.OnClickListener {
    private ArcGISMap arcGISMap;
    private FeatureLayer mainShapeFileLayer;
    //草图编辑器
    private SketchEditor mainSketchEditor;
    private SketchStyle mainSketchStyle;
    //底图
    private String url = Environment.getExternalStorageDirectory().getAbsolutePath() + "/测试数据/bxbxbx.tpk";

    //shp文件
    private String shpPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/测试数据/gdtbt.shp";

    private MapView mMapView;
    private boolean isSelect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shape_file);
        findViews();
        showShapeFile();
    }

    private void findViews() {
        mMapView = findViewById(R.id.mapView);
        Button drawButton = findViewById(R.id.drawButton);
        Button saveButton = findViewById(R.id.saveButton);
        Button selectButton = findViewById(R.id.selectButton);
        Button deleteButton = findViewById(R.id.deleteButton);

        drawButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        selectButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.drawButton: //绘制
                isSelect = false;
                mainShapeFileLayer.clearSelection();
                mainSketchEditor.stop();
                mainSketchEditor.start(SketchCreationMode.POLYGON);
                break;
            case R.id.saveButton: //把绘制的图形添加到shp
                saveDraw();
                break;
            case R.id.selectButton: //选择要素
                isSelect = true;
                mainSketchEditor.stop();
                break;
            case R.id.deleteButton: //删除要素
                final ListenableFuture<FeatureQueryResult> selectResult = mainShapeFileLayer.getSelectedFeaturesAsync();
                selectResult.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mainShapeFileLayer.getFeatureTable().deleteFeaturesAsync(selectResult.get());
                        } catch (Exception e) {
                            e.getCause();
                        }
                    }
                });
                break;
        }
    }

    private void showShapeFile() {
        //包含用于从ArcGIS服务到本地设备的平铺地图图层下载并同步平铺缓存的类。 帮助提供离线映射工作流程。
        TileCache cache = new TileCache(url);
        ArcGISTiledLayer arcGISTiledLayer = new ArcGISTiledLayer(cache);
        Basemap basemap = new Basemap(arcGISTiledLayer);
        arcGISMap = new ArcGISMap(basemap);
        mMapView.setMap(arcGISMap);
        File file = new File(shpPath);
        if (!file.exists()) {
            AssistStatic.showToast(this, "文件不存在");
            return;
        }

        //shape 文件加载方式
        ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(shpPath);
        shapefileFeatureTable.loadAsync();
        shapefileFeatureTable.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                GeometryType type = shapefileFeatureTable.getGeometryType();
                String name = shapefileFeatureTable.getTableName();
                mainShapeFileLayer = new FeatureLayer(shapefileFeatureTable);
                //获取此图层的完整范围，及包含所有层数的完整范围
                if (mainShapeFileLayer.getFullExtent() != null) {
                    mMapView.setViewpointGeometryAsync(mainShapeFileLayer.getFullExtent());
                } else {
                    mainShapeFileLayer.addDoneLoadingListener(new Runnable() {
                        @Override
                        public void run() {
                            mMapView.setViewpointGeometryAsync(mainShapeFileLayer.getFullExtent());
                        }
                    });
                }
                arcGISMap.getOperationalLayers().add(mainShapeFileLayer);
                startDrawing();
            }
        });

        SimpleLineSymbol simpleLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 1.0f);
        SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.YELLOW, simpleLineSymbol);
        SimpleRenderer renderer = new SimpleRenderer(simpleFillSymbol);
        mainShapeFileLayer.setRenderer(renderer);
        //设置选中要素的颜色
        mainShapeFileLayer.setSelectionColor(Color.BLUE);
        //设置选中要素的边缘宽度
        mainShapeFileLayer.setSelectionWidth(5);
    }

    private void startDrawing() {
        try {
            mainSketchEditor = new SketchEditor();
            mainSketchStyle = new SketchStyle();
            mainSketchEditor.setSketchStyle(mainSketchStyle);
            mMapView.setSketchEditor(mainSketchEditor);

            //查询shape文件方式
            mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    if (isSelect) {
                        Point clickPoint = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                        int tolance = 1;
                        double mapTolance = tolance * mMapView.getUnitsPerDensityIndependentPixel();
                        Envelope envelope = new Envelope(clickPoint.getX() - mapTolance, clickPoint.getY() - mapTolance, clickPoint.getX() + mapTolance, clickPoint.getY() + mapTolance, mMapView.getSpatialReference());
                        QueryParameters queryParameters = new QueryParameters();
                        queryParameters.setGeometry(envelope);
                        queryParameters.setSpatialRelationship(QueryParameters.SpatialRelationship.WITHIN);

                        //进行空间查询
                        ListenableFuture<FeatureQueryResult> listenableFuture = mainShapeFileLayer.selectFeaturesAsync(queryParameters, FeatureLayer.SelectionMode.NEW);
                        listenableFuture.addDoneListener(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    FeatureQueryResult queryResult = listenableFuture.get();
                                    Iterator<Feature> iterator = queryResult.iterator();
                                    while (iterator.hasNext()) {
                                        Feature feature = iterator.next();
                                        //高亮显示选择区域
                                        mainShapeFileLayer.selectFeature(feature);
                                        Geometry geometry = feature.getGeometry();
                                        mMapView.setViewpointGeometryAsync(geometry.getExtent());
                                    }
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });
                    }
                    return super.onSingleTapConfirmed(e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveDraw() {
        if (mainSketchEditor.getGeometry() != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("NAME", "自己画的省份");
            Feature featureLayer = mainShapeFileLayer.getFeatureTable().createFeature(map, mainSketchEditor.getGeometry());
            ListenableFuture<Void> addFeatureFuture = mainShapeFileLayer.getFeatureTable().addFeatureAsync(featureLayer);
            mainSketchEditor.stop();
        }
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
