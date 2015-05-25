package com.jesus.smsforged.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class RateHelper {

	//在应用市场中查找该应用
	public static void rateMyApp(Context ctx, String pkg) {
		String addres = "market://details?id=" + pkg;
		Intent marketIntent = new Intent("android.intent.action.VIEW");
		marketIntent.setData(Uri.parse(addres));
		ctx.startActivity(marketIntent);
	}
}
