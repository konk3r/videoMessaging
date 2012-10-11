package com.warmice.android.videomessaging.tools.networktasks;

import java.util.ArrayList;

import org.codehaus.jackson.map.ObjectMapper;

import ch.boye.httpclientandroidlib.HttpStatus;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

import com.google.android.gcm.GCMRegistrar;
import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.data.CurrentUser;
import com.warmice.android.videomessaging.tools.networktasks.RestService.*;

import android.content.Context;

public class SignInTask extends RestTask {

	private Context mContext;
	private SignInListener mListener;
	
	private String mUsername;
	private String mPassword;

	public SignInTask(Context context, SignInListener listener) {
		super(context, HttpVerb.POST);
		mContext = context;
		mListener = listener;
	}
	
	public void setUsername(String username) {
		mUsername = username;
	}

	public void setPassword(String password) {
		mPassword = password;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		setupAdditionalParameters();
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
	public void onPostExecute(RestResponse response) {
		if (response.getCode() == HttpStatus.SC_OK){
			registerDeviceForGCM();
			storeCredentials(response.getData());
		}
		mListener.onSignInResult(response);
	}
	
	private void registerDeviceForGCM(){
		final String senderId = mContext.getString(R.string.notifications_sender_id);
		GCMRegistrar.checkDevice(mContext);
		GCMRegistrar.checkManifest(mContext);
		GCMRegistrar.register(mContext, senderId);
	}

	private void storeCredentials(String json) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final CurrentUser user = mapper.readValue(json, CurrentUser.class);
			user.setPassword(mPassword);
			user.store(mContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public interface SignInListener{
		public abstract void onSignInResult(RestResponse response);
	}
}
