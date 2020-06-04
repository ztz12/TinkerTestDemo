package com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SketchCreationMode;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.google.android.material.snackbar.Snackbar;
import com.wanandroid.zhangtianzhu.tinkertestdemo.R;
import com.wanandroid.zhangtianzhu.tinkertestdemo.utils.AssistStatic;

/**
 * sketchEditor 草图编辑器
 * 允许用户在地图上用交互的方式绘制几何图形 通过使用相应的SketchCreationMode启动SketchEditor，可以从头开始绘制不同的几何类型，例如点，多点，折线或多边形。
 */
public class SketchEditorActivity extends AppCompatActivity implements View.OnClickListener {
    private MapView mMapView;
    private SimpleMarkerSymbol mPointSymbol;
    private SimpleLineSymbol mLineSymbol;
    private SimpleFillSymbol mFillSymbol;
    private SketchEditor mSketchEditor;
    private GraphicsOverlay mGraphicsOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sketch_editor);
        findViews();
        ArcGISMap map = new ArcGISMap(Basemap.Type.LIGHT_GRAY_CANVAS, 34.056295, -117.195800, 16);
        mMapView.setMap(map);

        mGraphicsOverlay = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mGraphicsOverlay);

        // 创建一个新的草图编辑器并将其添加到地图视图中
        mSketchEditor = new SketchEditor();
        mMapView.setSketchEditor(mSketchEditor);

        mPointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, 0xFFFF0000, 20);
        mLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF8800, 4);
        mFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.CROSS, 0x40FFA9A9, mLineSymbol);
    }

    private void findViews() {
        mMapView = findViewById(R.id.sktech_mapView);
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
        AssistStatic.showToast(SketchEditorActivity.this, report);
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
}
