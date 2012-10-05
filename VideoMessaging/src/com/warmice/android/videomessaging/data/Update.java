package com.warmice.android.videomessaging.data;

import java.util.ArrayList;
import java.util.Iterator;

import org.codehaus.jackson.map.ObjectMapper;

import com.warmice.android.videomessaging.tools.NotificationCenter;

import android.content.Context;

public class Update {
	NotificationCenter mNotificationCenter;

	public ArrayList<Contact> contacts;
	public ArrayList<Message> messages;
	public String last_update;
	
	private Context mContext;

	public static Update parse(Context context, String json) {
		Update update = null;
		try {
			final ObjectMapper mapper = new ObjectMapper();
			update = mapper.readValue(json, Update.class);
			update.mContext = context;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return update;
	}

	public void updateExistingMessages() {
		for (Iterator<Message> it = messages.iterator(); it.hasNext();){
			Message message = it.next();
			if (message.update(mContext)) {
				it.remove();
			}
		}
	}

	public void store() {
		updateExistingMessages();
		Contact.storeContacts(mContext, contacts);
		Message.storeMessages(mContext, messages);
		CurrentUser user = CurrentUser.load(mContext);
		user.last_update = last_update;
		user.store(mContext);
	}

	public void prepareNotifications() {
		mNotificationCenter = new NotificationCenter(mContext);
		mNotificationCenter.prepareContacts(contacts);
		mNotificationCenter.prepareMessages(messages);
		
	}

	public void sendNotifications() {
		mNotificationCenter.sendNotifications();
	}
}
