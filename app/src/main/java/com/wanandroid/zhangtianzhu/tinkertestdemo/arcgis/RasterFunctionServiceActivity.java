package com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.raster.ImageServiceRaster;
import com.esri.arcgisruntime.raster.Raster;
import com.esri.arcgisruntime.raster.RasterFunction;
import com.esri.arcgisruntime.raster.RasterFunctionArguments;
import com.wanandroid.zhangtianzhu.tinkertestdemo.R;
import com.wanandroid.zhangtianzhu.tinkertestdemo.utils.AssistStatic;

import java.util.List;

/**
 * RasterFunction(定义加载)
 * RasterFunction是针对Raster结合展现的方法进而呈现不同渲染的影像，本质上不改变源数据。
 * 案例：
 * 通过RasterFunction定义山体阴影，得到新的RasterLayer
 */
public class RasterFunctionServiceActivity extends AppCompatActivity {

    private MapView mMapView;

    private Button mRasterFunctionButton;

    private String image_service_raster_url = "http://sampleserver6.arcgisonline.com/arcgis/rest/services/NLCDLandCover2001/ImageServer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raster_function_service);
        mMapView = findViewById(R.id.raster_function_mapView);
        mRasterFunctionButton = findViewById(R.id.btn_raster);
        initMap();
    }

    private void initMap() {
        // create a map with the BasemapType topographic
        ArcGISMap map = new ArcGISMap(Basemap.createDarkGrayCanvasVector());
        final ImageServiceRaster imageServiceRaster = new ImageServiceRaster(image_service_raster_url);
        final RasterLayer imageRasterLayer = new RasterLayer(imageServiceRaster);
        map.getOperationalLayers().add(imageRasterLayer);

        // zoom to the extent of the raster service
        imageRasterLayer.addDoneLoadingListener(() -> {
            if (imageRasterLayer.getLoadStatus() == LoadStatus.LOADED) {
                // get the center point
                Point centerPnt = imageServiceRaster.getServiceInfo().getFullExtent().getCenter();
                mMapView.setViewpointCenterAsync(centerPnt, 55000000);
                mRasterFunctionButton.setEnabled(true);
            } else {
                String error = "Error loading image raster layer: " + imageRasterLayer.getLoadError();
                AssistStatic.showToast(RasterFunctionServiceActivity.this, error);
            }
        });

        // 点击按钮：通过RasterFunction定义山体阴影，得到新的RasterLayer
        mRasterFunctionButton.setOnClickListener(v -> applyRasterFunction(imageServiceRaster));

        // set the map to be displayed in this view
        mMapView.setMap(map);
    }

    private void applyRasterFunction(Raster raster) {
        // 通过Json设置渲染规则
        RasterFunction rasterFuntionFromJson = RasterFunction.fromJson(getString(R.string.hillshade_simplified));
        // get parameter name value pairs used by hillside
        RasterFunctionArguments rasterFunctionArguments = rasterFuntionFromJson.getArguments();
        // get a list of raster names associated with the raster function
        List<String> rasterNames = rasterFunctionArguments.getRasterNames();
        rasterFunctionArguments.setRaster(rasterNames.get(0), raster);
        // create raster as raster layer
        raster = new Raster(rasterFuntionFromJson);
        RasterLayer hillshadeLayer = new RasterLayer(raster);
        mMapView.getMap().getOperationalLayers().add(hillshadeLayer);
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
