package com.warmice.android.videomessaging.ui.adapter;

import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.provider.MessagingContract.ContactColumns;
import com.warmice.android.videomessaging.ui.MessagesActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ConversationAdapter extends CursorAdapter {
	private LayoutInflater mInflater;
	private final int mNameIndex;
	private final int mIdIndex;
	private final int mDateIndex;

	public ConversationAdapter(Context context, Cursor c) {
		super(context, c, false);
		mInflater = LayoutInflater.from(context);
		
		mNameIndex = c.getColumnIndex(ContactColumns.CONTACT_NAME);
		mIdIndex = c.getColumnIndex(ContactColumns.CONTACT_ID);
		mDateIndex = c.getColumnIndex(ContactColumns.CONTACT_LAST_POST_DATE);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final ViewHolder holder = (ViewHolder) view.getTag();
		final String name = cursor.getString(mNameIndex);
		final String date = cursor.getString(mDateIndex);
		
		holder.name.setText(name);
		holder.date.setText(date);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.list_conversations, parent, false);
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
		@SuppressWarnings("unused")
		ImageView image;
	}

	public Intent setupMessagesIntent(Intent intent, int position) {
		final Cursor c = getCursor();
		c.moveToPosition(position);
		
		final String userId = c.getString(mIdIndex);
		final String userName = c.getString(mNameIndex);
		
		intent.putExtra(MessagesActivity.EXTRA_CONTACT_ID, userId);
		intent.putExtra(MessagesActivity.EXTRA_USERNAME, userName);
		
		return intent;
	}

}
