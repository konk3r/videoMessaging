package com.warmice.android.videomessaging.tools.networktasks;

import com.warmice.android.videomessaging.R;

import android.content.Context;

import com.warmice.android.videomessaging.data.Contact;
import com.warmice.android.videomessaging.tools.networktasks.RestService.*;

public class AddContactTask extends RestTask {

	Context mContext;
	
	public AddContactTask(Context context, String username) {
		super(context, HttpVerb.POST);
		
		mContext = context;
		String url = formUrl(username);
		setUri(url);
	}

	private String formUrl(String username) {
		String unformattedUrl = mContext.getString(R.string.url_add_contact);
		String url = String.format(unformattedUrl, username);
		return url;
	}

	@Override
	protected void onPostExecute(RestResponse result) {
		String json = result.getData();
		Contact.storeContactFromJson(mContext, json);
	}

}
