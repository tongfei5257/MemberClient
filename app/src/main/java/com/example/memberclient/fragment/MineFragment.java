package com.example.memberclient.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.memberclient.BuildConfig;
import com.example.memberclient.ISaveCallback;
import com.example.memberclient.R;
import com.example.memberclient.application.MyApp;
import com.example.memberclient.model.ConsumeProject;
import com.example.memberclient.model.ConsumeRecord;
import com.example.memberclient.model.Project;
import com.example.memberclient.model.Source;
import com.example.memberclient.model.User2;
import com.example.memberclient.ui.AboutActivity;
import com.example.memberclient.ui.LoginActivity;
import com.example.memberclient.ui.MainActivity;
import com.example.memberclient.model.User;
import com.example.memberclient.utils.ProgressUtils;
import com.example.memberclient.utils.SPUtils;
import com.example.memberclient.utils.ToastUtil;
import com.example.memberclient.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.school.pandakeep.view.CircleImageView;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.util.FileUtils;
import cn.leancloud.LCUser;


/**
 * on 2016/3/7 0007.
 * 我的模块
 */
public class MineFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.iv_user_photo)
    CircleImageView mIvUserPhoto;
    @Bind(R.id.me_username)
    TextView mUsername;
    @Bind(R.id.cb_use_lc)
    CheckBox cb_use_lc;
    private MainActivity mContext;
    private static final int UPDATE_USER = 0X1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (MainActivity) getActivity();
        getPeriodData();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mUsername = view.findViewById(R.id.me_username);
        cb_use_lc = view.findViewById(R.id.cb_use_lc);
        cb_use_lc.setChecked(SPUtils.getBoolean(getActivity().getApplicationContext(), "user_lc", false));
        cb_use_lc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtils.saveBoolean(getContext(), "user_lc", isChecked);
                MyApp.USE_LC = isChecked;
            }
        });
        view.findViewById(R.id.ll_me_about).setOnClickListener(this);
        view.findViewById(R.id.ll_modify_password).setOnClickListener(this);
        view.findViewById(R.id.ll_loginout).setOnClickListener(this);

//        User currentUser = BmobUser.getCurrentUser(User.class);
//        mUsername.setText(currentUser.getName() + "  " + currentUser.getUsername());
        if (MyApp.USE_LC) {
            mUsername.setText("当前操作人:" + LCUser.getCurrentUser().getUsername());
        } else {
            mUsername.setText("当前操作人:" + BmobUser.getCurrentUser(User.class).getName());
        }
        return view;
    }

    @Override
    protected Fragment getFragement() {
        return this;
    }


    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);

    }

    private List<String> mListPeriod = new ArrayList<>();

    private List<String> getPeriodData() {
        mListPeriod.add("仅一次");
        mListPeriod.add("每个工作日");
        mListPeriod.add("每个周末(六、日)");
        mListPeriod.add("每周");
        mListPeriod.add("每月");
        return mListPeriod;
    }

    @OnClick({R.id.ll_me_user, R.id.ll_me_reminder, R.id.ll_me_share,
            R.id.ll_me_about, R.id.ll_me_check, R.id.ll_loginout, R.id.ll_modify_password})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_me_user: {
//                startActivityForResult(new Intent(mContext, UserActivity.class), UPDATE_USER);
            }
            break;
            case R.id.ll_me_reminder: {

//                startActivityForResult(new Intent(mContext, AccountRemindAddActivity.class), 2);

            }
            break;


            case R.id.ll_me_about: {
                startActivity(new Intent(mContext, AboutActivity.class));
            }
            break;
            case R.id.ll_loginout: {
                if (MyApp.USE_LC) {

                } else {
                    BmobUser.logOut();
                }
                startActivity(new Intent(mContext, LoginActivity.class));
            }
            break;
            case R.id.ll_modify_password:
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
//                String format = simpleDateFormat.format(new Date());
//                File excelFile = new File("/storage/emulated/0/Android/data/com.example.memberclient/cache/会员数据(2022-09-09-08-24).xls");
//                shareFile(getContext(),excelFile);
//                if (true){
//                    return;
//                }
                ProgressUtils.show(getContext(), "导出中,请稍后...");
                Utils.save(getContext(), new ISaveCallback() {
                    @Override
                    public void onSave(Source source) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    File file = parseSource(source);
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            ProgressUtils.dismiss();
                                            ToastUtil.show(getContext(), "导出成功", Toast.LENGTH_LONG);
                                            shareFile(getContext(), file);
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            ProgressUtils.dismiss();
                                            ToastUtil.show(getContext(), "导出失败" + Log.getStackTraceString(e), Toast.LENGTH_LONG);

                                        }
                                    });
                                }
                            }
                        }).start();

                    }
                });

                break;


        }
    }

    public void shareFile(Context context, File file) {
        try {
            Log.e("tf_test", "shareFile=" + file.getAbsolutePath());
            if (null != file && file.exists()) {
                Intent share = new Intent(Intent.ACTION_SEND);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);
                    share.putExtra(Intent.EXTRA_STREAM, contentUri);
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                }
                share.setType("application/vnd.ms-excel");//此处可发送多种文件
                share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(Intent.createChooser(share, "分享文件"));
            } else {
                Toast.makeText(context, "分享文件不存在", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("tf_test", Log.getStackTraceString(e));
        }

    }

    private File parseSource(Source source) throws Exception {
        Log.d("tf_test", "开始解析数据......");


        List<ConsumeRecord> crList = source.crs;
        List<User2> userList = source.users;
        List<ConsumeProject> cpList = source.cps;
        List<Project> pList = source.projects;


        Log.d("tf_test", "开始绑定CP....1");
        int errorCount = 0;
        for (ConsumeProject invdata : cpList) {
            User2 userId = invdata.user;
            for (User2 tUser : userList) {
                if (tUser.getObjectId() == null) {
                    Log.d("tf_test", "tUser 异常：" + errorCount);
                    errorCount++;
                    continue;
                }
                if (tUser.getObjectId().equals(userId.getObjectId())) {
                    Project project = Project.find(pList, invdata.getParent());
                    if (project == null) {
                        Log.d("tf_test", "find project 异常=" + invdata);
                        continue;
                    }
                    ConsumeProject consumeProject = new ConsumeProject(invdata);
                    consumeProject.setParent(project);
                    consumeProject.setUser(tUser);
                    tUser.setConsumeProject(consumeProject);
                }
            }
        }
        Log.d("tf_test", "开始合并CR....2");
        int count = -1;

        for (ConsumeRecord consumeRecord : crList) {
            count++;
            ConsumeProject from = consumeRecord.getFrom();
            User2 temp = from.getUser();
            if (from.getObjectId() == null || temp.getObjectId() == null || temp.getObjectId().equals("")
                    || from.getObjectId().equals("null")) {
                Log.d("tf_test", "开始解析ConsumeRecord第" + count + "个发生异常");
                continue;
            }
            for (User2 tUser : userList) {
                if (tUser.getObjectId().equals(temp.getObjectId())) {
                    tUser.setConsumeRecord(consumeRecord);
                }
            }
        }
        Log.d("tf_test", "合并CR结束");
        Log.d("tf_test", "开始生成execl表格");
//            HSSFWorkbook
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
        String format = simpleDateFormat.format(new Date());
        File excelFile = new File(getActivity().getExternalCacheDir(), "会员数据(" + format + ").xls");
        FileOutputStream out = new FileOutputStream(excelFile);
        // 第一步，创建一个workbook，对应一个Excel文件
        HSSFWorkbook workbook = new HSSFWorkbook();


        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet userSt = workbook.createSheet("会员&项目表");
        HSSFSheet hssfSheet2 = workbook.createSheet("会员表");
        HSSFSheet hssfSheet3 = workbook.createSheet("会员消费记录表");
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
        int index = 0;
        int index2 = 0, index3 = 0;

        HSSFRow row = userSt.createRow(index);
        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle hssfCellStyle = workbook.createCellStyle();
        String[] titles = new String[]{
                "编号", "姓名", "电话号码", "备注", "创建时间", "cTime", "uTime", "会员Id", "项目(名称)"
                , "项目(总次数)", "项目(剩余次数)", "项目id"
        };
        String[] titles2 = new String[]{
                "编号", "姓名", "电话号码", "备注", "创建时间", "cTime", "uTime", "会员Id"
        };
        String[] titles3 = new String[]{
                "编号", "姓名", "电话号码", "备注", "创建时间", "cTime", "uTime", "项目(名称)"
                , "项目(总次数)", "项目(剩余次数)", "项目id", "消费项目", "消费时间", "消费CTime", "消费uTime", "消费备注", "消费Id"
        };

        HSSFRow row2 = hssfSheet2.createRow(index2);
        HSSFRow row3 = hssfSheet3.createRow(index3);
        HSSFCell hssfCell = null;
        for (int i = 0; i < titles.length; i++) {
            hssfCell = row.createCell(i);//列索引从0开始
            hssfCell.setCellValue(titles[i]);//列名1
            hssfCell.setCellStyle(hssfCellStyle);//列居中显示
        }
        for (int i = 0; i < titles2.length; i++) {
            hssfCell = row2.createCell(i);//列索引从0开始
            hssfCell.setCellValue(titles2[i]);//列名1
            hssfCell.setCellStyle(hssfCellStyle);//列居中显示
        }
        for (int i = 0; i < titles3.length; i++) {
            hssfCell = row3.createCell(i);//列索引从0开始
            hssfCell.setCellValue(titles3[i]);//列名1
            hssfCell.setCellStyle(hssfCellStyle);//列居中显示
        }

        for (int i = 0; i < userList.size(); i++) {
            User2 user2 = userList.get(i);
            List<ConsumeProject> consumeProjects = user2.consumeProjects;
            index2++;
            row2 = hssfSheet2.createRow(index2);
            row2.createCell(0).setCellValue(user2.newNumber);
            row2.createCell(1).setCellValue(user2.name);
            row2.createCell(2).setCellValue(user2.username);
            row2.createCell(3).setCellValue(user2.remark);
            row2.createCell(4).setCellValue(user2.date);
            row2.createCell(5).setCellValue(user2.getCreatedAt());
            row2.createCell(6).setCellValue(user2.getUpdatedAt());
            row2.createCell(7).setCellValue(user2.getObjectId());
            if (consumeProjects.size() == 0) {
                Log.d("tf_test", user2 + "未有绑定项目");
                index++;
                row = userSt.createRow(index);
                // 第六步，创建单元格，并设置值
                row.createCell(0).setCellValue(user2.newNumber);
                row.createCell(1).setCellValue(user2.name);
                row.createCell(2).setCellValue(user2.username);
                row.createCell(3).setCellValue(user2.remark);
                row.createCell(4).setCellValue(user2.date);
                row.createCell(5).setCellValue(user2.getCreatedAt());
                row.createCell(6).setCellValue(user2.getUpdatedAt());
                row.createCell(7).setCellValue(user2.getObjectId());
                continue;
            }


            for (ConsumeProject consumeProject : consumeProjects) {
                index++;
                row = userSt.createRow(index);
                row.createCell(0).setCellValue(user2.newNumber);
                row.createCell(1).setCellValue(user2.name);
                row.createCell(2).setCellValue(user2.username);
                row.createCell(3).setCellValue(user2.remark);
                row.createCell(4).setCellValue(user2.date);
                row.createCell(5).setCellValue(user2.getCreatedAt());
                row.createCell(6).setCellValue(user2.getUpdatedAt());
                row.createCell(7).setCellValue(user2.getObjectId());
                row.createCell(8).setCellValue(consumeProject.getParent().getName());
                row.createCell(9).setCellValue(consumeProject.getParent().getTotalCount());
                row.createCell(10).setCellValue(consumeProject.getRemainCount());
                row.createCell(11).setCellValue(consumeProject.getObjectId());

                List<ConsumeRecord> consumeRecords = consumeProject.consumeRecords;
                if (consumeRecords.size() > 0) {
                    for (ConsumeRecord consumeRecord : consumeRecords) {
                        if (consumeRecord == null || consumeRecord.getObjectId() == null) {
                            Log.d("tf_test", consumeRecord + "有异常");
                            continue;
                        }
                        index3++;
                        row3 = hssfSheet3.createRow(index3);
                        row3.createCell(0).setCellValue(user2.newNumber);
                        row3.createCell(1).setCellValue(user2.name);
                        row3.createCell(2).setCellValue(user2.username);
                        row3.createCell(3).setCellValue(user2.remark);
                        row3.createCell(4).setCellValue(user2.date);
                        row3.createCell(5).setCellValue(user2.getCreatedAt());
                        row3.createCell(6).setCellValue(user2.getUpdatedAt());
                        row3.createCell(7).setCellValue(consumeProject.getParent().getName());
                        row3.createCell(8).setCellValue(consumeProject.getParent().getTotalCount());
                        row3.createCell(9).setCellValue(consumeProject.getRemainCount());
                        row3.createCell(10).setCellValue(consumeProject.getObjectId());
                        row3.createCell(11).setCellValue(consumeRecord.getName());
                        row3.createCell(12).setCellValue(consumeRecord.getDate());
                        row3.createCell(13).setCellValue(consumeRecord.getCreatedAt());
                        row3.createCell(14).setCellValue(consumeRecord.getUpdatedAt());
                        row3.createCell(15).setCellValue(consumeRecord.getRemark());
                        row3.createCell(16).setCellValue(consumeRecord.getObjectId());
                    }

                }
            }
        }
        workbook.write(out);
        out.flush();
        out.close();
        Log.d("tf_test", "生成excel结束:" + excelFile.getAbsolutePath());
        return excelFile;
    }


}
