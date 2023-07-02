package com.example.myapplication1;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class ParentViewGroup extends LinearLayout {

    public ParentViewGroup(Context context) {
        super(context);
    }
    private boolean isIntercept=false;
    public ParentViewGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean b = super.dispatchTouchEvent(ev);
        if (!b){
            isIntercept=true;
            return true;
        }
        Log.e("tf_test","ParentViewGroup dispatchTouchEvent,action="+ev.getAction()+"，b="+b);
        return b;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean b = super.onInterceptTouchEvent(ev);
//        Log.e("tf_test","ParentViewGroup onInterceptTouchEvent,action="+ev.getAction()+"，b="+b);
        return b;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean b = super.onTouchEvent(event);
        Log.e("tf_test","ParentViewGroup onTouchEvent,action="+event.getAction()+"，b="+b);
        if (isIntercept){
            return true;
        }
        return b;
    }
}
