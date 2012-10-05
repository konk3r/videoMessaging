package com.warmice.android.videomessaging.data.notification;

import java.util.ArrayList;

import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.data.Contact;
import com.warmice.android.videomessaging.ui.ContactsActivity;

import android.content.Context;
import android.content.Intent;

public class ContactNotification extends BaseNotification {

	public Content content;
	private ArrayList<Contact> mContacts;
	int mType;

	public ContactNotification() {
	}

	public ContactNotification(ArrayList<Contact> contacts) {
		mContacts = contacts;
		setType();
	}

	private void setType() {
		if (mContacts.size() < 1) {
			return;
		}
		
		Contact contact = mContacts.get(0);
		if (contact.approved.equals("true")) {
			mType = TYPE_REQUEST_ACCEPTED;
		} else if (contact.approved.equals("response_requested")) {
			mType = TYPE_CONTACT_REQUEST;
		}
	}

	@Override
	public String getMessage(Context context) {
		String name = getName();
		String unformattedMessage = null;
		switch (mType) {
		case TYPE_CONTACT_REQUEST:
			unformattedMessage = context
					.getString(R.string.message_contact_request);
			break;
		case TYPE_REQUEST_ACCEPTED:
			unformattedMessage = context
					.getString(R.string.message_contact_accepted);
		}
		return String.format(unformattedMessage, name);
	}

	private String getName() {
		Contact contact = mContacts.get(0);
		if (contact.name.equals(" ")) {
			return contact.username;
		}
		return contact.name;
	}

	@Override
	public String getTitle(Context context) {
		switch (mType) {
		case TYPE_CONTACT_REQUEST:
			return context.getString(R.string.title_contact_request);
		case TYPE_REQUEST_ACCEPTED:
			return context.getString(R.string.title_contact_accepted);
		default:
			return null;
		}
	}

	@Override
	public String getTickerText(Context context) {
		switch(mType){
		case TYPE_CONTACT_REQUEST:
			return context.getString(R.string.title_contact_request);
		case TYPE_REQUEST_ACCEPTED:
			return context.getString(R.string.title_contact_accepted);
		default:
			return null;
		}
	}

	@Override
	public int getIcon() {
		return R.drawable.ic_stat_contact_request;
	}

	@Override
	public Intent getIntent(Context context) {
		return new Intent(context, ContactsActivity.class);
	}

	@Override
	public boolean hasNotifications() {
		if (mContacts != null && mContacts.size() > 0) {
			return true;
		}
		return false;
	}

	public class Content {
		public String name;
		public String username;
	}
}
