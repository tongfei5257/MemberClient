package com.example.memberclient.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.memberclient.R;
import com.example.memberclient.application.MyApp;
import com.example.memberclient.model.User;
import com.example.memberclient.model.User2LC;
import com.example.memberclient.utils.CommonUtils;
import com.example.memberclient.utils.ToastUtil;


import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.leancloud.LCUser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class RegisterActivity extends BaseActivity {

    @Bind(R.id.et_reg_username)
    EditText mRegUsername;
    @Bind(R.id.et_reg_code)
    EditText mRegCode;
    @Bind(R.id.et_reg_pass)
    EditText mRegPass;
    @Bind(R.id.tv_get_code)
    TextView mCode;
    @Bind(R.id.et_reg_name)
    TextView et_reg_name;
    Timer timer = null;
    private int count = 60;

    private static Handler mhandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mRegUsername = findViewById(R.id.et_reg_username);
        mRegCode = findViewById(R.id.et_reg_code);
        mRegPass = findViewById(R.id.et_reg_pass);
        mCode = findViewById(R.id.tv_get_code);
        et_reg_name = findViewById(R.id.et_reg_name);
       findViewById(R.id.btn_register).setOnClickListener(this);


        setTitle("用户注册");
        showBackwardView(true);
        timer = new Timer();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                registerClick();
                break;

        }
    }
    @Override
    protected Activity getSubActivity() {
        return this;
    }



    //注册按钮，执行的操作
    public void registerClick() {
        //获取输入框中用户输入的信息
        final String name = mRegUsername.getText().toString().trim();
        final String userName = et_reg_name.getText().toString().trim();
        final String pass = mRegPass.getText().toString().trim();
        final String code = mRegCode.getText().toString().trim();
        if (!CommonUtils.isMobile(name)) {
            ToastUtil.showShort(this, "输入正确的手机号");
            return;
        }

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pass)
                && !TextUtils.isEmpty(userName)
        ) {
            if (MyApp.USE_LC){
                // 创建实例
                User2LC user = new User2LC();
                user.setUsername(name);
                user.setPassword(pass);
                user.setType("1");
                user.setName(userName);
                user.setRemark("管理员");
                user.setPass(pass);

                user.signUpInBackground().subscribe(new Observer<LCUser>() {
                    public void onSubscribe(Disposable disposable) {}
                    public void onNext(LCUser user) {
                        ToastUtil.showShort(getSubActivity(), "注册成功：" + user.getUsername());
                        RegisterActivity.this.finish();
                    }
                    public void onError(Throwable throwable) {
                        // 注册失败（通常是因为用户名已被使用）
                        ToastUtil.showShort(getSubActivity(), "注册失败：" + user.getUsername());
                    }
                    public void onComplete() {}
                });
            }else {
                final User user = new User();
                user.setUsername(name);
                user.setPassword(pass);
                user.setType("1");
                user.setName(userName);
                user.setRemark("管理员");
                user.setPass(pass);
                user.signUp(new SaveListener<User>() {
                    @Override
                    public void done(User user, BmobException e) {
                        if (e == null) {
                            ToastUtil.showShort(getSubActivity(), "注册成功：" + user.getUsername());
                            RegisterActivity.this.finish();

                        } else {
                            ToastUtil.showShort(getSubActivity(), "注册失败：" + user.getUsername());

                        }
                    }
                });
            }

        } else {
            ToastUtil.showShort(this, "请填写完整信息");
        }
    }

    //验证码，执行的操作
    @OnClick(R.id.tv_get_code)
    public void codeClick() {
//        mCode.setEnabled(false);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCode.setText((count--) + " 秒后重新获取");
                        mCode.setEnabled(false);
                        if (count < 1) {
                            timer.cancel();
                            mCode.setEnabled(true);
                        }
                    }
                });

            }
        }, 0, 1000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mRegCode.setText((new Random().nextInt(8999) + 1000) + "");
                    }
                });

            }
        }, 1000);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
