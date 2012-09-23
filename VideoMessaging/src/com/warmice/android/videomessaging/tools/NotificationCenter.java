package com.warmice.android.videomessaging.tools;

import com.warmice.android.videomessaging.data.PushMessage;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationCenter {
	private Context mContext;
	private NotificationManager mNotificationManager;
	private PushMessage mMessage;
	private Notification mNotification;

	public NotificationCenter(Context context) {
		mContext = context;
		String ns = Context.NOTIFICATION_SERVICE;
		mNotificationManager = (NotificationManager) mContext.getSystemService(ns);
	}
	
	public void buildNotification(PushMessage message){
		mMessage = message;
		initializeNotification();
		setFlags();
		setEventInfo();
		display();
	}

	@SuppressWarnings("deprecation")
	private void initializeNotification() {
		int icon = mMessage.getIcon();
		String tickerText = mMessage.getTickerText(mContext);
		long when = System.currentTimeMillis();
		
		mNotification = new Notification(icon, tickerText, when);
	}
	
	private void setFlags() {
		mNotification.defaults |= Notification.DEFAULT_VIBRATE;
		mNotification.defaults |= Notification.DEFAULT_SOUND;
		mNotification.defaults |= Notification.DEFAULT_LIGHTS;
		mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
	}
	
	@SuppressWarnings("deprecation")
	private void setEventInfo() {
		CharSequence contentTitle = mMessage.getTitle(mContext);
		CharSequence contentText = mMessage.getMessage(mContext);
		Intent notificationIntent = mMessage.getIntent(mContext);
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);

		mNotification.setLatestEventInfo(mContext, contentTitle, contentText, contentIntent);
	}

	private void display() {
		int id = mMessage.getType();
		mNotificationManager.notify(id, mNotification);
	}

}
