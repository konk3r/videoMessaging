package com.warmice.android.videomessaging.tools.networktasks;

import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.data.User;

import android.content.Context;

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
		User user = User.load(mContext);
		String unformattedUrl = mContext.getString(R.string.url_add_contact);
		String url = String.format(unformattedUrl, user.id, username);
		return url;
	}

	@Override
	protected void onPostExecute(RestResponse result) {
		super.onPostExecute(result);
	}

}
