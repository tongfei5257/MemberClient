package com.example.memberclient.utils;

import android.app.ProgressDialog;
import android.content.Context;


/**
 * Created by tf on 2017/12/28.
 * ProgressDialog封装工具类
 */
public class ProgressUtils {

    private static ProgressDialog dialog = null;

    public static void show(Context context){
        show(context, "数据加载中,请稍后");
    }

    public static void show(Context context, String msg){
//        Log.e("tf_test",Log.getStackTraceString(new Throwable("show")));
        dialog = new ProgressDialog(context);
        dialog.setMessage(msg == null ? "正在加载中...." : msg);
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void dismiss(){
//        Log.e("tf_test",Log.getStackTraceString(new Throwable("dismiss1")));

        if(dialog != null && dialog.isShowing()){
//            Log.e("tf_test","dismiss1");
            dialog.dismiss();
        }
    }
    public static void dismiss(Context context){
//        Log.e("tf_test",Log.getStackTraceString(new Throwable("dismiss2")));
        if(dialog != null && dialog.isShowing()){
//            Log.e("tf_test","dismiss2");
            dialog.dismiss();
        }
    }
}
