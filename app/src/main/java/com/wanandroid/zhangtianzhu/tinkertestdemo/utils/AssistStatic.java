package com.wanandroid.zhangtianzhu.tinkertestdemo.utils;

import android.app.ProgressDialog;
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

    /***
     * 展示ProgressDialog
     */
    public static ProgressDialog ProgressDialogShow(Context context, String title, String text) {
        ProgressDialog progressdialog = new ProgressDialog(context);
        progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressdialog.setTitle(title);
        progressdialog.setMessage(text);
        progressdialog.setCancelable(false);
        progressdialog.setCanceledOnTouchOutside(false);
        progressdialog.show();
        return progressdialog;
    }

    /***
     * 取消ProgressDialog
     */
    public static void ProgressDialogMiss(ProgressDialog progressdialog) {
        if (progressdialog != null && progressdialog.isShowing()) {
            progressdialog.dismiss();
            progressdialog = null;
        }
    }
}
