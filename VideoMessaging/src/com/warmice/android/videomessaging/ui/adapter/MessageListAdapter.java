package com.warmice.android.videomessaging.ui.adapter;

import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.provider.MessagingContract.UserColumns;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageListAdapter extends CursorAdapter {
	private LayoutInflater mInflater;
	private final int nameIndex;
	private final int idIndex;
	private final int dateIndex;

	public MessageListAdapter(Context context, Cursor c) {
		super(context, c, 0);
		mInflater = LayoutInflater.from(context);
		
		nameIndex = c.getColumnIndex(UserColumns.USER_NAME);
		idIndex = c.getColumnIndex(UserColumns.USER_ID);
		dateIndex = c.getColumnIndex(UserColumns.USER_LAST_POST_DATE);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final ViewHolder holder = (ViewHolder) view.getTag();
		final String name = cursor.getString(nameIndex);
		final String id = cursor.getString(idIndex);
		
		holder.name.setText(name);
		holder.date.setText(id);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.list_message_overview, parent, false);
		ViewHolder holder = new ViewHolder();
		view.setTag(holder);
		
		holder.name = (TextView) view.findViewById(R.id.contact_name);
		holder.date = (TextView) view.findViewById(R.id.last_message_date);
		holder.image = (ImageView) view.findViewById(R.id.contact_image);
		
		return view;
	}
	
	private class ViewHolder{
		TextView name;
		TextView date;
		ImageView image;
	}

}
