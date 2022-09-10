package com.example.memberclient.ui;


import static com.example.memberclient.utils.DateUtils.FORMAT_D;
import static com.example.memberclient.utils.DateUtils.FORMAT_M;
import static com.example.memberclient.utils.DateUtils.FORMAT_Y;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memberclient.R;
import com.example.memberclient.model.Project;
import com.example.memberclient.model.User;
import com.example.memberclient.utils.DateUtils;
import com.example.memberclient.utils.ProgressUtils;



import java.util.Date;

import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


public class ProjectDetailActivity extends BaseActivity {


    @Bind(R.id.spn_type)
    Spinner spn_type;
    @Bind(R.id.consumeType)
    Spinner consumeType;
    @Bind(R.id.t_total)
    TextView t_total;
    @Bind(R.id.number)
    EditText t_total_count;
    @Bind(R.id.name)
    EditText name;

    @Bind(R.id.remark)
    EditText remark;
    @Bind(R.id.money)
    EditText money;

    @Bind(R.id.time)
    TextView mTimeTv;
    @Bind(R.id.btn_update_user)
    Button btn_update_user;
    @Bind(R.id.btn_delete)
    Button btn_delete;

    @Bind(R.id.btn_update_time)
    Button btn_update_time;
    @Bind(R.id.money_root)
    View money_root;


    private Project mProject = new Project();
    //选择时间
    protected int mYear;
    protected int mMonth;
    protected int mDay;
    protected String days;

    private final String[] types = new String[]{"鲜花", "乐园"};
    private final String[] consumeTypes = new String[]{"使用金额", "计次消费"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_detail);
        spn_type = findViewById(R.id.spn_type);
        consumeType = findViewById(R.id.consumeType);

        t_total = findViewById(R.id.t_total);
        t_total_count = findViewById(R.id.number);
        name = findViewById(R.id.name);
        remark = findViewById(R.id.remark);
        money = findViewById(R.id.money);
        mTimeTv = findViewById(R.id.time);
        btn_update_user = findViewById(R.id.btn_update_user);
        btn_delete = findViewById(R.id.btn_delete);
        btn_update_time = findViewById(R.id.btn_update_time);
        money_root = findViewById(R.id.money_root);


        setTitle("项目操作");
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


    @SuppressLint("SetTextI18n")
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
            mProject = (Project) getIntent().getSerializableExtra("bean");
            remark.setText(mProject.getRemark());
            money.setText(mProject.getMoney() + "");
            if (mProject.getConsumeType() == 1) {
                t_total_count.setText(mProject.getMoney() + "");
            } else if (mProject.getConsumeType() == 2) {
                t_total_count.setText(mProject.getTotalCount() + "");
            }
            btn_delete.setVisibility(View.VISIBLE);
            String userDate = mProject.getDate();
            if (!TextUtils.isEmpty(userDate)) {
                Date curDate = DateUtils.getCurDate(userDate, format);
                days = DateUtils.date2Str(curDate, format);
                mTimeTv.setText(userDate);
            }
            name.setText(mProject.getName());
        } else if (action.equals("add")) {
            btn_delete.setVisibility(View.GONE);
        }

        btn_update_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String numberStr = t_total_count.getText().toString();
                String remarkStr = remark.getText().toString();
                String moneyStr = money.getText().toString();
                String nameStr = name.getText().toString();

                mProject.setType(spn_type.getSelectedItemPosition() + 1);
                mProject.setConsumeType(consumeType.getSelectedItemPosition() + 1);
                if (!TextUtils.isEmpty(numberStr)) {
                    mProject.setTotalCount(Integer.valueOf(numberStr));
                }
                mProject.setMoney(Double.valueOf(moneyStr));
                mProject.setDate(days);
                mProject.setRemark(remarkStr);
                mProject.setOperator(BmobUser.getCurrentUser(User.class));
                mProject.setName(nameStr);
                ProgressUtils.show(getSubActivity());

                if (action.equals("add")) {
                    mProject.save(new SaveListener<String>() {
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
                    mProject.update(new UpdateListener() {
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
                        .setMessage("确定要删除 " + mProject.getName() + " 项目吗")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mProject.setDelete(true);
                        ProgressUtils.show(getSubActivity());

                        mProject.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                ProgressUtils.dismiss(getSubActivity());

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
        setConsumeTypeSpinner();
        setTypeSpinner();
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

    private void setTypeSpinner() {
        final String[] identify_ShowText = types;

        // 将可选内容与ArrayAdapter连接起来
        ArrayAdapter identify_adapter = new ArrayAdapter<String>(getSubActivity(), android.R.layout.simple_spinner_item, identify_ShowText);
        // 设置下拉列表的风格
        identify_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 将adapter 添加到spinner中
        spn_type.setAdapter(identify_adapter);
        // 添加事件Spinner事件监听
        spn_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String typeName = identify_ShowText[position];
                mProject.setType(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });
        if (mProject.getType() > 0) {
            spn_type.setSelection(mProject.getType() - 1);

        } else {
            spn_type.setSelection(1);
        }
    }

    private void setConsumeTypeSpinner() {
        final String[] identify_ShowText = consumeTypes;

        // 将可选内容与ArrayAdapter连接起来
        ArrayAdapter identify_adapter = new ArrayAdapter<String>(getSubActivity(), android.R.layout.simple_spinner_item, identify_ShowText);
        // 设置下拉列表的风格
        identify_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 将adapter 添加到spinner中
        consumeType.setAdapter(identify_adapter);
        // 添加事件Spinner事件监听
        consumeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String typeName = identify_ShowText[position];
                mProject.setConsumeType(position + 1);
                if (position == 0) {
//   使用金额
                    t_total.setText("总金额");
                } else if (position == 1) {
                    //   使用次数
                    t_total.setText("总次数");

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });
        if (mProject.getConsumeType() > 0) {
            consumeType.setSelection(mProject.getConsumeType() - 1);
        } else {
            consumeType.setSelection(1);
        }
    }

}
