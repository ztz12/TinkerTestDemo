package com.wanandroid.zhangtianzhu.tinkertestdemo.utils;

import android.content.Context;
import android.widget.Toast;

public class AssistStatic {
    //避免多次点击重复弹吐司
    private static Toast toast;
    public static void showToast(Context context, String msg){
        if(toast == null){
            toast = Toast.makeText(context.getApplicationContext(),msg,Toast.LENGTH_SHORT);
        }else {
            toast.setText(msg);
        }
        toast.show();
    }
}
