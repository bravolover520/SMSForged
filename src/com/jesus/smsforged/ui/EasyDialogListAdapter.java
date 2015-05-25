package com.jesus.smsforged.ui;

import java.util.List;

import com.jesus.smsforged.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class EasyDialogListAdapter extends BaseAdapter {
	
	public static final int THEME_HOLO = 0x01;

	private List<EasyDialog.ListItem> mListItems;
	private LayoutInflater mInflater;

	private int mListStyle;
	private int mRowTheme;
	private int mTextColor = -1;
	private Typeface mTypeface;

	public EasyDialogListAdapter(EasyDialog.Builder builder) {
		mInflater = LayoutInflater.from(builder.mContext);
		mListItems = builder.mListItems;
		mListStyle = builder.mListStyle;
		mRowTheme = builder.mListRowTheme;
		mTextColor = builder.mListItemTextColor;
		mTypeface = builder.mMainFont;
	}

	public EasyDialogListAdapter(Context context, List<EasyDialog.ListItem> listItems, int listStyle) {
		mInflater = LayoutInflater.from(context);
		mListItems = listItems;
		mListStyle = listStyle;
		mRowTheme = THEME_HOLO;
	}

	public List<EasyDialog.ListItem> getListItems() {
		return mListItems;
	}

	public void setListItems(List<EasyDialog.ListItem> listItems) {
		mListItems = listItems;
	}

	public int getListStyle() {
		return mListStyle;
	}

	public void setListStyle(int listStyle) {
		mListStyle = listStyle;
	}

	public void setTypeface(Typeface typeface) {
		mTypeface = typeface;
	}

	public void setTextColor(int color) {
		mTextColor = color;
	}

	@Override
	public int getCount() {
		return mListItems.size();
	}

	@Override
	public EasyDialog.ListItem getItem(int position) {
		try {
			return mListItems.get(position);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = viewHolder.mConvertView;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.setItem(getItem(position));

		return convertView;
	}

	private class ViewHolder {
		private View mConvertView;
		private ImageView mIcon;
		private TextView mLabel;
		private TextView mSubLabel;
		private CheckBox mCheckBox;
		private RadioButton mRadioButton;

		public ViewHolder() {
			mConvertView = mInflater.inflate(R.layout.sdl_list_item, null, false);
			mIcon        = (ImageView)   mConvertView.findViewById(R.id.icon       );
			mLabel       = (TextView)    mConvertView.findViewById(R.id.label      );
			mSubLabel    = (TextView)    mConvertView.findViewById(R.id.sublabel   );
			mCheckBox    = (CheckBox)    mConvertView.findViewById(R.id.checkbox   );
			mRadioButton = (RadioButton) mConvertView.findViewById(R.id.radiobutton);
		}

		private void setBackground() {
			if (mListStyle == EasyDialog.LIST_STYLE_GRIDVIEW) {
				if (mRowTheme == THEME_HOLO) {
					mConvertView.setBackgroundResource(R.drawable.gv_border_light);
				} else {
					mConvertView.setBackgroundResource(R.drawable.gv_border_light);
				}
			} else {
				mConvertView.setBackgroundColor(Color.TRANSPARENT);
			}
		}

		private void setIcon(Drawable icon) {
			if (icon != null) {
				mIcon.setVisibility(View.VISIBLE);
				mIcon.setImageDrawable(icon);
			} else {
				mIcon.setVisibility(View.GONE);
			}
		}

		private void setLabel(String label) {
			if (label != null) {
				mLabel.setVisibility(View.VISIBLE);
				mLabel.setText(label);
			} else {
				mLabel.setVisibility(View.GONE);
			}
		}

		private void setSubLabel(String label) {
			if (label != null) {
				mSubLabel.setVisibility(View.VISIBLE);
				mSubLabel.setText(label);
			} else {
				mSubLabel.setVisibility(View.GONE);
			}
		}

		private void setCheckableButtons(Boolean checked) {
			if (mListStyle == EasyDialog.LIST_STYLE_SINGLE_CHOICE) {
				mCheckBox.setVisibility(View.GONE);
				mRadioButton.setVisibility(View.VISIBLE);

				if (checked == null) {
					checked = false;
				}

				mRadioButton.setChecked(checked);
			} else if (mListStyle == EasyDialog.LIST_STYLE_LISTVIEW
					|| mListStyle == EasyDialog.LIST_STYLE_GRIDVIEW) {
				if (checked == null) {
					mCheckBox.setVisibility(View.GONE);
					mRadioButton.setVisibility(View.GONE);
				} else {
					mCheckBox.setVisibility(View.VISIBLE);
					mRadioButton.setVisibility(View.GONE);
					mCheckBox.setChecked(checked);
				}
			} else if (mListStyle == EasyDialog.LIST_STYLE_MULTI_CHOICE) {
				mCheckBox.setVisibility(View.VISIBLE);
				mRadioButton.setVisibility(View.GONE);

				if (checked == null) {
					checked = false;
				}

				mCheckBox.setChecked(checked);
			}
		}

		private void setTypeface() {
			if (mTypeface != null) {
				mLabel.setTypeface(mTypeface);
				mSubLabel.setTypeface(mTypeface);
			}
		}
		
		private void setTheme(EasyDialog.ListItem item) {
			int textColor;
			if (mRowTheme == THEME_HOLO) {
//				mCheckBox.setButtonDrawable(R.drawable.btn_check_holo_light);
//				mRadioButton.setButtonDrawable(R.drawable.btn_radio_holo_light);
				textColor = 0xFF040404;
			} else {
				textColor = 0xFFFFFFFF;
			}

			if (mTextColor != -1) {
				textColor = mTextColor;
			}

			if (item.labelColor == -1) {
				mLabel.setTextColor(textColor);
			} else {
				mLabel.setTextColor(item.labelColor);
			}

			if (item.labelColor == -1) {
				mSubLabel.setTextColor(textColor);
			} else {
				mSubLabel.setTextColor(item.subLabelColor);
			}
		}

		public void setItem(final EasyDialog.ListItem item) {
			setBackground();
			setTheme(item);
			setTypeface();
			setIcon(item.icon);
			setLabel(item.label);
			setSubLabel(item.subLabel);
			setCheckableButtons(item.checked);
		}
	}

}
