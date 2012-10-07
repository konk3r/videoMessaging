package com.warmice.android.videomessaging.data.notification;

import java.util.ArrayList;

import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.data.Message;
import com.warmice.android.videomessaging.data.User;
import com.warmice.android.videomessaging.ui.ContactsActivity;
import com.warmice.android.videomessaging.ui.MessagesActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class MessageNotification extends BaseNotification {

	public Content content;
	private ArrayList<Message> mMessages;
	
	private String mTitle;
	private String mMessage;
	private String mTickerText;
	private PendingIntent mIntent;
	
	private Context mContext;

	public MessageNotification() {
	}
	
	public MessageNotification(ArrayList<Message> messages) {
		mMessages = messages;
		mType = TYPE_NEW_MESSAGE;
	}

	@Override
	public String getTitle(Context context) {
		return mTitle;
	}

	@Override
	public String getMessage(Context context) {
		return mMessage;
	}

	@Override
	public String getTickerText(Context context) {
		return mTickerText;
	}

	@Override
	public PendingIntent getIntent(Context context) {
		return mIntent;
	}
	
	@Override
	public void prepare(Context context){
		mContext = context;
		setMessage();
		setTitle();
		setTickerText();
		setIntent();
		super.prepare(context);
	}
	
	private void setMessage() {
		if (mMessages.size() > 1) {
			setBulkMessage();
		} else {
			setSingleMessage();
		}
	}

	private void setBulkMessage() {
		String unformattedMessage = mContext
				.getString(R.string.message_new_message);
		mMessage = String.format(unformattedMessage, mMessages.size());
	}

	private void setSingleMessage() {
		mMessage = mMessages.get(0).text;
	}

	private void setTitle() {
		if (mMessages.size() > 1) {
			setBulkTitle();
		} else {
			setSingleTitle();
		}
	}

	private void setBulkTitle() {
		mTitle = mContext.getString(R.string.title_new_message);
	}

	private void setSingleTitle() {
		int senderId = mMessages.get(0).sender_id;
		User user = getUser(mContext, senderId);
		mTitle = String.format("%s:", user.getName());
	}

	private void setTickerText() {
		if (mMessages.size() > 1) {
			setBulkTickerText();
		} else {
			setSingleTickerText();
		}
	}

	private void setBulkTickerText() {
		mTickerText = mContext.getString(R.string.title_new_message);
	}

	private void setSingleTickerText() {
		int senderId = mMessages.get(0).sender_id;
		String message = mMessages.get(0).text;
		String name = getUser(mContext, senderId).getName();
		mTickerText = String.format("%s: %s", name, message);
	}

	@Override
	public int getIcon() {
		return R.drawable.ic_stat_new_message;
	}

	private void setIntent() {
		if (allMessagesFromSameContact()) {
			setSingleIntent();
		} else {
			setBulkIntent();
		}
	}

	private boolean allMessagesFromSameContact() {
		boolean fromSameContact = true;
		final Integer previousId = mMessages.get(0).getContactId(mContext);
		for (Message message : mMessages) {
			if (message.sender_id != previousId
					&& message.receiver_id != previousId) {
				fromSameContact = false;
			}
		}
		return fromSameContact;
	}

	private void setBulkIntent() {
		Intent intent = new Intent(mContext, ContactsActivity.class);
		mIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
	}

	private void setSingleIntent() {
		Intent intent = new Intent(mContext, MessagesActivity.class);
		int contactId = mMessages.get(0).getContactId(mContext);
		intent.putExtra(MessagesActivity.EXTRA_CONTACT_ID, contactId);
		mIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
	}
	
	@Override
	public boolean hasNotifications() {
		if (mMessages != null && mMessages.size() > 0){
			return true;
		}
		return false;
	}

	public class Content {
		public String id;
	}
	
}
