package com.warmice.android.videomessaging.tools;

import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.ui.ContactsActivity;
import com.warmice.android.videomessaging.ui.adapter.ContactAdapter;
import com.warmice.android.videomessaging.ui.dialog.ContactRequestedFragment;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
		sendNotification();
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


	@SuppressWarnings("deprecation")
	private void sendNotification() {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(ns);
		
		int icon = R.drawable.ic_stat_contact_request;
		CharSequence tickerText = "Hello";
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		CharSequence contentTitle = "My notification";
		CharSequence contentText = "Hello World!";
		Intent notificationIntent = new Intent(mContext, ContactsActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);

		notification.setLatestEventInfo(mContext, contentTitle, contentText, contentIntent);
		final int HELLO_ID = 1;
		

		mNotificationManager.notify(HELLO_ID, notification);
	}

}
