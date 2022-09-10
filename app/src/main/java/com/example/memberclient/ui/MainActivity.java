package com.example.memberclient.ui;


import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.example.memberclient.R;
import com.example.memberclient.fragment.DetailFragment;
import com.example.memberclient.fragment.MineFragment;
import com.example.memberclient.fragment.ReportFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

//radio_btn_chart  radio_btn_account radio_btn_me radio_btn_detail
public class MainActivity extends BaseActivity1 implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener, View.OnClickListener {
    @Bind(R.id.radio_gp_tab)
    RadioGroup mRadioGpTab;

    @Bind(R.id.vg_main)
    ViewPager mViewPagerMain;

    @Bind(R.id.iv_btn_account)
    ImageView iv_btn_account;
    private long exitTime;
    private DetailFragment detailFragment;
    private MineFragment mineFragment;
    private ReportFragment reportFragment;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        setContentView( R.layout.activity_main);
        ButterKnife.bind(this);
        mRadioGpTab = findViewById(R.id.radio_gp_tab);
        mViewPagerMain = findViewById(R.id.vg_main);
        iv_btn_account = findViewById(R.id.iv_btn_account);
        mRadioGpTab.setOnCheckedChangeListener(this);
        mViewPagerMain.setOnPageChangeListener(this);
        iv_btn_account.setOnClickListener(this);
        final List<Fragment> fragments = new ArrayList<>();
        detailFragment = new DetailFragment();
        mineFragment = new MineFragment();
        reportFragment = new ReportFragment();
        fragments.add(detailFragment);
        fragments.add(reportFragment);

        fragments.add(mineFragment);
        mViewPagerMain.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }
        });
        mRadioGpTab.check(R.id.radio_btn_chart);
        mViewPagerMain.setOffscreenPageLimit(3);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), R.string.text_exit, Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    //radio_btn_chart  radio_btn_account radio_btn_me
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radio_btn_chart://报表
                mViewPagerMain.setCurrentItem(0, true);
                break;
            case R.id.radio_btn_account://记账
                mViewPagerMain.setCurrentItem(1, true);
//                     Intent intent = new Intent(MainActivity.this, BillAddActivity.class);
//                     startActivityForResult(intent, 0);
                break;
            case R.id.radio_btn_me://我的
                mViewPagerMain.setCurrentItem(2, true);

                break;


        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0://报表
                mRadioGpTab.check(R.id.radio_btn_chart);
                break;
            case 1://记账
                mRadioGpTab.check(R.id.radio_btn_account);
                break;
            case 2://我的
                mRadioGpTab.check(R.id.radio_btn_me);
                break;


        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_btn_account://记账
//                     Intent intent = new Intent(MainActivity.this, BillAddActivity.class);
//                     startActivityForResult(intent, 0);
                break;

        }
    }


}
