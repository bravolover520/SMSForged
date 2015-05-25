package com.jesus.smsforged.helper;

import me.utils.TextUtils;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

/**
 * 相关的协议：
 * content://sms/inbox        	收件箱
 * content://sms/sent        	已发送
 * content://sms/draft        	草稿
 * content://sms/outbox        	发件箱
 * content://sms/failed        	发送失败
 * content://sms/queued       	待发送列表
 * 
 * sms相关的字段如下：
 * _id				一个自增字段，从1开始
 * thread_id    	序号，同一发信人的id相同
 * address      	发件人手机号码
 * person        	联系人列表里的序号，陌生人为null
 * date            	发件日期
 * date_sent		
 * protocol      	协议：分为： 0 SMS_RPOTO, 1 MMS_PROTO 
 * read           	是否阅读 0未读， 1已读 
 * status         	状态：-1接收，0 complete, 64 pending, 128 failed
 * type
    	ALL    = 0;
    	INBOX  = 1;
    	SENT   = 2;
    	DRAFT  = 3;
    	OUTBOX = 4;
    	FAILED = 5;
    	QUEUED = 6;
 * body				短信内容
 * service_center  	短信服务中心号码编号
 * subject          短信的主题
 * reply_path_present     	TP-Reply-Path
 */
public class SFHelper {
	
	public static String[] type = new String[]{
		"收短信",
		"发短信",
		"存草稿",
		"发送失败"};
	
	public static String[] read = new String[] {
		"未读",
		"已读"
	};

	/**
	 * @param ctx			上下文
	 * @param pNumber		手机号
	 * @param content		短信内容
	 * @param date			生成日期(long 形)
	 * @param read			阅读状态(0未读， 1已读 )
	 * @param type			收发类型(1收，2发，3草稿，4发送失败，5待发列表)
	 */
	public static void sf(Context ctx, String pNumber, String content, Long date, int read, int type) {
        /** 手机号码 与输入内容 必需不为空 **/
        if (!TextUtils.isEmpty(pNumber) && !TextUtils.isEmpty(content)) {        
            /**将发送的短信插入数据库**/
            ContentValues values = new ContentValues();
            //发送时间
            values.put("date", date);
            //阅读状态
            values.put("read", read);
            //收发状态
            values.put("type", type);
            //送达号码
            values.put("address", pNumber);
            //送达内容
            values.put("body", content);
            //插入短信库
            ctx.getContentResolver().insert(Uri.parse("content://sms"),values);
        }
	}
}
