package com.warmice.android.videomessaging.ui.adapter;

import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.data.CurrentUser;
import com.warmice.android.videomessaging.provider.MessagingContract.MessageColumns;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MessageAdapter extends CursorAdapter {
	private static final int TYPE_OUTBOUND = 0;
	private static final int TYPE_INBOUND = 1;
	private LayoutInflater mInflater;
	private int mTextIndex;
	private int mDateIndex;
	private int mSenderIdIndex;
	private int userId;

	public MessageAdapter(Context context, Cursor c) {
		super(context, c, false);
		mInflater = LayoutInflater.from(context);
		loadIndices();
		userId = CurrentUser.load(context).id;
	}

	private void loadIndices() {
		Cursor cursor = getCursor();
		if (cursor != null) {
			mTextIndex = cursor.getColumnIndex(MessageColumns.MESSAGE_TEXT);
			mDateIndex = cursor
					.getColumnIndex(MessageColumns.MESSAGE_SENT_DATE);
			mSenderIdIndex = cursor.getColumnIndex(MessageColumns.SENDER_ID);
		}
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final ViewHolder holder = (ViewHolder) view.getTag();
		final String name = cursor.getString(mTextIndex);
		final String date = cursor.getString(mDateIndex);

		holder.text.setText(name);
		holder.date.setText(date);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		int position = cursor.getPosition();
		switch (getItemViewType(position)) {
		case TYPE_INBOUND:
			return newInboundView(context, cursor, parent);
		case TYPE_OUTBOUND:
			return newOutboundView(context, cursor, parent);
		default:
			return null;
		}
	}

	public View newOutboundView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.list_messages_text_outbound,
				parent, false);
		ViewHolder holder = new ViewHolder();
		view.setTag(holder);

		holder.text = (TextView) view.findViewById(R.id.message_text);
		holder.date = (TextView) view.findViewById(R.id.message_sent_date);

		return view;
	}

	public View newInboundView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.list_messages_text_inbound,
				parent, false);
		ViewHolder holder = new ViewHolder();
		view.setTag(holder);

		holder.text = (TextView) view.findViewById(R.id.message_text);
		holder.date = (TextView) view.findViewById(R.id.message_sent_date);

		return view;
	}

	@Override
	public int getItemViewType(int position) {
		mCursor.moveToPosition(position);
		int senderId = mCursor.getInt(mSenderIdIndex);
		if (senderId == userId) {
			return TYPE_OUTBOUND;
		} else {
			return TYPE_INBOUND;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	private class ViewHolder {
		TextView text;
		TextView date;
	}

	@Override
	public Cursor swapCursor(Cursor newCursor) {
		super.swapCursor(newCursor);
		loadIndices();
		return getCursor();
	}

}
