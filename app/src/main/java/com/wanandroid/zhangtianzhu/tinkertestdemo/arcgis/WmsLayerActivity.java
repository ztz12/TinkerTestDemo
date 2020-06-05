package com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.esri.arcgisruntime.layers.WmsLayer;
import com.esri.arcgisruntime.layers.WmtsLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.ogc.wmts.WmtsLayerInfo;
import com.esri.arcgisruntime.ogc.wmts.WmtsService;
import com.esri.arcgisruntime.ogc.wmts.WmtsServiceInfo;
import com.wanandroid.zhangtianzhu.tinkertestdemo.R;
import com.wanandroid.zhangtianzhu.tinkertestdemo.utils.AssistStatic;

import java.util.ArrayList;
import java.util.List;

/**
 * 代表开放地理空间联盟（OGC）Web地图服务（WMS）层。
 * 该服务可以在ArcGIS Online上的云中，第三方服务器上或ArcGIS Server上内部托管。
 */
public class WmsLayerActivity extends AppCompatActivity {
    private MapView mMapView;
    private String wms_layer_url = "https://certmapper.cr.usgs.gov/arcgis/services/geology/africa/MapServer/WMSServer?request=GetCapabilities&amp;service=WMS";

    private String wmts_url = "http://sampleserver6.arcgisonline.com/arcgis/rest/services/WorldTimeZones/MapServer/WMTS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wms_layer);
        mMapView = findViewById(R.id.wmsLayer_mapView);
        initMap();
    }

    private void initMap() {
//        ArcGISMap map = new ArcGISMap(Basemap.Type.IMAGERY,2.0,18.0,3);
//        mMapView.setMap(map);
//
//        //保存一个用于显示wms唯一标识的名称列表
//        List<String> wmsName = new ArrayList<>();
//        wmsName.add("0");
//        WmsLayer wmsLayer = new WmsLayer(wms_layer_url,wmsName);
//        map.getOperationalLayers().add(wmsLayer);
//
//        map.addDoneLoadingListener(new Runnable() {
//            @Override
//            public void run() {
//                if(wmsLayer.getLoadStatus()== LoadStatus.LOADED){
//
//                }
//            }
//        });
        ArcGISMap map = new ArcGISMap();
        mMapView.setMap(map);

        // create wmts service from url string
        //从WMS服务检索感兴趣的图层时创建WMS图层的示例:
        WmtsService wmtsService = new WmtsService(wmts_url);
        wmtsService.loadAsync();
        wmtsService.addDoneLoadingListener(() -> {
            if (wmtsService.getLoadStatus() == LoadStatus.LOADED) {
                // get service info
                WmtsServiceInfo wmtsServiceInfo = wmtsService.getServiceInfo();
                // get the first layer id
                List<WmtsLayerInfo> layerInfoList = wmtsServiceInfo.getLayerInfos();
                // create WMTS layer from layer info
                WmtsLayer wmtsLayer = new WmtsLayer(layerInfoList.get(0));
                // set the basemap of the map with WMTS layer
                map.setBasemap(new Basemap(wmtsLayer));
            } else {
                String error = "Error loading WMTS Service: " + wmtsService.getLoadError();
                AssistStatic.showToast(WmsLayerActivity.this, error);
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
