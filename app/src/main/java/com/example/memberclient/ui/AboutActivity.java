package com.example.memberclient.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


import com.example.memberclient.R;

import butterknife.Bind;

public class AboutActivity extends BaseActivity {

    @Bind(R.id.tv_app_version)
    TextView textVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

//        setTitle(getString(R.string.about_us));
        showBackwardView(true);
//        textVersion.setText(AppUtils.getAppVersionName(this));
    }

    @Override
    protected Activity getSubActivity() {
        return this;
    }
}
