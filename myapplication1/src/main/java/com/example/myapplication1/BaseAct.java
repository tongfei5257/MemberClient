package com.example.myapplication1;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class BaseAct extends Activity {

    protected TextView textView;
    protected Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String msg = getPackageName() + "：" + this.getClass().getName() + ":" + getTaskId();
        Log.e("tf_test", msg);
        mActivity=this;
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        textView.setText(msg);
    }
}
