package com.example.memberclient.application;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.widget.RadioButton;
import android.widget.TabHost;

import com.example.memberclient.model.Project;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.bmob.v3.Bmob;


/**
 * 全局变量类
 * @author hjgang
 */
public class MyApp extends Application {


	private boolean IsCheckLogin;
	public   List<Project> projectBeans=new ArrayList<>();

	private TabHost tabHost;
	private RadioButton MyStoreButton;
	private RadioButton searchButton;
	private RadioButton cartButton;
	private RadioButton HomeButton;
    public boolean m_bKeyRight = true;



	private List<Activity> activityList = new LinkedList();



	@Override
	public void onCreate() {
		super.onCreate();
//		Bmob.resetDomain("http://bmob.hyglapp.cn/8/");
		Bmob.resetDomain("http://bmob2.hyglapp.cn/8/");
//		//Bmob初始化
        Bmob.initialize(this.getApplicationContext(),
//                "42261a6ee51b4e08f8b2b1807b1bb476");
                "1e3f7a39c4badc886a82d2614e055684");
	}

	
	
	// 添加Activity到容器中
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}
	// 遍历所有Activity并finish

		public void exit(Context context) {

			for (Activity activity : activityList) {
				activity.finish();
			}
			
			ActivityManager activityMgr= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE );

	        activityMgr.restartPackage(context.getPackageName());

			System.exit(0);

		}
}
