package com.example.memberclient.fragment;


import static com.example.memberclient.utils.DateUtils.FORMAT_D;
import static com.example.memberclient.utils.DateUtils.FORMAT_M;
import static com.example.memberclient.utils.DateUtils.FORMAT_Y;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.memberclient.R;
import com.example.memberclient.application.MyApp;
import com.example.memberclient.model.ConsumeProjectLC;
import com.example.memberclient.model.ConsumeRecordLC;
import com.example.memberclient.model.ProjectLC;
import com.example.memberclient.model.User2LC;
import com.example.memberclient.model.UserLC;
import com.example.memberclient.ui.ConsumeProjectActivity;
import com.example.memberclient.model.ConsumeProject;
import com.example.memberclient.model.ConsumeRecord;
import com.example.memberclient.model.Project;
import com.example.memberclient.model.User;
import com.example.memberclient.model.User2;
import com.example.memberclient.utils.DateUtils;
import com.example.memberclient.utils.ProgressUtils;
import com.example.memberclient.utils.Utils;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;
import cn.leancloud.LCUser;

public class ReportFragment extends BaseFragment {


    @Bind(R.id.spn_type)
    Spinner spn_type;
    //选择时间
    protected int mYear;
    protected int mMonth;
    protected int mDay;
    protected int mYear2;
    protected int mMonth2;
    protected int mDay2;
    protected String days;
    protected String days2;
    @Bind(R.id.time)
    TextView mTimeTv;
    @Bind(R.id.time2)
    TextView mTimeTv2;
    @Bind(R.id.search1)
    Button btnSearch;
    @Bind(R.id.user)
    TextView user;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private List datas = new ArrayList<>();
    @Bind(R.id.btn_update_time)
    Button btn_update_time;

    @Bind(R.id.btn_update_time2)
    Button btn_update_time2;
    private String format;
    @Bind(R.id.record)
    TextView record;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main3;
    }

    @Override
    protected Fragment getFragement() {
        return this;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mTimeTv = root.findViewById(R.id.time);
        spn_type = root.findViewById(R.id.spn_type);
        mTimeTv2 = root.findViewById(R.id.time2);
        btnSearch = root.findViewById(R.id.search1);
        user = root.findViewById(R.id.user);
        recyclerView = root.findViewById(R.id.recyclerView);
        btn_update_time = root.findViewById(R.id.btn_update_time);
        btn_update_time2 = root.findViewById(R.id.btn_update_time2);
        record = root.findViewById(R.id.record);
        ;


        setTypeSpinner();
        spn_type.setBackgroundColor(Color.GRAY);
        mYear = Integer.parseInt(DateUtils.getCurYear(FORMAT_Y));
        mMonth = Integer.parseInt(DateUtils.getCurMonth(FORMAT_M));
        mDay = Integer.parseInt(DateUtils.getCurDay(FORMAT_D));

        mYear2 = Integer.parseInt(DateUtils.getCurYear(FORMAT_Y));
        mMonth2 = Integer.parseInt(DateUtils.getCurMonth(FORMAT_M));
        mDay2 = Integer.parseInt(DateUtils.getCurDay(FORMAT_D));
        //设置当前 日期
        format = "yyyy-MM-dd";
        days = DateUtils.getCurDateStr(format);
        days2 = DateUtils.getCurDateStr(format);
        mTimeTv.setText(days);
        mTimeTv2.setText(days2);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//        user.setText("当前操作人:" + BmobUser.getCurrentUser(User.class).getName());
        if (MyApp.USE_LC) {
            user.setText("当前操作人:" + LCUser.getCurrentUser().getUsername());
        } else {
            user.setText("当前操作人:" + BmobUser.getCurrentUser(User.class).getName());
        }
        myAdapter = new MyAdapter(this.getContext());
        myAdapter.setData(datas);
        recyclerView.setAdapter(myAdapter);
        btn_update_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeSelector();
            }
        });
        btn_update_time2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeSelectorV2();
            }
        });
        mTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeSelector();
            }
        });
        mTimeTv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeSelectorV2();
            }
        });
//        search();
    }

    private void showTimeSelectorV2() {
        Log.e("tf_test", "mYear=" + mYear2 + "，mMonth=" + mMonth2 + ",mDay=" + mDay2);

        format = "yyyy-MM-dd";
        days2 = DateUtils.getCurDateStr(format);
        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                mYear2 = i;
                mMonth2 = i1;
                mDay2 = i2;
                Log.e("tf_test", "after mYear=" + mYear2 + "，mMonth=" + mMonth2 + ",mDay=" + mDay2);
                if (mMonth2 + 1 < 10) {
                    if (mDay2 < 10) {
                        days2 = new StringBuffer().append(mYear2).append("-").append("0").
                                append(mMonth2 + 1).append("-").append("0").append(mDay2).toString();
                    } else {
                        days2 = new StringBuffer().append(mYear2).append("-").append("0").
                                append(mMonth2 + 1).append("-").append(mDay2).toString();
                    }

                } else {
                    if (mDay2 < 10) {
                        days2 = new StringBuffer().append(mYear2).append("-").
                                append(mMonth2 + 1).append("-").append("0").append(mDay2).toString();
                    } else {
                        days2 = new StringBuffer().append(mYear2).append("-").
                                append(mMonth2 + 1).append("-").append(mDay2).toString();
                    }

                }
                mTimeTv2.setText(days2);
                mMonth2 += 1;
            }
        }, mYear2, mMonth2 - 1, mDay2).show();
    }

    private void search() {


        int position = spn_type.getSelectedItemPosition();
        try {
            ProgressUtils.show(getActivity(), "");
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date start = null;
            Date end = null;

            start = sdf.parse(days);
            end = sdf.parse(days2);
            long l = start.getTime() + 24 * 60 * 60 * 1000;
            end = new Date(end.getTime() + 24 * 60 * 60 * 1000);
            if (MyApp.USE_LC) {
                if (position == 0) {
                    Date finalStart = start;
                    Date finalEnd = end;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //            消费项目
                            LCQuery<LCObject> lcObjectLCQuery = new LCQuery<>("ConsumeProjectLC")
                                    .whereEqualTo("delete", false)
                                    .orderByDescending("createdAt")
                                    .include("parent,user");
//                                    .whereLessThan("createdAt", finalStart)
//                                    .whereGreaterThan("createdAt", finalEnd)
                            ;
                            ArrayList<LCObject> result = new ArrayList<>();
                            Utils.queryLCConsumeProject(lcObjectLCQuery, 1000, 0, result);
                            ProgressUtils.dismiss(getActivity());
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    datas.clear();
                                    for (LCObject lcObject:result) {
                                        Date createdAt = lcObject.getCreatedAt();
                                        if (createdAt.after(finalStart)&&createdAt.before(finalEnd)) {
                                            datas.add(ConsumeProjectLC.toBean(lcObject));
                                        }
                                    }
                                    record.setText("消费项目总计：" + datas.size() + "个");
                                    myAdapter.setData(datas);
                                    myAdapter.notifyDataSetChanged();
                                }
                            });

                        }
                    }).start();

                } else if (position == 1) {
                    Date finalStart1 = start;
                    Date finalEnd1 = end;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            LCQuery<LCObject> lcObjectLCQuery = new LCQuery<>("ConsumeRecordLC")
                                    .whereEqualTo("delete", false);
                            lcObjectLCQuery.orderByDescending("createdAt")
                                    .include("from,from.parent,,from.user");
                            ArrayList<LCObject> result = new ArrayList<>();
                            Utils.queryLCConsumeRecord(lcObjectLCQuery, 1000, 0, result);
                            ProgressUtils.dismiss(getActivity());
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    List<ConsumeRecordLC> list = new ArrayList<>();

                                    for (LCObject lcObject : result) {
                                        Date createdAt = lcObject.getCreatedAt();
                                        if (createdAt.after(finalStart1)&&createdAt.before(finalEnd1)) {
                                            list.add(ConsumeRecordLC.toBean(lcObject));
                                        }
                                    }
                                    int count = 0;
                                    for (ConsumeRecordLC record : list) {
                                        count += record.getCount();
                                    }
                                    record.setText("子消费项目总计：" + result.size() + "个,已核销" + count + "次");
                                    datas.clear();
                                    datas.addAll(list);

                                    myAdapter.setData(datas);
                                    myAdapter.notifyDataSetChanged();
                                }
                            });

                        }
                    }).start();


                }
            } else {
                BmobDate startDate = new BmobDate(start);
                BmobDate endDate = new BmobDate(end);

                if (position == 0) {
//            消费项目
                    BmobQuery<ConsumeProject> query1 = new BmobQuery<>();
                    query1.addWhereEqualTo("delete", false)
                            .order("-createdAt")
                            .include("operator,parent,user")
                            .setLimit(300)
                            .addWhereLessThan("createdAt", endDate)
                            .addWhereGreaterThan("createdAt", startDate);

                    Utils.queryConsumeProject(query1, 150, 0, new FindListener<ConsumeProject>() {
                        @Override
                        public void done(List<ConsumeProject> list, BmobException e) {
                            ProgressUtils.dismiss(getActivity());
                            if (e == null) {
//                                    datas = new ArrayList<>(list);
                                record.setText("消费项目总计：" + list.size() + "个");
                                datas.clear();
                                datas.addAll(list);
                                myAdapter.setData(datas);
                                myAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getContext(), "搜索异常=" + Log.getStackTraceString(e), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else if (position == 1) {
                    BmobQuery<ConsumeRecord> query = new BmobQuery<>();
                    query.addWhereEqualTo("delete", false)
                            .order("-createdAt")
                            .include("from,operator,from.user,from.parent")
                            .addWhereLessThan("createdAt", endDate)
                            .addWhereGreaterThan("createdAt", startDate);
                    Utils.queryConsumeRecord(query, 100, 0, new FindListener<ConsumeRecord>() {
                        @Override
                        public void done(List<ConsumeRecord> list, BmobException e) {
                            ProgressUtils.dismiss(getActivity());
                            if (e == null) {
//                                    datas = new ArrayList<>(list);
                                int count = 0;
                                for (ConsumeRecord record : list) {
                                    count += record.getCount();
                                }
                                record.setText("子消费项目总计：" + list.size() + "个,已核销" + count + "次");

                                datas.clear();
                                datas.addAll(list);
                                myAdapter.setData(datas);
                                myAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getContext(), "搜索异常=" + Log.getStackTraceString(e), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
            }

        } catch (Exception e) {
            ProgressUtils.dismiss(getActivity());

        }
    }

    private final String[] types = new String[]{"消费项目", "子消费项目"};

    private void setTypeSpinner() {
        final String[] identify_ShowText = types;

        // 将可选内容与ArrayAdapter连接起来
        ArrayAdapter identify_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, identify_ShowText);
        // 设置下拉列表的风格
        identify_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 将adapter 添加到spinner中
        spn_type.setAdapter(identify_adapter);
        // 添加事件Spinner事件监听
        spn_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });
        spn_type.setSelection(0);
    }

    /**
     * 显示日期选择器
     */
    public void showTimeSelector() {
        Log.e("tf_test", "mYear=" + mYear + "，mMonth=" + mMonth + ",mDay=" + mDay);

        format = "yyyy-MM-dd";
        days = DateUtils.getCurDateStr(format);
        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                mYear = i;
                mMonth = i1;
                mDay = i2;
                Log.e("tf_test", "after mYear=" + mYear + "，mMonth=" + mMonth + ",mDay=" + mDay);
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
                mMonth += 1;
            }
        }, mYear, mMonth - 1, mDay).show();
    }

    private class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<Object> list;
        Context context;

        public void setData(List<Object> list) {
            this.list = list;
        }

        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_recycler_consume_project, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Object bean = list.get(position);
            holder.bindView(context, bean);

            holder.card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean instanceof ConsumeProject) {
                        Intent intent = new Intent(getActivity(), ConsumeProjectActivity.class);
                        User2 user = ((ConsumeProject) bean).getUser();
                        intent.putExtra("user", user);
                        intent.putExtra("ConsumeProject", (ConsumeProject) bean);
                        startActivity(intent);
                    }else if (bean instanceof ConsumeProjectLC) {
                        Intent intent = new Intent(getActivity(), ConsumeProjectActivity.class);
                        UserLC user = ((ConsumeProjectLC) bean).getUser();
                        intent.putExtra("user", user);
                        intent.putExtra("ConsumeProject", (ConsumeProjectLC) bean);
                        startActivity(intent);
                    }

                }
            });


        }

        @Override
        public int getItemCount() {
            return list.size();
        }


    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView phone;
        private TextView time;
        private TextView time1;
        private TextView number;
        private TextView remark;
        private TextView name1;
        private TextView name2;
        private Button btn_update;
        private Button btn_delete;
        private LinearLayout ll_btn;

        public CardView card_view;


        private View.OnClickListener onClickListener;

        public void setOnClickListener(View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            phone = view.findViewById(R.id.phone);
            time = view.findViewById(R.id.time);
            time1 = view.findViewById(R.id.time1);
            number = view.findViewById(R.id.number);
            remark = view.findViewById(R.id.remark);
            name1 = view.findViewById(R.id.name1);
            name2 = view.findViewById(R.id.name2);
            btn_update = view.findViewById(R.id.btn_update);
            btn_delete = view.findViewById(R.id.btn_delete);
            card_view = view.findViewById(R.id.card_view);
            ll_btn = view.findViewById(R.id.ll_btn);

        }

        public void bindView(Context context, final Object bean) {
            btn_delete.setVisibility(View.GONE);
            ll_btn.setVisibility(View.GONE);
            if (bean instanceof ConsumeProject) {
                ConsumeProject consumeProject = (ConsumeProject) bean;
                Project parent = consumeProject.getParent();
                User2 user = consumeProject.getUser();
                name2.setText("所属会员:" + user.getName() + "-" + user.getNewNumber() + "(" + user.getUsername() + ")");
                name2.setTextColor(Color.RED);
                name2.setVisibility(View.VISIBLE);
                number.setText("类型:" + (parent.getType() == 2 ? "乐园" : "鲜花"));
                name.setText("金额:" + parent.getMoney() + "元");
                time.setText((parent.getConsumeType() == 1 ? "金额" : "计次") + "消费");
                StringBuilder sb = new StringBuilder();
                sb.append("总");
                if (parent.getConsumeType() == 1) {
                    sb.append(parent.getMoney());
                    sb.append(" 元");
                } else {
                    sb.append(parent.getTotalCount());
                    sb.append(" 次");
                }

                phone.setText(sb.toString());
                if (!TextUtils.isEmpty((parent.getRemark()))) {
                    remark.setText("项目备注:" + parent.getRemark());
                    remark.setVisibility(View.VISIBLE);
                } else {
                    remark.setVisibility(View.GONE);
                }
                name1.setText("项目名称:" + parent.getName());
                time1.setText("项目消费时间:" + consumeProject.oldCrateTime);
                number.setVisibility(View.GONE);
                name.setVisibility(View.GONE);
                phone.setVisibility(View.VISIBLE);

            } else if (bean instanceof ConsumeRecord) {
                ConsumeRecord consumeRecord = (ConsumeRecord) bean;
                ConsumeProject consumeProject = consumeRecord.getFrom();
                Project parent = consumeProject.getParent();
                User2 user = consumeProject.getUser();

                name2.setText("所属会员:" + user.getName() + "-" + user.getNewNumber() + "(" + user.getUsername() + ")");
                name2.setTextColor(Color.RED);
                name2.setVisibility(View.VISIBLE);

                time.setText((parent.getConsumeType() == 1 ? "金额" : "计次") + "消费");
                StringBuilder sb = new StringBuilder();
                sb.append("总 ");
                if (parent.getConsumeType() == 1) {
                    sb.append(parent.getMoney());
                    sb.append(" 元");
                } else {
                    sb.append(parent.getTotalCount());
                    sb.append(" 次");
                }
                time.setText("所属项目:" + consumeRecord.getFrom().getParent().getSimpleName());
//                phone.setText(sb.toString());
                if (!TextUtils.isEmpty((parent.getRemark()))) {
                    remark.setText("项目备注:" + parent.getRemark());
                    remark.setVisibility(View.VISIBLE);
                } else {
                    remark.setVisibility(View.GONE);
                }
                name1.setText("消费项目:" + consumeRecord.getName());
                time1.setText("消费时间:" + consumeRecord.getDate() + "(" + consumeRecord.getUpdatedAt() + ")");
                number.setVisibility(View.VISIBLE);
                phone.setVisibility(View.GONE);
                name.setVisibility(View.GONE);
                if (consumeRecord.getFrom().getParent().getConsumeType() == 1) {
                    number.setText("核销 " + consumeRecord.getMoney() + " 元");
                } else {
                    number.setText("核销 " + consumeRecord.getCount() + " 次");
                }
            }

            if (bean instanceof ConsumeProjectLC) {
                ConsumeProjectLC consumeProject = (ConsumeProjectLC) bean;
                ProjectLC parent = consumeProject.getParent();
                UserLC user = consumeProject.getUser();
                name2.setText("所属会员:" + user.getName() + "-" + user.getNewNumber() + "(" + user.getUsername() + ")");
                name2.setTextColor(Color.RED);
                name2.setVisibility(View.VISIBLE);
                number.setText("类型:" + (parent.getType() == 2 ? "乐园" : "鲜花"));
                name.setText("金额:" + parent.getMoney() + "元");
                time.setText((parent.getConsumeType() == 1 ? "金额" : "计次") + "消费");
                StringBuilder sb = new StringBuilder();
                sb.append("总");
                if (parent.getConsumeType() == 1) {
                    sb.append(parent.getMoney());
                    sb.append(" 元");
                } else {
                    sb.append(parent.getTotalCount());
                    sb.append(" 次");
                }

                phone.setText(sb.toString());
                if (!TextUtils.isEmpty((parent.getRemark()))) {
                    remark.setText("项目备注:" + parent.getRemark());
                    remark.setVisibility(View.VISIBLE);
                } else {
                    remark.setVisibility(View.GONE);
                }
                name1.setText("项目名称:" + parent.getName());
                time1.setText("项目消费时间:" + consumeProject.oldCrateTime);
                number.setVisibility(View.GONE);
                name.setVisibility(View.GONE);
                phone.setVisibility(View.VISIBLE);

            } else if (bean instanceof ConsumeRecordLC) {
                ConsumeRecordLC consumeRecord = (ConsumeRecordLC) bean;
                ConsumeProjectLC consumeProject = consumeRecord.getFrom();
                ProjectLC parent = consumeProject.getParent();
                UserLC user = consumeProject.getUser();

                name2.setText("所属会员:" + user.getName() + "-" + user.getNewNumber() + "(" + user.getUsername() + ")");
                name2.setTextColor(Color.RED);
                name2.setVisibility(View.VISIBLE);

                time.setText((parent.getConsumeType() == 1 ? "金额" : "计次") + "消费");
                StringBuilder sb = new StringBuilder();
                sb.append("总 ");
                if (parent.getConsumeType() == 1) {
                    sb.append(parent.getMoney());
                    sb.append(" 元");
                } else {
                    sb.append(parent.getTotalCount());
                    sb.append(" 次");
                }
                time.setText("所属项目:" + consumeRecord.getFrom().getParent().getSimpleName());
//                phone.setText(sb.toString());
                if (!TextUtils.isEmpty((parent.getRemark()))) {
                    remark.setText("项目备注:" + parent.getRemark());
                    remark.setVisibility(View.VISIBLE);
                } else {
                    remark.setVisibility(View.GONE);
                }
                name1.setText("消费项目:" + consumeRecord.getName());
                time1.setText("消费时间:" + consumeRecord.getDate() + "(" + consumeRecord.getUpdatedAt() + ")");
                number.setVisibility(View.VISIBLE);
                phone.setVisibility(View.GONE);
                name.setVisibility(View.GONE);
                if (consumeRecord.getFrom().getParent().getConsumeType() == 1) {
                    number.setText("核销 " + consumeRecord.getMoney() + " 元");
                } else {
                    number.setText("核销 " + consumeRecord.getCount() + " 次");
                }
            }
        }
    }
}
