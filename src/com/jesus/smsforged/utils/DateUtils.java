package com.jesus.smsforged.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	
	@SuppressLint("SimpleDateFormat")
	private final static ThreadLocal<SimpleDateFormat> DEFAULT_FORMAT = new ThreadLocal<SimpleDateFormat>() {
    	protected SimpleDateFormat initialValue() {
    		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	};
    };
    
	/**
	 * 得到当前的时间戳
	 */
	public static String getCurrentTime(Long time) {
		return DEFAULT_FORMAT.get().format(new Date(time));
	}
	
	@SuppressLint("SimpleDateFormat")
	public static long getTime(String date, String time) throws ParseException {
		String sDt = date + " " + time;
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date dt2 = sdf.parse(sDt);
		return dt2.getTime();
	}
	
}
