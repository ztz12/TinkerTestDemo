package com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis;

import android.graphics.Color;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.BackgroundGrid;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SketchCreationMode;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.wanandroid.zhangtianzhu.tinkertestdemo.BaseActivity;
import com.wanandroid.zhangtianzhu.tinkertestdemo.R;
import com.wanandroid.zhangtianzhu.tinkertestdemo.utils.AssistStatic;
import com.wanandroid.zhangtianzhu.tinkertestdemo.utils.FeatureUtils;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

/**
 * 1.MapView作为屏幕展示容器，接收ArcGISMap。
 * <p>
 * 2.ArcGISMap可由Web Map、MMPK文件直接填充也可由Basemap和OperationLayers组合填充。
 * <p>
 * 3.绘制图层（GraphicsOverLay）依托于MapView展示。
 * <p>
 * 4.基础底图（Basemap）可由在线的、离线的切片地图服务（ArcGISTiledLayer、ArcGISVectorTiledLayer）填充。
 * <p>
 * 5.业务图层（OperationLayers）可由在线的要素服务（FeatureLayer）、离线的本地文件（MMPK、.geodatabase）填充。
 */
public class ArcGisActivity extends BaseActivity implements View.OnClickListener {
    private ArcGISMap mainArcGISMap;

    private FeatureLayer mainShapefileLayer;

    //shp文件
    private String shpPath;

    private MapView mMapView;

    private SimpleMarkerSymbol mPointSymbol;
    private SimpleLineSymbol mLineSymbol;
    private SimpleFillSymbol mFillSymbol;
    private SimpleFillSymbol highlighFillSymbol;
    private SketchEditor mSketchEditor;
    private GraphicsOverlay mGraphicsOverlay;
    private GraphicsOverlay highLightLayer;


    @Override
    public void initViews() {
        mMapView = findViewById(R.id.mapView);
        findViewById(R.id.btn_point).setOnClickListener(this);
        findViewById(R.id.btn_multi).setOnClickListener(this);
        findViewById(R.id.btn_polyline).setOnClickListener(this);
        findViewById(R.id.btn_polygon).setOnClickListener(this);
        findViewById(R.id.btn_freehandleLine).setOnClickListener(this);
        findViewById(R.id.btn_freePolygon).setOnClickListener(this);
        findViewById(R.id.btn_undo).setOnClickListener(this);
        findViewById(R.id.btn_redo).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_arc_gis;
    }

    @Override
    public void initData() {
        mainArcGISMap = new ArcGISMap(Basemap.Type.OCEANS, 30.671475859566514,
                104.07567785156248, 11);
        mMapView.setMap(mainArcGISMap);
        mGraphicsOverlay = new GraphicsOverlay();
        highLightLayer = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(highLightLayer);
        mMapView.getGraphicsOverlays().add(mGraphicsOverlay);

        // 创建一个新的草图编辑器并将其添加到地图视图中
        mSketchEditor = new SketchEditor();
        mMapView.setSketchEditor(mSketchEditor);

        mPointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, 0xFFFF0000, 20);
        mLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF8800, 4);
        mFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.CROSS, 0x40FFA9A9, mLineSymbol);
        //define the fill symbol and outline
        SimpleLineSymbol outlineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, Color.RED, 0.1f);
        highlighFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.DIAGONAL_CROSS, Color.RED, outlineSymbol);
        new Thread(new Runnable() {
            @Override
            public void run() {
                showShapeFile();
            }
        }).start();
        queryByQueryFeaturesAsync();
    }

    private void showShapeFile() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            shpPath = Environment.getExternalStorageDirectory() + "/" + "com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis" + "/resource/";
        } else {
            shpPath = "/data/data/com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis/resource";
        }
        File file = new File(shpPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        showShapeZRDFile();
//        String mid_century_url = "https://www.arcgis.com/home/item.html?id=7675d44bb1e4428aa2c30a9b68f97822";
//        ArcGISVectorTiledLayer arcGISVectorTiledLayer = new ArcGISVectorTiledLayer(mid_century_url);
//        Basemap basemap = new Basemap(arcGISVectorTiledLayer);
//        mainArcGISMap = new ArcGISMap(basemap);
        String shapePath = shpPath + "shp/dltb.shp";
        String shapePathTwo = shpPath + "房产shp/zrz.shp";
        final ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(shapePathTwo);
        shapefileFeatureTable.loadAsync();
        shapefileFeatureTable.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                GeometryType gt = shapefileFeatureTable.getGeometryType();
                String name = shapefileFeatureTable.getTableName();
                mainShapefileLayer = new FeatureLayer(shapefileFeatureTable);
                if (mainShapefileLayer.getFullExtent() != null) {
                    mMapView.setViewpointGeometryAsync(mainShapefileLayer.getFullExtent());
                } else {
                    mainShapefileLayer.addDoneLoadingListener(new Runnable() {
                        @Override
                        public void run() {
                            mMapView.setViewpointGeometryAsync(mainShapefileLayer.getFullExtent());
                        }
                    });
                }
                mainArcGISMap.getOperationalLayers().add(mainShapefileLayer);
            }
        });


        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 1.0f);
        SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.YELLOW, lineSymbol);
        SimpleRenderer renderer = new SimpleRenderer(fillSymbol);
        mainShapefileLayer.setRenderer(renderer);
        mainShapefileLayer.setSelectionColor(Color.GREEN);
        mainShapefileLayer.setSelectionWidth(5);
    }

    private void showShapeZRDFile() {
        String shapeFilePathThree = shpPath + "房产shp/zd.shp";
        final ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(shapeFilePathThree);
        shapefileFeatureTable.loadAsync();
        shapefileFeatureTable.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                GeometryType gt = shapefileFeatureTable.getGeometryType();
                String name = shapefileFeatureTable.getTableName();
                mainShapefileLayer = new FeatureLayer(shapefileFeatureTable);
                if (mainShapefileLayer.getFullExtent() != null) {
                    mMapView.setViewpointGeometryAsync(mainShapefileLayer.getFullExtent());
                } else {
                    mainShapefileLayer.addDoneLoadingListener(new Runnable() {
                        @Override
                        public void run() {
                            mMapView.setViewpointGeometryAsync(mainShapefileLayer.getFullExtent());
                        }
                    });
                }
                mainArcGISMap.getOperationalLayers().add(mainShapefileLayer);
            }
        });


        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 1.0f);
        SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.WHITE, lineSymbol);
        SimpleRenderer renderer = new SimpleRenderer(fillSymbol);
        mainShapefileLayer.setRenderer(renderer);
        mainShapefileLayer.setSelectionColor(Color.GREEN);
        mainShapefileLayer.setSelectionWidth(5);
    }

    /**
     * 查询shp方式2:queryFeaturesAsync
     */
    public void queryByQueryFeaturesAsync() {
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                selectFeatureByClick(e, mainShapefileLayer);
                return super.onSingleTapConfirmed(e);
            }
        });
    }

    private void selectFeatureByClick(final MotionEvent e, FeatureLayer shpLayer) {

        Point clickPoint = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setGeometry(clickPoint);
        queryParameters.setSpatialRelationship(QueryParameters.SpatialRelationship.WITHIN);
        queryParameters.setReturnGeometry(true);

        final ListenableFuture<FeatureQueryResult> future = shpLayer.getFeatureTable().queryFeaturesAsync(queryParameters);

        future.addDoneListener(new Runnable() {
            @Override
            public void run() {

                try {

                    FeatureQueryResult result = future.get();

                    Iterator<Feature> iterator = result.iterator();

                    while (iterator.hasNext()) {

                        Feature feature = iterator.next();
                        Map<String, Object> attributes = feature.getAttributes();
                        for (String key : attributes.keySet()) {

                        }
                        String area = FeatureUtils.getFeatureArea(feature);
                        highlightFeature(feature);
                        //只取选中的第一个
                        break;
                    }

                } catch (Exception e) {

                    e.printStackTrace();

                }
            }
        });
    }


    public void highlightFeature(Feature feature) {

        Graphic graphic = new Graphic(feature.getGeometry(), highlighFillSymbol);

        highLightLayer.getGraphics().clear();

        highLightLayer.getGraphics().add(graphic);
    }

    /**
     * 如果草图无效，则调用。向用户报告草图无效的原因。
     */
    private void reportNotValid() {
        String validIf;
        if (mSketchEditor.getSketchCreationMode() == SketchCreationMode.POINT) {
            validIf = "Point only valid if it contains an x & y coordinate.";
        } else if (mSketchEditor.getSketchCreationMode() == SketchCreationMode.MULTIPOINT) {
            validIf = "Multipoint only valid if it contains at least one vertex.";
        } else if (mSketchEditor.getSketchCreationMode() == SketchCreationMode.POLYLINE
                || mSketchEditor.getSketchCreationMode() == SketchCreationMode.FREEHAND_LINE) {
            validIf = "Polyline only valid if it contains at least one part of 2 or more vertices.";
        } else if (mSketchEditor.getSketchCreationMode() == SketchCreationMode.POLYGON
                || mSketchEditor.getSketchCreationMode() == SketchCreationMode.FREEHAND_POLYGON) {
            validIf = "Polygon only valid if it contains at least one part of 3 or more vertices which form a closed ring.";
        } else {
            validIf = "No sketch creation mode selected.";
        }
        String report = "Sketch geometry invalid:\n" + validIf;
        AssistStatic.showToast(ArcGisActivity.this, report);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_point:
                createModePoint();
                break;
            case R.id.btn_multi:
                createModeMultipoint();
                break;
            case R.id.btn_polyline:
                createModePolyline();
                break;
            case R.id.btn_polygon:
                createModePolygon();
                break;
            case R.id.btn_freehandleLine:
                createModeFreehandLine();
                break;
            case R.id.btn_freePolygon:
                createModeFreehandPolygon();
                break;
            case R.id.btn_undo:
                undo();
                break;
            case R.id.btn_redo:
                redo();
                break;
            case R.id.btn_stop:
                stop();
                break;
            default:
                break;
        }
    }

    /**
     * 单击point按钮时，重置其他按钮，显示选中的point按钮和开始点绘图模式。
     */
    private void createModePoint() {
        mSketchEditor.start(SketchCreationMode.POINT);
    }

    /**
     * 单击multipoint按钮时，重置其他按钮，显示选中的multipoint按钮，然后开始多点绘图模式。
     */
    private void createModeMultipoint() {
        mSketchEditor.start(SketchCreationMode.MULTIPOINT);
    }

    /**
     * 单击折线按钮时，重置其他按钮，显示选中的折线按钮，然后开始折线图模式。
     */
    private void createModePolyline() {
        mSketchEditor.start(SketchCreationMode.POLYLINE);

    }

    /**
     * 当单击多边形按钮时，重置其他按钮，显示选中的多边形按钮，并启动多边形绘图模式。
     */
    private void createModePolygon() {
        mSketchEditor.start(SketchCreationMode.POLYGON);
    }

    /**
     * 单击FREEHAND_LINE按钮时，重置其他按钮，显示选定的FREEHAND_LINE按钮，和开始徒手画线模式。
     */
    private void createModeFreehandLine() {
        mSketchEditor.start(SketchCreationMode.FREEHAND_LINE);
    }

    /**
     * 单击FREEHAND_POLYGON按钮时，重置其他按钮，显示选定的FREEHAND_POLYGON按钮，并启用徒手绘制多边形模式。
     */
    private void createModeFreehandPolygon() {
        mSketchEditor.start(SketchCreationMode.FREEHAND_POLYGON);
    }

    /**
     * 当单击undo按钮时，撤消SketchEditor上的最后一个事件。
     */
    private void undo() {
        if (mSketchEditor.canUndo()) {
            mSketchEditor.undo();
        }

        if (mGraphicsOverlay.getGraphics().size() > 0) {
            mGraphicsOverlay.getGraphics().clear();
        }
    }

    /**
     * 当单击redo按钮时，在SketchEditor上重做最后一个未完成的事件。
     */
    private void redo() {
        if (mSketchEditor.canRedo()) {
            mSketchEditor.redo();
        }
    }

    /**
     * 检查草图是否有效，有效则从草图中获取几何图形，添加到覆盖层中
     */
    private void stop() {
        //无效报告原因
        if (!mSketchEditor.isSketchValid()) {
            reportNotValid();
            mSketchEditor.stop();
            return;
        }

        //从草图中获取编辑的几何图形
        Geometry geometry = mSketchEditor.getGeometry();
        mSketchEditor.stop();

        if (geometry != null) {
            //从草图编辑器中创建几何图形
            Graphic graphic = new Graphic(geometry);
            // 根据几何类型分配符号
            if (graphic.getGeometry().getGeometryType() == GeometryType.POLYGON) {
                graphic.setSymbol(mFillSymbol);
            } else if (graphic.getGeometry().getGeometryType() == GeometryType.POLYLINE) {
                graphic.setSymbol(mLineSymbol);
            } else if (graphic.getGeometry().getGeometryType() == GeometryType.POINT ||
                    graphic.getGeometry().getGeometryType() == GeometryType.MULTIPOINT) {
                graphic.setSymbol(mPointSymbol);
            }

            //将图形添加到图形覆盖层
            mGraphicsOverlay.getGraphics().add(graphic);
        }

    }


    private void initMapView() {
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
        ArcGISMap arcGisMap = new ArcGISMap(Basemap.Type.TOPOGRAPHIC, 30.671475859566514,
                104.07567785156248, 16);
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
//        String mid_century_url = "https://www.arcgis.com/home/item.html?id=7675d44bb1e4428aa2c30a9b68f97822";
//        ArcGISVectorTiledLayer arcGISVectorTiledLayer = new ArcGISVectorTiledLayer(mid_century_url);
//        Basemap basemap = new Basemap(arcGISVectorTiledLayer);
//        ArcGISMap map = new ArcGISMap(basemap);
//        Viewpoint vp = new Viewpoint(47.606726, -122.335564, 72223.819286);
//        map.setInitialViewpoint(vp);
//        mapView.setMap(map);

        String url = "http://map.geoq.cn/arcgis/rest/services/ChinaOnlineCommunity/MapServer";
        ArcGISTiledLayer arcGISTiledLayer = new ArcGISTiledLayer(url);
        Basemap basemap = new Basemap(arcGISTiledLayer);
        ArcGISMap arcGISMap = new ArcGISMap(basemap);
        mMapView.setMap(arcGISMap);

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
