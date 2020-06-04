package com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.esri.arcgisruntime.arcgisservices.RenderingRuleInfo;
import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.raster.ImageServiceRaster;
import com.esri.arcgisruntime.raster.RenderingRule;
import com.wanandroid.zhangtianzhu.tinkertestdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * RasterLayer 栅格图层
 * 用来加载移动端本地文件，移动镶嵌数据集，影像服务
 * RenderingRuleInfo（渲染规则）
 * 用以定义如何对请求的影像进行渲染和处理。
 * 它可以从服务中定义的名称创建，也可以使用获得getRenderingRuleInfos()。它可以用来构建一个RenderingRule。
 * 通过ImageServiceRaster，获取RenderingRuleInfo，进一步得到Rendering Rule。
 */
public class RasterLayerActivity extends AppCompatActivity {
    private MapView mMapView;
    private ArcGISMap map;

    private String image_service_url = "https://sampleserver6.arcgisonline.com/arcgis/rest/services/CharlotteLAS/ImageServer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raster_layer);
        mMapView = findViewById(R.id.raster_mapView);
        initMap();
    }

    private void initMap(){
        map = new ArcGISMap(Basemap.createTopographic());
        mMapView.setMap(map);

        ImageServiceRaster imageServiceRaster = new ImageServiceRaster(image_service_url);
        RasterLayer layer = new RasterLayer(imageServiceRaster);
        map.getOperationalLayers().add(layer);

        Spinner spinner = findViewById(R.id.spinner);
        final List<String> renderRulesList = new ArrayList<>();
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, renderRulesList);
        spinner.setAdapter(spinnerAdapter);

        imageServiceRaster.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                if(imageServiceRaster.getLoadStatus()== LoadStatus.LOADED){
                    mMapView.setViewpointGeometryAsync(imageServiceRaster.getServiceInfo().getFullExtent());
                    List<RenderingRuleInfo> renderingRuleInfos = imageServiceRaster.getServiceInfo().getRenderingRuleInfos();
                    for (RenderingRuleInfo renderingRuleInfo : renderingRuleInfos) {
                        String name = renderingRuleInfo.getName();
                        renderRulesList.add(name);
                        spinnerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyRenderingRule(imageServiceRaster, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Apply a rendering rule on a Raster and add it to the map
     *
     * @param imageServiceRaster image service raster to apply rendering on
     * @param index              spinner selected position representing the rule to apply
     */
    private void applyRenderingRule(ImageServiceRaster imageServiceRaster, int index) {
        // clear all rasters
        map.getOperationalLayers().clear();
        // get the rendering rule info at the selected index
        RenderingRuleInfo renderRuleInfo = imageServiceRaster.getServiceInfo().getRenderingRuleInfos().get(index);
        // create a rendering rule object using the rendering rule info
        RenderingRule renderingRule = new RenderingRule(renderRuleInfo);
        // create a new image service raster
        ImageServiceRaster appliedImageServiceRaster = new ImageServiceRaster(image_service_url);
        // apply the rendering rule
        appliedImageServiceRaster.setRenderingRule(renderingRule);
        // create a raster layer using the image service raster
        RasterLayer rasterLayer = new RasterLayer(appliedImageServiceRaster);
        // add the raster layer to the map
        map.getOperationalLayers().add(rasterLayer);
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
