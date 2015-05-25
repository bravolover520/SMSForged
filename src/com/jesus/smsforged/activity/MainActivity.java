package com.jesus.smsforged.activity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.jesus.smsforged.R;
import com.jesus.smsforged.SFApplication;
import com.jesus.smsforged.bean.ContactEntity;
import com.jesus.smsforged.helper.SFHelper;
import com.jesus.smsforged.helper.UMSNSHelper;
import com.jesus.smsforged.ui.EasyDialog;
import com.jesus.smsforged.ui.FloatLabel;
import com.jesus.smsforged.utils.DateUtils;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.umeng.analytics.MobclickAgent;

import a.b.c.br.AdSize;
import a.b.c.br.AdView;
import a.b.c.diy.DiyManager;
import a.b.c.st.SpotDialogListener;
import a.b.c.st.SpotManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import me.base.BaseFragmentActivity;
import me.base.UiHelper;
import me.utils.L;
import me.utils.PreferencesUtils;
import me.utils.TextUtils;

/**
 * 找到请求规律
 * 不一样的仅仅是最后的lct=-1，当请求下一页的时候，完全可以用从上一个请求中的最后一条作为时间点来请求，
 * 
 * GET http://log.umsns.com/comment/get/4e18fe42431fe33c8b000590/77dc16a390dded81f2e16a0cccec34ce/?dt=1409560290353&uid=e1e6dcbd5c265c8a52fb1fbddc282d10&sid=907536386eff7630a4aa6b515da93342&os=Android&md5imei=E1E6DCBD5C265C8A52FB1FBDDC282D10&tp=0&imei=351912051281567&opid=2&mac=98%3Ae7%3A9a%3A0d%3A22%3A72&ak=4e18fe42431fe33c8b000590&de=XT535&sdkv=3.2.20121211&ek=77dc16a390dded81f2e16a0cccec34ce&pcv=2.0&
 * lct=-1&en=Wi-Fi
 * 
 * GET http://log.umsns.com/comment/get/4e18fe42431fe33c8b000590/77dc16a390dded81f2e16a0cccec34ce/?dt=1409560398183&uid=e1e6dcbd5c265c8a52fb1fbddc282d10&sid=907536386eff7630a4aa6b515da93342&os=Android&md5imei=E1E6DCBD5C265C8A52FB1FBDDC282D10&tp=0&imei=351912051281567&opid=2&mac=98%3Ae7%3A9a%3A0d%3A22%3A72&ak=4e18fe42431fe33c8b000590&de=XT535&sdkv=3.2.20121211&ek=77dc16a390dded81f2e16a0cccec34ce&pcv=2.0&
 * lct=1409560199489&en=Wi-Fi
 * 
 * 而用户每生成一次都是在umsns中添加一条评论。
 * POST http://log.umsns.com/comment/add/4e18fe42431fe33c8b000590/77dc16a390dded81f2e16a0cccec34ce/
 * 
 * 
 * 
 * http://dev.umeng.com/social/android/comment/specific-integration
 */
public class MainActivity extends BaseFragmentActivity implements android.view.View.OnClickListener, 
		OnDateSetListener, TimePickerDialog.OnTimeSetListener{	
	
	/**首次生成短信***/
	public static final String FIRST_CREATE = "FIRST_CREATE";
	
	public static final int HANDLER_MSG_INIT_AD = 1;
    public static final int HANDLER_MSG_SHOW = HANDLER_MSG_INIT_AD + 1;
	
	public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";
    
    public static final int SMS_WALL_REQUEST = 0x001;
    public static final int CONTACT_REQUEST = SMS_WALL_REQUEST + 1;
    
    private ImageButton mSmsWall;
    
	private FloatLabel mPhoneFloatLabel;
	private EditText mPhoneEditText;
	private FloatLabel mContentFloatLabel;
	private EditText mContentEditText;
	private ImageButton mPhoneAdd;
	private ImageButton mContentAdd;
	
	private Spinner mTypeSpinner;
	private Spinner mStateSpinner;
	private Button mDateBtn;
	private Button mTimeBtn;
	
	private Button mSubmit;
	
	private Calendar calendar = null;
	private DatePickerDialog datePickerDialog = null;
	private TimePickerDialog timePickerDialog = null;
	
	private int type = 1;	//收发类型(1.2.3.4)
	private int state = 0;	//阅读状态(0.1)
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initCalendar();
		
		mSmsWall = (ImageButton) findViewById(R.id.sms_wall);
		mSmsWall.setOnClickListener(this);
		
		mPhoneFloatLabel = (FloatLabel) findViewById(R.id.float_label_pNumber);
		mPhoneEditText = mPhoneFloatLabel.getEditText();
		mContentFloatLabel = (FloatLabel) findViewById(R.id.float_label_content);
		mContentEditText = mContentFloatLabel.getEditText();
		mPhoneAdd = (ImageButton) findViewById(R.id.pNumber_add);
		mPhoneAdd.setOnClickListener(this);
		mContentAdd = (ImageButton) findViewById(R.id.content_add);
		mContentAdd.setOnClickListener(this);
		mTypeSpinner = (Spinner) findViewById(R.id.type);
		typeSpinner();
		mStateSpinner = (Spinner) findViewById(R.id.state);
		stateSpinner();
		mDateBtn = (Button) findViewById(R.id.date);
		mDateBtn.setOnClickListener(this);
		mDateBtn.setText(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
		mTimeBtn = (Button) findViewById(R.id.time);
		mTimeBtn.setOnClickListener(this);
		mTimeBtn.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
		
		mSubmit = (Button) findViewById(R.id.submit);
		mSubmit.setOnClickListener(this);
		

        myHandler.sendEmptyMessageDelayed(HANDLER_MSG_INIT_AD, 3000);
	}
	
	private void initIntent(Intent intent) {
		//提取内容
		CharSequence text = intent.getCharSequenceExtra(Intent.EXTRA_TEXT);
		if (null != text) {
			L.d(text.toString());
			mContentEditText.setText(text);
		}
	}
	
	public Handler myHandler = new Handler(Looper.getMainLooper()) {
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
    		case HANDLER_MSG_INIT_AD: 
    	        if (SFApplication.showAd()) ad(); 
				break;
    		case HANDLER_MSG_SHOW:
			default:
				if (SFApplication.showAd())	spotAds();
				break;
			}
    	};
    };
    
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
		this.addContentView(adView, layoutParams);
		

		
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
		
		myHandler.sendEmptyMessageDelayed(HANDLER_MSG_SHOW, 2000);
	}
    
    //插屏广告
    private void spotAds() {
    	SpotManager.getInstance(
    			MainActivity.this).showSpotAds(MainActivity.this,
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
    
    private void initCalendar() {
    	calendar = Calendar.getInstance();

    	datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    	timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);
    }
    
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sms_wall:		//短信墙,改为应用墙
			// 展示所有应用推荐墙
        	DiyManager.showRecommendWall(MainActivity.this);
			break;
		case R.id.content_add:	//短信墙
			Intent intent = new Intent(MainActivity.this, SMSWallActivity.class);
			MainActivity.this.startActivityForResult(intent, SMS_WALL_REQUEST);
			break;
		case R.id.pNumber_add:	//添加联系人
			Intent phoneIntent = new Intent(MainActivity.this, ContactActivity.class);
//			MainActivity.this.startActivity(phoneIntent);
			MainActivity.this.startActivityForResult(phoneIntent, CONTACT_REQUEST);
			break;
		case R.id.date:		//日期
			datePickerDialog.setVibrate(false);
            datePickerDialog.setYearRange(1985, 2028);
            datePickerDialog.setCloseOnSingleTapDay(false);
            datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
			break;
		case R.id.time:		//时间
			timePickerDialog.setVibrate(false);
            timePickerDialog.setCloseOnSingleTapMinute(false);
            timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
			break;
		case R.id.submit:
			if (!PreferencesUtils.getBoolean(MainActivity.this, FIRST_CREATE, false)) {
				showCreateDialog();
			} else {
				submit();				
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 生成前的友好提示
	 */
	private void showCreateDialog() {
		new EasyDialog.Builder(MainActivity.this)
		.setTitle("温馨提示")
		.setMessage("通过此软件生成的短信不会产生任何的短信费用，请放心使用，仅供娱乐，如有雷同纯属巧合。")
		.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				submit();
				PreferencesUtils.putBoolean(MainActivity.this, FIRST_CREATE, true);
				dialog.dismiss();
			}
		}).show();
	}
	
	/**
	 * 生成后的提示
	 */
	private void showGotoDialog() {
		new EasyDialog.Builder(MainActivity.this)
		.setTitle("温馨提示")
		.setMessage("短信生成成功，请查看")
		.setNegativeButton("继续伪造", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mPhoneEditText.setText("");
				mContentEditText.setText("");
				dialog.dismiss();
			}
		}).setPositiveButton("去信箱查看", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				goSMS();
				dialog.dismiss();
			}
		}).show();
	}
	
	private void goSMS() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("vnd.android-dir/mms-sms");
		MainActivity.this.startActivity(intent);
	}
	
	private void typeSpinner() {
		mTypeSpinner.setOnItemSelectedListener(new TypeOnItemSelectedListener());
		//createFromResouce将返回ArrayAdapter<CharSequence>，具有三个参数： 
		//第一个是conetxt，也就是application的环境，可以设置为this，也可以通过getContext()获取. 
		//第二个参数是从data source中的array ID，也就是我们在strings中设置的ID号； 
		//第三个参数是spinner未展开的UI格式 
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( this, R.array.sms_type, android.R.layout.simple_spinner_item); 
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		mTypeSpinner.setAdapter(adapter);
	}
	
	public class TypeOnItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener{ 
	    @Override 
	    public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3){ 
	        L.i((String)mTypeSpinner.getAdapter().getItem(pos));
	        type = pos + 1;
	    }
	    
	    @Override 
	    public void onNothingSelected(AdapterView<?> arg0) {  
	        //nothing to do 
	    } 
	}
	
	private void stateSpinner() {
		mStateSpinner.setOnItemSelectedListener(new StateOnItemSelectedListener());
		//createFromResouce将返回ArrayAdapter<CharSequence>，具有三个参数： 
		//第一个是conetxt，也就是application的环境，可以设置为this，也可以通过getContext()获取. 
		//第二个参数是从data source中的array ID，也就是我们在strings中设置的ID号； 
		//第三个参数是spinner未展开的UI格式 
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( this, R.array.sms_state, android.R.layout.simple_spinner_item); 
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		mStateSpinner.setAdapter(adapter);
	}
	
	public class StateOnItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener{ 
	    @Override 
	    public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3){ 
	    	L.i((String)mStateSpinner.getAdapter().getItem(pos));
	    	state = pos;
	    }
	    
	    @Override 
	    public void onNothingSelected(AdapterView<?> arg0) {  
	        //nothing to do 
	    } 
	}
	
	private void submit() {
		String phone = mPhoneEditText.getText().toString();
		String content = mContentEditText.getText().toString();
		String date = mDateBtn.getText().toString();
		String time = mTimeBtn.getText().toString();
		
		if (me.utils.TextUtils.isEmpty(phone)) {
			UiHelper.showShortToast(MainActivity.this, "请输入联系人");
			return;
		}
		
		if (me.utils.TextUtils.isEmpty(content)) {
			UiHelper.showShortToast(MainActivity.this, "请输入短信内容");
			return;
		}
		
		if (me.utils.TextUtils.isEmpty(date)) {
			UiHelper.showShortToast(MainActivity.this, "请选择日期");
			return;
		}
		
		if (me.utils.TextUtils.isEmpty(time)) {
			UiHelper.showShortToast(MainActivity.this, "请选择时间");
			return;
		}
		
		String[] phones = phone.split(";");
		for (int i = 0; i < phones.length; i++) {			
			try {
				long dt = DateUtils.getTime(date, time);
				L.i(">" + dt + "phones[i]" + phones[i]);
				SFHelper.sf(MainActivity.this, phones[i], content, dt, state, type);		//伪造
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		UMSNSHelper.sendComment(MainActivity.this, content);							//提交Umeng
		
		showGotoDialog();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case SMS_WALL_REQUEST:
				//得到短信的内容,String
				if (null != data) {
					String sms = data.getStringExtra("content");
					if (!me.utils.TextUtils.isEmpty(sms)) {
						mContentEditText.setText(sms);
					}
				}
				break;
			case CONTACT_REQUEST:
				if (null != data) {
					ArrayList<ContactEntity> contacts = data.getParcelableArrayListExtra("contact");
					if (null != contacts && !contacts.isEmpty()){
						//打印
						StringBuilder sBuilder = new StringBuilder(); 
						for (ContactEntity cEntity : contacts) {
							L.d(cEntity.getDisplayName());
							sBuilder.append(cEntity.getPhoneNum()).append(";");
						}
						String phones = sBuilder.toString();
						if (!TextUtils.isEmpty(phones)) {
							phones.substring(0, phones.length() - 1);
						}
						mPhoneEditText.setText(phones);
					}
				}
				break;
			default:
				break;
			}
		}
	}

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        L.i("new date:" + year + "-" + month + "-" + day);
        mDateBtn.setText(year + "-" + (month + 1) + "-" + day);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        L.i("new time:" + hourOfDay + ":" + minute);
        mTimeBtn.setText(hourOfDay + ":" + minute);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
		initIntent(getIntent());
    	MobclickAgent.onResume(this);
    };
    
    @Override
    protected void onPause() {
    	super.onPause();
    	MobclickAgent.onPause(this);
    }
    
    private static long back_pressed;
	@Override
	public void onBackPressed() {
		//双击两次退出
		if (back_pressed + 2000 > System.currentTimeMillis()) 
			super.onBackPressed();
		else 
			UiHelper.showShortToast(MainActivity.this, "再次按返回键退出!");
		back_pressed = System.currentTimeMillis();
		
	}
}
