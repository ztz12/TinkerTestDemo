package com.wanandroid.zhangtianzhu.tinkertestdemo.utils;

import com.esri.arcgisruntime.data.Feature;

import java.text.DecimalFormat;

public class FeatureUtils {
    public static Long getFeatureId(Feature feature){

        Long fId = (Long) feature.getAttributes().get(FieldConstants.FEATURE_ID);

        return fId;
    }

    /**
     * 获取图斑地类名称
     * @param feature
     * @return
     */
    public static String getFeatureType(Feature feature){

        String ret = (String) feature.getAttributes().get(FieldConstants.FEATURE_TYPE);

        return ret;
    }

    /**
     * 获取坐落单位代码属性
     * @param feature
     * @return
     */
    public static String getFeatureUnitCode(Feature feature){

        String ret = (String) feature.getAttributes().get(FieldConstants.FEATURE_UNIT_CODE);

        return ret;
    }

    /**
     * 获取坐落单位名称属性
     * @param feature
     * @return
     */
    public static String getFeatureUnitName(Feature feature){

        String ret = (String) feature.getAttributes().get(FieldConstants.FEATURE_UNIT_NAME);

        return ret;
    }

    /**
     * 获取图斑面积
     * @param feature
     * @return
     */
    public static String getFeatureArea(Feature feature){

        Double ret = (Double) feature.getAttributes().get(FieldConstants.FEATURE_AREA);

        if(ret==null) return "";

        //保留两位小数
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        return decimalFormat.format(ret) + " m2";
    }
}
