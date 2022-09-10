package com.example.memberclient.ui;



import static com.example.memberclient.utils.DateUtils.FORMAT_D;
import static com.example.memberclient.utils.DateUtils.FORMAT_M;
import static com.example.memberclient.utils.DateUtils.FORMAT_Y;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import com.example.memberclient.model.ConsumeRecord;
import com.example.memberclient.model.Project;
import com.example.memberclient.model.User;
import com.example.memberclient.utils.DateUtils;
import com.example.memberclient.utils.ProgressUtils;
import com.example.memberclient.utils.ToastUtil;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


public class SubConsumeRecordEditActivity extends BaseActivity {


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
    @Bind(R.id.key_id)
    TextView key_id;

    //选择时间
    protected int mYear;
    protected int mMonth;
    protected int mDay;
    protected String days;

    private List<String> projects = new ArrayList<>();
    private List<Project> projectBeans = new ArrayList<>();
    private ConsumeProject consumeProject;
    private ConsumeRecord consumeRecord = new ConsumeRecord();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_consume_record);
        tv_name = findViewById(R.id.tv_name);
        tv_phone = findViewById(R.id.tv_phone);
        number = findViewById(R.id.number);
        remark = findViewById(R.id.remark);
        btn_update_user = findViewById(R.id.btn_update_user);
        btn_delete = findViewById(R.id.btn_delete);
        btn_add_project = findViewById(R.id.btn_add_project);
        btn_update_time = findViewById(R.id.btn_update_time);
        btn_consume_record = findViewById(R.id.btn_consume_record);
        mTimeTv = findViewById(R.id.time);
        key_id = findViewById(R.id.key_id);



        setTitle("子项目的消费记录");
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
            }
        }, mYear, mMonth - 1, mDay).show();
    }


    private void initData() {
        Serializable bean = getIntent().getSerializableExtra("bean");
        if (bean instanceof ConsumeProject) {
            consumeProject = (ConsumeProject) bean;
        } else {
            consumeRecord = (ConsumeRecord) bean;
        }
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
            btn_delete.setVisibility(View.VISIBLE);
            tv_name.setText(consumeRecord.getName());
            if (consumeRecord.getFrom().getParent().getConsumeType() == 1) {
                key_id.setText("金额");
                tv_phone.setText(consumeRecord.getMoney() + "");
            } else {
                key_id.setText("计次");
                tv_phone.setText(consumeRecord.getCount() + "");
            }
            String userDate = consumeRecord.getDate();

            if (!TextUtils.isEmpty(userDate)) {
                Date curDate = DateUtils.getCurDate(userDate, format);
                days = DateUtils.date2Str(curDate, format);
                mTimeTv.setText(userDate);
            }
            remark.setText(consumeRecord.getRemark() + "");
        } else if (action.equals("add")) {
            setTitle(consumeProject.getUser().getName() + "的消费记录");
            if (consumeProject.getParent().getConsumeType() == 1) {
                key_id.setText("金额");
                tv_phone.setText(consumeRecord.getMoney() + "");
            } else {
                key_id.setText("计次");
                tv_phone.setText(consumeRecord.getCount() + "");
            }
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

                if (TextUtils.isEmpty(name) || (TextUtils.isEmpty(phone))) {
                    ToastUtil.showShort(getSubActivity(), "请输入必填项");
                    return;
                }

                consumeRecord.setName(name);
                consumeRecord.setOperator(BmobUser.getCurrentUser(User.class));
                consumeRecord.setDate(days);
                consumeRecord.setRemark(remarkStr);
                ProgressUtils.show(getSubActivity());

                if (action.equals("add")) {
                    consumeRecord.setFrom(consumeProject);
                    if (consumeProject.getParent().getConsumeType() == 1) {
                        consumeRecord.setMoney(Double.parseDouble(phone));
                    } else {
                        consumeRecord.setCount(Integer.parseInt(phone));
                    }

                    consumeRecord.save(new SaveListener<String>() {
                        @Override
                        public void done(String objectId, BmobException e) {
                            ProgressUtils.dismiss(getSubActivity());

                            if (e == null) {
                                Toast.makeText(getSubActivity(), "保存成功：",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(getSubActivity(), "保存失败：", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    if (consumeRecord.getFrom().getParent().getConsumeType() == 1) {
                        consumeRecord.setMoney(Double.parseDouble(phone));
                    } else {
                        consumeRecord.setCount(Integer.parseInt(phone));
                    }
                    consumeRecord.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            ProgressUtils.dismiss(getSubActivity());
                            if (e == null) {
                                Toast.makeText(getSubActivity(), "保存成功：",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(getSubActivity(), "保存失败：",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getSubActivity())
                        .setTitle("提示")
                        .setMessage("确定要删除该消费记录吗")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        consumeRecord.setDelete(true);
                        consumeRecord.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Toast.makeText(getSubActivity(), "删除成功：",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(getSubActivity(), "删除失败：",
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
