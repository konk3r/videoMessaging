package com.warmice.android.videomessaging.tools;

import com.warmice.android.videomessaging.ui.MessagesActivity;
import com.warmice.android.videomessaging.ui.adapter.ContactAdapter;
import com.warmice.android.videomessaging.ui.dialog.ContactRequestedFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class ContactClickListener implements OnItemClickListener{
	Context mContext;
	ContactAdapter mAdapter;
	int mPosition;
	
	public ContactClickListener(Context context){
		mContext = context;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position,
			long id) {
		mAdapter = (ContactAdapter) adapterView.getAdapter();
		mPosition = position;
		
		int viewType = adapterView.getAdapter().getItemViewType(position);

		switch (viewType){
		case ContactAdapter.TYPE_PENDING:
			onContactPendingClick();
			break;
		case ContactAdapter.TYPE_REQUESTED:
			onContactRequestedClick();
			break;
		default:
			onStandardContactClick();
			break;
		}
	}

	private void onStandardContactClick() {
		loadMessageActivity();
	}

	private void loadMessageActivity() {
		Intent intent = buildIntent();
		mContext.startActivity(intent);
	}

	private Intent buildIntent() {
		Intent intent = new Intent(mContext, MessagesActivity.class);
		int contactId = mAdapter.getContactId(mPosition);
		String username = mAdapter.getUsername(mPosition);
		intent.putExtra(MessagesActivity.EXTRA_CONTACT_ID, contactId);
		intent.putExtra(MessagesActivity.EXTRA_USERNAME, username);
		return intent;
	}

	private void onContactRequestedClick() {
		displayRespondToRequestDialog();
	}

	private void displayRespondToRequestDialog() {
		int contactId = mAdapter.getContactId(mPosition);
		
		Bundle args = new Bundle();
		args.putInt(ContactRequestedFragment.CONTACT_ID_KEY, contactId);
		
		FragmentActivity activity = (FragmentActivity) mContext;
		FragmentManager manager = activity.getSupportFragmentManager();
		DialogFragment newFragment = ContactRequestedFragment.newInstance(args);
	    newFragment.show(manager, "dialog");
	}

	private void onContactPendingClick() {
		
	}

}
