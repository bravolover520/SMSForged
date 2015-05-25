package com.jesus.smsforged.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.jesus.smsforged.bean.ContactEntity;

public class ContactQueryHelper {
	private ContactQueryListener mContactQueryListener;

	private Map<Integer, ContactEntity> contactIdMap = null;
	private List<ContactEntity> contactInfos;
	
	/**
	 * 通讯录查询状态
	 */
	public interface ContactQueryListener {
		public abstract void queryComplete();
	}
	
	public void setContactQueryListener(ContactQueryListener l) {
		this.mContactQueryListener = l;
	}
	
	/**
	 * 得到通讯录
	 * @param cr
	 * @return
	 */
	public void getContacts(ContentResolver cr) {
		/**
		 * ContactsContract.Contacts与ContactsContract.CommonDataKinds.Phone的区别
		 * </br>
		 * ContactsContract.Contacts:
		 * Constants for the contacts table, which contains a record per aggregate of raw contacts representing the same person   常量的联系人表,其中包含一个记录每个联系人表示同一个人的集合。
		 * </br>
		 * ContactsContract.CommonDataKinds.Phone：
		 * A data kind representing a telephone number. 一个数据类型代表一个电话号码。
		 * 
		 */
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人的Uri
		String[] projection = { 
				ContactsContract.CommonDataKinds.Phone._ID,				//_id
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,	//显示的名
				ContactsContract.CommonDataKinds.Phone.DATA1,			//号码
				"sort_key",												//排序
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID,		//联系人id
				ContactsContract.CommonDataKinds.Phone.PHOTO_ID,		//Phone id
				ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY		//查找
		}; // 查询的列
		
		new ContactQueryHandler(cr).startQuery(
				0, null, uri, projection, null, null,
				"sort_key COLLATE LOCALIZED asc"); // 按照sort_key升序查询
	}
	
	public List<ContactEntity> getContactInfos() {
		return contactInfos;
	}
	
	/**
	 * 数据库异步查询类
	 */
	@SuppressLint("HandlerLeak")
	private class ContactQueryHandler extends AsyncQueryHandler {

		public ContactQueryHandler(ContentResolver cr) {
			super(cr);
		}
		
		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			// 查询结束后的回调函数
//			super.onQueryComplete(token, cookie, cursor);
			if (cursor != null && cursor.getCount() > 0) {
				contactIdMap = new HashMap<Integer, ContactEntity>();
				contactInfos = new ArrayList<ContactEntity>();
				if (cursor.moveToFirst()) {
					do {
						String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
						String number = cursor.getString(2);
						String sortKey = cursor.getString(3);
						int contactId = cursor.getInt(4);
						Long photoId = cursor.getLong(5);
						String lookUpKey = cursor.getString(6);
						
						//不包含此用户的id
						if (!contactIdMap.containsKey(contactId)){
							ContactEntity cInfo = new ContactEntity();
							cInfo.setDisplayName(name);
							cInfo.setPhoneNum(number);
							cInfo.setSortKey(sortKey);
							cInfo.setContactId(contactId);
							cInfo.setPhotoId(photoId);
							cInfo.setLookUpKey(lookUpKey);
							contactInfos.add(cInfo);
							contactIdMap.put(contactId, cInfo);
						}
					} while (cursor.moveToNext());
				}
			} 
			if (mContactQueryListener != null)
				mContactQueryListener.queryComplete();	
		}
		
	}
}
