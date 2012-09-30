package com.warmice.android.videomessaging.data;

import android.content.Context;
import android.content.Intent;

public abstract class PushMessage {
	public static final String CONTACT_REQUEST = "contact_request";
	public static final String CONTACT_ACCEPTED = "contact_accepted";
	public static final String MESSAGE_NEW = "new_message";
	
	public static final int TYPE_CONTACT_REQUEST = 0;
	public static final int TYPE_REQUEST_ACCEPTED = 1;
	public static final int TYPE_MESSAGE_NEW = 2;
	
	public String type;

	public abstract String getTitle(Context context);
	public abstract String getMessage(Context context);
	public abstract String getTickerText(Context context);
	public abstract int getIcon();
	public abstract Intent getIntent(Context context);

	public int getType() {
		if (type.equals(CONTACT_REQUEST)){
			return TYPE_CONTACT_REQUEST;
		} else if(type.equals(CONTACT_ACCEPTED)){
			return TYPE_REQUEST_ACCEPTED;
		} else if(type.equals(MESSAGE_NEW)){
			return TYPE_MESSAGE_NEW;
		} else {
			return -1;
		}
	}
	public boolean hasNotifications() {
		return false;
	}
}
