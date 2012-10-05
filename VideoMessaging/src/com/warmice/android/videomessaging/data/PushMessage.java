package com.warmice.android.videomessaging.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PushMessage {
	public static final String CONTACT_REQUEST = "contact_request";
	public static final String CONTACT_ACCEPTED = "contact_accepted";
	public static final String MESSAGE_NEW = "new_message";

	public static final int TYPE_NOT_FOUND = -1;
	public static final int TYPE_CONTACT_REQUEST = 0;
	public static final int TYPE_REQUEST_ACCEPTED = 1;
	public static final int TYPE_MESSAGE_NEW = 2;
	
	public String type;

	public int getType() {
		if (type.equals(CONTACT_REQUEST)){
			return TYPE_CONTACT_REQUEST;
		} else if(type.equals(CONTACT_ACCEPTED)){
			return TYPE_REQUEST_ACCEPTED;
		} else if(type.equals(MESSAGE_NEW)){
			return TYPE_MESSAGE_NEW;
		} else {
			return TYPE_NOT_FOUND;
		}
	}
}
