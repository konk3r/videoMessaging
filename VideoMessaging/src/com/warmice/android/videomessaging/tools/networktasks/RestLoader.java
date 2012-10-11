package com.warmice.android.videomessaging.tools.networktasks;

import java.util.ArrayList;

import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

import com.warmice.android.videomessaging.tools.networktasks.RestService.*;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class RestLoader extends AsyncTaskLoader<RestService.RestResponse> {
	private static final int STALE_DELTA = 60 * 1000;
	
	private RestService mRestService;
	private RestResponse mRestResponse;
	
	private long mLastLoad;
	
	public RestLoader(Context context){
		super(context);
		mRestService = new RestService(context);
	}

	public RestLoader(Context context, HttpVerb verb) {
		super(context);
		mRestService = new RestService(context);
		mRestService.setVerb(verb);
	}

	protected void setUri(String url) {
		mRestService.setUri(url);
	}

	protected void setParams(ArrayList<BasicNameValuePair> params) {
		mRestService.setParams(params);
	}

	@Override
	public RestResponse loadInBackground() {
		mRestResponse = mRestService.performRequest();
		return mRestResponse;
	}
	
	@Override
	protected void onStartLoading() {
		boolean timeoutReached = System.currentTimeMillis() - mLastLoad >= STALE_DELTA;

		if (mRestResponse != null) {
			super.deliverResult(mRestResponse);
		}

		if (mRestResponse == null || timeoutReached)
			forceLoad();
		mLastLoad = System.currentTimeMillis();
	}

	@Override
	protected void onStopLoading() {
		cancelLoad();
	}

	@Override
	protected void onReset() {
		super.onReset();

		onStopLoading();

		mRestResponse = null;
		mLastLoad = 0;
	}

}