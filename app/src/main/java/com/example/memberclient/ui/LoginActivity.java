package com.example.memberclient.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.memberclient.R;
import com.example.memberclient.application.MyApp;
import com.example.memberclient.model.User;
import com.example.memberclient.model.User2LC;
import com.example.memberclient.utils.NetUtils;
import com.example.memberclient.utils.ProgressUtils;
import com.example.memberclient.utils.SPUtils;
import com.example.memberclient.utils.ToastUtil;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.leancloud.LCUser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

//登录界面
public class LoginActivity extends BaseActivity {

    @Bind(R.id.et_login_username)
    EditText mEtLoginName;
    @Bind(R.id.et_login_pass)
    EditText mEtLoginPass;
    @Bind(R.id.cb_use_lc)
    CheckBox cb_use_lc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login);
        setTitle("用户登录");
        User cache = BmobUser.getCurrentUser(User.class);
        mEtLoginName = findViewById(R.id.et_login_username);
        mEtLoginPass = findViewById(R.id.et_login_pass);
        cb_use_lc = findViewById(R.id.cb_use_lc);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.label_register).setOnClickListener(this);
        if (cache != null && !MyApp.USE_LC) {
            mEtLoginName.setText(cache.getUsername());
            mEtLoginPass.setText(cache.getPass());
            Toast.makeText(LoginActivity.this, "登录成功：" + cache.getUsername(),
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
        if (LCUser.getCurrentUser(User2LC.class) != null && MyApp.USE_LC) {
            mEtLoginName.setText(LCUser.getCurrentUser().getUsername());
//            mEtLoginPass.setText(cache.getPass());
            Toast.makeText(LoginActivity.this, "登录成功：" + LCUser.getCurrentUser().getUsername(),
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
        cb_use_lc.setChecked(SPUtils.getBoolean(getApplicationContext(),"user_lc",false));
        cb_use_lc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                SPUtils.saveBoolean(getApplicationContext(),"user_lc",isChecked);
//                MyApp.USE_LC=isChecked;
            }
        });
//        1111111
    }

    @OnClick({R.id.btn_login, R.id.label_register, R.id.label_forget})
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.btn_login: {
                if (!NetUtils.isConnected(this)) {
                    ToastUtil.showShort(this, getString(R.string.check_net_connect));
                    return;
                }
                String name = mEtLoginName.getText().toString().trim();
                final String pass = mEtLoginPass.getText().toString().trim();
                if (MyApp.USE_LC) {
                    // 创建实例
                    User2LC lcUser = new User2LC();
// 等同于 user.put("username", "Tom")
                    lcUser.setUsername(name);
                    lcUser.setPassword(pass);
                    lcUser.setType("1");

                    LCUser.logIn(name, pass).subscribe(new Observer<LCUser>() {
                        public void onSubscribe(Disposable disposable) {
                        }

                        public void onNext(LCUser user) {
                            // 注册成功
                            Toast.makeText(LoginActivity.this, "登录成功：" + user.getUsername(),
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));

                        }

                        public void onError(Throwable throwable) {
                            // 注册失败（通常是因为用户名已被使用）
                            Toast.makeText(LoginActivity.this, Log.getStackTraceString(throwable),
                                    Toast.LENGTH_SHORT).show();
                        }

                        public void onComplete() {
                        }
                    });
                } else {
                    final User user = new User();
                    //此处替换为你的用户名
                    user.setUsername(name);
                    user.setType("1");
                    //此处替换为你的密码
                    user.setPassword(pass);
                    ProgressUtils.show(getSubActivity());
                    user.login(new SaveListener<User>() {
                        @Override
                        public void done(User bmobUser, BmobException e) {
                            ProgressUtils.dismiss();
                            if (e == null) {
                                User user = BmobUser.getCurrentUser(User.class);
                                Toast.makeText(LoginActivity.this, "登录成功：" + user.getUsername(),
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));

                            } else {
                                Toast.makeText(LoginActivity.this, e.toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }


            break;
            case R.id.label_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.label_forget:
//                startActivity(new Intent(this, ForgetPassActivity.class));
                break;
        }

    }


    @Override
    protected Activity getSubActivity() {
        return this;
    }

}
