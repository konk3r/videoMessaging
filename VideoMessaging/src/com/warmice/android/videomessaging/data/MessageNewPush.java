package com.warmice.android.videomessaging.data;

import java.util.ArrayList;

import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.ui.ContactsActivity;
import com.warmice.android.videomessaging.ui.MessagesActivity;

import android.content.Context;
import android.content.Intent;

public class MessageNewPush extends PushMessage {

	public Content content;
	private ArrayList<Message> mMessages;
	
	private String mTitle;
	private String mMessage;
	private String mTickerText;
	private Intent mIntent;
	
	private Context mContext;

	public MessageNewPush() {
	}
	
	public MessageNewPush(ArrayList<Message> messages) {
		mMessages = messages;
		type = MESSAGE_NEW;
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
	public Intent getIntent(Context context) {
		return mIntent;
	}
	
	public void setup(Context context){
		mContext = context;
		setMessage();
		setTitle();
		setTickerText();
		setIntent();
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
		int senderId = mMessages.get(0).id;
		mTitle = String.format("New message from %s", senderId);
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
		mTickerText = mMessages.get(0).text;
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
		mIntent = new Intent(mContext, ContactsActivity.class);
	}

	private void setSingleIntent() {
		Intent intent = new Intent(mContext, MessagesActivity.class);
		int contactId = mMessages.get(0).getContactId(mContext);
		intent.putExtra(MessagesActivity.EXTRA_CONTACT_ID, contactId);
		mIntent = intent;
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
