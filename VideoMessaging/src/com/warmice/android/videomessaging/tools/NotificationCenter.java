package com.warmice.android.videomessaging.tools;

import java.util.ArrayList;

import com.warmice.android.videomessaging.data.Contact;
import com.warmice.android.videomessaging.data.ContactRequestPush;
import com.warmice.android.videomessaging.data.Message;
import com.warmice.android.videomessaging.data.MessageNewPush;
import com.warmice.android.videomessaging.data.PushMessage;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationCenter {
	private Context mContext;
	private NotificationManager mNotificationManager;
	
	private PushMessage mMessageData;
	private PushMessage mContactData;
	
	private Notification mContactNotification;
	private Notification mMessageNotification;

	public NotificationCenter(Context context) {
		mContext = context;
		String ns = Context.NOTIFICATION_SERVICE;
		mNotificationManager = (NotificationManager) mContext.getSystemService(ns);
	}

	@SuppressWarnings("deprecation")
	private void setEventInfo(Notification notification, PushMessage notificationData) {
		String title = notificationData.getTitle(mContext);
		String text = notificationData.getMessage(mContext);
		Intent intent = notificationData.getIntent(mContext);
		
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
		notification.setLatestEventInfo(mContext, title, text, contentIntent);
	}

	private Notification createMessagesNotification(PushMessage message) {
		int icon = message.getIcon();
		String tickerText = message.getTickerText(mContext);
		return initializeNotification(icon, tickerText);
	}

	@SuppressWarnings("deprecation")
	private Notification initializeNotification(int icon, String tickerText) {
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);
		return notification;
	}
	
	private void setFlags(Notification notification) {
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
	}
	
	private void display(Notification notification, int id) {
		mNotificationManager.notify(id, notification);
	}

	public void sendNotifications() {
		if (mContactData.hasNotifications()){
			display(mContactNotification, mContactData.getType());
		}
		if (mMessageData.hasNotifications()){
			display(mMessageNotification, mMessageData.getType());
		}
	}

	public void prepareContacts(ArrayList<Contact> contacts) {
		mContactData = new ContactRequestPush(contacts);
		if (mContactData.hasNotifications()){
			mContactNotification = prepareNotification(mContactData);
		}
	}

	public void prepareMessages(ArrayList<Message> messages) {
		mMessageData = new MessageNewPush(messages);
		if (mMessageData.hasNotifications()){
			((MessageNewPush)mMessageData).setup(mContext);
			mMessageNotification = prepareNotification(mMessageData);
		}
	}

	private Notification prepareNotification(PushMessage message) {
		Notification notification = createMessagesNotification(message);
		setFlags(notification);
		setEventInfo(notification, message);
		return notification;
	}


}
