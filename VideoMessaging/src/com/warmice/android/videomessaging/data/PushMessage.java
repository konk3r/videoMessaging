package com.warmice.android.videomessaging.data;

import android.content.Context;
import android.content.Intent;

public abstract class PushMessage {
	public static final String CONTACT_REQUEST = "contact_request";
	public static final String CONTACT_ACCEPTED = "contact_accepted";
	
	public static final int TYPE_CONTACT_REQUEST = 0;
	public static final int TYPE_REQUEST_ACCEPTED = 1;
	
	public String type;

	public abstract String getTitle(Context context);
	public abstract String getMessage(Context context);
	public abstract String getTickerText(Context context);
	public abstract int getIcon();
	public abstract Intent getIntent(Context context);

	public int getType() {
		if (type.equals(CONTACT_REQUEST)){
			return 0;
		} else if(type.equals(CONTACT_ACCEPTED)){
			return 1;
		}
		return 2;
	}
}
