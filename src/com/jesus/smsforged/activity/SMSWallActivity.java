package com.jesus.smsforged.activity;

import java.util.ArrayList;
import java.util.List;

//import net.youmi.android.banner.AdSize;
//import net.youmi.android.banner.AdView;
//import net.youmi.android.diy.DiyManager;
//import net.youmi.android.spot.SpotDialogListener;
//import net.youmi.android.spot.SpotManager;

import com.jesus.smsforged.R;
import com.jesus.smsforged.SFApplication;
import com.jesus.smsforged.utils.DateUtils;
import com.melnykov.fab.FloatingActionButton;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.UMComment;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.FetchCommetsListener;

import a.b.c.br.AdSize;
import a.b.c.br.AdView;
import a.b.c.diy.DiyManager;
import a.b.c.st.SpotDialogListener;
import a.b.c.st.SpotManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import me.base.BaseFragmentActivity;
import me.base.ViewHolder;
import me.utils.L;

public class SMSWallActivity extends BaseFragmentActivity implements OnScrollListener, OnClickListener {
	
	public static final int SMS_GET = 0x001;
	public static final int SMS_SHOW = SMS_GET + 1;

    public static final int HANDLER_MSG_INIT_AD = SMS_SHOW + 1;
    public static final int HANDLER_MSG_SHOW = HANDLER_MSG_INIT_AD + 1;
	
    private LinearLayout mActionBack;
	private ListView mWallList;
	private FloatingActionButton mFloatingActionButton;
	
	private List<UMComment> mUmComments = new ArrayList<UMComment>();
	private long mSinceTime;
	private boolean isFirst = true;
	private SMSWallAdapter mAdapter;	

	private UMSocialService controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
        controller = UMServiceFactory.getUMSocialService("com.umeng.comment");
        setContentView(R.layout.activity_wall);
        
        findViewById(R.id.home).setVisibility(View.GONE);
		((TextView) findViewById(R.id.action_bar_title)).setText(R.string.app_name);
        
        mActionBack = (LinearLayout) findViewById(R.id.action_mode_back);
        mActionBack.setOnClickListener(this);
        mWallList = (ListView) findViewById(R.id.wall_list);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.button_floating_action);
        mFloatingActionButton.attachToListView(mWallList);
        mFloatingActionButton.setOnClickListener(this);
                
        mWallList.setOnScrollListener(this);
        
        obtion();

        myHandler.sendEmptyMessageDelayed(HANDLER_MSG_INIT_AD, 3000);
	}
	
	private void obtion() {
		myHandler.sendEmptyMessage(SMS_GET);
	}
	
	private Handler myHandler = new Handler(Looper.getMainLooper()) {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SMS_GET:
				if (isFirst) {
					mSinceTime = System.currentTimeMillis() - 500;	
				} else {
					mSinceTime = mUmComments.get(mUmComments.size() -1).mDt;
				}

				getSmsList(mSinceTime);
				break;
			case SMS_SHOW:
				isFirst = false;
				List<UMComment> objs = (List<UMComment>) msg.obj;
				if (null != objs && !objs.isEmpty()) {
					mUmComments.addAll(objs);
				} 
				refreshAdapter();
				break;
			case HANDLER_MSG_INIT_AD:    	        
    	        if (SFApplication.showAd()) ad();    
			case HANDLER_MSG_SHOW:
			default:
				if (SFApplication.showAd())	spotAds();
				break;
			}
		};
	};
	
	//插屏广告
    private void spotAds() {
    	SpotManager.getInstance(
    			SMSWallActivity.this).showSpotAds(SMSWallActivity.this,
				new SpotDialogListener() {
					@Override
					public void onShowSuccess() {
						L.i("YoumiAd 展示成功");
					}

					@Override
					public void onShowFailed() {
						L.i("YoumiAd 展示失败");
					}

				}); 

		// 可以根据需要设置Theme，如下调用，如果无特殊需求，直接调用上方的接口即可
		// SpotManager.getInstance(YoumiAdDemo.this).showSpotAds(YoumiAdDemo.this,
		// android.R.style.Theme_Translucent_NoTitleBar);
		// //
	}
	
	private void ad() {
		// 广告条接口调用（适用于应用）
		// 将广告条adView添加到需要展示的layout控件中
		// LinearLayout adLayout = (LinearLayout) findViewById(R.id.adLayout);
		// AdView adView = new AdView(this, AdSize.FIT_SCREEN);
		// adLayout.addView(adView);

		// 广告条接口调用（适用于游戏）

		// 实例化LayoutParams(重要)
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		// 设置广告条的悬浮位置
		layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT; // 这里示例为右下角
		// 实例化广告条
		AdView adView = new AdView(this, AdSize.FIT_SCREEN);
		// 调用Activity的addContentView函数
//		this.addContentView(adView, layoutParams);
		((FrameLayout)findViewById(R.id.wall_ad)).addView(adView, layoutParams);
		
		// 插播接口调用
		// 开发者可以到开发者后台设置展示频率，需要到开发者后台设置页面（详细信息->业务信息->无积分广告业务->高级设置）
		// 自4.03版本增加云控制是否开启防误点功能，需要到开发者后台设置页面（详细信息->业务信息->无积分广告业务->高级设置）

		// 加载插播资源
		SpotManager.getInstance(this).loadSpotAds();
		// 设置展示超时时间，加载超时则不展示广告，默认0，代表不设置超时时间
		SpotManager.getInstance(this).setSpotTimeout(5000);// 设置5秒
		SpotManager.getInstance(this).setShowInterval(20);// 设置20秒的显示时间间隔
		// 如需要使用自动关闭插屏功能，请取消注释下面方法
		// SpotManager.getInstance(this)
		// .setAutoCloseSpot(true);// 设置自动关闭插屏开关
		// SpotManager.getInstance(this)
		// .setCloseTime(6000); // 设置关闭插屏时间
		
		myHandler.sendEmptyMessageDelayed(HANDLER_MSG_SHOW, 3000);
	}
	
	/**
	 * 获取评论列表
	 * @param ctx
	 * @param sinceTime		返回此指定时间以前的10条评论
	 */
	private void getComment(final Context ctx, long sinceTime) {
		controller.getComments(ctx, new FetchCommetsListener() {
			
			@Override
			public void onStart() {
				
			}
			
			@Override
			public void onComplete(int status, List<UMComment> coments, SocializeEntity entiy) {
				if(status == 200){
	                L.d("获取成功" + coments.size());	                

	        		Message msg = myHandler.obtainMessage();
	        		msg.what = SMS_SHOW;
	        		msg.obj = coments;	
	        		myHandler.sendMessage(msg);
	                
	                for (int i = 0; i < coments.size(); i++) {
						UMComment comment = coments.get(i);
						L.i("时间:" + comment.mDt + ";内容:" + comment.mText + ";用户:" + comment.mUname + ";Uid:" + comment.mUid);
					}
				} else {
	            	L.d("获取失败" + status);
	            	if (-104 == status) {
	            		obtion();
	            	}
				}
			}
		}, sinceTime);
	}
	
	private void refreshAdapter() {
		if (null == mAdapter) {
			mAdapter = new SMSWallAdapter(SMSWallActivity.this, mUmComments);
			mWallList.setAdapter(mAdapter);
		}
		mAdapter.notifyDataSetChanged();
	}
	
	class SMSWallAdapter extends me.base.EngineAdapter<UMComment> {

		public SMSWallAdapter(Context ctx, List<UMComment> datas) {
			super(ctx, datas);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == convertView)
				convertView = mInflater.inflate(R.layout.list_item_sms, parent, false);
			
			TextView name = ViewHolder.get(convertView, R.id.item_name);
			TextView date = ViewHolder.get(convertView, R.id.item_date);
			TextView sms = ViewHolder.get(convertView, R.id.item_sms);
			ImageButton add = ViewHolder.get(convertView, R.id.item_add);
			UMComment sEntity = getItem(position);
			
			name.setText(sEntity.mUname);
			date.setText(DateUtils.getCurrentTime(sEntity.mDt));
			
			final String smsTxt = sEntity.mText;
			sms.setText(smsTxt);
			
			add.setOnClickListener(new android.view.View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					callbackSms(smsTxt);
				}
			});
			
			return convertView;
		}
		
	}
	
	//System.currentTimeMillis()
	private void getSmsList(long sinceTime) {
		getComment(SMSWallActivity.this, sinceTime);
	}
	
	private void callbackSms(String sms) {
		//数据是使用Intent返回
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("content", sms);
        //设置返回数据
        SMSWallActivity.this.setResult(RESULT_OK, intent);
        //关闭Activity
        SMSWallActivity.this.finish();
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 当不滚动时
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			// 判断是否滚动到底部
			if (view.getLastVisiblePosition() == view.getCount() - 1) {
				obtion();
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_floating_action:
			//广告
			// 展示所有应用推荐墙
        	DiyManager.showRecommendWall(SMSWallActivity.this);
			break;
		case R.id.action_mode_back:
			SMSWallActivity.this.finish();
			break;
		default:
			break;
		}
	}
}
