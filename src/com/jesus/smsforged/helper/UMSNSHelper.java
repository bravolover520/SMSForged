package com.jesus.smsforged.helper;

import java.util.ArrayList;
import java.util.List;

import me.utils.L;

import com.umeng.socialize.bean.MultiStatus;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.UMComment;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.FetchCommetsListener;
import com.umeng.socialize.controller.listener.SocializeListeners.MulStatusListener;

import android.content.Context;

public class UMSNSHelper {
	
	private static List<UMComment> mComments = new ArrayList<UMComment>();

	/**
	 * 发表评论
	 * @param ctx
	 * @param params		要发表的评论内容
	 */
	public static final void sendComment(final Context ctx, String params) {
		UMSocialService controller = UMServiceFactory.getUMSocialService("com.umeng.comment");
		UMComment socializeComment = new UMComment();
		socializeComment.mText = params;
		if (android.os.Build.MODEL != null)
			socializeComment.mUname = (android.os.Build.MODEL + " 用户");
	
		controller.postComment(ctx, socializeComment, new MulStatusListener() {

			@Override
			public void onComplete(MultiStatus multiStatus,int status, SocializeEntity entity) {
				if(status == 200)
	                L.d("发送成功");
	            else
	            	L.d("发送失败" + status);
			}

			@Override
			public void onStart() {
				
			}
			
		});
	}
	
	/**
	 * 获取评论列表
	 * @param ctx
	 * @param sinceTime		返回此指定时间以前的10条评论
	 */
	public static List<UMComment> getComment(final Context ctx, long sinceTime) {
		mComments.clear();
		UMSocialService controller = UMServiceFactory.getUMSocialService("com.umeng.comment");
		controller.getComments(ctx, new FetchCommetsListener() {
			
			@Override
			public void onStart() {
				
			}
			
			@Override
			public void onComplete(int status, List<UMComment> coments, SocializeEntity entiy) {
				if(status == 200){
	                L.d("获取成功" + coments.size());
	                for (int i = 0; i < coments.size(); i++) {
						UMComment comment = coments.get(i);
						L.i("时间:" + comment.mDt + ";内容:" + comment.mText + ";用户:" + comment.mUname + ";Uid:" + comment.mUid);
					}
	                mComments = coments;
				} else
	            	L.d("获取失败" + status);
			}
		}, sinceTime);
		return mComments;
	}
	
}
