package com.warmice.android.videomessaging.tools.networktasks;

import java.util.ArrayList;

import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.tools.networktasks.RestService.*;

import android.content.Context;

public class CreateAccountLoader extends RestLoader {

	Context mContext;

	String mUsername;
	String mPassword;
	String mFirstName;
	String mLastName;

	public CreateAccountLoader(Context context) {
		super(context, HttpVerb.POST);
		mContext = context;
	}

	@Override
	protected void onStartLoading() {
		setupAdditionalParameters();
		super.onStartLoading();
	}

	private void setupAdditionalParameters() {
		String createAccountUrl = mContext.getString(R.string.url_create_account);
		ArrayList<BasicNameValuePair> params = createParameters();
		
		setUri(createAccountUrl);
		setParams(params);
	}

	protected ArrayList<BasicNameValuePair> createParameters() {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("username", mUsername));
		params.add(new BasicNameValuePair("password", mPassword));
		if (mFirstName != null) {
			params.add(new BasicNameValuePair("first_name", mFirstName));
		}
		if (mLastName != null) {
			params.add(new BasicNameValuePair("last_name", mLastName));
		}
		
		return params;
	}
	
	public void setUsername(String username) {
		mUsername = username;
	}

	public void setPassword(String password) {
		mPassword = password;
	}

	public void setFirstName(String firstName) {
		mFirstName = firstName;
	}

	public void setLastName(String lastName) {
		mLastName = lastName;
	}

}
