package com.warmice.android.videomessaging.data.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

public abstract class BaseNotification {
	public static final String CONTACT_REQUEST = "contact_request";
	public static final String CONTACT_ACCEPTED = "contact_accepted";
	public static final String MESSAGE_NEW = "new_message";

	public static final int TYPE_CONTACT_REQUEST = 0;
	public static final int TYPE_REQUEST_ACCEPTED = 1;
	public static final int TYPE_NEW_MESSAGE = 2;

	protected int mType;
	protected Notification mNotification;

	public void prepare(Context context) {
		createNotification(context);
		setNotificationDefaults();
	}

	private void createNotification(Context context) {
		mNotification = new NotificationCompat.Builder(context)
				.setContentTitle(getTitle(context))
				.setContentText(getMessage(context))
				.setTicker(getTickerText(context))
				.setSmallIcon(getIcon())
				.setContentIntent(getIntent(context))
				.getNotification();
	}

	private void setNotificationDefaults() {
		mNotification.defaults |= Notification.DEFAULT_VIBRATE;
		mNotification.defaults |= Notification.DEFAULT_SOUND;
		mNotification.defaults |= Notification.DEFAULT_LIGHTS;
		mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
	}

	public void send(Context mContext) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager notificationManager = (NotificationManager) mContext
				.getSystemService(ns);
		notificationManager.notify(mType, mNotification);
	}

	public abstract String getTitle(Context context);

	public abstract String getMessage(Context context);

	public abstract String getTickerText(Context context);

	public abstract int getIcon();

	public abstract PendingIntent getIntent(Context context);

	public abstract boolean hasNotifications();
}
