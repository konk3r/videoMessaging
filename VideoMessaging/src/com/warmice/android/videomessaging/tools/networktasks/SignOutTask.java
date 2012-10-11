package com.warmice.android.videomessaging.tools.networktasks;

import java.util.ArrayList;

import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

import com.google.android.gcm.GCMRegistrar;
import com.warmice.android.videomessaging.R;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.warmice.android.videomessaging.data.CurrentUser;
import com.warmice.android.videomessaging.provider.MessagingContract.AuthenticatedTables;
import com.warmice.android.videomessaging.tools.networktasks.RestService.RestResponse;
import com.warmice.android.videomessaging.tools.networktasks.RestService.*;

public class SignOutTask extends RestTask {

	Context mContext;
	String mDeviceId;
	CurrentUser mUser;
	
	public SignOutTask(Context context) {
		super(context, HttpVerb.DELETE);
		mContext = context;
	}

	@Override
	protected void onPreExecute() {
		mUser = CurrentUser.load(mContext);
		
		setupRegistrarAndUnregister();
		setAdditionalParameters();
		setUrl();
		
		mUser.signOut(mContext);
	}

	@Override
	protected RestResponse doInBackground(Void... params) {
		clearDatabase();
		return super.doInBackground(params);
	}

	private void clearDatabase() {
		ContentResolver resolver = mContext.getContentResolver();
		Uri uri = AuthenticatedTables.getClearAllUri();
		resolver.delete(uri, null, null);
	}

	private void setupRegistrarAndUnregister() {
		GCMRegistrar.checkDevice(mContext);
		GCMRegistrar.checkManifest(mContext);
		mDeviceId = GCMRegistrar.getRegistrationId(mContext);
		GCMRegistrar.unregister(mContext);
	}

	private void setAdditionalParameters() {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("device_id", mDeviceId));
		params.add(new BasicNameValuePair("user_id", Integer.toString(mUser.id)));
		params.add(new BasicNameValuePair("api_key", mUser.api_key));
		
		setParams(params);
	}

	private void setUrl() {
		String createAccountUrl = mContext.getString(R.string.url_logout);
		setUri(createAccountUrl);
	}

	@Override
	protected void onPostExecute(RestResponse result) {
		super.onPostExecute(result);
	}

}
