package com.warmice.android.videomessaging.tools.networktasks;

import java.util.ArrayList;

import ch.boye.httpclientandroidlib.entity.mime.MultipartEntity;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

import com.warmice.android.videomessaging.tools.networktasks.RestService.*;

import android.content.Context;
import android.os.AsyncTask;

public class RestTask extends AsyncTask<Void, Void, RestResponse> {
	private RestService mRestService;
	private RestResponse mRestResponse;
	
	
	public RestTask(Context context){
		mRestService = new RestService(context);
	}

	public RestTask(Context context, HttpVerb verb) {
		mRestService = new RestService(context);
		mRestService.setVerb(verb);
	}

	protected void setUri(String url) {
		mRestService.setUri(url);
	}

	protected void setParams(ArrayList<BasicNameValuePair> params) {
		mRestService.setParams(params);
	}
	
	protected void setEntity(MultipartEntity entity){
		mRestService.setEntity(entity);
	}

	@Override
	protected RestResponse doInBackground(Void... params) {
		mRestResponse = mRestService.performRequest();
		return mRestResponse;
	}

}