package com.jesus.smsforged;

import com.umeng.analytics.AnalyticsConfig;

import me.base.UiHelper;
import me.utils.L;
import me.utils.TextUtils;
import a.b.c.AdManager;
import a.b.c.dev.AppUpdateInfo;
import a.b.c.dev.CheckAppUpdateCallBack;
import a.b.c.dev.OnlineConfigCallBack;
//import net.youmi.android.AdManager;
//import net.youmi.android.dev.AppUpdateInfo;
//import net.youmi.android.dev.CheckAppUpdateCallBack;
//import net.youmi.android.dev.OnlineConfigCallBack;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;

public class SFApplication extends me.base.BaseApplication{

	
	static String mykey = "_ad_config";  // key
	static String defaultValue = "true";    // 默认的 value，当获取不到在线参数时，会返回该值
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();		
		crashHandler.sendPreviousReportsToServer();
		L.setDebug(false);
		
		L.d(mykey);
		mykey = metaData() + mykey;
		L.d(mykey);
		
		// 初始化接口，应用启动的时候调用
		// 参数：appId, appSecret, 调试模式
		AdManager.getInstance(this).init("611784a618709bfd", "45d6c2c280671f6e", false);
		
		//关闭有米的 Debug Log
		AdManager.getInstance(this).setEnableDebugLog(false);
		// 开启用户数据统计服务,默认不开启，传入 false 值也不开启，只有传入 true 才会调用
		AdManager.getInstance(this).setUserDataCollect(true);
		
		// 2. 异步调用（可在任意线程中调用）
		AdManager.getInstance(this).asyncGetOnlineConfig(mykey, new OnlineConfigCallBack() {
			
			@Override
			public void onGetOnlineConfigSuccessful(String key, String value) {
				// 获取在线参数成功
				L.i("获取在线参数成功<key>" + key + "<value>" + value);
				defaultValue = value;
			}
			
			@Override
			public void onGetOnlineConfigFailed(String key) {
				// 获取在线参数失败，可能原因有：键值未设置或为空、网络异常、服务器异常
				L.i("获取在线参数失败<key>" + key);
			}
		});
		
		// 2. 异步调用本应用更新：
		AdManager.getInstance(this).asyncCheckAppUpdate(new CheckAppUpdateCallBack() {
			
			@Override
			public void onCheckAppUpdateFinish(AppUpdateInfo updateInfo) {
				// 检查更新回调，注意，这里是在 UI 线程回调的，因此您可以直接与 UI 交互，但不可以进行长时间的操作（如在这里访问网络是不允许的）
		        if (updateInfo == null) {
		            // 当前已经是最新版本
		        }
		        else {
		            // 有更新信息
		        	update(updateInfo);
		        }
			}
		});
		
		//友盟
		AnalyticsConfig.setAppkey("54043947fd98c57d9c03d641");
		//每台设备仅记录首次安装激活的渠道，如果该设备再次安装其他渠道包，则数据仍会被记录在初始的安装渠道上。 所以在测试不同的渠道时，请使用不同的设备来分别测试。也可使用集成测试功能进行测试
		AnalyticsConfig.setChannel("91sj");
		//session统计
		//在每个Activity的onResume方法中调用 MobclickAgent.onResume(Context), onPause方法中调用 MobclickAgent.onPause(Context).才能够保证获取正确的新增用户、活跃用户、启动次数、使用时长等基本数据。
	}
	
	public static boolean showAd() {
		boolean isOpen = TextUtils.equals(defaultValue, "true");
		L.i("isOpen>" + isOpen);
		return isOpen;
	}
	
	private void update(final AppUpdateInfo result) {
		try {
            if (result == null || result.getUrl() == null) {
                // 如果 AppUpdateInfo 为 null 或它的 url 属性为 null，则可以判断为没有新版本。
                UiHelper.showShortToast(globalContext, "当前版本已经是最新版");
                return;
            }

            // 这里简单示例使用一个对话框来显示更新信息
            new AlertDialog.Builder(globalContext)
                .setTitle("发现新版本")
                .setMessage(result.getUpdateTips()) // 这里是版本更新信息
                .setNegativeButton("马上升级",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getUrl()) );
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            globalContext.startActivity(intent);
                            // ps：这里示例点击“马上升级”按钮之后简单地调用系统浏览器进行新版本的下载，
                            // 但强烈建议开发者实现自己的下载管理流程，这样可以获得更好的用户体验。
                        }
                })
                .setPositiveButton("下次再说",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                }).create().show();
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
	}

	@Override
	public void init(Context context) {
		me.utils.L.setDebug(true);
		
		config = new me.base.Config.Builder(context)
		.dirAppRoot("SF")
		.dirErrorLog("Log")
		.reportsDelete(false)
		.reportsExtension(".log")
		.build();
	}
	
	private String metaData() {
		ApplicationInfo appInfo;
		try {
			appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = appInfo.metaData;
			if (null != bundle && bundle.containsKey("channel")) 
				return appInfo.metaData.getString("channel");
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	@Override
	public void sendReport(String content) {
		//发送错误日志
//		FileUtils.writeFile(FileUtils.getAppRootPath() + "saa.txt", content, false);
	}
	
}
