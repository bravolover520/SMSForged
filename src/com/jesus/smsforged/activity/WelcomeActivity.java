package com.jesus.smsforged.activity;

import com.jesus.smsforged.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import me.base.BaseFragmentActivity;

public class WelcomeActivity extends BaseFragmentActivity {

	//引导
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		
		myHandler.sendEmptyMessageDelayed(0, 3000);
	}
	
	private Handler myHandler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(android.os.Message msg) {
			startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
			WelcomeActivity.this.finish();
		};
	};
}
