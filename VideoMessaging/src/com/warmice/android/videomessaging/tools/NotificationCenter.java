package com.warmice.android.videomessaging.tools;

import java.util.ArrayList;

import com.warmice.android.videomessaging.data.Contact;
import com.warmice.android.videomessaging.data.Message;
import com.warmice.android.videomessaging.data.notification.BaseNotification;
import com.warmice.android.videomessaging.data.notification.ContactNotification;
import com.warmice.android.videomessaging.data.notification.MessageNotification;

import android.content.Context;

public class NotificationCenter {
	private Context mContext;

	private ArrayList<BaseNotification> mNotifications;

	public NotificationCenter(Context context) {
		mContext = context;
		mNotifications = new ArrayList<BaseNotification>();
	}

	public void prepareContacts(ArrayList<Contact> contacts) {
		ContactNotification notification = new ContactNotification(contacts);
		prepareNotification(notification);
	}

	public void prepareMessages(ArrayList<Message> messages) {
		MessageNotification notification = new MessageNotification(messages);
		prepareNotification(notification);
	}

	private void prepareNotification(BaseNotification notification) {
		if (notification.hasNotifications()) {
			notification.prepare(mContext);
			mNotifications.add(notification);
		}
	}

	public void sendNotifications() {
		for(BaseNotification notification : mNotifications){
			notification.send(mContext);
		}
	}

}
