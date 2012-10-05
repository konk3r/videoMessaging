package com.warmice.android.videomessaging.tools.networktasks;

import java.util.ArrayList;

import org.apache.http.HttpStatus;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.android.gcm.GCMRegistrar;
import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.data.CurrentUser;
import com.warmice.android.videomessaging.tools.networktasks.RestService.*;

import android.content.Context;

public class SignInLoader extends RestLoader {

	Context mContext;

	String mUsername;
	String mPassword;

	public SignInLoader(Context context) {
		super(context, HttpVerb.POST);
		mContext = context;
	}

	@Override
	protected void onStartLoading() {
		setupAdditionalParameters();
		super.onStartLoading();
	}

	private void setupAdditionalParameters() {
		String signInUrl = mContext.getString(R.string.url_login);
		ArrayList<BasicNameValuePair> params = createParameters();
		
		setUri(signInUrl);
		setParams(params);
	}

	protected ArrayList<BasicNameValuePair> createParameters() {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("username", mUsername));
		params.add(new BasicNameValuePair("password", mPassword));
		return params;
	}

	@Override
	public void deliverResult(RestResponse data) {
		if (data.getCode() == HttpStatus.SC_OK){
			registerDeviceForGCM();
			storeCredentials(data.getData());
		}
		super.deliverResult(data);
	}
	
	private void registerDeviceForGCM(){
		GCMRegistrar.checkDevice(mContext);
		GCMRegistrar.checkManifest(mContext);
		final String senderId = mContext.getString(R.string.notifications_sender_id);
		GCMRegistrar.register(mContext, senderId);
	}

	private void storeCredentials(String json) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final CurrentUser user = mapper.readValue(json, CurrentUser.class);
			user.store(mContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setUsername(String username) {
		mUsername = username;
	}

	public void setPassword(String password) {
		mPassword = password;
	}

}
