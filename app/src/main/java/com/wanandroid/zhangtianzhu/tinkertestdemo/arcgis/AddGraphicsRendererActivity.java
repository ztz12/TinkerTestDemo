package com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PolygonBuilder;
import com.esri.arcgisruntime.geometry.PolylineBuilder;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer;
import com.wanandroid.zhangtianzhu.tinkertestdemo.R;

import java.util.ArrayList;
import java.util.List;

public class AddGraphicsRendererActivity extends AppCompatActivity {
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_graphics_renderer);
        mapView = findViewById(R.id.render_mapView);
//        initMap();
        initUniqueValueRenderer();
    }

    private void initMap() {
        ArcGISMap mMap = new ArcGISMap(Basemap.Type.TOPOGRAPHIC, 15.169193, 16.333479, 2);
        addGraphicsOverlay();
        mapView.setMap(mMap);
        //GraphicsOverlay 图层 GraphicsOverlay默认的构造模式一共有两种：DYNAMIC和 STATIC，默认的是动态渲染模式 DYNAMIC。
        //DYNAMIC 当图像有变化的时候，图层就会立刻更新图形  STATIC：默认不会立即更新图形，只有进行地图缩放等操作才会更新图形，这样导致图形更新缓慢
//        GraphicsOverlay graphicsOverlay = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
//        mapView.getGraphicsOverlays().add(graphicsOverlay);
//        mapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this,mapView){
//            @SuppressLint("ClickableViewAccessibility")
//            @Override
//            public boolean onSingleTapConfirmed(MotionEvent e) {
//                Point clickPoint = mapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()),Math.round(e.getY())));
//                GraphicsOverlay graphicsOverlay1 = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
//                //Symbol  控制显示图形符号的样式1.点符号（MarkerSymbol）
//                //点符号，顾名思义就是用来修饰Point的符号。包含三种类型，分别是
//                //PictureMarkerSymbol（图片点符号）,
//                //SimpleMarkerSymbol（简单点符号），
//                //TextSymbol（文本符号）。
//                //2.线符号（LineSymbol）
//                //线符号是用来修饰线PolyLine的符号。只有一种类型，SimpleLineSymbol（简单线符号）。
//                //3.面符号（FillSymbol）
//                //面符号是用来修饰面Polygon的符号，一共有两种PictureFillSymbol（图片面符号）, SimpleFillSymbol（简单面符号）。
//                SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED,10);
//                Graphic graphic = new Graphic(clickPoint,markerSymbol);
//                graphicsOverlay1.getGraphics().add(graphic);
//                mapView.getGraphicsOverlays().add(graphicsOverlay1);
//                return true;
//            }
//        });
    }

    /**
     * 渲染Renderer使用 设置图片样式，当渲染与符号同时存在的时候，优先使用符号样式
     */
    private void addGraphicsOverlay() {
        Point pointGeometry = new Point(40e5, 40e5, SpatialReferences.getWebMercator());
        SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 10);
        Graphic pointGraphic = new Graphic(pointGeometry);
        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        SimpleRenderer simpleRenderer = new SimpleRenderer(markerSymbol);
        graphicsOverlay.setRenderer(simpleRenderer);
        graphicsOverlay.getGraphics().add(pointGraphic);
        mapView.getGraphicsOverlays().add(graphicsOverlay);

        // line graphic
        PolylineBuilder lineGeometry = new PolylineBuilder(SpatialReferences.getWebMercator());
        lineGeometry.addPoint(-10e5, 40e5);
        lineGeometry.addPoint(20e5, 50e5);
        // solid blue line symbol
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 5);
        // create graphic for polyline
        Graphic lineGraphic = new Graphic(lineGeometry.toGeometry());
        // create graphic overlay for polyline
        GraphicsOverlay lineGraphicOverlay = new GraphicsOverlay();
        // create simple renderer
        SimpleRenderer lineRenderer = new SimpleRenderer(lineSymbol);
        // add graphic to overlay
        lineGraphicOverlay.setRenderer(lineRenderer);
        // add graphic to overlay
        lineGraphicOverlay.getGraphics().add(lineGraphic);
        // add graphics overlay to the MapView
        mapView.getGraphicsOverlays().add(lineGraphicOverlay);

        //polygon graphic 用于构建不可变的多边形辅助类
        PolygonBuilder polygonGeometry = new PolygonBuilder(SpatialReferences.getWebMercator());
        polygonGeometry.addPoint(-20e5, 20e5);
        polygonGeometry.addPoint(20e5, 20e5);
        polygonGeometry.addPoint(20e5, -20e5);
        polygonGeometry.addPoint(-20e5, -20e5);

        // solid yellow polygon symbol
        SimpleFillSymbol polygonSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.YELLOW, null);
        // create graphic for polygon toGeometry 描述图形的位置或者形状
        Graphic polygonGraphic = new Graphic(polygonGeometry.toGeometry());
        // create graphic overlay for polygon
        GraphicsOverlay polygonGraphicOverlay = new GraphicsOverlay();
        // create simple renderer
        SimpleRenderer polygonRenderer = new SimpleRenderer(polygonSymbol);
        // add graphic to overlay
        polygonGraphicOverlay.setRenderer(polygonRenderer);
        // add graphic to overlay
        polygonGraphicOverlay.getGraphics().add(polygonGraphic);
        // add graphics overlay to MapView
        mapView.getGraphicsOverlays().add(polygonGraphicOverlay);
    }

    private String sample_service_url = "https://sampleserver6.arcgisonline.com/arcgis/rest/services/Census/MapServer/3";

    /**
     * UniqueValueRenderer表示具有匹配属性的功能/图形组。这在名义或字符串数据中最常见。
     *
     * UniqueValueRenderer用于绘制具有不同符号的多个要素/图形，并将UniqueValueRenderer中的字段名称与UniqueValues中的值进行匹配。
     *
     * 示例：使用UniqueValueRenderer对分区指定进行符号化：黄色表示“住宅”，紫色表示“工业”，红色表示“商业”，等等。
     */
    private void initUniqueValueRenderer(){
        ArcGISMap map = new ArcGISMap(Basemap.createTopographic());

        //[DocRef: Name=Unique Value Renderer, Topic=Symbols and Renderers, Category=Fundamentals]
        // Create service feature table
        ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(sample_service_url);

        // Create the feature layer using the service feature table
        FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);

        // Override the renderer of the feature layer with a new unique value renderer
        UniqueValueRenderer uniqueValueRenderer = new UniqueValueRenderer();

        // Set the field to use for the unique values
        //You can add multiple fields to be used for the renderer in the form of a list, in this case
        uniqueValueRenderer.getFieldNames().add("STATE_ABBR");
        // we are only adding a single field

        // Create the symbols to be used in the renderer
        SimpleFillSymbol defaultFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.NULL, Color.BLACK,
                new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GRAY, 2));

        SimpleFillSymbol californiaFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.RED,
                new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 2));

        SimpleFillSymbol arizonaFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.GREEN,
                new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 2));

        SimpleFillSymbol nevadaFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.BLUE,
                new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 2));

        // Set default symbol
        uniqueValueRenderer.setDefaultSymbol(defaultFillSymbol);
        uniqueValueRenderer.setDefaultLabel("Other");

        // Set value for california
        List<Object> californiaValue = new ArrayList<>();
        // You add values associated with fields set on the unique value renderer.
        // If there are multiple values, they should be set in the same order as the fields are set
        californiaValue.add("CA");
        uniqueValueRenderer.getUniqueValues().add(
                new UniqueValueRenderer.UniqueValue("California", "State of California", californiaFillSymbol,
                        californiaValue));

        // Set value for arizona
        List<Object> arizonaValue = new ArrayList<>();
        // You add values associated with fields set on the unique value renderer.
        // If there are multiple values, they should be set in the same order as the fields are set
        arizonaValue.add("AZ");
        uniqueValueRenderer.getUniqueValues()
                .add(new UniqueValueRenderer.UniqueValue("Arizona", "State of Arizona", arizonaFillSymbol, arizonaValue));

        // Set value for nevada
        List<Object> nevadaValue = new ArrayList<>();
        // You add values associated with fields set on the unique value renderer.
        // If there are multiple values, they should be set in the same order as the fields are set
        nevadaValue.add("NV");
        uniqueValueRenderer.getUniqueValues()
                .add(new UniqueValueRenderer.UniqueValue("Nevada", "State of Nevada", nevadaFillSymbol, nevadaValue));

        // Set the renderer on the feature layer
        featureLayer.setRenderer(uniqueValueRenderer);
        //[DocRef: END]

        // add the layer to the map
        map.getOperationalLayers().add(featureLayer);

        map.setInitialViewpoint(new Viewpoint(
                new Envelope(-13893029.0, 3573174.0, -12038972.0, 5309823.0, SpatialReferences.getWebMercator())));

        // set the map to be displayed in the mapview
        mapView.setMap(map);
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
