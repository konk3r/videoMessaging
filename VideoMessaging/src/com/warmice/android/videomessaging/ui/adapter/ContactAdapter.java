package com.warmice.android.videomessaging.ui.adapter;

import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.provider.MessagingContract.ContactColumns;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ContactAdapter extends CursorAdapter {
	
	public static final int TYPE_APPROVED = 0;
	public static final int TYPE_REQUESTED = 1;
	public static final int TYPE_PENDING = 2;
	
	private LayoutInflater mInflater;
	private int mNameIndex;
	private int mUsernameIndex;
	private int mApprovedIndex;
	private int mContactIdIndex;

	public ContactAdapter(Context context, Cursor cursor) {
		super(context, cursor, false);
		mInflater = LayoutInflater.from(context);
		extractIndices(cursor);
	}

	private void extractIndices(Cursor cursor) {
		if (cursor != null){
			mNameIndex = cursor.getColumnIndex(ContactColumns.CONTACT_NAME);
			mUsernameIndex = cursor.getColumnIndex(ContactColumns.CONTACT_USERNAME);
			mApprovedIndex = cursor.getColumnIndex(ContactColumns.CONTACT_APPROVAL_STATUS);
			mContactIdIndex = cursor.getColumnIndex(ContactColumns.CONTACT_ID);
		}
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		int viewType = getItemViewType(cursor.getPosition());
		
		switch (viewType){
		case TYPE_PENDING:
			bindContactPendingView(view, context, cursor);
			break;
		case TYPE_REQUESTED:
			bindContactRequestedView(view, context, cursor);
			break;
		default:
			bindStandardContactView(view, context, cursor);
		}
	}

	private Object bindStandardContactView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		String username = cursor.getString(mUsernameIndex);
		String name = cursor.getString(mNameIndex);
		
		if (name.equals(" ")) {
			name = username;
		}
		holder.name.setText(name);
		holder.username.setText(username);
		
		return view;
	}

	private Object bindContactRequestedView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		String username = cursor.getString(mUsernameIndex);
		String name = cursor.getString(mNameIndex);
		
		if (name.equals(" ")) {
			name = username;
		}
		holder.name.setText(name);
		holder.username.setText(username);
		return view;
	}

	private Object bindContactPendingView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		String username = cursor.getString(mUsernameIndex);
		
		holder.username.setText(username);
		return view;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		int viewType = getItemViewType(cursor.getPosition());
		
		switch (viewType){
		case TYPE_PENDING:
			return createContactPendingView(parent);
		case TYPE_REQUESTED:
			return createContactRequestedView(parent);
		default:
			return createStandardContactView(parent);
		}
		
	}

	private View createStandardContactView(ViewGroup parent) {
		View view;
		ViewHolder holder = new ViewHolder();
		
		view = mInflater.inflate(R.layout.list_contacts, parent, false);
		holder.username = (TextView) view.findViewById(R.id.contact_username);
		holder.name = (TextView) view.findViewById(R.id.contact_name);

		view.setTag(holder);
		return view;
	}

	private View createContactRequestedView(ViewGroup parent) {
		View view;
		ViewHolder holder = new ViewHolder();
		
		view = mInflater.inflate(R.layout.list_contacts_requested, parent, false);
		holder.username = (TextView) view.findViewById(R.id.contact_username);
		holder.name = (TextView) view.findViewById(R.id.contact_name);

		view.setTag(holder);
		return view;
	}

	private View createContactPendingView(ViewGroup parent) {
		View view;
		ViewHolder holder = new ViewHolder();

		view = mInflater.inflate(R.layout.list_contacts_pending, parent, false);
		holder.username = (TextView) view.findViewById(R.id.contact_username);
		
		view.setTag(holder);
		return view;
	}

	@Override
	public int getItemViewType(int position) {
		mCursor.moveToPosition(position);
		String approved = mCursor.getString(mApprovedIndex);
		if (approved.equals("pending_partner_action")){
			return TYPE_PENDING;
		}else if (approved.equals("response_requested")){
			return TYPE_REQUESTED;
		}else {
			return TYPE_APPROVED;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}
	
	@Override
	public Cursor swapCursor(Cursor newCursor) {
		extractIndices(newCursor);
		return super.swapCursor(newCursor);
	}

	private class ViewHolder{
		TextView name;
		TextView username;
	}

	public int getContactId(int position) {
		mCursor.moveToPosition(position);
		final int contactId = mCursor.getInt(mContactIdIndex);
		return contactId;
	}

	public String getUsername(int position) {
		mCursor.moveToPosition(position);
		return mCursor.getString(mUsernameIndex);
	}

}
