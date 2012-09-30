package com.warmice.android.videomessaging.tools.networktasks;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.data.Update;
import com.warmice.android.videomessaging.data.User;

import android.content.Context;

import com.warmice.android.videomessaging.tools.networktasks.RestService.RestResponse;
import com.warmice.android.videomessaging.tools.networktasks.RestService.*;

public class GetUpdatesTask extends RestTask {

	Context mContext;
	Update mUpdate;

	public GetUpdatesTask(Context context) {
		super(context, HttpVerb.GET);
		mContext = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		setupAdditionalParameters();
		String url = mContext.getString(R.string.url_get_updates);
		setUri(url);
	}

	@Override
	protected RestResponse doInBackground(Void... params) {
		RestResponse response = super.doInBackground(params);
		String json = response.getData();
		mUpdate = Update.parse(mContext, json);
		mUpdate.store();
		mUpdate.prepareNotifications();
		return response;
	}

	private void setupAdditionalParameters() {
		User user = User.load(mContext);
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("updates_since", user.last_update));
		
		setParams(params);
	}

	@Override
	protected void onPostExecute(RestResponse result) {
		if (result.actionSucceeded()){
			mUpdate.sendNotifications();
		}
	}
}
