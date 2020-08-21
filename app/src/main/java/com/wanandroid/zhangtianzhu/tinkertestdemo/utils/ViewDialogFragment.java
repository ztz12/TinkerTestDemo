package com.wanandroid.zhangtianzhu.tinkertestdemo.utils;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

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
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.wanandroid.zhangtianzhu.tinkertestdemo.R;

public class ViewDialogFragment extends DialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = 0;
        lp.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(lp);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.map_preview_window, container, false);
        MapView mapView = view.findViewById(R.id.preview_mapView);
        ArcGISMap mainArcGISMap = new ArcGISMap(Basemap.Type.OCEANS, 34.631040,
                117.981978, 19);
        mapView.setMap(mainArcGISMap);
        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(graphicsOverlay);
        PointCollection coloradoCorners = new PointCollection(SpatialReferences.getWgs84());
        coloradoCorners.add(117.982239, 34.631144);
        coloradoCorners.add(117.982244, 34.631035);
        coloradoCorners.add(117.981978, 34.631040);
        coloradoCorners.add(117.981980, 34.631151);
        Polygon polygon = new Polygon(coloradoCorners);
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 2);
        SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.DIAGONAL_CROSS, Color.RED, lineSymbol);
        Graphic graphic = new Graphic(polygon, fillSymbol);
        graphicsOverlay.getGraphics().add(graphic);


//        Polyline polyline = new Polyline(coloradoCorners);
//        SimpleLineSymbol simpleLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.parseColor("#FC8145"), 5);
//        Graphic graphic1 = new Graphic(polyline, simpleLineSymbol);
//        graphicsOverlay.getGraphics().add(graphic1);

        //点
        Point point = new Point(117.982239, 34.631144);
        SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 10);
        Graphic graphic2 = new Graphic(point,markerSymbol);
        graphicsOverlay.getGraphics().add(graphic2);
        mapView.setOnTouchListener(new DefaultMapViewOnTouchListener(getActivity(), mapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Point clickPoint = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 10);
                Graphic graphic = new Graphic(clickPoint, markerSymbol);
                //清除上一个点
                graphicsOverlay.getGraphics().clear();
                graphicsOverlay.getGraphics().add(graphic);
                return super.onSingleTapConfirmed(e);
            }
        });
        return view;
    }
}
