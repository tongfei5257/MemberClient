package com.example.memberclient.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.example.memberclient.model.Project;
import com.example.memberclient.model.User;
import com.example.memberclient.utils.ProgressUtils;
import com.google.gson.Gson;


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
public class ProjectActivity extends BaseActivity {

    @Bind(R.id.add)
    Button add;
    @Bind(R.id.user)
    TextView user;

    @Bind(R.id.refresh)
    Button refresh;
    @Bind(R.id.search)
    Button mSearch;
    @Bind(R.id.et_search_content)
    EditText editText;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    private List<Project> datas = new ArrayList<>();
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        add = findViewById(R.id.add);
        user = findViewById(R.id.user);

        refresh = findViewById(R.id.refresh);
        mSearch = findViewById(R.id.search);
        editText = findViewById(R.id.et_search_content);
        recyclerView = findViewById(R.id.recyclerView);

        setTitle("项目管理");
        showBackwardView(true);
        initData();

    }

    private void initData() {
        user.setText("当前操作人:" + BmobUser.getCurrentUser(User.class).getName());
        myAdapter = new MyAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        myAdapter.setData(new ArrayList<Project>());
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
                Intent intent = new Intent(getSubActivity(), ProjectDetailActivity.class);
                intent.putExtra("action", "add");
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

            BmobQuery<Project> query = new BmobQuery<>();
            query.addWhereEqualTo("delete", false)
                    .order("-createdAt")
                    .findObjects(new FindListener<Project>() {
                        @Override
                        public void done(List<Project> list, BmobException e) {
                            ProgressUtils.dismiss(getSubActivity());
                            if (e == null) {
                                datas = list;
                                Gson gson=new Gson();
                                String s = gson.toJson(list);
                                Log.e("tf_test",s);
                                List<Project> temp = new ArrayList<>();
                                for (Project user : list) {
                                    if (TextUtils.isEmpty(source)) {
                                        temp.add(user);
                                        continue;
                                    }
                                    if ((user.getName().contains(source))) {
                                        temp.add(user);
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
            List<Project> temp = new ArrayList<>();
            for (Project user : datas) {
                if (TextUtils.isEmpty(source)) {
                    temp.add(user);
                    continue;
                }
                if ((user.getName().contains(source))) {
                    temp.add(user);
                }
            }
            myAdapter.setData(temp);
            myAdapter.notifyDataSetChanged();
        }

    }


    private class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<Project> list;
        Context context;

        public void setData(List<Project> list) {
            this.list = list;
        }

        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_recycler_project, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Project bean = list.get(position);
            holder.bindView(context, bean);

            holder.card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getSubActivity(), ProjectDetailActivity.class);
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
            card_view = view.findViewById(R.id.card_view);

        }

        public void bindView(Context context, final Project bean) {
            number.setText("类型:" + (bean.getType() == 2 ? "乐园" : "鲜花"));
            name.setText("金额:" + bean.getMoney() + "元");
            time.setText((bean.getConsumeType() == 1 ? "金额" : "计次") + "消费");
            StringBuilder sb = new StringBuilder();
            sb.append("总");
            if (bean.getConsumeType() == 1) {
                sb.append(bean.getMoney());
                sb.append(" 元");
            } else {
                sb.append(bean.getTotalCount());
                sb.append(" 次");
            }
            phone.setText(sb.toString());
            if (!TextUtils.isEmpty((bean.getRemark()))) {
                remark.setText("备注:" + bean.getRemark());
                remark.setVisibility(View.VISIBLE);
            } else {
                remark.setVisibility(View.GONE);
            }
            name1.setText("名称:" + bean.getName());
        }
    }

}
