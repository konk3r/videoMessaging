package com.warmice.android.videomessaging.tools.networktasks;

import com.warmice.android.videomessaging.R;

import android.content.Context;

import com.warmice.android.videomessaging.tools.networktasks.RestService.*;

public class AddDeviceTask extends RestTask {

	Context mContext;
	String mDeviceId;
	
	public AddDeviceTask(Context context) {
		super(context, HttpVerb.POST);
		mContext = context;
	}
	
	public void setDeviceId(String deviceId){
		mDeviceId = deviceId;
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
		String unformattedUrl = mContext.getString(R.string.url_add_device);
		String url = String.format(unformattedUrl, mDeviceId);
		return url;
	}

	@Override
	protected void onPostExecute(RestResponse result) {
		super.onPostExecute(result);
	}

}
