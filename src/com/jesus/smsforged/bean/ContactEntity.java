package com.jesus.smsforged.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactEntity implements Parcelable{

	private int contactId; //id
	private String displayName;	//显示的名字
//	private EDIT  头像
	private String phoneNum;	//电话号
	private String formattedNum;//格式化后的手机号
	private long photoId;		//相册id
	private String sortKey;		//排序键
	private String pinyin;		//拼音全拼
	private String lookUpKey;	//查找键
	
	private boolean isSelected;	//是否被选中
	
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}	
	public int getContactId() {
		return contactId;
	}
	public void setContactId(int contactId) {
		this.contactId = contactId;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getFormattedNum() {
		return formattedNum;
	}
	public void setFormattedNum(String formattedNum) {
		this.formattedNum = formattedNum;
	}
	public long getPhotoId() {
		return photoId;
	}
	public void setPhotoId(long photoId) {
		this.photoId = photoId;
	}
	public String getSortKey() {
		return sortKey;
	}
	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	public String getLookUpKey() {
		return lookUpKey;
	}
	public void setLookUpKey(String lookUpKey) {
		this.lookUpKey = lookUpKey;
	}
	
	public ContactEntity() {
	}
	
	public ContactEntity(Parcel in) {
		contactId = in.readInt();
		displayName = in.readString();
		phoneNum = in.readString();
		formattedNum = in.readString();
		photoId = in.readLong();
		sortKey = in.readString();
		pinyin = in.readString();
		lookUpKey = in.readString();
		isSelected = in.readByte() != 0;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(contactId);
		dest.writeString(displayName);
		dest.writeString(phoneNum);
		dest.writeString(formattedNum);
		dest.writeLong(photoId);
		dest.writeString(sortKey);
		dest.writeString(pinyin);
		dest.writeString(lookUpKey);
		dest.writeByte((byte) (isSelected ? 1 : 0));
	}
	
	public static final Parcelable.Creator<ContactEntity> CREATOR = new Creator<ContactEntity>() {  
        
        @Override  
        public ContactEntity[] newArray(int size) {  
            // TODO Auto-generated method stub  
            return new ContactEntity[size];  
        }  
          
        @Override  
        public ContactEntity createFromParcel(Parcel source) {  
            // TODO Auto-generated method stub  
            return new ContactEntity(source);  
        }  
    };  
}  
