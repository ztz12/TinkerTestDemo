package com.wanandroid.zhangtianzhu.tinkertestdemo.arcgis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LayerSceneProperties;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.wanandroid.zhangtianzhu.tinkertestdemo.R;

/**
 * 三维地图
 * SceneView
 * Runtime100是用了一个GeoView类作为地图的基类直接继承于ViewGroup，然后MapView和SceneView分别作为二维和三维地图的容器继承于GeoView。
 * 其实把SceneView当做MapView，把ArcGISScene当做ArcGISMap就行
 */
public class SceneViewActivity extends AppCompatActivity {
    private SceneView mSceneView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_view);
        mSceneView = findViewById(R.id.sceneView);
//        initScene();
//        showScenePlace();
//        showElevation();
//        showGraphicOverlay();
        initPortalItem();
    }

    private void initScene() {
        //基本地图就是地球
        ArcGISScene arcGISScene = new ArcGISScene();
        //设置瓦片图层作为底图
        ArcGISTiledLayer arcGISTiledLayer = new ArcGISTiledLayer(
                "https://services.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer");
        Basemap basemap = new Basemap(arcGISTiledLayer);
        arcGISScene.setBasemap(basemap);
        mSceneView.setScene(arcGISScene);
    }

    private String brest_buildings = " http://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/Buildings_Brest/SceneServer";

    /**
     * 展示三维场景
     */
    private void showScenePlace() {
        ArcGISScene scene = new ArcGISScene();
        scene.setBasemap(Basemap.createImagery());
        mSceneView.setScene(scene);

        ArcGISTiledLayer layer = new ArcGISTiledLayer(brest_buildings);

        scene.getOperationalLayers().add(layer);

        // 设置三维场景视角镜头（camera）
        //纬度，经度，高程，
//        Heading：镜头水平朝向
//        0度表示指北，从0度逐渐增加，镜头顺时针旋转，360度回到0度指北。
//        Pitch：镜头垂直朝向
//        0度表示垂直俯视地球，从0度逐渐增加，镜头沿其水平朝向，从俯视地球朝天空旋转，360度回到0度俯视地球。
        Camera camera = new Camera(48.378, -4.494, 200, 345, 65, 0);
        mSceneView.setViewpointCamera(camera);
    }

    /**
     * 使用高程表面（ArcGISTiledElevationSource、RasterElevationSource）
     * ArcGISTiledElevationSource：将在线服务作为高程表面
     * RasterElevationSource：将本地DEM文件作为高程表面
     */
    private String elevation_image_service = "http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer";

    private void showElevation() {
        ArcGISScene arcGISScene = new ArcGISScene();
        arcGISScene.setBasemap(Basemap.createImagery());
        mSceneView.setScene(arcGISScene);

        ArcGISTiledElevationSource source = new ArcGISTiledElevationSource(elevation_image_service);
        arcGISScene.getBaseSurface().getElevationSources().add(source);
        Camera camera = new Camera(28.4, 83.9, 10010.0, 0.0, 0.0, 0.0);
        mSceneView.setViewpointCamera(camera);
    }

    /**
     * 表面置放模式（LayerSceneProperties.SurfacePlacement）
     * 与二维不同的是，通过GraphicsOverlay添加空间要素时，需要设置表面置放模式，默认为DRAPED。
     * <p>
     * DRAPED：空间要素紧贴场景表面（surface layer），不考虑空间要素的高程值（Z-values）
     * ABSOLUTE：空间要素通过其高程值（Z-values）设置距离球体表面（海平面）的高度
     * RELATIVE：空间要素通过其高程值（Z-values）设置距离场景表面（surface layer）的高度
     * <p>
     * 球体表面和场景表面的差异在于是否使用高程表面，若不使用高程表面ABSOLUTE和RELATIVE在三维场景中展示的位置相同。
     */
    private void showGraphicOverlay() {
        // create a scene and add a basemap to it
        ArcGISScene agsScene = new ArcGISScene();
        agsScene.setBasemap(Basemap.createImagery());
        mSceneView.setScene(agsScene);

        // add base surface for elevation data
        ArcGISTiledElevationSource elevationSource = new ArcGISTiledElevationSource(elevation_image_service);
        agsScene.getBaseSurface().getElevationSources().add(elevationSource);

        // add a camera and initial camera position
        Camera camera = new Camera(53.04, -4.04, 1300, 0, 90.0, 0);
        mSceneView.setViewpointCamera(camera);

        // create overlays with elevation modes
        GraphicsOverlay drapedOverlay = new GraphicsOverlay();
        drapedOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.DRAPED);
        mSceneView.getGraphicsOverlays().add(drapedOverlay);

        GraphicsOverlay relativeOverlay = new GraphicsOverlay();
        relativeOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.RELATIVE);
        mSceneView.getGraphicsOverlays().add(relativeOverlay);

        GraphicsOverlay absoluteOverlay = new GraphicsOverlay();
        absoluteOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.ABSOLUTE);
        mSceneView.getGraphicsOverlays().add(absoluteOverlay);

        // create point for graphic location
        Point point = new Point(-4.04, 53.06, 1000, camera.getLocation().getSpatialReference());

        // create a red (0xFFFF0000) circle symbol
        SimpleMarkerSymbol circleSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFFFF0000, 10);

        // create a text symbol for each elevation mode
        TextSymbol drapedText = new TextSymbol(10, "DRAPED", 0xFFFFFFFF, TextSymbol.HorizontalAlignment.LEFT,
                TextSymbol.VerticalAlignment.MIDDLE);
        TextSymbol relativeText = new TextSymbol(10, "RELATIVE", 0xFFFFFFFF, TextSymbol.HorizontalAlignment.LEFT,
                TextSymbol.VerticalAlignment.MIDDLE);
        TextSymbol absoluteText = new TextSymbol(10, "ABSOLUTE", 0xFFFFFFFF, TextSymbol.HorizontalAlignment.LEFT,
                TextSymbol.VerticalAlignment.MIDDLE);

        // add the point graphic and text graphic to the corresponding graphics
        // overlay
        drapedOverlay.getGraphics().add(new Graphic(point, circleSymbol));
        drapedOverlay.getGraphics().add(new Graphic(point, drapedText));

        relativeOverlay.getGraphics().add(new Graphic(point, circleSymbol));
        relativeOverlay.getGraphics().add(new Graphic(point, relativeText));

        absoluteOverlay.getGraphics().add(new Graphic(point, circleSymbol));
        absoluteOverlay.getGraphics().add(new Graphic(point, absoluteText));
    }

    /**
     * PortalItem：表示存储在ArcGIS门户中的项目（内容单位）。 包含有关项目的信息，例如项目的唯一ID，拥有的Portal以及项目的类型（PortalItem.Type），
     * 例如Web地图，地图服务或tile包。
     */
    private void initPortalItem() {
        // get the portal url and portal item from ArcGIS online
        Portal portal = new Portal("http://www.arcgis.com/", false);
        PortalItem portalItem = new PortalItem(portal, "a13c3c3540144967bc933cb5e498b8e4");

        ArcGISScene scene = new ArcGISScene(portalItem);
        mSceneView.setScene(scene);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSceneView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSceneView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSceneView.dispose();
    }
}
