package com.example.myapplication1;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class MyTextView extends TextView {
    private Camera camera;
    public MyTextView(Context context) {
        super(context);
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
         camera = new Camera();
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean b = super.dispatchTouchEvent(ev);
        Log.e("tf_test","MyTextView dispatchTouchEvent,action="+ev.getAction()+"，b="+b);
        return b;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        resolveSize()
        setMeasuredDimension();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        animate().scaleX(1).start();
        boolean b = super.onTouchEvent(event);
        Log.e("tf_test","MyTextView onTouchEvent,action="+event.getAction()+"，b="+b);
        return b;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        camera.save();
        camera.rotateX(30);
        camera.rotateX(30);
        camera.applyToCanvas(canvas);

        super.onDraw(canvas);
    }
}
