package com.jesus.smsforged.ui;

//import net.youmi.android.diy.DiyManager;
import me.utils.L;

import com.jesus.smsforged.R;
import com.jesus.smsforged.helper.RateHelper;

import a.b.c.diy.DiyManager;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

public class CurtainView extends RelativeLayout implements android.view.View.OnTouchListener{

	private Context mContext;
	/** Scroller 拖动类 */
	private Scroller mScroller;
	/** 点击时候Y的坐标*/
	private int mDownY = 0;
	/** 拖动时候Y的坐标*/
	private int mMoveY = 0;
	/** 拖动时候Y的方向距离*/
	private int mScrollY = 0;
	/** 松开时候Y的坐标*/
	private int mUpY = 0;
	/** 广告幕布的高度*/
	private int mCurtainHeigh = 0;
	/** 是否 打开*/
	private boolean isOpen = false;
	/** 是否在动画 */
	private boolean isMove = false;
	/** 绳子的图片*/
	private ImageView mCurtainRrope;
	/** 广告的图片*/
	private FrameLayout mAdLayout;
	/** 上升动画时间 */
	private int mUpDuration = 1000;
	/** 下落动画时间 */
	private int mDownDuration = 500;
	
	public CurtainView(Context context) {
		super(context);
		init(context);
	}

	public CurtainView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public CurtainView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	/** 初始化 */
	private void init(Context context) {
		this.mContext = context;
		//Interpolator 设置为有反弹效果的  （Bounce：反弹）
		Interpolator interpolator = new BounceInterpolator();
		mScroller = new Scroller(context, interpolator);
		// 背景设置成透明
		this.setBackgroundColor(Color.argb(0, 0, 0, 0));
		final View view = LayoutInflater.from(mContext).inflate(R.layout.curtain, null);
		mAdLayout = (FrameLayout)view.findViewById(R.id.layout_curtain_ad);
		mCurtainRrope = (ImageView)view.findViewById(R.id.img_curtain_rope);
		mAdLayout.addView(adView());
		addView(view);
		mAdLayout.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mCurtainHeigh  = mAdLayout.getHeight();
				L.d("curtainHeigh= " + mCurtainHeigh);
				CurtainView.this.scrollTo(0, mCurtainHeigh);
				//注意scrollBy和scrollTo的区别
			}
		});
		mCurtainRrope.setOnTouchListener(this);
	}
	
	private View adView() {
		final View parentView = LayoutInflater.from(mContext).inflate(R.layout.layout_ad, null);
		
		LinearLayout app_bt = (LinearLayout) parentView.findViewById(R.id.app_0);
		app_bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RateHelper.rateMyApp(mContext, "com.jyxp.transition");
			}
		});
		
		LinearLayout app_mt = (LinearLayout) parentView.findViewById(R.id.app_1);
		app_mt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RateHelper.rateMyApp(mContext, "com.jesus.mt");
			}
		});
		
		LinearLayout app_lover = (LinearLayout) parentView.findViewById(R.id.app_2);
		app_lover.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RateHelper.rateMyApp(mContext, "com.jyxp.sweetlover");
			}
		});
		
		Button ad_app_wall = (Button) parentView.findViewById(R.id.ad_app_wall);
		ad_app_wall.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 展示所有应用推荐墙
	        	DiyManager.showRecommendWall(mContext);
			}
		});
		return parentView;
	}
	
	/**
	 * 拖动动画
	 * @param startY  
	 * @param dy  垂直距离, 滚动的y距离
	 * @param duration 时间
	 */
	public void startMoveAnim(int startY, int dy, int duration) {
		isMove = true;
		mScroller.startScroll(0, startY, 0, dy, duration);
		invalidate();//通知UI线程的更新
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
	}
	
	@Override
	public void computeScroll() {
		//判断是否还在滚动，还在滚动为true
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			// 更新界面
			postInvalidate();
			isMove = true;
		} else {
			isMove = false;
		}
		super.computeScroll();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (!isMove) {
			int offViewY = 0;//屏幕顶部和该布局顶部的距离
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mDownY = (int) event.getRawY();
				offViewY = mDownY - (int)event.getX();
				return true;
			case MotionEvent.ACTION_MOVE:
				mMoveY = (int) event.getRawY();
				mScrollY = mMoveY - mDownY;
				if (mScrollY < 0) {
					// 向上滑动
					if(isOpen){
						if(Math.abs(mScrollY) <= mAdLayout.getBottom() - offViewY){
							scrollTo(0, -mScrollY);
						}
					}
				} else {
					// 向下滑动
					if(!isOpen){
						if (mScrollY <= mCurtainHeigh) {
							scrollTo(0, mCurtainHeigh - mScrollY);
						}
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				mUpY = (int) event.getRawY();
				if(Math.abs(mUpY - mDownY) < 10){
					onRopeClick();
					break;
				}
				if (mDownY > mUpY) {
					// 向上滑动
					if(isOpen){
						if (Math.abs(mScrollY) > mCurtainHeigh / 2) {
							// 向上滑动超过半个屏幕高的时候 开启向上消失动画
							startMoveAnim(this.getScrollY(),
									(mCurtainHeigh - this.getScrollY()), mUpDuration);
							isOpen = false;
						} else {
							startMoveAnim(this.getScrollY(), -this.getScrollY(),mUpDuration);
							isOpen = true;
						}
					}
				} else {
					// 向下滑动
					if (mScrollY > mCurtainHeigh / 2) {
						// 向上滑动超过半个屏幕高的时候 开启向上消失动画
						startMoveAnim(this.getScrollY(), -this.getScrollY(),mUpDuration);
						isOpen = true;
					} else {
						startMoveAnim(this.getScrollY(),(mCurtainHeigh - this.getScrollY()), mUpDuration);
						isOpen = false;
					}
				}
				break;
			default:
				break;
			}
		}
		return false;
	}
	
	/**
	 * 点击绳索开关，会展开关闭
	 * 在onToch中使用这个中的方法来当点击事件，避免了点击时候响应onTouch的衔接不完美的影响
	 */
	public void onRopeClick(){
		if(isOpen){
			CurtainView.this.startMoveAnim(0, mCurtainHeigh, mUpDuration);
		}else{
			CurtainView.this.startMoveAnim(mCurtainHeigh, -mCurtainHeigh, mDownDuration);
		}
		isOpen = !isOpen;
	}
}
