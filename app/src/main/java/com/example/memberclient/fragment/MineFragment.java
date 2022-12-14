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
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.memberclient.BuildConfig;
import com.example.memberclient.ISaveCallback;
import com.example.memberclient.R;
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


/**
 * on 2016/3/7 0007.
 * ๆ็ๆจกๅ
 */
public class MineFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.iv_user_photo)
    CircleImageView mIvUserPhoto;
    @Bind(R.id.me_username)
    TextView mUsername;
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
        view.findViewById(R.id.ll_me_about).setOnClickListener(this);
        view.findViewById(R.id.ll_modify_password).setOnClickListener(this);
        view.findViewById(R.id.ll_loginout).setOnClickListener(this);

        User currentUser = BmobUser.getCurrentUser(User.class);
        mUsername.setText(currentUser.getName() + "  " + currentUser.getUsername());
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
        mListPeriod.add("ไปไธๆฌก");
        mListPeriod.add("ๆฏไธชๅทฅไฝๆฅ");
        mListPeriod.add("ๆฏไธชๅจๆซ(ๅญใๆฅ)");
        mListPeriod.add("ๆฏๅจ");
        mListPeriod.add("ๆฏๆ");
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
                BmobUser.logOut();
                startActivity(new Intent(mContext, LoginActivity.class));
            }
            break;
            case R.id.ll_modify_password:
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
//                String format = simpleDateFormat.format(new Date());
//                File excelFile = new File("/storage/emulated/0/Android/data/com.example.memberclient/cache/ไผๅๆฐๆฎ(2022-09-09-08-24).xls");
//                shareFile(getContext(),excelFile);
//                if (true){
//                    return;
//                }
                ProgressUtils.show(getContext(),"ๅฏผๅบไธญ,่ฏท็จๅ...");
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
                                            ToastUtil.show(getContext(),"ๅฏผๅบๆๅ", Toast.LENGTH_LONG);
                                            shareFile(getContext(),file);
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            ProgressUtils.dismiss();
                                            ToastUtil.show(getContext(),"ๅฏผๅบๅคฑ่ดฅ"+Log.getStackTraceString(e), Toast.LENGTH_LONG);

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

    public  void shareFile(Context context, File  file) {
        try {
            Log.e("tf_test","shareFile="+file.getAbsolutePath());
            if (null != file && file.exists()) {
                Intent share = new Intent(Intent.ACTION_SEND);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider",file);
                    share.putExtra(Intent.EXTRA_STREAM, contentUri);
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                }
                share.setType("application/vnd.ms-excel");//ๆญคๅคๅฏๅ้ๅค็งๆไปถ
                share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(Intent.createChooser(share, "ๅไบซๆไปถ"));
            } else {
                Toast.makeText(context, "ๅไบซๆไปถไธๅญๅจ", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Log.e("tf_test",Log.getStackTraceString(e));
        }

    }

    private File parseSource(Source source) throws Exception {
        Log.d( "tf_test","ๅผๅง่งฃๆๆฐๆฎ......");


        List<ConsumeRecord> crList = source.crs;
        List<User2> userList = source.users;
        List<ConsumeProject> cpList =source.cps;
        List<Project> pList =source.projects;


        Log.d( "tf_test","ๅผๅง็ปๅฎCP....1");
        int errorCount = 0;
        for (ConsumeProject invdata : cpList) {
            User2 userId = invdata.user;
            for (User2 tUser : userList) {
                if (tUser.getObjectId() == null) {
                    Log.d( "tf_test","tUser ๅผๅธธ๏ผ" + errorCount);
                    errorCount++;
                    continue;
                }
                if (tUser.getObjectId().equals(userId.getObjectId())) {
                    Project project = Project.find(pList, invdata.getParent());
                    if (project == null) {
                        Log.d( "tf_test","find project ๅผๅธธ=" + invdata);
                        continue;
                    }
                    ConsumeProject consumeProject = new ConsumeProject(invdata);
                    consumeProject.setParent(project);
                    consumeProject.setUser(tUser);
                    tUser.setConsumeProject(consumeProject);
                }
            }
        }
        Log.d( "tf_test","ๅผๅงๅๅนถCR....2");
        int count = -1;

        for (ConsumeRecord consumeRecord : crList) {
            count++;
            ConsumeProject from = consumeRecord.getFrom();
            User2 temp = from.getUser();
            if (from.getObjectId() == null || temp.getObjectId() == null || temp.getObjectId().equals("")
                    || from.getObjectId().equals("null")) {
                Log.d( "tf_test","ๅผๅง่งฃๆConsumeRecord็ฌฌ" + count + "ไธชๅ็ๅผๅธธ");
                continue;
            }
            for (User2 tUser : userList) {
                if (tUser.getObjectId().equals(temp.getObjectId())) {
                    tUser.setConsumeRecord(consumeRecord);
                }
            }
        }
        Log.d( "tf_test","ๅๅนถCR็ปๆ");
        Log.d( "tf_test","ๅผๅง็ๆexecl่กจๆ?ผ");
//            HSSFWorkbook
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
        String format = simpleDateFormat.format(new Date());
        File excelFile = new File(getActivity().getExternalCacheDir(),"ไผๅๆฐๆฎ("+format+").xls");
        FileOutputStream out = new FileOutputStream(excelFile);
        // ็ฌฌไธๆญฅ๏ผๅๅปบไธไธชworkbook๏ผๅฏนๅบไธไธชExcelๆไปถ
        HSSFWorkbook workbook = new HSSFWorkbook();


        // ็ฌฌไบๆญฅ๏ผๅจwebbookไธญๆทปๅ?ไธไธชsheet,ๅฏนๅบExcelๆไปถไธญ็sheet
        HSSFSheet userSt = workbook.createSheet("ไผๅ&้กน็ฎ่กจ");
        HSSFSheet hssfSheet2 = workbook.createSheet("ไผๅ่กจ");
        HSSFSheet hssfSheet3 = workbook.createSheet("ไผๅๆถ่ดน่ฎฐๅฝ่กจ");
        // ็ฌฌไธๆญฅ๏ผๅจsheetไธญๆทปๅ?่กจๅคด็ฌฌ0่ก,ๆณจๆ่็ๆฌpoiๅฏนExcel็่กๆฐๅๆฐๆ้ๅถshort
        int index=0;
        int index2=0,index3=0;

        HSSFRow row = userSt.createRow(index);
        // ็ฌฌๅๆญฅ๏ผๅๅปบๅๅๆ?ผ๏ผๅนถ่ฎพ็ฝฎๅผ่กจๅคด ่ฎพ็ฝฎ่กจๅคดๅฑไธญ
        HSSFCellStyle hssfCellStyle = workbook.createCellStyle();
        String[] titles=new String[]{
                "็ผๅท", "ๅงๅ","็ต่ฏๅท็?","ๅคๆณจ","ๅๅปบๆถ้ด","cTime","uTime","ไผๅId","้กน็ฎ(ๅ็งฐ)"
                ,"้กน็ฎ(ๆปๆฌกๆฐ)","้กน็ฎ(ๅฉไฝๆฌกๆฐ)","้กน็ฎid"
        };
        String[] titles2=new String[]{
                "็ผๅท", "ๅงๅ","็ต่ฏๅท็?","ๅคๆณจ","ๅๅปบๆถ้ด","cTime","uTime","ไผๅId"
        };
        String[] titles3=new String[]{
                "็ผๅท", "ๅงๅ","็ต่ฏๅท็?","ๅคๆณจ","ๅๅปบๆถ้ด","cTime","uTime","้กน็ฎ(ๅ็งฐ)"
                ,"้กน็ฎ(ๆปๆฌกๆฐ)","้กน็ฎ(ๅฉไฝๆฌกๆฐ)","้กน็ฎid","ๆถ่ดน้กน็ฎ","ๆถ่ดนๆถ้ด","ๆถ่ดนCTime","ๆถ่ดนuTime","ๆถ่ดนๅคๆณจ","ๆถ่ดนId"
        };

        HSSFRow row2 = hssfSheet2.createRow(index2);
        HSSFRow row3 = hssfSheet3.createRow(index3);
        HSSFCell hssfCell = null;
        for (int i = 0; i < titles.length; i++) {
            hssfCell = row.createCell(i);//ๅ็ดขๅผไป0ๅผๅง
            hssfCell.setCellValue(titles[i]);//ๅๅ1
            hssfCell.setCellStyle(hssfCellStyle);//ๅๅฑไธญๆพ็คบ
        }
        for (int i = 0; i < titles2.length; i++) {
            hssfCell = row2.createCell(i);//ๅ็ดขๅผไป0ๅผๅง
            hssfCell.setCellValue(titles2[i]);//ๅๅ1
            hssfCell.setCellStyle(hssfCellStyle);//ๅๅฑไธญๆพ็คบ
        }
        for (int i = 0; i < titles3.length; i++) {
            hssfCell = row3.createCell(i);//ๅ็ดขๅผไป0ๅผๅง
            hssfCell.setCellValue(titles3[i]);//ๅๅ1
            hssfCell.setCellStyle(hssfCellStyle);//ๅๅฑไธญๆพ็คบ
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
            if (consumeProjects.size()==0){
                Log.d( "tf_test",user2+"ๆชๆ็ปๅฎ้กน็ฎ");
                index++;
                row = userSt.createRow(index);
                // ็ฌฌๅญๆญฅ๏ผๅๅปบๅๅๆ?ผ๏ผๅนถ่ฎพ็ฝฎๅผ
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


            for (ConsumeProject consumeProject:consumeProjects) {
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
                if (consumeRecords.size()>0){
                    for (ConsumeRecord consumeRecord:consumeRecords) {
                        if (consumeRecord==null||consumeRecord.getObjectId()==null){
                            Log.d( "tf_test",consumeRecord+"ๆๅผๅธธ");
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
        Log.d( "tf_test","็ๆexcel็ปๆ:"+excelFile.getAbsolutePath());
        return excelFile;
    }


}
