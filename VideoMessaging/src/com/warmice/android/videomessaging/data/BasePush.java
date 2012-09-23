package com.warmice.android.videomessaging.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import android.content.Context;
import android.content.Intent;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BasePush extends PushMessage {

	@Override
	public String getMessage(Context context) {
		return null;
	}

	@Override
	public String getTitle(Context context) {
		return null;
	}

	@Override
	public String getTickerText(Context context) {
		return null;
	}

	@Override
	public int getIcon() {
		return -1;
	}

	@Override
	public Intent getIntent(Context context) {
		return null;
	}
}
