package com.example.memberclient.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.memberclient.ISaveCallback;
import com.example.memberclient.model.ConsumeProject;
import com.example.memberclient.model.ConsumeRecord;
import com.example.memberclient.model.Project;
import com.example.memberclient.model.Source;
import com.example.memberclient.model.User2;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.util.FileUtils;

public class Utils {
    public static List sConsumeRecordResult = new ArrayList<>();
    public static List sUserResult = new ArrayList<>();
    public static List sConsumeProjectResult = new ArrayList<>();
    public static int count=0;
    @TargetApi(Build.VERSION_CODES.FROYO)
    public synchronized static  void save(Context context, ISaveCallback callback){
        final File externalCacheDir = context.getExternalCacheDir();
         count=0;
        Source source=new Source();
        BmobQuery<User2>  bmobQuery = new BmobQuery<User2>();
        bmobQuery.addWhereEqualTo("type", "2")
                .addWhereEqualTo("delete", false)
                .order("-newNumber,-createdAt");
//        final Object lock=new Object();
        queryUser(bmobQuery, 500, 0, new FindListener<User2>() {
            @Override
            public void done(List<User2> list, BmobException e) {

                Gson gson=new Gson();
                String s = gson.toJson(list);
                Log.e("tf_test","user="+list.size());
                File file =new File(externalCacheDir,"user.json");
                try {
                    FileUtils.writeStringToFile(file,s,"utf-8");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                source.users=list;
                count++;
                write2local(context,source,count, callback);

//                        List<User2> result = gson.fromJson(s, new TypeToken<List<User2>>() {
//                        }.getType());
//                        File dataDir = getContext().getDataDir();
//                lock.notifyAll();
            }

        });



        BmobQuery<Project> query = new BmobQuery<>();
        query.addWhereEqualTo("delete", false)
                .order("-createdAt")
                .findObjects(new FindListener<Project>() {
                    @Override
                    public void done(List<Project> list, BmobException e) {
                        Gson gson=new Gson();
                        String s = gson.toJson(list);
                        Log.e("tf_test","project="+list.size());
                        File file =new File(externalCacheDir,"project.json");
                        try {
                            FileUtils.writeStringToFile(file,s,"utf-8");
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        source.projects=list;
                        count++;
                        write2local(context,source,count,callback);

//                        List<User2> result = gson.fromJson(s, new TypeToken<List<User2>>() {
//                        }.getType());
//                        File dataDir = getContext().getDataDir();
//                lock.notifyAll();
                    }


                });


        BmobQuery<ConsumeRecord> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("delete", false)
                .order("-createdAt")
                .include("from,operator,from.user,from.parent");
        queryConsumeRecord(query2, 100, 0, new FindListener<ConsumeRecord>() {
            @Override
            public void done(List<ConsumeRecord> list, BmobException e) {

                if (e == null) {
                    Gson gson=new Gson();
                    String s = gson.toJson(list);

                    File file = new File(externalCacheDir, "ConsumeRecord.json");
                    Log.e("tf_test",file.getAbsolutePath());
                    Log.e("tf_test","queryConsumeRecord="+list.size());
                    source.crs=list;
                    count++;
                    write2local(context,source,count, callback);
                    try {
                        FileUtils.writeStringToFile(file,s,"utf-8");
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }

                } else {

                }

            }
        });



//        try {
//            lock.wait();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        BmobQuery<ConsumeProject> query3 = new BmobQuery<>();
        query3.addWhereEqualTo("delete", false)
                .order("-createdAt")
                .include("user,parent");
        queryConsumeProject(query3, 100, 0, new FindListener<ConsumeProject>() {
            @Override
            public void done(List<ConsumeProject> list, BmobException e) {
                if (e == null) {
                    source.cps=list;
                    count++;
                    write2local(context,source,count, callback);
                    Gson gson=new Gson();
                    String s = gson.toJson(list);

                    File file = new File(externalCacheDir, "ConsumeProject.json");
                    Log.e("tf_test",file.getAbsolutePath());
                    Log.e("tf_test","ConsumeProject="+list.size());

                    try {
                        FileUtils.writeStringToFile(file,s,"utf-8");
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }

                } else {

                }

            }
        });
    }

    private static void write2local(Context context, Source source, int count, ISaveCallback callback) {
        Log.e("tf_test","write2local="+count);
        final File externalCacheDir = context.getExternalCacheDir();

        if (count==4){
            Gson gson=new Gson();
            String s = gson.toJson(source);
            File file = new File(externalCacheDir, "source.json");
            Log.e("tf_test","write2local="+file.getAbsolutePath());
            callback.onSave(source);
            try {
                FileUtils.writeStringToFile(file,s,"utf-8");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


    public static void queryConsumeRecord(final BmobQuery<ConsumeRecord> bmobQuery, final int limit, final int skip, final FindListener<ConsumeRecord> findListener) {
        if (skip == 0) {
            sConsumeRecordResult = new ArrayList<ConsumeRecord>();
        }
        bmobQuery.setLimit(limit);
        if (skip > 0) {
            bmobQuery.setSkip(skip);
        }
        FindListener<ConsumeRecord> proxy = new FindListener<ConsumeRecord>() {
            @Override
            public void done(List<ConsumeRecord> list, BmobException e) {

                Log.e("tf_test", "queryConsumeRecord list=" +(list!=null? list.size():0) + ",e=" + Log.getStackTraceString(e));
                if (e == null) {
                    sConsumeRecordResult.addAll(list);
                    if (list.size() < limit) {
                        findListener.done(sConsumeRecordResult, null);
                    } else {
                        queryConsumeRecord(bmobQuery, limit, skip + limit, findListener);
                    }
                } else {
                    findListener.done(list, e);
                }
            }
        };
        bmobQuery.findObjects(proxy);
    }

    public static void queryConsumeProject(final BmobQuery<ConsumeProject> bmobQuery, final int limit, final int skip, final FindListener<ConsumeProject> findListener) {
        if (skip == 0) {
            sConsumeProjectResult = new ArrayList<ConsumeProject>();
        }
        bmobQuery.setLimit(limit);
        if (skip > 0) {
            bmobQuery.setSkip(skip);
        }
        FindListener<ConsumeProject> proxy = new FindListener<ConsumeProject>() {
            @Override
            public void done(List<ConsumeProject> list, BmobException e) {
                if (e == null) {
                    Log.e("tf_test", "queryConsumeProject list=" + list.size() + ",e=" + Log.getStackTraceString(e));
                    sConsumeProjectResult.addAll(list);
                    if (list.size() < limit) {
                        findListener.done(sConsumeProjectResult, null);
                    } else {
                        queryConsumeProject(bmobQuery, limit, skip + limit, findListener);
                    }
                } else {
                    findListener.done(list, e);
                }
            }
        };
        bmobQuery.findObjects(proxy);
    }

    public static void queryUser(final BmobQuery<User2> bmobQuery, final int limit, final int skip, final FindListener<User2> findListener) {
        if (skip == 0) {
            sUserResult = new ArrayList<User2>();
        }
        bmobQuery.setLimit(limit);
        if (skip > 0) {
            bmobQuery.setSkip(skip);
        }
        FindListener<User2> proxy = new FindListener<User2>() {
            @Override
            public void done(List<User2> list, BmobException e) {
                if (e == null) {
                    Log.e("tf_test", "queryUser list=" + list.size());
                    sUserResult.addAll(list);
                    if (list.size() < limit) {
                        findListener.done(sUserResult, null);
                    } else {
                        queryUser(bmobQuery, limit, skip + limit, findListener);
                    }
                } else {
                    findListener.done(list, e);
                }
            }
        };
        bmobQuery.findObjects(proxy);
    }

    public static <T extends BmobObject> void update(final List<T> list) {
        if (list.size() <= 49) {
            Log.e("tf_test", "update.....");
            new BmobBatch().insertBatch((List<BmobObject>) list).doBatch(new QueryListListener<BatchResult>() {
                @Override
                public void done(List<BatchResult> results, BmobException e) {
                    if (e == null) {
                        Log.e("tf_test", "update..... success");

                        for (int i = 0; i < results.size(); i++) {
                            BatchResult result = results.get(i);
                            BmobException ex = result.getError();
                            if (ex == null) {
                                Log.e("tf_test", "第" + i + "个数据批量更新成功：" + result.getCreatedAt() + "," + result.getObjectId() + "," + result.getUpdatedAt());
                            } else {
                                Log.e("tf_test", "第" + i + "个数据批量更新失败：" + ex.getMessage() + "," + ex.getErrorCode());

                            }
                        }
                    } else {
                        Log.e("tf_test", Log.getStackTraceString(e));
//                                    Snackbar.make(mBtnUpdate, "失败：" + e.getMessage() + "," + e.getErrorCode(), Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            for (int i = 0; i < list.size(); i += 49) {
                final int finalI = i;
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ((finalI + 49) < list.size()) {
                            update(list.subList(finalI, finalI + 49));
                        } else {
                            update(list.subList(finalI, list.size()));
                        }
                    }
                }, 3000 + i * 30);

            }
        }


    }

    public static String getJson(String fileName,Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

}
