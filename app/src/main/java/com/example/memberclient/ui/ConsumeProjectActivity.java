package com.example.memberclient.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.memberclient.model.Project;
import com.example.memberclient.model.User;
import com.example.memberclient.model.User2;
import com.example.memberclient.utils.ProgressUtils;
import com.example.memberclient.utils.Utils;


import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


public class ConsumeProjectActivity extends BaseActivity {

    @Bind(R.id.add)
    Button add;
    @Bind(R.id.user)
    TextView user;
    @Bind(R.id.size)
    TextView size;

    @Bind(R.id.refresh)
    Button refresh;
    @Bind(R.id.search)
    Button mSearch;
    @Bind(R.id.et_search_content)
    EditText editText;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    private List<ConsumeProject> datas = new ArrayList<>();
    private MyAdapter myAdapter;
    private User2 mUser;

    private boolean isShowAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consume);
        add = findViewById(R.id.add);
        user = findViewById(R.id.user);
        size = findViewById(R.id.size);
        refresh = findViewById(R.id.refresh);
        mSearch = findViewById(R.id.search);
        editText = findViewById(R.id.et_search_content);
        recyclerView = findViewById(R.id.recyclerView);

        showBackwardView(true);

        initData();
        setTitle("消费项目");
        if (mUser != null) {
            setTitle(mUser.getName() + " 的消费项目");
            isShowAll = false;
        } else {
            isShowAll = true;
            setTitle("全部消费项目");
        }
    }

    private void initData() {
        mUser = (User2) getIntent().getSerializableExtra("user");

//        ConsumeProject consumeProject = (ConsumeProject) getIntent().getSerializableExtra("ConsumeProject");
        user.setText("当前操作人:" + BmobUser.getCurrentUser(User.class).getName());
        myAdapter = new MyAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        myAdapter.setData(new ArrayList<ConsumeProject>());
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
                Intent intent = new Intent(getSubActivity(), MemberDetailActivity.class);
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

            BmobQuery<ConsumeProject> query = new BmobQuery<>();
            query.addWhereEqualTo("delete", false)
                    .addWhereEqualTo("user", mUser)
                    .include("parent,user")
                    .order("-createdAt");
//                    .findObjects(new FindListener<ConsumeProject>() {
//                        @Override
//                        public void done(List<ConsumeProject> list, BmobException e) {
//                            ProgressUtils.dismiss(getSubActivity());
//
//                            if (e == null) {
//                                datas = list;
//                                List<ConsumeProject> temp = new ArrayList<>();
//                                for (ConsumeProject user : list) {
//                                    if (TextUtils.isEmpty(source)) {
//                                        temp.add(user);
//                                        continue;
//                                    }
//                                    if ((user.getParent().getName().contains(source))) {
//                                        temp.add(user);
//                                    }
//                                }
//                                myAdapter.setData(temp);
//                                myAdapter.notifyDataSetChanged();
//                            } else {
//                                Toast.makeText(getApplicationContext(), "搜索异常", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                    });


            Utils.queryConsumeProject(query, 150, 0, new FindListener<ConsumeProject>() {
                @Override
                public void done(List<ConsumeProject> list, BmobException e) {
                    ProgressUtils.dismiss(getSubActivity());
                    if (e == null) {
                        datas = list;
                        size.setText("消费总数:" + datas.size());
                        List<ConsumeProject> temp = new ArrayList<>();
                        for (ConsumeProject user : list) {
                            if (TextUtils.isEmpty(source)) {
                                temp.add(user);
                                continue;
                            }
                            if ((user.getParent().getName().contains(source))) {
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
            List<ConsumeProject> temp = new ArrayList<>();
            for (ConsumeProject user : datas) {
                if (TextUtils.isEmpty(source)) {
                    temp.add(user);
                    continue;
                }
                if ((user.getParent().getName().contains(source))) {
                    temp.add(user);
                }
            }
            myAdapter.setData(temp);
            myAdapter.notifyDataSetChanged();
        }

    }


    private class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<ConsumeProject> list;
        Context context;

        public void setData(List<ConsumeProject> list) {
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
            final ConsumeProject consumeProject = list.get(position);
            final Project bean = consumeProject.getParent();
            holder.bindView(context, consumeProject);

            holder.card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getSubActivity(), ComsumeDetailActivity.class);
                    intent.putExtra("action", "edit");
                    intent.putExtra("bean", consumeProject);
                    startActivity(intent);
                }
            });
            holder.btn_update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getSubActivity())
                            .setTitle("提示")
                            .setMessage("确定要删除 " + bean.getName() + " 消费项目吗")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            consumeProject.setDelete(true);
                            ProgressUtils.show(getSubActivity());

                            consumeProject.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    ProgressUtils.dismiss(getSubActivity());

                                    if (e == null) {
                                        list.remove(consumeProject);
                                        notifyDataSetChanged();
                                        Toast.makeText(getSubActivity(), "删除成功：",
                                                Toast.LENGTH_SHORT).show();

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

        }

        public void bindView(Context context, final ConsumeProject ConsumeProject) {
            final Project bean = ConsumeProject.getParent();
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
                remark.setText("项目备注:" + bean.getRemark());
                remark.setVisibility(View.VISIBLE);
            } else {
                remark.setVisibility(View.GONE);
            }
            name1.setText("项目名称:" + bean.getName());
            time1.setText("项目消费时间:" + ConsumeProject.getCreatedAt());
            if (isShowAll) {
                User2 user = ConsumeProject.getUser();
                name2.setVisibility(View.VISIBLE);
                name2.setText("所属会员:" + user.getName() + "-" + user.getNewNumber() + "(" + user.getUsername() + ")");
            } else {
                name2.setVisibility(View.GONE);

            }
        }
    }


}
