package com.wanandroid.zhangtianzhu.tinkertestdemo.utils;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Field;

public class Binding {
    public static void bind(Activity activity) {
        //通过反射获取Activity内部view
        for (Field field : activity.getClass().getDeclaredFields()) {
            BindView bindView = field.getAnnotation(BindView.class);
            if (bindView != null) {
                field.setAccessible(true);
                View view = activity.findViewById(bindView.value());
                try {
                    field.set(activity, view);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
