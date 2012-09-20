package com.warmice.android.videomessaging.ui.adapter;

import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.provider.MessagingContract.UserColumns;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ContactAdapter extends CursorAdapter {
	private LayoutInflater mInflater;
	private int mNameIndex;

	public ContactAdapter(Context context, Cursor cursor) {
		super(context, cursor, false);
		mInflater = LayoutInflater.from(context);
		if (cursor != null){
			mNameIndex = cursor.getColumnIndex(UserColumns.USER_NAME);
		}
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final ViewHolder holder = (ViewHolder) view.getTag();
		final String name = cursor.getString(mNameIndex);
		
		holder.name.setText(name);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.list_contacts, parent, false);
		ViewHolder holder = new ViewHolder();
		view.setTag(holder);
		
		holder.name = (TextView) view.findViewById(R.id.contact_name);
		
		return view;
	}
	
	@Override
	public Cursor swapCursor(Cursor newCursor) {
		if (newCursor != null){
			mNameIndex = newCursor.getColumnIndex(UserColumns.USER_NAME);
		}
		return super.swapCursor(newCursor);
	}

	private class ViewHolder{
		TextView name;
	}

}
