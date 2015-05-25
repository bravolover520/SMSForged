package com.jesus.smsforged.activity;

import java.util.ArrayList;
import java.util.List;

import com.jesus.smsforged.R;
import com.jesus.smsforged.bean.ContactEntity;
import com.jesus.smsforged.helper.ContactQueryHelper;
import com.jesus.smsforged.ui.IndexableListView;
import com.jesus.smsforged.utils.StringMatcher;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import me.base.BaseFragmentActivity;
import me.base.EngineAdapter;
import me.base.ViewHolder;
import me.utils.L;

public class ContactActivity extends BaseFragmentActivity implements OnClickListener {
	
	private LinearLayout backLayout;	//返回标题
	private LinearLayout doneLayout;	//完成标题
	
	private IndexableListView indexableListView;	//右侧滑块的ListView
	private ImageView homeIcon;				//程序图标
	private TextView title;					//标题
	private ImageView actionDone;			//完成
	private TextView countText;				//条数
	private CheckBox actionSelectedChanged;	//全不选/全选
	
	private int count = 0;
	
	private List<ContactEntity> mContactInfos = new ArrayList<ContactEntity>();
	private ArrayList<ContactEntity> mSelContactInfos = new ArrayList<ContactEntity>();
	private ContactAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);
		
		homeIcon = (ImageView) findViewById(R.id.home);		//图标
		homeIcon.setVisibility(View.GONE);
		title = (TextView) findViewById(R.id.action_bar_title);
		title.setText(R.string.app_name);
		
		backLayout = (LinearLayout) findViewById(R.id.action_mode_back);
		backLayout.setOnClickListener(this);
		doneLayout = (LinearLayout) findViewById(R.id.action_mode_close_button);
		
		indexableListView = (IndexableListView) findViewById(R.id.indexListView);
		indexableListView.setFastScrollEnabled(true);
		
		indexableListView.setOnItemClickListener(new ContactItemClickListener());
		
		actionDone = (ImageView) findViewById(R.id.action_done);
		actionDone.setOnClickListener(new DoneClickListener());
		countText = (TextView) findViewById(R.id.count);
		actionSelectedChanged = (CheckBox) findViewById(R.id.action_selected_changed);
		actionSelectedChanged.setOnCheckedChangeListener(new SelectedChanged());
		
		obtionContact();
	}
	
	private void obtionContact() {
		final ContactQueryHelper support = new ContactQueryHelper();
		support.setContactQueryListener(new ContactQueryHelper.ContactQueryListener() {
			
			@Override
			public void queryComplete() {
				mContactInfos = support.getContactInfos();
				myHandler.sendEmptyMessage(0);
			}
		});
		support.getContacts(ContactActivity.this.getContentResolver());
	}
	
	private Handler myHandler = new Handler(Looper.getMainLooper()){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				//加载
				refreshAdapter();
				break;
			case 1:
				//按钮点击
				mSelContactInfos.clear();
				count = 0;
				for (ContactEntity entity : mContactInfos) {
					if (entity.isSelected()) {
						++ count;
						mSelContactInfos.add(entity);
					}
				}	
				
				System.out.println(count);
				
				if (count > 0) {
					//则显示done
					backLayout.setVisibility(View.GONE);
					doneLayout.setVisibility(View.VISIBLE);
					countText.setText(count + "条联系人被选中");
				} else {
					doneLayout.setVisibility(View.GONE);
					backLayout.setVisibility(View.VISIBLE);
					countText.setText("");
				}
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.action_mode_back:			
			ContactActivity.this.finish();
			break;
		default:
			break;
		}
	};
	
	private class SelectedChanged implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			for (ContactEntity cEntity : mContactInfos) {
				cEntity.setSelected(isChecked);
			}			
			myHandler.sendEmptyMessage(0);
			myHandler.sendEmptyMessage(1);
		}		
	}
	
	private class DoneClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			//得到真正的集合数列
			//数据是使用Intent返回
	        Intent intent = new Intent();
	        intent.putParcelableArrayListExtra("contact", mSelContactInfos);
	        //设置返回数据
	        ContactActivity.this.setResult(RESULT_OK, intent);
	        //关闭Activity
	        ContactActivity.this.finish();
		}		
	}
	
	private class ContactItemClickListener implements OnItemClickListener {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			//改变CheckBox的状态
			CheckBox checkBox = ViewHolder.get(view, android.R.id.checkbox);
			checkBox.toggle();
			
			mAdapter.getItem(position).setSelected(checkBox.isChecked());
			
			myHandler.sendEmptyMessage(1);
		}
	}
	
	private void refreshAdapter() {
		if (null == mAdapter) {
			mAdapter = new ContactAdapter(ContactActivity.this, mContactInfos);
			indexableListView.setAdapter(mAdapter);
		}
		
		mAdapter.notifyDataSetChanged();
	}	
	
	class ContactAdapter extends EngineAdapter<ContactEntity> implements SectionIndexer {
		
		private String mSections = "☆#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		public ContactAdapter(Context ctx, List<ContactEntity> datas) {
			super(ctx, datas);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == convertView)
				convertView = mInflater.inflate(R.layout.list_item_contact, parent, false);
			
			TextView name = ViewHolder.get(convertView, android.R.id.text1);
			TextView phone = ViewHolder.get(convertView, android.R.id.text2);
			CheckBox checkBox = ViewHolder.get(convertView, android.R.id.checkbox);
			
			ContactEntity cEntity = getItem(position);
			name.setText(cEntity.getDisplayName());
			phone.setText(cEntity.getPhoneNum());
			checkBox.setChecked(cEntity.isSelected());			

			change(checkBox, cEntity);
			
			return convertView;
		}
		
		public void change(CheckBox checkBox, final ContactEntity cEntity) {	
			checkBox.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (v instanceof CheckBox) {
						boolean isChecked = ((CheckBox) v).isChecked();
						((CheckBox) v).setChecked(!isChecked);
					}
				}
			});
			
			
			checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					L.i("isss");
					cEntity.setSelected(!isChecked);					
				}
			});
		}
		
		@Override
		public int getPositionForSection(int section) {
			// If there is no item for current section, previous section will be selected
			for (int i = section; i >= 0; i--) {
				for (int j = 0; j < getCount(); j++) {
					if (i == 0) {
						// For numeric section
						for (int k = 0; k <= 9; k++) {
							if (StringMatcher.match(String.valueOf(getItem(j).getSortKey().charAt(0)), String.valueOf(k)))
								return j;
						}
					} else {
						if (StringMatcher.match(String.valueOf(getItem(j).getSortKey().charAt(0)), String.valueOf(mSections.charAt(i))))
							return j;
					}
				}
			}
			return 0;
		}

		@Override
		public int getSectionForPosition(int position) {
			return 0;
		}

		@Override
		public Object[] getSections() {
			String[] sections = new String[mSections.length()];
			for (int i = 0; i < mSections.length(); i++)
				sections[i] = String.valueOf(mSections.charAt(i));
			return sections;
		}
	}
}
