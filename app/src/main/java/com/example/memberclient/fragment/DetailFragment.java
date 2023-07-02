package com.example.memberclient.fragment;


import static cn.bmob.v3.Bmob.getApplicationContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memberclient.R;
import com.example.memberclient.application.MyApp;
import com.example.memberclient.model.ConsumeProject;
import com.example.memberclient.model.ConsumeProjectLC;
import com.example.memberclient.model.ConsumeRecord;
import com.example.memberclient.model.ConsumeRecordLC;
import com.example.memberclient.model.Project;
import com.example.memberclient.model.ProjectLC;
import com.example.memberclient.model.Source;
import com.example.memberclient.model.User2LC;
import com.example.memberclient.model.UserLC;
import com.example.memberclient.ui.ConsumeProjectActivity;
import com.example.memberclient.ui.MemberActivity;
import com.example.memberclient.ui.MemberDetailActivity;
import com.example.memberclient.ui.ProjectActivity;
import com.example.memberclient.model.User;
import com.example.memberclient.model.User2;
import com.example.memberclient.utils.ProgressUtils;
import com.example.memberclient.utils.ToastUtil;
import com.example.memberclient.utils.Utils;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.leancloud.LCException;
import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;
import cn.leancloud.LCUser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class DetailFragment extends BaseFragment {

    @Bind(R.id.search)
    Button mSearch;

    @Bind(R.id.btn_member)
    Button btn_member;

    @Bind(R.id.btn_project)
    Button btn_project;
    @Bind(R.id.btn_modify)
    Button btn_modify;
    @Bind(R.id.btn_save)
    Button btn_save;
    @Bind(R.id.btn_get)
    Button btn_get;

    @Bind(R.id.btn_consume)
    Button btn_consume;
    @Bind(R.id.refresh)
    Button refresh;
    @Bind(R.id.user)
    TextView user;
    @Bind(R.id.size)
    TextView size;

    @Bind(R.id.et_search_content)
    EditText editText;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    private MyAdapter myAdapter;
    private List<User2> datas = new ArrayList<>();
    private List<UserLC> datasLC = new ArrayList<>();
    private BmobQuery<User2> bmobQuery;
    private static final int MSG_TRANS_USER = 1;
    private static final int MSG_TRANS_CP = 2;
    private static final int MSG_TRANS_CR = 3;
    private static final int MSG_TRANS_END = 4;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TRANS_USER:
                    transUser();
                    break;
                case MSG_TRANS_CP:
                    transCp();
                    break;
                case MSG_TRANS_CR:
                    transCR();
                    break;
                case MSG_TRANS_END:
                    ToastUtil.show(getContext(), "转换完成", Toast.LENGTH_LONG);
                    break;
            }
        }
    };


    private void transUser() {
//                =============================用户转换============================

        bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("type", "2")
//                .addWhereEqualTo("delete", false)
                .order("-newNumber,-createdAt");
        Utils.queryBombUser(bmobQuery, 500, 0, new FindListener<User2>() {
            @Override
            public void done(List<User2> list, BmobException e) {
                Log.e("tf_test", "user=" + list.size() + ",e=" + e);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<LCObject> projectLCList = new ArrayList<>();
                        LCQuery<LCObject> lcObjectLCQuery = new LCQuery<>("UserLC")
//                                .whereEqualTo("delete", false)
                                .orderByDescending("createdAt");
                        Utils.queryLCUser(lcObjectLCQuery,1000, 0, projectLCList);
                        Log.e("tf_test", "UserLC  start...=" + projectLCList.size());
                        // comments 包含与 post 相关联的评论
                        List<UserLC> userLCS = new ArrayList<>();
                        if (!projectLCList.isEmpty()) {
                            Log.e("tf_test", "UserLC  start .size=" + projectLCList.size());
                            for (User2 user2 : list) {
                                if (!user2.find(projectLCList)) {
                                    userLCS.add(UserLC.newInstance(user2).saveV2());
                                }
                            }

                        } else {
                            for (User2 user2 : list) {
                                userLCS.add(UserLC.newInstance(user2).saveV2());
                            }
                        }
                        if (userLCS.isEmpty()) {
                            Log.e("tf_test", "User2 userLCS.isEmpty");
                            mHandler.sendEmptyMessage(MSG_TRANS_CP);
                            return;
                        }
                        Log.e("tf_test", "User2 save userLCS.size=" + userLCS.size());

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 批量更新
                                try {
                                    Log.e("tf_test", "User2 save start");
                                    LCObject.saveAll(userLCS);
                                    Log.e("tf_test", "User2 save end");
                                    mHandler.sendEmptyMessage(MSG_TRANS_CP);
                                } catch (LCException ex) {
                                    Log.e("tf_test", "btn_transform:" + Log.getStackTraceString(ex));
                                }
                            }
                        }).start();
                    }
                }).start();
            }
        });
//                =============================用户转换end============================
    }

    private void transCp() {
        BmobQuery<ConsumeProject> query1 = new BmobQuery<>();
        query1.order("-createdAt")
//                .addWhereEqualTo("delete", false)
                .include("operator,parent,user")
                .setLimit(300);

        Utils.queryConsumeProject(query1, 150, 0, new FindListener<ConsumeProject>() {
            @Override
            public void done(List<ConsumeProject> list, BmobException e) {
                Log.e("tf_test", "cp=" + list.size() + ",e=" + e);
                if (e == null) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<ConsumeProjectLC> cpLcsResult = new ArrayList<>();
//                            for (ConsumeProject user2 : list) {
//                                ConsumeProjectLC cp = ConsumeProjectLC.newInstance(user2).saveV2();
//                                if (TextUtils.isEmpty(cp.user.bmId)){
//                                    Log.e("tf_test","user 匹配 user object为空："+cp.bmId);
//                                    continue;
//                                }
//                                cpLcsResult.add(cp);
//                            }

                            List<LCObject> CpLCs = new ArrayList<>();

                            Utils.queryLCConsumeProject(new LCQuery<>("ConsumeProjectLC")
//                                    .whereEqualTo("delete", false)
                                    .orderByAscending("createdAt"),1000, 0, CpLCs);

                            if (CpLCs.isEmpty()) {
                                for (ConsumeProject user2 : list) {
                                    ConsumeProjectLC cp = ConsumeProjectLC.newInstance(user2).saveV2();
                                    if (TextUtils.isEmpty(cp.user.bmId)) {
                                        Log.e("tf_test", "user 匹配 user object为空：" + cp.bmId);
                                        continue;
                                    }
                                    cpLcsResult.add(cp);
                                }
                            } else {
                                for (ConsumeProject cp : list) {
                                    if (!cp.find(CpLCs)) {
                                        cpLcsResult.add(ConsumeProjectLC.newInstance(cp).saveV2());
                                    }
                                }
                            }
                            if (cpLcsResult.isEmpty()) {
                                Log.e("tf_test", "ConsumeProjectLC cpLcsResult.isEmpty");
                                mHandler.sendEmptyMessage(MSG_TRANS_CR);
                                return;
                            }
                            Log.e("tf_test", "ConsumeProjectLC cpLcsResult=" + cpLcsResult.size());
                            List<LCObject> UserLCs = new ArrayList<>();
                            Utils.queryLCUser(new LCQuery<>("UserLC")
//                                .whereEqualTo("delete", false)
                                    .orderByDescending("createdAt"),1000, 0, UserLCs);
                            List<LCObject> ProjectLCs = new LCQuery<>("ProjectLC").whereEqualTo("delete", false)
                                    .orderByAscending("createdAt").find();
                            int count = 0;
                            for (ConsumeProjectLC consumeProjectLC : cpLcsResult) {
                                boolean isFind = false;
                                for (LCObject lcObject : UserLCs) {
                                    if (lcObject.getString("bmId").equals(consumeProjectLC.user.bmId)) {
                                        count++;
//                                        Log.e("tf_test","user 匹配:"+count);
                                        isFind = true;
//                                        consumeProjectLC.user.setObjectId(lcObject.getObjectId());
//                                        consumeProjectLC.saveV2();
                                        LCObject user = LCObject.createWithoutData("UserLC", lcObject.getObjectId());
                                        consumeProjectLC.put("user", user);
                                    }
                                }
                                if (!isFind) {
                                    Log.e("tf_test", "user 匹配 未找到:" + consumeProjectLC.bmId);
                                }
                                isFind = false;
                                for (LCObject lcObject : ProjectLCs) {
                                    if (lcObject.getString("bmId").equals(consumeProjectLC.parent.bmId)) {
//                                        consumeProjectLC.parent.setObjectId(lcObject.getObjectId());
//                                        consumeProjectLC.saveV2();
                                        isFind = true;
                                        LCObject project = LCObject.createWithoutData("ProjectLC", lcObject.getObjectId());
                                        consumeProjectLC.put("parent", project);
                                    }
                                }
                                if (!isFind) {
                                    Log.e("tf_test", "project 匹配 未找到:" + consumeProjectLC.bmId);
                                }
                            }
                            // 批量更新
                            try {
                                Log.e("tf_test", "save ConsumeProjectLC start=" + cpLcsResult.size());
                                LCObject.saveAll(cpLcsResult);
                                Log.e("tf_test", "save ConsumeProjectLC end");
                                mHandler.sendEmptyMessage(MSG_TRANS_CR);
                            } catch (Exception ex) {
                                Log.e("tf_test", "btn_transform:" + Log.getStackTraceString(ex));
                            }
                        }
                    }).start();
                }
            }
        });
    }

    private void transCR() {
        BmobQuery<ConsumeRecord> query2 = new BmobQuery<>();
        query2.order("-createdAt")
//                .addWhereEqualTo("delete", false)
//                .include("from,operator,from.user,from.parent");
                .include("from,operator");
        Utils.queryConsumeRecord(query2, 200, 0, new FindListener<ConsumeRecord>() {
            @Override
            public void done(List<ConsumeRecord> list, BmobException e) {
                Log.e("tf_test", "cr=" + list.size() + ",e=" + e);
                if (e == null) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<ConsumeRecordLC> cpLcsResult = new ArrayList<>();
//                            for (ConsumeProject user2 : list) {
//                                ConsumeProjectLC cp = ConsumeProjectLC.newInstance(user2).saveV2();
//                                if (TextUtils.isEmpty(cp.user.bmId)){
//                                    Log.e("tf_test","user 匹配 user object为空："+cp.bmId);
//                                    continue;
//                                }
//                                cpLcsResult.add(cp);
//                            }

                            List<LCObject> CpLCs = new ArrayList<>();
                            Utils.queryLCConsumeRecord(new LCQuery<>("ConsumeRecordLC")
//                    .whereEqualTo("delete", false)
                                    .orderByAscending("createdAt"),1000, 0, CpLCs);

                            if (CpLCs.isEmpty()) {
                                for (ConsumeRecord user2 : list) {
                                    ConsumeRecordLC cr = ConsumeRecordLC.newInstanceV2(user2).saveV2();
                                    if (TextUtils.isEmpty(cr.from.bmId)) {
                                        Log.e("tf_test", "cr 匹配 from object为空：" + cr.bmId);
                                        continue;
                                    }
                                    cpLcsResult.add(cr);
                                }
                            } else {
                                for (ConsumeRecord cp : list) {
                                    if (!cp.find(CpLCs)) {
                                        cpLcsResult.add(ConsumeRecordLC.newInstanceV2(cp).saveV2());
                                    }
                                }
                            }
                            if (cpLcsResult.isEmpty()) {
                                Log.e("tf_test", "ConsumeRecordLC cpLcsResult.isEmpty");
//                                mHandler.sendEmptyMessage(MSG_TRANS_CR);
                                return;
                            }
                            Log.e("tf_test", "ConsumeRecordLC cpLcsResult=" + cpLcsResult.size());
                            List<LCObject> CpLCss = new ArrayList<>();
                            Utils.queryLCConsumeProject(new LCQuery<>("ConsumeProjectLC")
//                                    .whereEqualTo("delete", false)
                                    .orderByAscending("createdAt"),1000, 0, CpLCss);
                            int count = 0;
                            Iterator<ConsumeRecordLC> iterator = cpLcsResult.iterator();
                            boolean isFind = false;
                            while (iterator.hasNext()) {
                                ConsumeRecordLC next = iterator.next();
                                for (LCObject lcObject : CpLCss) {
                                    if (lcObject.getString("bmId").equals(next.from.bmId)) {
                                        count++;
//                                        Log.e("tf_test","user 匹配:"+count);
                                        isFind = true;
//                                        consumeProjectLC.user.setObjectId(lcObject.getObjectId());
//                                        consumeProjectLC.saveV2();
                                        LCObject user = LCObject.createWithoutData("ConsumeProjectLC", lcObject.getObjectId());
                                        next.put("from", user);
                                    }
                                }

                                if (!isFind) {
                                    Log.e("tf_test", "ConsumeRecordLC cp 匹配 未找到:" + next.bmId);
                                    iterator.remove();
                                }
                            }
//                            for (ConsumeRecordLC consumeProjectLC : cpLcsResult) {
//                                boolean isFind=false;
//                                for (LCObject lcObject : CpLCss) {
//                                    if (lcObject.getString("bmId").equals(consumeProjectLC.from.bmId)) {
//                                        count++;
////                                        Log.e("tf_test","user 匹配:"+count);
//                                        isFind=true;
////                                        consumeProjectLC.user.setObjectId(lcObject.getObjectId());
////                                        consumeProjectLC.saveV2();
//                                        LCObject user = LCObject.createWithoutData("ConsumeProjectLC", lcObject.getObjectId());
//                                        consumeProjectLC.put("from", user);
//                                    }
//                                }
//
//                                if (!isFind){
//                                    Log.e("tf_test","ConsumeRecordLC cp 匹配 未找到:"+consumeProjectLC.bmId);
//                                }
//                            }
                            if (cpLcsResult.isEmpty()) {
                                Log.e("tf_test", "ConsumeProjectLC cpLcsResult.isEmpty");
                                mHandler.sendEmptyMessage(MSG_TRANS_END);
                                return;
                            }
                            // 批量更新
                            try {
                                Log.e("tf_test", "save ConsumeRecordLC start=" + cpLcsResult.size());
                                LCObject.saveAll(cpLcsResult);
                                Log.e("tf_test", "save ConsumeRecordLC end");
//                                mHandler.sendEmptyMessage(MSG_TRANS_CR);
                            } catch (Exception ex) {
                                Log.e("tf_test", "btn_transform:" + Log.getStackTraceString(ex));
                            }
                        }
                    }).start();
                }

            }
        });
    }


    @Override
//    protected int getLayoutId() {
//        return R.layout.book_list;
//    }
    protected int getLayoutId() {
        return R.layout.activity_main2;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mSearch = root.findViewById(R.id.search);
        btn_member = root.findViewById(R.id.btn_member);
        btn_project = root.findViewById(R.id.btn_project);
        btn_modify = root.findViewById(R.id.btn_modify);
        btn_save = root.findViewById(R.id.btn_save);
        btn_get = root.findViewById(R.id.btn_get);
        btn_consume = root.findViewById(R.id.btn_consume);
        refresh = root.findViewById(R.id.refresh);
        user = root.findViewById(R.id.user);
        size = root.findViewById(R.id.size);
        editText = root.findViewById(R.id.et_search_content);
        recyclerView = root.findViewById(R.id.recyclerView);
        Button btn_transform = root.findViewById(R.id.btn_transform);
        MyApp myApp = (MyApp) getContext().getApplicationContext();
        if (MyApp.USE_LC) {
            user.setText("当前操作人:" + LCUser.getCurrentUser().getString("name"));
        } else {
            user.setText("当前操作人:" + BmobUser.getCurrentUser(User.class).getName());
        }
        myAdapter = new MyAdapter(this.getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        if (MyApp.USE_LC) {
            myAdapter.setDataLC(new ArrayList<UserLC>());
        } else {
            myAdapter.setData(new ArrayList<User2>());
        }
        recyclerView.setAdapter(myAdapter);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String s = editText.getText().toString().trim();
                search(s, false);
            }
        });
        search("", true);
        btn_transform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                =============================Project============================
                BmobQuery<Project> query = new BmobQuery<>();
                query.addWhereEqualTo("delete", false)
                        .order("-createdAt")
                        .findObjects(new FindListener<Project>() {
                            @Override
                            public void done(List<Project> list, BmobException e) {
                                Log.e("tf_test", "Project=" + list.size() + ",e=" + e);

                                if (e == null) {
                                    final LCQuery<LCObject> priorityQuery = new LCQuery<>("ProjectLC");
                                    priorityQuery.whereEqualTo("delete", false)
                                            .orderByAscending("createdAt")
                                            .findInBackground().subscribe((Observer<? super List<LCObject>>) new Observer<List<LCObject>>() {
                                                public void onSubscribe(Disposable disposable) {
                                                }

                                                public void onNext(List<LCObject> projectLCList) {
                                                    // comments 包含与 post 相关联的评论
                                                    List<ProjectLC> userLCS = new ArrayList<>();
                                                    if (projectLCList != null && !projectLCList.isEmpty()) {

                                                        for (Project user2 : list) {
                                                            if (!user2.find(projectLCList)) {
                                                                userLCS.add(ProjectLC.newInstance(user2).saveV2());
                                                            }
                                                        }

                                                    } else {
                                                        for (Project user2 : list) {
                                                            userLCS.add(ProjectLC.newInstance(user2).saveV2());
                                                        }
                                                    }
                                                    if (userLCS.isEmpty()) {
                                                        Log.e("tf_test", "Project userLCS.isEmpty");
                                                        mHandler.sendEmptyMessage(MSG_TRANS_USER);
                                                        return;
                                                    }
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            // 批量更新
                                                            try {
                                                                Log.e("tf_test", "Project save start");
                                                                LCObject.saveAll(userLCS);
                                                                Log.e("tf_test", "Project save end");
                                                                mHandler.sendEmptyMessage(MSG_TRANS_USER);
                                                            } catch (LCException ex) {
                                                                Log.e("tf_test", "btn_transform:" + Log.getStackTraceString(ex));
                                                            }
                                                        }
                                                    }).start();
                                                }

                                                public void onError(Throwable throwable) {

                                                    Toast.makeText(getApplicationContext(), "搜索异常", Toast.LENGTH_SHORT).show();

                                                }

                                                public void onComplete() {
                                                }
                                            });

                                }
                            }

                        });


//                =============================Project end============================


            }
        });
        btn_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MemberActivity.class));
            }
        });

        btn_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ProjectActivity.class));

            }
        });

        btn_consume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ConsumeProjectActivity.class));

            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search("", true);
            }
        });
        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (false) {
                    int start = 0;
                    int end = 48;
                    for (int i = 0; i < datas.size(); i++) {
                        User2 item = datas.get(i);
                        item.setNewNumber(Integer.parseInt(item.getNumber()));
                        if (i == end) {
                            List<User2> subList = datas.subList(start, end);
                            List<BmobObject> list = new ArrayList<>();
                            list.addAll(subList);
                            Log.e("tf_test", "list.size=" + list.size());
                            new BmobBatch().updateBatch(list).doBatch(new QueryListListener<BatchResult>() {
                                @Override
                                public void done(List<BatchResult> results, BmobException e) {
                                    if (e == null) {
                                        for (int i = 0; i < results.size(); i++) {
                                            BatchResult result = results.get(i);
                                            BmobException ex = result.getError();
//                                        if (ex == null) {
//                                            Snackbar.make(mBtnUpdate, "第" + i + "个数据批量更新成功：" + result.getCreatedAt() + "," + result.getObjectId() + "," + result.getUpdatedAt(), Snackbar.LENGTH_LONG).show();
//                                        } else {
//                                            Snackbar.make(mBtnUpdate, "第" + i + "个数据批量更新失败：" + ex.getMessage() + "," + ex.getErrorCode(), Snackbar.LENGTH_LONG).show();
//
//                                        }
                                        }
                                    } else {
                                        Log.e("tf_test", Log.getStackTraceString(e));
//                                    Snackbar.make(mBtnUpdate, "失败：" + e.getMessage() + "," + e.getErrorCode(), Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });
                            start = end;
                            end += 48;
                        }

                    }
                    if (datas.size() < 50) {
                        for (int i = 0; i < datas.size(); i++) {
                            User2 item = datas.get(i);
                            item.setNewNumber(Integer.parseInt(item.getNumber()));
                        }
                        List<BmobObject> list = new ArrayList<>();
                        list.addAll(datas);
                        new BmobBatch().updateBatch(list).doBatch(new QueryListListener<BatchResult>() {
                            @Override
                            public void done(List<BatchResult> results, BmobException e) {
                                if (e == null) {
                                    for (int i = 0; i < results.size(); i++) {
                                        BatchResult result = results.get(i);
                                        BmobException ex = result.getError();
//                                        if (ex == null) {
//                                            Snackbar.make(mBtnUpdate, "第" + i + "个数据批量更新成功：" + result.getCreatedAt() + "," + result.getObjectId() + "," + result.getUpdatedAt(), Snackbar.LENGTH_LONG).show();
//                                        } else {
//                                            Snackbar.make(mBtnUpdate, "第" + i + "个数据批量更新失败：" + ex.getMessage() + "," + ex.getErrorCode(), Snackbar.LENGTH_LONG).show();
//
//                                        }
                                    }
                                } else {
                                    Log.e("tf_test", Log.getStackTraceString(e));
//                                    Snackbar.make(mBtnUpdate, "失败：" + e.getMessage() + "," + e.getErrorCode(), Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });
                    }

                } else {
                    List<BmobObject> list = new ArrayList<>();
                    for (int i = 0; i < 10; i++) {
                        User2 user2 = new User2();
                        user2.setNewNumber(i + 638);
                        user2.setNumber(user2.getNewNumber() + "");
                        list.add(user2);
                    }

                    new BmobBatch().insertBatch(list).doBatch(new QueryListListener<BatchResult>() {

                        @Override
                        public void done(List<BatchResult> results, BmobException e) {

                        }
                    });
                }


            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                    }
                }).start();
                Utils.save(getContext(), null);

//                Utils.queryConsumeProject();
            }
        });

        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String source = Utils.getJson("source.json", getContext());
                        Gson gson = new Gson();
                        Source source1 = gson.fromJson(source, Source.class);
                        Log.e("tf_test", "source1=" + source1);
                        List<User2> users = source1.users;
                        List<Project> projects = source1.projects;
                        List<ConsumeProject> cps = source1.cps;
                        List<ConsumeRecord> crs = source1.crs;
                        List<User2> tempUsers = new ArrayList<>();
                        List<Project> tempPs = new ArrayList<>();
                        List<ConsumeProject> tempCps = new ArrayList<>();
                        List<ConsumeRecord> tempCrs = new ArrayList<>();
                        Log.e("tf_test", "开始上传用户表");
                        User currentUser = BmobUser.getCurrentUser(User.class);
                        for (User2 user2 : users) {
                            tempUsers.add(user2.deepCopy().setOperator(currentUser));
                        }
                        for (Project project : projects) {
                            tempPs.add(project.deepCopy().setOperator(currentUser));
                        }
                        for (ConsumeProject project : cps) {
                            tempCps.add(project.deepCopy().setOperator(currentUser));
                        }
                        for (ConsumeRecord project : crs) {
                            tempCrs.add(project.deepCopy().setOperator(currentUser));
                        }
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
//                                上传用户表 start
//                                BmobQuery<User2>  bmobQuery = new BmobQuery<User2>();
//                                bmobQuery.addWhereEqualTo("type", "2")
//                                        .addWhereEqualTo("delete", false)
//                                        .order("-newNumber,-createdAt");
//                                Utils.queryUser(bmobQuery, 500, 0, new FindListener<User2>() {
//                                    @Override
//                                    public void done(List<User2> list, BmobException e) {
//                                        Log.e("tf_test","queryUser="+list.size()+",e="+e+"，tempUsers="+tempUsers.size());
//                                        if (e==null){
////                                            旧的
//                                            for (User2 user2:list) {
//                                                Iterator<User2> iterator = tempUsers.iterator();
//                                                while (iterator.hasNext()){
//                                                    User2 next = iterator.next();
//                                                    if (next.oldId.equals(user2.oldId)){
//                                                        Log.e("tf_test",next.oldId+" remove");
//                                                        iterator.remove();
////                                                        tempUsers.remove(next);
//                                                        break;
//                                                    }
//                                                }
//                                            }
//                                        }
//                                        Log.e("tf_test","after queryUser="+tempUsers.size());
//                                        if (!tempUsers.isEmpty()){
//                                            Utils.update(tempUsers);
//                                        }
//                                    }
//                                });
//                                上传用户表 end

//                                上传Project
//                                Utils.update(tempPs);
//                                上传queryConsumeProject  start
//                                bmobQuery = new BmobQuery<User2>();
//                                bmobQuery.addWhereEqualTo("type", "2")
//                                        .addWhereEqualTo("delete", false)
//                                        .order("-newNumber,-createdAt");
//                                Utils.queryUser(bmobQuery, 500, 0, new FindListener<User2>() {
//                                    @Override
//                                    public void done(List<User2> listU, BmobException e) {
//                                        Log.e("tf_test", "queryUser=" + listU.size() + ",e=" + e + "，tempUsers=" + tempUsers.size());
//                                        if (e == null) {
//
//                                            BmobQuery<ConsumeProject> query3 = new BmobQuery<>();
//                                            query3.addWhereEqualTo("delete", false)
//                                                    .order("-createdAt")
//                                                    .include("user,parent");
//                                            Utils.queryConsumeProject(query3, 100, 0, new FindListener<ConsumeProject>() {
//                                                @Override
//                                                public void done(List<ConsumeProject> list, BmobException e) {
//                                                    if (e!=null){
//                                                        list=new ArrayList<>();
//                                                    }
//                                                    Log.e("tf_test","queryConsumeProject="+list.size()+",e="+e+"，cps="+tempCps.size());
//                                                    for (ConsumeProject cp : list) {
//                                                        Iterator<ConsumeProject> iterator = tempCps.iterator();
//                                                        while (iterator.hasNext()){
//                                                            ConsumeProject next = iterator.next();
//                                                            if (next.oldId.equals(cp.oldId)){
//                                                                iterator.remove();
//                                                                Log.e("tf_test",next.oldId+" remove");
//                                                            }
//                                                        }
//                                                    }
//                                                    Log.e("tf_test","after queryConsumeProject="+"，cps="+tempCps.size());
//
//                                                    for (ConsumeProject cp:tempCps) {
//                                                        Project parent = cp.getParent();
//                                                        User2 user = cp.getUser();
//                                                        for (Project p:myApp.projectBeans) {
//                                                            if (p.oldId.equals(parent.getObjectId())){
//                                                                cp.setParent(p);
//                                                                break;
//                                                            }
//                                                        }
//                                                        for (User2 u:listU) {
//                                                            if (u.oldId.equals(user.getObjectId())){
//                                                                cp.setUser(u);
//                                                                break;
//                                                            }
//                                                        }
//                                                    }
//                                                    if (!tempCps.isEmpty()){
//                                                        Utils.update(tempCps);
//                                                    }
//
//                                                }
//                                            });
//                                        }
//
//                                    }
//                                });
//                              上传queryConsumeProject end

                                BmobQuery<ConsumeRecord> query2 = new BmobQuery<>();
                                query2.addWhereEqualTo("delete", false)
                                        .order("-createdAt")
                                        .include("from,operator,from.user,from.parent");
                                Utils.queryConsumeRecord(query2, 100, 0, new FindListener<ConsumeRecord>() {
                                    @Override
                                    public void done(List<ConsumeRecord> list, BmobException e) {
                                        if (e != null) {
                                            list = new ArrayList<>();
                                        }
                                        Log.e("tf_test", "queryConsumeRecord=" + list.size() + ",e=" + e + "，crs=" + tempCrs.size());
                                        for (ConsumeRecord record : list) {
                                            Iterator<ConsumeRecord> iterator = tempCrs.iterator();
                                            while (iterator.hasNext()) {
                                                ConsumeRecord next = iterator.next();
                                                if (next.oldId.equals(record.oldId)) {
                                                    iterator.remove();
                                                    Log.e("tf_test", next.oldId + " remove");
                                                }
                                            }
                                        }
                                        Log.e("tf_test", "after=" + tempCrs.size());
                                        for (ConsumeRecord cr : tempCrs) {
                                            ConsumeProject from = cr.getFrom();
                                            if (from == null || from.getObjectId() == null) {
                                                Log.e("tf_test", cr + " 有异常");

                                                continue;
                                            }
                                            for (ConsumeProject cp : consumeProjects) {
                                                if (from.getObjectId().equals(cp.oldId)) {
                                                    cr.setFrom(cp);
                                                }
                                            }
                                        }
                                        Utils.update(tempCrs);
                                    }
                                });

                            }
                        });
                    }
                }).start();


//                Gson gson = new Gson();
//                File file = new File(getContext().getDataDir(), "user.json");
//                try {
//                    String result = FileUtils.readFileToString(file, "utf-8");
//                    List<User2> user2s = gson.fromJson(result, new TypeToken<List<User2>>() {
//                    }.getType());
//                    List<User2> temp = new ArrayList<>();
//                    for (User2 user2 : user2s) {
//                        user2.setOperator(BmobUser.getCurrentUser(User.class));
//                        temp.add(user2.deepCopy());
//                    }
//                    Utils.update(temp.subList(0, 5));
//                    Log.e("tf_test", "user2s");
//                } catch (IOException ioException) {
//                    ioException.printStackTrace();
//                }
            }
        });
        if (MyApp.USE_LC) {
            final LCQuery<LCObject> priorityQuery = new LCQuery<>("ProjectLC");
            priorityQuery.whereEqualTo("delete", false)
                    .orderByAscending("createdAt")
                    .findInBackground().subscribe((Observer<? super List<LCObject>>) new Observer<List<LCObject>>() {
                        public void onSubscribe(Disposable disposable) {
                        }

                        public void onNext(List<LCObject> projectLCList) {

                            if (projectLCList == null || projectLCList.isEmpty()) {
                                return;
                            }

                            for (LCObject lcObject : projectLCList) {
                                MyApp.projectBeansLC.add(ProjectLC.toBean(lcObject));
                            }
                        }

                        public void onError(Throwable throwable) {


                        }

                        public void onComplete() {
                        }
                    });
        } else {
            BmobQuery<Project> query = new BmobQuery<>();
            query.addWhereEqualTo("delete", false)
                    .order("-createdAt")
                    .findObjects(new FindListener<Project>() {
                        @Override
                        public void done(List<Project> list, BmobException e) {
                            if (e == null) {
                                myApp.projectBeans = list;

                            } else {
                                Toast.makeText(myApp, "搜索异常", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
        }


//        BmobQuery<ConsumeProject> query1 = new BmobQuery<>();
//        query1.addWhereEqualTo("delete", false)
//                .order("-createdAt")
//                .include("operator,parent,user")
//                .setLimit(300);
//        Utils.queryConsumeProject(query1, 150, 0, new FindListener<ConsumeProject>() {
//            @Override
//            public void done(List<ConsumeProject> list, BmobException e) {
//                if (e == null) {
//                    Log.e("tf_test","queryConsumeProject ready ="+list.size());
//                    consumeProjects=list;
//                } else {
//                    Toast.makeText(getContext(), "搜索异常=" + Log.getStackTraceString(e), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    private List<ConsumeProject> consumeProjects;

    @SuppressLint("NotifyDataSetChanged")
    private void search(final String source, boolean req) {
//        if (true) {
//            return;
//        }

        if (req) {
            ProgressUtils.show(getActivity());
            if (MyApp.USE_LC) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<UserLC> userLCS = Utils.queryLCUser();
                        datasLC.clear();
                        datasLC = userLCS;

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void run() {
                                ProgressUtils.dismiss();
                                List<UserLC> temp = new ArrayList<>();
                                userLCS.sort(new Comparator<UserLC>() {
                                    @Override
                                    public int compare(UserLC o1, UserLC o2) {
                                        return o2.getNewNumber() - o1.getNewNumber();
                                    }
                                });
                                size.setText("会员总数:" + userLCS.size());
                                for (UserLC user : userLCS) {
                                    if (TextUtils.isEmpty(source)) {
                                        temp.add(user);
                                        continue;
                                    }
                                    if ((user.getName().contains(source) || user.getUsername().contains(source)) || user.getNumber().contains(source)) {
                                        temp.add(user);
                                    }
                                }
                                myAdapter.setDataLC(temp);
                                myAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }).start();
            } else {

//            query.order("-score,createdAt");
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
                                                        return o2.getNewNumber() - o1.getNewNumber();
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
                                                Toast.makeText(mContext, "搜索异常", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(mContext, "搜索异常", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }


        } else {
            if (MyApp.USE_LC){
                List<UserLC> temp = new ArrayList<>();
                for (UserLC user : datasLC) {
                    if (TextUtils.isEmpty(source)) {
                        temp.add(user);
                        continue;
                    }
                    if ((user.getName().contains(source) || user.getUsername().contains(source)) || user.getNumber().contains(source)) {
                        temp.add(user);
                    }
                }
                myAdapter.setDataLC(temp);
            }else {
                List<User2> temp = new ArrayList<>();
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
            }
            myAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected Fragment getFragement() {
        return this;
    }


    private class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<User2> list;
        private List<UserLC> listLC;
        Context context;

        public void setData(List<User2> list) {
            this.list = list;
        }

        public void setDataLC(List<UserLC> list) {
            this.listLC = list;
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
            if (!MyApp.USE_LC) {
                final User2 bean = list.get(position);
                holder.bindView(context, bean);
                holder.card_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), MemberDetailActivity.class);
                        intent.putExtra("action", "edit");
                        intent.putExtra("bean", bean);
                        startActivity(intent);
                    }
                });
            } else {
                final UserLC bean = listLC.get(position);
                holder.bindView(context, bean);
                holder.card_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), MemberDetailActivity.class);
                        intent.putExtra("action", "edit");
                        intent.putExtra("bean", bean);
                        startActivity(intent);
                    }
                });
            }


        }

        @Override
        public int getItemCount() {
            return list == null?listLC.size():list.size();
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

        public void bindView(Context context, final UserLC bean) {
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
