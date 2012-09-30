package com.warmice.android.videomessaging.data;

import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.ui.ContactsActivity;

import android.content.Context;
import android.content.Intent;

public class ContactAcceptedPush extends PushMessage{
	
	public Content content;
	
	@Override
	public String getMessage(Context context) {
		String name = getName();
		String unformattedMessage = context.getString(R.string.message_contact_accepted);
		return String.format(unformattedMessage, name);
	}

	private String getName() {
		if(content.name.equals(" ")){
			return content.username;
		}
		return content.name;
	}

	@Override
	public String getTitle(Context context) {
		return context.getString(R.string.title_contact_accepted);
	}

	@Override
	public String getTickerText(Context context) {
		return context.getString(R.string.title_contact_accepted);
	}

	@Override
	public int getIcon() {
		return R.drawable.ic_stat_contact_request;
	}

	@Override
	public Intent getIntent(Context context) {
		return new Intent(context, ContactsActivity.class);
	}
	
	public class Content {
		public String name;
		public String username;
	}
}
