package com.example.memberclient.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memberclient.R;
import com.example.memberclient.model.User;
import com.example.memberclient.model.User2;
import com.example.memberclient.utils.ProgressUtils;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class MemberActivity extends BaseActivity {

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
    private List<User2> datas = new ArrayList<>();
    private MyAdapter myAdapter;
    private BmobQuery<User2> bmobQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        add = findViewById(R.id.add);
        user = findViewById(R.id.user);
        size = findViewById(R.id.size);
        refresh = findViewById(R.id.refresh);
        mSearch = findViewById(R.id.search);
        editText = findViewById(R.id.et_search_content);
        recyclerView = findViewById(R.id.recyclerView);
        setTitle("会员操作");
        showBackwardView(true);

        initData();

    }

    private void initData() {
        user.setText("当前操作人:" + BmobUser.getCurrentUser(User.class).getName());
        myAdapter = new MyAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        myAdapter.setData(new ArrayList<User2>());
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

            bmobQuery = new BmobQuery<>();
            bmobQuery.addWhereEqualTo("type", "2")
                    .addWhereEqualTo("delete", false)
                    .order("-newNumber,-createdAt").setLimit(500)
                    .findObjects(new FindListener<User2>() {
                        @Override
                        public void done(List<User2> list, BmobException e) {
                            ProgressUtils.dismiss();
                            if (e == null) {
                                datas.clear();
                                datas = list;
                                bmobQuery = new BmobQuery<>();
                                bmobQuery.addWhereEqualTo("type", "2")
                                        .addWhereEqualTo("delete", false).setSkip(500)
                                        .order("-newNumber,-createdAt").setLimit(500);
                                bmobQuery.findObjects(new FindListener<User2>() {
                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    @Override
                                    public void done(List<User2> list, BmobException e) {
                                        if (e == null) {
                                            List<User2> temp = new ArrayList<>();
                                            datas.addAll(list);
                                            datas.sort(new Comparator<User2>() {
                                                @Override
                                                public int compare(User2 o1, User2 o2) {
//                                                    return o2.getNewNumber() - o1.getNewNumber();
                                                    return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                                                }
                                            });
                                            size.setText("会员总数:" + datas.size());
                                            for (User2 user : datas) {
                                                if (TextUtils.isEmpty(source)) {
                                                    temp.add(user);
                                                    continue;
                                                }
                                                if ((user.getName().contains(source) || user.getUsername().contains(source)) || user.getNumber().contains(source)) {
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
                                Toast.makeText(getApplicationContext(), "搜索异常", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
        } else {
            List<User2> temp = new ArrayList<>();
            for (User2 user : datas) {
                if (TextUtils.isEmpty(source)) {
                    temp.add(user);
                    continue;
                }
                if ((user.getName().contains(source) || user.getUsername().contains(source)||user.getNumber().contains(source))) {
                    temp.add(user);
                }
            }
            myAdapter.setData(temp);
            myAdapter.notifyDataSetChanged();
        }

    }


    private class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<User2> list;
        Context context;

        public void setData(List<User2> list) {
            this.list = list;
        }

        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_recycler_user, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final User2 bean = list.get(position);
            holder.bindView(context, bean);

            holder.card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getSubActivity(), MemberDetailActivity.class);
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
            card_view = view.findViewById(R.id.card_view);

        }

        public void bindView(Context context, final User2 bean) {
            name.setText(bean.getName());
            phone.setText(bean.getUsername());
            number.setText(bean.getNumber());
            if (!TextUtils.isEmpty((bean.getRemark()))) {
                remark.setText("备注:" + bean.getRemark());
                remark.setVisibility(View.VISIBLE);
            } else {
                remark.setVisibility(View.GONE);
            }
            time.setText(bean.getDate());
        }
    }

}
