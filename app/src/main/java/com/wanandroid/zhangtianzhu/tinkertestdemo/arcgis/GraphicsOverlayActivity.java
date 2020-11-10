package com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;

import android.os.Bundle;
import android.view.Display;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;

import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.wanandroid.zhangtianzhu.tinkertestdemo.R;
import com.wanandroid.zhangtianzhu.tinkertestdemo.utils.ViewDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 绘制点、线、面、圆，添加文本与图片
 * Geometries用以在特定的位置通过形状表达真实世界的对象
 * 图层范围，视图范围，GPS定位 都是通过Geometries表达实现进一步的数据编辑、空间分析、地理处理、位置与面积量算都离不开空间要素。
 * Graphics：
 * 1、图形由几何，可选属性组成，并使用符号或渲染器在地图上绘制。
 * 2、图形用于表示临时数据，例如查询或分析的结果，或突出显示地图中的现有内容。 它们通常用于显示定期更改位置的数据，因为它们保存在内存中，
 * 而不是持久保存在地图上。 它们通过GraphicsOverlay显示在地图上。
 * 3、每个图形都必须具有一个setGeometry（Geometry）来描述图形的位置和形状。 如果图形的几何具有不同的SpatialReference，
 * 则会即时将其几何图形重新投影到地图的SpatialReference。 重新投影可能会很昂贵-理想情况下，几何图形应与地图具有相同的SpatialReference，以最大化性能。
 */
@SuppressLint("ClickableViewAccessibility")
public class GraphicsOverlayActivity extends AppCompatActivity {
    private MapView mMapView;

    private RadioGroup mRadioGroup;

    private GraphicsOverlay mGraphicsOverlay;

    /**
     * Multipoint 表示由零件组成的集合，每个零件都是线段集合 Multipart是Polygon和Polyline实例化类从其继承的抽象类。
     * getParts（）方法返回组成Multipart几何的零件的不可变集合。 每个部分都彼此独立，但是存在针对不同类型的多部分的规则。
     * 使用“简化”将创建一个形状的副本，该副本应遵守该几何类型的拓扑简单性规则
     * 表示用于从MultipointBuilder创建多点几何的可变点集合。 可以将点添加到集合中，从集合中插入或从集合中删除，以定义或更改多点的形状。 也用于几何构造器和构造器。
     * 添加到PointCollection的任何Point的SpatialReference必须与PointCollection的SpatialReference匹配，
     * 或者为null（在这种情况下，假定这些Point与PointCollection具有相同的SpatialReference）。
     * SpatialReferences定义坐标如何与现实世界的位置相对应
     */
    private PointCollection pointCollection = new PointCollection(SpatialReferences.getWebMercator());
    private List<Point> pointList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphics_overlay);
        findView();
        addBaseMap();
        initListener();
    }

    private void addBaseMap() {
        ArcGISMap arcGisMap = new ArcGISMap(Basemap.Type.OCEANS, 30.671475859566514,
                104.07567785156248, 11);
        mMapView.setMap(arcGisMap);

        mGraphicsOverlay = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mGraphicsOverlay);
    }

    private void initListener() {
        mRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_draw_point: //绘制点
                    drawPoint();
                    break;
                case R.id.rb_draw_polyline: //绘制线
                    drawPolyline();
                    break;
                case R.id.rb_draw_scroll_line: //绘制曲线
                    drawScrollLine();
                    break;
                case R.id.rb_draw_polygon: //绘制面
                    drawPolygon();
                    break;
                case R.id.rb_add_image: //添加图片
                    addImage();
                    break;
                case R.id.rb_draw_circle: //绘制圆
                    drawCircle();
                    break;
                case R.id.rb_draw_text: //绘制文字
                    drawText();
                    break;
                default:
                    break;
            }
        });
    }

    /**
     * 绘制文字
     */
    private void drawText() {
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Point clickPoint = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                TextSymbol textSymbol = new TextSymbol(20, "绘制文字", Color.RED, TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
                Graphic graphic = new Graphic(clickPoint, textSymbol);
                mGraphicsOverlay.getGraphics().clear();
                mGraphicsOverlay.getGraphics().add(graphic);
                return super.onSingleTapConfirmed(e);
            }
        });
    }

    private void drawCircle() {
        pointList = new ArrayList<>();
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                mGraphicsOverlay.getGraphics().clear();
                double radius = 0;
                Point clickPoint = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                pointList.add(clickPoint);
                if (pointList.size() >= 2) {
                    double x = pointList.get(pointList.size() - 1).getX() - pointList.get(pointList.size() - 2).getX();
                    double y = pointList.get(pointList.size() - 1).getY() - pointList.get(pointList.size() - 2).getY();
                    radius = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
                }
                getCircle(clickPoint, radius);
                return super.onSingleTapConfirmed(e);
            }
        });
    }

    private void getCircle(Point point, double radius) {
        Point[] points = getPoints(point, radius);
        pointCollection.clear();

        for (Point p : points) {
            pointCollection.add(p);
        }

        SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 10);
        Graphic graphic = new Graphic(point, markerSymbol);
        mGraphicsOverlay.getGraphics().add(graphic);

        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 5);
        SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.YELLOW, lineSymbol);
        Polygon polygon = new Polygon(pointCollection);
        Graphic graphic1 = new Graphic(polygon, fillSymbol);
        mGraphicsOverlay.getGraphics().add(graphic1);
    }

    /**
     * 添加图片
     */
    private void addImage() {
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Point clickPoint = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                BitmapDrawable drawable = new BitmapDrawable(bitmap);
                PictureMarkerSymbol pictureMarkerSymbol = new PictureMarkerSymbol(drawable);
                Graphic graphic = new Graphic(clickPoint, pictureMarkerSymbol);
                //涉及到图片加载，需要进行异步监听处理

                pictureMarkerSymbol.loadAsync();
                pictureMarkerSymbol.addDoneLoadingListener(() -> {
                    mGraphicsOverlay.getGraphics().clear();
                    mGraphicsOverlay.getGraphics().add(graphic);
                });
                return super.onSingleTapConfirmed(e);
            }
        });
    }

    /**
     * 绘制面
     */
    private void drawPolygon() {
        pointCollection.add(-109.048, 40.998);
        pointCollection.add(-102.047, 40.998);
        pointCollection.add(-102.037, 36.989);
        pointCollection.add(-109.048, 36.998);
        Polygon polygon = new Polygon(pointCollection);
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 5);
        SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.parseColor("#33e97676"), lineSymbol);
        Graphic graphic = new Graphic(polygon, fillSymbol);
        mGraphicsOverlay.getGraphics().add(graphic);
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                mGraphicsOverlay.getGraphics().clear();
                Point point = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                pointCollection.add(point);
                /**
                 * 表示由零件集合和SpatialReference定义的平面形状。 每个零件都定义了一个以段集合为边界的区域。 如果部件不止一个，则每个部件可能位于内部或与其他部件脱节。 基于点的帮助器方法允许将多边形视为一系列连接的点。
                 * 多边形是不可变的。 无需更改现有Polygon的属性，而是创建新的Polygon实例或使用PolygonBuilder。
                 * 多边形可以用作要素或图形的几何。
                 */
                Polygon polygon = new Polygon(pointCollection);
                if (pointCollection.size() == 1) {
                    SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.YELLOW, 10);
                    Graphic graphic = new Graphic(point, markerSymbol);
                    mGraphicsOverlay.getGraphics().add(graphic);
                }

                SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 5);
                SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.parseColor("#33e97676"), lineSymbol);
                Graphic graphic = new Graphic(polygon, fillSymbol);
                mGraphicsOverlay.getGraphics().add(graphic);
                double area = GeometryEngine.area(polygon);
                return super.onSingleTapConfirmed(e);
            }
        });
    }

    /**
     * 绘制曲线
     */
    private void drawScrollLine() {
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Point point1 = mMapView.screenToLocation(new android.graphics.Point(Math.round(e1.getX()), Math.round(e1.getY())));
                Point point2 = mMapView.screenToLocation(new android.graphics.Point(Math.round(e2.getX()), Math.round(e2.getY())));
                pointCollection.add(point1);
                pointCollection.add(point2);

                Polyline polyline = new Polyline(pointCollection);
                SimpleLineSymbol simpleLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.parseColor("#FC8145"), 5);
                Graphic graphic1 = new Graphic(polyline, simpleLineSymbol);
                mGraphicsOverlay.getGraphics().add(graphic1);
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
    }

    /**
     * 绘制线
     */
    private void drawPolyline() {
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Point point = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                pointCollection.add(point);

                //表示由零件集合和SpatialReference定义的线性形状。 每个零件都是由线段集合表示的连续线。
                // 如果存在多个部分，则每个部分可能会分支或与其他部分不连续。 基于点的辅助方法允许将折线视为一系列连接的点。
                //折线是不可变的。 无需更改现有折线的属性，而是创建新的折线实例或使用PolylineBuilder。
                //折线可以用作要素或图形的几何。
                Polyline polyline = new Polyline(pointCollection);

                //点
                SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.BLUE, 10);
                Graphic graphic = new Graphic(point, simpleMarkerSymbol);
                mGraphicsOverlay.getGraphics().add(graphic);

                //线
                SimpleLineSymbol simpleLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.parseColor("#FC8145"), 5);
                //Graphic 用于显示临时数据，突出地图上的内容，通常显示定期更改的位置数据，保存在内存中，而不是持久保存在地图上面，通过GraphicsOverlay显示在地图上面
                Graphic graphic1 = new Graphic(polyline, simpleLineSymbol);
                mGraphicsOverlay.getGraphics().add(graphic1);
                return super.onSingleTapConfirmed(e);
            }
        });
    }

    private void drawPoint() {
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Point clickPoint = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 10);
                Graphic graphic = new Graphic(clickPoint, markerSymbol);
                //清除上一个点
                mGraphicsOverlay.getGraphics().clear();
                mGraphicsOverlay.getGraphics().add(graphic);
                return super.onSingleTapConfirmed(e);
            }
        });
    }

    private void findView() {
        mMapView = findViewById(R.id.graphic_mapView);
        mRadioGroup = findViewById(R.id.radiogroup);
    }

    public void clear(View view) {
        mGraphicsOverlay.getGraphics().clear();
        pointCollection.clear();
        if (pointList != null) {
            pointList.clear();
        }
//        showWindow();
        //mapview必须加载到Fragment或Activity这样的界面中，如果加入到Dialog或者PopupWindow中会出现加载问题，显示错乱
        new ViewDialogFragment().show(getSupportFragmentManager(), "view");
    }

    private void showWindow(){
        View view = LayoutInflater.from(this).inflate(R.layout.map_preview_window,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(true);
        MapView mapView = view.findViewById(R.id.preview_mapView);
        ArcGISMap mainArcGISMap = new ArcGISMap(Basemap.Type.OCEANS, 30.671475859566514,
                104.07567785156248, 11);
        mapView.setMap(mainArcGISMap);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Window alertWindow = alertDialog.getWindow();
        WindowManager windowManager = this.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lParams = alertWindow.getAttributes();
        lParams.height = dip2px(this,200);
        lParams.width = dip2px(this,200);
        alertWindow.setAttributes(lParams);
        //设置窗体宽度
//        alertDialog.getWindow().setLayout(dip2px(this,200),dip2px(this,200));
//        PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#00000000"));
//        popupWindow.setBackgroundDrawable(colorDrawable);
//        popupWindow.setOutsideTouchable(true);
//        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 通过中心点和半径计算得出圆形的边线点集合
     *
     * @param center
     * @param radius
     * @return
     */
    private static Point[] getPoints(Point center, double radius) {
        Point[] points = new Point[50];
        double sin;
        double cos;
        double x;
        double y;
        for (double i = 0; i < 50; i++) {
            sin = Math.sin(Math.PI * 2 * i / 50);
            cos = Math.cos(Math.PI * 2 * i / 50);
            x = center.getX() + radius * sin;
            y = center.getY() + radius * cos;
            points[(int) i] = new Point(x, y);
        }
        return points;
    }
}
