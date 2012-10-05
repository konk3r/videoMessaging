package com.warmice.android.videomessaging.tools;

import org.codehaus.jackson.map.ObjectMapper;

import com.warmice.android.videomessaging.data.PushMessage;

import android.content.Intent;

public class PushMessageFactory {
	public static final String EXTRA_MESSAGE_TEXT = "message_text";
	
	private String mData;
	private PushMessage mMessage;

	public PushMessage buildMessage(Intent intent) {
		pullDataFromIntent(intent);
		createMessage();
		return mMessage;
	}

	private void pullDataFromIntent(Intent intent) {
		mData = intent.getStringExtra(EXTRA_MESSAGE_TEXT);
	}

	private void createMessage() {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			mMessage = mapper.readValue(mData, PushMessage.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
