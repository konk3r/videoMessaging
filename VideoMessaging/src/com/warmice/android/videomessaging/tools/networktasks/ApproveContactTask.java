package com.warmice.android.videomessaging.tools.networktasks;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import com.warmice.android.videomessaging.R;

import android.content.Context;

import com.warmice.android.videomessaging.data.Contact;
import com.warmice.android.videomessaging.tools.networktasks.RestService.*;

public class ApproveContactTask extends RestTask {

	Context mContext;

	public ApproveContactTask(Context context, int contactId) {
		super(context, HttpVerb.PUT);

		mContext = context;
		String url = formUrl(contactId);
		setUri(url);
		setAdditionalParams();
	}

	private String formUrl(int contactId) {
		String unformattedUrl = mContext
				.getString(R.string.url_approve_contact);
		String url = String.format(unformattedUrl, Integer.toString(contactId));
		return url;
	}

	private void setAdditionalParams() {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("accept", "true"));

		setParams(params);
	}

	@Override
	protected void onPostExecute(RestResponse result) {
		String json = result.getData();
		Contact.storeContactFromJson(mContext, json);
	}

}
