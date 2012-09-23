package com.warmice.android.videomessaging.tools;

import org.codehaus.jackson.map.ObjectMapper;

import com.warmice.android.videomessaging.data.BasePush;
import com.warmice.android.videomessaging.data.ContactAcceptedPush;
import com.warmice.android.videomessaging.data.ContactRequestPush;
import com.warmice.android.videomessaging.data.PushMessage;

import android.content.Intent;

public class MessageFactory {
	public static final String EXTRA_MESSAGE_TEXT = "message_text";
	
	private String mData;
	private int mRequestType;
	private PushMessage mMessage;

	public PushMessage buildMessage(Intent intent) {
		pullDataFromIntent(intent);
		findRequestType();
		createMessage();
		return mMessage;
	}

	private void pullDataFromIntent(Intent intent) {
		mData = intent.getStringExtra(EXTRA_MESSAGE_TEXT);
	}

	private void findRequestType() {
		PushMessage message = null;
		try {
			final ObjectMapper mapper = new ObjectMapper();
			message = mapper.readValue(mData, BasePush.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mRequestType = message.getType();
	}

	private void createMessage() {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			switch(mRequestType){
			case PushMessage.TYPE_CONTACT_REQUEST:
				mMessage = mapper.readValue(mData, ContactRequestPush.class);
				break;
			case PushMessage.TYPE_REQUEST_ACCEPTED:
				mMessage = mapper.readValue(mData, ContactAcceptedPush.class);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
