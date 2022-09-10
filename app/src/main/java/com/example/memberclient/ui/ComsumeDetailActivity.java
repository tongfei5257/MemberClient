package com.example.memberclient.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memberclient.R;
import com.example.memberclient.model.ConsumeProject;
import com.example.memberclient.model.ConsumeRecord;
import com.example.memberclient.model.Project;
import com.example.memberclient.model.User;
import com.example.memberclient.utils.ProgressUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 项目
 */
public class ComsumeDetailActivity extends BaseActivity {

    @Bind(R.id.add)
    Button add;
    @Bind(R.id.user)
    TextView user;
    @Bind(R.id.record)
    TextView record;

    @Bind(R.id.refresh)
    Button refresh;
    @Bind(R.id.search)
    Button mSearch;
    @Bind(R.id.et_search_content)
    EditText editText;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    private List<ConsumeRecord> datas = new ArrayList<>();
    private MyAdapter myAdapter;
    private ConsumeProject consumeProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comsume_detail_activity);
        add = findViewById(R.id.add);
        user = findViewById(R.id.user);
        record = findViewById(R.id.record);
        refresh = findViewById(R.id.refresh);
        mSearch = findViewById(R.id.search);
        editText = findViewById(R.id.et_search_content);
        recyclerView = findViewById(R.id.recyclerView);

        setTitle("消费记录");
        showBackwardView(true);
        initData();
        if (consumeProject != null) {
            setTitle(consumeProject.getUser().getName() + " 的消费记录");
        }


    }

    private void initData() {
        consumeProject = (ConsumeProject) getIntent().getSerializableExtra("bean");
        user.setText("当前操作人:" + BmobUser.getCurrentUser(User.class).getName());
        myAdapter = new MyAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        myAdapter.setData(new ArrayList<ConsumeRecord>());
        recyclerView.setAdapter(myAdapter);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String s = editText.getText().toString().trim();
                search(s, false);
            }
        });
        search("", true);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search("", true);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getSubActivity(), SubConsumeRecordEditActivity.class);
                intent.putExtra("action", "add");
                intent.putExtra("bean", consumeProject);
                startActivity(intent);
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

    private void search(final String source, boolean req) {
        if (req) {
            ProgressUtils.show(getSubActivity());

            BmobQuery<ConsumeRecord> query = new BmobQuery<>();
            query.addWhereEqualTo("delete", false)
                    .addWhereEqualTo("from", consumeProject)
                    .include("from,from.parent,,from.user")
                    .order("-createdAt")
                    .findObjects(new FindListener<ConsumeRecord>() {
                        @Override
                        public void done(List<ConsumeRecord> list, BmobException e) {
                            ProgressUtils.dismiss(getSubActivity());

                            if (e == null) {
                                datas = list;
                                Project parent = consumeProject.getParent();
                                double remain = getRemain(datas, parent.getConsumeType());
                                if (parent.getConsumeType() == 1) {
                                    record.setText("总" + parent.getMoney() + "元," + "已核销" + remain + "元,剩余" + (parent.getMoney() - remain) + "元");
                                } else {
                                    record.setText("总" + parent.getTotalCount() + "次," + "已核销" + (int) remain + "次,剩余" + (parent.getTotalCount() - (int) remain) + "次");

                                }
                                List<ConsumeRecord> temp = new ArrayList<>();
                                for (ConsumeRecord user : list) {
                                    if (TextUtils.isEmpty(source)) {
                                        temp.add(user);
                                        continue;
                                    }

                                }
                                myAdapter.setData(temp);
                                myAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getApplicationContext(), "搜索异常", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
        } else {
            List<ConsumeRecord> temp = new ArrayList<>();
            for (ConsumeRecord user : datas) {
                if (TextUtils.isEmpty(source)) {
                    temp.add(user);
                    continue;
                }
            }
            myAdapter.setData(temp);
            myAdapter.notifyDataSetChanged();
        }

    }

    private double getRemain(List<ConsumeRecord> datas, int ConsumeType) {
        double reuslt = 0;
        for (ConsumeRecord consumeRecord : datas) {
            if (ConsumeType == 1) {
                reuslt += consumeRecord.getMoney();
            } else {
                reuslt += consumeRecord.getCount();

            }
        }
        return reuslt;
    }


    private class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<ConsumeRecord> list;
        Context context;

        public void setData(List<ConsumeRecord> list) {
            this.list = list;
        }

        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_recycler_comsume_record, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final ConsumeRecord bean = list.get(position);
            holder.bindView(context, bean);

            holder.card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getSubActivity(), SubConsumeRecordEditActivity.class);
                    intent.putExtra("action", "edit");
                    intent.putExtra("bean", bean);
                    startActivity(intent);
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
        private TextView number;
        private TextView remark;
        private TextView name1;
        private TextView time1;

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
            number = view.findViewById(R.id.number);
            remark = view.findViewById(R.id.remark);
            name1 = view.findViewById(R.id.name1);
            time1 = view.findViewById(R.id.time1);
            card_view = view.findViewById(R.id.card_view);

        }

        public void bindView(Context context, final ConsumeRecord bean) {
            name1.setText("消费项目名称:" + bean.getName());
            time1.setText("消费时间:" + bean.getDate());
            number.setText("所属项目:" + bean.getFrom().getParent().getSimpleName());
            phone.setText("类型:" + (bean.getFrom().getParent().getType() == 2 ? "乐园" : "鲜花"));
            if (bean.getFrom().getParent().getConsumeType() == 1) {
                time.setText("核销 " + bean.getMoney() + " 元");
            } else {
                time.setText("核销 " + bean.getCount() + " 次");
            }
//            name.setText("金额:" + bean.getMoney() + "元");
//            time.setText((bean.getConsumeType() == 1 ? "金额" : "计次") + "消费");
//            StringBuilder sb = new StringBuilder();
//            sb.append("总");
//            if (bean.getConsumeType() == 1) {
//                sb.append(bean.getMoney());
//                sb.append(" 元");
//            } else {
//                sb.append(bean.getTotalCount());
//                sb.append(" 次");
//            }
//            phone.setText(sb.toString());
//            if (!TextUtils.isEmpty((bean.getRemark()))) {
//                remark.setText("备注:" + bean.getRemark());
//            }
//            name1.setText("名称:" + bean.getName());
        }
    }

}
