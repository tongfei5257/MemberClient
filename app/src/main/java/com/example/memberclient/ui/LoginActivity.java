package com.example.memberclient.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.memberclient.R;
import com.example.memberclient.model.User;
import com.example.memberclient.utils.NetUtils;
import com.example.memberclient.utils.ProgressUtils;
import com.example.memberclient.utils.ToastUtil;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

//登录界面
public class LoginActivity extends BaseActivity {

    @Bind(R.id.et_login_username)
    EditText mEtLoginName;
    @Bind(R.id.et_login_pass)
    EditText mEtLoginPass;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login);
        setTitle("用户登录");
        User cache = BmobUser.getCurrentUser(User.class);
        mEtLoginName=findViewById(R.id.et_login_username);
        mEtLoginPass=findViewById(R.id.et_login_pass);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.label_register).setOnClickListener(this);
        if (cache != null) {
            mEtLoginName.setText(cache.getUsername());
            mEtLoginPass.setText(cache.getPass());
            Toast.makeText(LoginActivity.this, "登录成功：" + cache.getUsername(),
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }

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
