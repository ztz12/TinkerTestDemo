package com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.ImmutablePart;
import com.esri.arcgisruntime.geometry.ImmutablePartCollection;
import com.esri.arcgisruntime.geometry.ImmutablePointCollection;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PolygonBuilder;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.PolylineBuilder;
import com.esri.arcgisruntime.geometry.Segment;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.util.ListenableList;
import com.wanandroid.zhangtianzhu.tinkertestdemo.R;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 查询当前图层中的图形
 */
public class IdentifyGraphicsOverlaysAsync extends AppCompatActivity {
    private MapView mMapView;

    private GraphicsOverlay grOverlay;
    private GraphicsOverlay lightGraphicsOverlay;
    private ListenableList<Graphic> graphics;
    private PolylineBuilder polylineBuilder;
    private SimpleLineSymbol lineSymbol;
    private Graphic graphic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_graphics_overlays_async);
        mMapView = findViewById(R.id.mapView);
        ArcGISMap mMap = new ArcGISMap(Basemap.Type.TOPOGRAPHIC, 3.184710, -4.734690, 2);
        mMapView.setMap(mMap);

        // set up gesture for interacting with the MapView
        MapViewTouchListener mMapViewTouchListener = new MapViewTouchListener(this, mMapView);
        mMapView.setOnTouchListener(mMapViewTouchListener);

        // create graphics overlay
        grOverlay = new GraphicsOverlay();
        // add graphics overlay to the MapView
        mMapView.getGraphicsOverlays().add(grOverlay);
        // create list of graphics
        graphics = grOverlay.getGraphics();
        lightGraphicsOverlay = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(lightGraphicsOverlay);

        addGraphicsOverlay();
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

    private void addGraphicsOverlay() {
        //投影坐标方式
        polylineBuilder = new PolylineBuilder(SpatialReferences.getWebMercator());
        polylineBuilder.addPoint(-20e5, 20e5);
        polylineBuilder.addPoint(20e5, 20e5);
        // create the polygon
//        PolygonBuilder polygonGeometry = new PolygonBuilder(SpatialReferences.getWebMercator());
//        polygonGeometry.addPoint(-20e5, 20e5);
//        polygonGeometry.addPoint(20e5, 20e5);
//        polygonGeometry.addPoint(20e5, -20e5);
//        polygonGeometry.addPoint(-20e5, -20e5);
//
//        // create solid line symbol
//        SimpleFillSymbol polygonSymbol = new SimpleFillSymbol(
//        SimpleFillSymbol.Style.SOLID, Color.YELLOW, null);
        lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 5);
        // create graphic from polygon geometry and symbol
        graphic = new Graphic(polylineBuilder.toGeometry(), lineSymbol);
        // add graphic to graphics overlay
        graphics.add(graphic);

        polylineBuilder = new PolylineBuilder(SpatialReferences.getWebMercator());
        polylineBuilder.addPoint(20e5, 20e5);
        polylineBuilder.addPoint(20e5, -20e5);

        lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.YELLOW, 5);
        // create graphic from polygon geometry and symbol
        graphic = new Graphic(polylineBuilder.toGeometry(), lineSymbol);
        // add graphic to graphics overlay
        graphics.add(graphic);

        polylineBuilder = new PolylineBuilder(SpatialReferences.getWebMercator());
        polylineBuilder.addPoint(20e5, -20e5);
        polylineBuilder.addPoint(-20e5, -20e5);

        lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 5);
        // create graphic from polygon geometry and symbol
        graphic = new Graphic(polylineBuilder.toGeometry(), lineSymbol);
        // add graphic to graphics overlay
        graphics.add(graphic);

        polylineBuilder = new PolylineBuilder(SpatialReferences.getWebMercator());
        polylineBuilder.addPoint(-20e5, -20e5);
        polylineBuilder.addPoint(-20e5, 20e5);

        lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 5);
        // create graphic from polygon geometry and symbol
        graphic = new Graphic(polylineBuilder.toGeometry(), lineSymbol);
        // add graphic to graphics overlay
        graphics.add(graphic);
    }

    /**
     * Override default gestures of the MapView
     */
    class MapViewTouchListener extends DefaultMapViewOnTouchListener {

        /**
         * Constructs a DefaultMapViewOnTouchListener with the specified Context and MapView.
         *
         * @param context the context from which this is being created
         * @param mapView the MapView with which to interact
         */
        public MapViewTouchListener(Context context, MapView mapView) {
            super(context, mapView);
        }

        /**
         * Override the onSingleTapConfirmed gesture to handle tapping on the MapView
         * and detected if the Graphic was selected.
         *
         * @param e the motion event
         * @return true if the listener has consumed the event; false otherwise
         */
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // get the screen point where user tapped
            android.graphics.Point screenPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());

            // identify graphics on the graphics overlay
            final ListenableFuture<IdentifyGraphicsOverlayResult> identifyGraphic = mMapView.identifyGraphicsOverlayAsync(
                    grOverlay, screenPoint, 10.0, false, 2);

            identifyGraphic.addDoneListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        IdentifyGraphicsOverlayResult grOverlayResult = identifyGraphic.get();
                        // get the list of graphics returned by identify graphic overlay
                        List<Graphic> graphic = grOverlayResult.getGraphics();
                        // get size of list in results
                        int identifyResultSize = graphic.size();
                        for (Graphic gra : graphic) {
                            Map<String, Object> attributes = gra.getAttributes();
                            Geometry geometry = gra.getGeometry();
                            Polyline polyline = (Polyline) geometry;
                            lightGraphicsOverlay.getGraphics().clear();
                            lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GRAY, 5);
                            Graphic graphic1 = new Graphic(polyline,lineSymbol);
                            lightGraphicsOverlay.getGraphics().add(graphic1);
                            ImmutablePartCollection collection = polyline.getParts();
                            //投影坐标
                            Point startPoint = collection.get(0).getStartPoint();
                            Point endPoint = collection.get(collection.size() - 1).getEndPoint();
                            double dist = GeometryEngine.distanceBetween(startPoint, endPoint);
                            dist = Math.abs(dist);
                            String distStr = String.format("%.2f", dist) + "米";
                            //经纬度坐标
                            Point projectedStartPoint = (Point) GeometryEngine.project(startPoint, SpatialReference.create(4236));
                            Point projectedEndPoint = (Point) GeometryEngine.project(endPoint, SpatialReference.create(4236));
                            for (int i = 0; i < collection.size(); i++) {
                                ImmutablePart part = collection.get(i);
                                for (int y = 0; y < part.size(); y++) {
                                    Segment segment = part.get(i);
                                    Point startSePoint = segment.getStartPoint();
                                    Point endSePoint = segment.getEndPoint();
                                    double distSe = GeometryEngine.distanceBetween(startSePoint, endSePoint);
                                    distSe = Math.abs(distSe);
                                    String distStrSe = String.format("%.2f", distSe) + "米";
                                }
                            }
                            if (!graphic.isEmpty()) {
                                // show a toast message if graphic was returned
                                Toast.makeText(getApplicationContext(), "Tapped on " + projectedStartPoint + " Graphic", Toast.LENGTH_SHORT).show();
                            }
                        }
//                        if (!graphic.isEmpty()) {
//                            // show a toast message if graphic was returned
//                            Toast.makeText(getApplicationContext(), "Tapped on " + identifyResultSize + " Graphic", Toast.LENGTH_SHORT).show();
//                        }
                    } catch (InterruptedException | ExecutionException ie) {
                        ie.printStackTrace();
                    }

                }
            });

            return super.onSingleTapConfirmed(e);
        }
    }

}
