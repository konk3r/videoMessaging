package com.warmice.android.videomessaging.ui.adapter;

import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.provider.MessagingContract.UserColumns;
import com.warmice.android.videomessaging.provider.MessagingContract.Users;
import com.warmice.android.videomessaging.provider.MessagingContract.VideoColumns;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageListAdapter extends CursorAdapter {
	private LayoutInflater mInflater;
	private final int mNoteIndex;
	private final int mDateIndex;
	private final int mUriIndex;

	@SuppressWarnings("deprecation")
	public MessageListAdapter(Context context, Cursor c) {
		super(context, c);
		mInflater = LayoutInflater.from(context);
		
		mNoteIndex = c.getColumnIndex(VideoColumns.VIDEO_NOTE);
		mDateIndex = c.getColumnIndex(VideoColumns.VIDEO_DATE);
		mUriIndex = c.getColumnIndex(VideoColumns.VIDEO_FILE_PATH);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final ViewHolder holder = (ViewHolder) view.getTag();
		final String name = cursor.getString(mNoteIndex);
		final String date = cursor.getString(mDateIndex);
		
		holder.note.setText(name);
		holder.date.setText(date);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.list_messages_outbound, parent, false);
		ViewHolder holder = new ViewHolder();
		view.setTag(holder);
		
		holder.note = (TextView) view.findViewById(R.id.message_note);
		holder.date = (TextView) view.findViewById(R.id.message_date);
		holder.thumbnail = (ImageView) view.findViewById(R.id.message_thumbnail);
		
		return view;
	}
	
	private class ViewHolder{
		TextView note;
		TextView date;
		@SuppressWarnings("unused")
		ImageView thumbnail;
	}

	public Uri getVideoUri(int position) {
		final Cursor c = getCursor();
		c.moveToPosition(position);
		final String videoUri = c.getString(mUriIndex);
		
		return Uri.parse(videoUri);
	}

}
