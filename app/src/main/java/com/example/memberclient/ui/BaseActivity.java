package com.example.memberclient.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;


import com.example.memberclient.R;
import com.example.memberclient.application.MyApp;

import butterknife.ButterKnife;


/**
 * Created by Silence on 2016/4/5
 */
public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {
    private ImageButton mBackwardButton;
    private TextView mTitleTextView;
    private ImageButton mForwardButton;
    private FrameLayout mContentLayout;
    private ImageView mTopDivider;
    protected MyApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        mTitleTextView = (TextView) findViewById(R.id.top_title);
        mTopDivider = (ImageView) findViewById(R.id.top_left_divider);
        mContentLayout = (FrameLayout) findViewById(R.id.layout_content);
        mBackwardButton = (ImageButton) findViewById(R.id.btn_top_backward);
        mForwardButton = (ImageButton) findViewById(R.id.btn_top_forward);
        mForwardButton.setOnClickListener(this);
        mBackwardButton.setOnClickListener(this);
        myApp= (MyApp) getApplicationContext();

    }

    protected void showBackwardView(boolean show, int resId) {
        if (show) {
            mBackwardButton.setImageResource(resId);
            mBackwardButton.setVisibility(View.VISIBLE);
            mTopDivider.setVisibility(View.VISIBLE);
        } else {
            if (mBackwardButton.getVisibility() == View.VISIBLE) {
                mBackwardButton.setVisibility(View.GONE);
                mTopDivider.setVisibility(View.INVISIBLE);
            }
        }
    }

    protected void showBackwardView(boolean show) {
        if (show) {
            if (mBackwardButton.getVisibility() == View.GONE) {
                mBackwardButton.setVisibility(View.VISIBLE);
                mTopDivider.setVisibility(View.VISIBLE);
            }
        } else {
            if (mBackwardButton.getVisibility() == View.VISIBLE) {
                mBackwardButton.setVisibility(View.GONE);
                mTopDivider.setVisibility(View.INVISIBLE);
            }
        }
    }

    protected void showForwardView(boolean show, int resId) {
        if (show) {
            mForwardButton.setImageResource(resId);
            mForwardButton.setVisibility(View.VISIBLE);
        } else {
            if (mForwardButton.getVisibility() == View.VISIBLE) {
                mForwardButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    protected void showForwardView(boolean show) {
        if (show) {
            if (mForwardButton.getVisibility() == View.INVISIBLE) {
                mForwardButton.setVisibility(View.VISIBLE);
            }
        } else {
            if (mForwardButton.getVisibility() == View.VISIBLE) {
                mForwardButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    protected void onBackward() {
        finish();
    }

    protected void onForward() {

    }

    @Override
    public void setTitle(int titleResId) {
        mTitleTextView.setText(titleResId);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitleTextView.setText(title);
    }

    @Override
    public void setContentView(int layoutResId) {
        mContentLayout.removeAllViews();
        View.inflate(this, layoutResId, mContentLayout);
        onContentChanged();
        ButterKnife.bind(getSubActivity());
    }

    @Override
    public void setContentView(View view) {
        mContentLayout.removeAllViews();
        mContentLayout.addView(view);
        onContentChanged();
        ButterKnife.bind(getSubActivity());
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        mContentLayout.removeAllViews();
        mContentLayout.addView(view, params);
        onContentChanged();
        ButterKnife.bind(getSubActivity());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_top_backward:
                onBackward();
                break;
            case R.id.btn_top_forward:
                onForward();
                break;
        }
    }

    protected abstract Activity getSubActivity();

}
