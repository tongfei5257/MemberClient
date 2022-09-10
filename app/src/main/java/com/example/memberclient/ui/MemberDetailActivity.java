package com.example.memberclient.ui;



import static com.example.memberclient.utils.DateUtils.FORMAT_D;
import static com.example.memberclient.utils.DateUtils.FORMAT_M;
import static com.example.memberclient.utils.DateUtils.FORMAT_Y;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memberclient.R;
import com.example.memberclient.model.ConsumeProject;
import com.example.memberclient.model.Project;
import com.example.memberclient.model.User;
import com.example.memberclient.model.User2;
import com.example.memberclient.utils.CommonUtils;
import com.example.memberclient.utils.DateUtils;
import com.example.memberclient.utils.ProgressUtils;
import com.example.memberclient.utils.ToastUtil;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


public class MemberDetailActivity extends BaseActivity {


    @Bind(R.id.tv_name)
    EditText tv_name;
    @Bind(R.id.tv_phone)
    EditText tv_phone;
    @Bind(R.id.number)
    EditText number;
    @Bind(R.id.remark)
    EditText remark;
    @Bind(R.id.btn_update_user)
    Button btn_update_user;
    @Bind(R.id.btn_delete)
    Button btn_delete;
    @Bind(R.id.btn_add_project)
    Button btn_add_project;
    @Bind(R.id.btn_update_time)
    Button btn_update_time;
    @Bind(R.id.btn_consume_record)
    Button btn_consume_record;
    @Bind(R.id.time)
    TextView mTimeTv;

    private User2 mUser;
    //选择时间
    protected int mYear;
    protected int mMonth;
    protected int mDay;
    protected String days;

    private List<String> projects = new ArrayList<>();
    private List<Project> projectBeans = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);
        tv_name = findViewById(R.id.tv_name);
        tv_phone = findViewById(R.id.tv_phone);
        number = findViewById(R.id.number);
        btn_update_user = findViewById(R.id.btn_update_user);
        btn_delete = findViewById(R.id.btn_delete);
        btn_add_project = findViewById(R.id.btn_add_project);
        btn_update_time = findViewById(R.id.btn_update_time);
        btn_consume_record = findViewById(R.id.btn_consume_record);
        remark = findViewById(R.id.remark);
        mTimeTv = findViewById(R.id.time);

        setTitle("会员操作");
        showBackwardView(true);

        initData();

    }

    /**
     * 显示日期选择器
     */
    public void showTimeSelector() {
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                mYear = i;
                mMonth = i1;
                mDay = i2;
                if (mMonth + 1 < 10) {
                    if (mDay < 10) {
                        days = new StringBuffer().append(mYear).append("-").append("0").
                                append(mMonth + 1).append("-").append("0").append(mDay).toString();
                    } else {
                        days = new StringBuffer().append(mYear).append("-").append("0").
                                append(mMonth + 1).append("-").append(mDay).toString();
                    }

                } else {
                    if (mDay < 10) {
                        days = new StringBuffer().append(mYear).append("-").
                                append(mMonth + 1).append("-").append("0").append(mDay).toString();
                    } else {
                        days = new StringBuffer().append(mYear).append("-").
                                append(mMonth + 1).append("-").append(mDay).toString();
                    }

                }
                mTimeTv.setText(days);
                mMonth+=1;
            }
        }, mYear, mMonth - 1, mDay).show();
    }


    private void initData() {

        //设置日期选择器初始日期
        mYear = Integer.parseInt(DateUtils.getCurYear(FORMAT_Y));
        mMonth = Integer.parseInt(DateUtils.getCurMonth(FORMAT_M));
        mDay = Integer.parseInt(DateUtils.getCurDay(FORMAT_D));
        //设置当前 日期
        String format = "yyyy-MM-dd";
        days = DateUtils.getCurDateStr(format);
        mTimeTv.setText(days);
        final String action = getIntent().getStringExtra("action");
        if (action.equals("edit")) {
            mUser = (User2) getIntent().getSerializableExtra("bean");
            tv_name.setText(mUser.getName());
            tv_phone.setText(mUser.getUsername());
            number.setText(mUser.getNumber());
            remark.setText(mUser.getRemark());
            btn_delete.setVisibility(View.VISIBLE);
            String userDate = mUser.getDate();
            if (!TextUtils.isEmpty(userDate)) {
                Date curDate = DateUtils.getCurDate(userDate, format);
                days = DateUtils.date2Str(curDate, format);
                mTimeTv.setText(userDate);
            }
        } else if (action.equals("add")) {
            btn_delete.setVisibility(View.GONE);
            btn_consume_record.setVisibility(View.GONE);
            btn_add_project.setVisibility(View.GONE);
        }

        btn_update_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = tv_name.getText().toString();
                String phone = tv_phone.getText().toString();
                String numberStr = number.getText().toString();
                String remarkStr = remark.getText().toString();

                if (TextUtils.isEmpty(name) || (TextUtils.isEmpty(phone)) || (TextUtils.isEmpty(numberStr))) {
                    ToastUtil.showShort(getSubActivity(), "请输入必填项");
                    return;
                }
                if (!CommonUtils.isMobile(phone)) {
                    ToastUtil.showShort(getSubActivity(), "输入正确的手机号");
                    return;
                }


                if (action.equals("add")) {
                    final User2 user = new User2();
                    user.setName(name);
                    user.setUsername(phone);
                    user.setNumber(numberStr);
                    user.setRemark(remarkStr);
                    user.setPassword(phone);
                    user.setPass(phone);
                    user.setType("2");
                    user.setDate(days);
                    user.setOperator(BmobUser.getCurrentUser(User.class));
                    new AlertDialog.Builder(getSubActivity())
                            .setTitle("提示")
                            .setMessage("确定要增加 " + user.getUsername() + " 用户吗")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ProgressUtils.show(getSubActivity());

                            user.save(new SaveListener<String>() {
                                @Override
                                public void done(String objectId, BmobException e) {
                                    ProgressUtils.dismiss(getSubActivity());


                                    if (e == null) {
                                        Toast.makeText(getSubActivity(), "保存成功：" + user.getUsername(),
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(getSubActivity(), "保存失败：" + user.getUsername(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }).show();




                } else {
                    mUser.setName(name);
                    mUser.setUsername(phone);
                    mUser.setNumber(numberStr);
                    mUser.setRemark(remarkStr);
                    mUser.setType("2");
                    mUser.setDate(days);
                    mUser.setOperator(BmobUser.getCurrentUser(User.class));
                    new AlertDialog.Builder(getSubActivity())
                            .setTitle("提示")
                            .setMessage("确定要修改 " + mUser.getName() + " 用户吗")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ProgressUtils.show(getSubActivity());
                            mUser.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    ProgressUtils.dismiss(getSubActivity());
                                    if (e == null) {
                                        Toast.makeText(getSubActivity(), "保存成功：" + mUser.getUsername(),
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(getSubActivity(), "保存失败：" + mUser.getUsername(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }).show();

                }
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getSubActivity())
                        .setTitle("提示")
                        .setMessage("确定要删除 " + mUser.getName() + " 用户吗")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProgressUtils.show(getSubActivity());

                        mUser.setDelete(true);
                        mUser.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                ProgressUtils.dismiss(getSubActivity());

                                if (e == null) {
                                    Toast.makeText(getSubActivity(), "删除成功：" + mUser.getUsername(),
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(getSubActivity(), "删除失败：" + mUser.getUsername(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).show();

            }
        });
        mTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeSelector();
            }
        });
        btn_update_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeSelector();
            }
        });
        btn_add_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (projects.isEmpty()) {
                    ToastUtil.showShort(getSubActivity(), "项目未加载成功，请稍后");
                    return;
                }
                showSingleChoiceDialog();
            }
        });
        BmobQuery<Project> query = new BmobQuery<>();
        if (myApp.projectBeans.isEmpty()) {
            query.addWhereEqualTo("delete", false)
                    .order("-createdAt")
                    .findObjects(new FindListener<Project>() {
                        @Override
                        public void done(List<Project> list, BmobException e) {
                            ProgressUtils.dismiss(getSubActivity());

                            if (e == null) {
                                myApp.projectBeans = list;
                                projects.clear();
                                for (Project project : list) {
                                    projects.add("(" + project.getTypeString() + ")" + project.getName() + ",金额=" + project.getMoney() + ",类型=" + project.getConsumeTypeString());
                                }
                                projectBeans = list;

                            } else {
                                Toast.makeText(getApplicationContext(), "搜索异常", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
        } else {
            for (Project project : myApp.projectBeans) {
                projects.add("(" + project.getTypeString() + ")" + project.getName() + ",金额=" + project.getMoney() + ",类型=" + project.getConsumeTypeString());
            }
            projectBeans= myApp.projectBeans;
        }


        btn_consume_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getSubActivity(), ConsumeProjectActivity.class);
                intent.putExtra("user", mUser);
                startActivity(intent);
            }
        });
    }

    int yourChoice = 0;

    private void showSingleChoiceDialog() {
        final String[] items = projects.toArray(new String[projects.size()]);
        yourChoice = items.length - 1;
        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(getSubActivity());
        singleChoiceDialog.setTitle("请选择项目");
        // 第二个参数是默认选项，此处设置为0
        singleChoiceDialog.setSingleChoiceItems(items, yourChoice,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yourChoice = which;
                    }
                });
        singleChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ConsumeProject consumeProject = new ConsumeProject();
                        consumeProject.setUser(mUser);
                        consumeProject.setOperator(BmobUser.getCurrentUser(User.class));
                        consumeProject.setParent(projectBeans.get(yourChoice));
                        consumeProject.setDelete(false);
                        consumeProject.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    ToastUtil.showShort(getSubActivity(), "添加成功");

                                } else {
                                    ToastUtil.showShort(getSubActivity(), s);

                                }
                            }
                        });
                    }
                });
        singleChoiceDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        singleChoiceDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

    }

    @Override
    protected void onBackward() {
        finish();
    }

    @Override
    protected Activity getSubActivity() {
        return this;
    }


}
