package com.warmice.android.videomessaging.tools.networktasks;

import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.data.User;

import android.content.Context;

import com.warmice.android.videomessaging.tools.networktasks.RestService.*;

public class GetContactsTask extends RestTask {

	Context mContext;
	
	public GetContactsTask(Context context) {
		super(context, HttpVerb.GET);
		mContext = context;
	}

	@Override
	protected void onPreExecute() {
		setupAdditionalParameters();
		super.onPreExecute();
	}

	private void setupAdditionalParameters() {
		String createAccountUrl = formUrl();
		setUri(createAccountUrl);
	}

	private String formUrl() {
		User user = User.load(mContext);
		String unformattedUrl = mContext.getString(R.string.url_get_contacts);
		String url = String.format(unformattedUrl, user.id);
		return url;
	}

	@Override
	protected void onPostExecute(RestResponse result) {
		super.onPostExecute(result);
	}

}
