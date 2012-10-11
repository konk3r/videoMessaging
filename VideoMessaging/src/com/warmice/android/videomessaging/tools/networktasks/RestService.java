package com.warmice.android.videomessaging.tools.networktasks;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpStatus;
import ch.boye.httpclientandroidlib.ParseException;
import ch.boye.httpclientandroidlib.StatusLine;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
import ch.boye.httpclientandroidlib.client.methods.HttpDelete;
import ch.boye.httpclientandroidlib.client.methods.HttpEntityEnclosingRequestBase;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.methods.HttpPut;
import ch.boye.httpclientandroidlib.client.methods.HttpRequestBase;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntity;
import ch.boye.httpclientandroidlib.entity.mime.content.StringBody;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;
import ch.boye.httpclientandroidlib.util.EntityUtils;

import com.warmice.android.videomessaging.BuildConfig;
import com.warmice.android.videomessaging.data.CurrentUser;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class RestService {
	private static final String TAG = "Rest Service";

	public enum HttpVerb {
		GET, POST, PUT, DELETE
	}

	public static class RestResponse {
		private String mData;
		private int mCode;

		public RestResponse() {
		}

		public RestResponse(String data, int code) {
			mData = data;
			mCode = code;
		}

		public String getData() {
			return mData;
		}

		public int getCode() {
			return mCode;
		}

		public boolean actionSucceeded() {
			return (mCode == HttpStatus.SC_OK)
					|| (mCode == HttpStatus.SC_CREATED);
		}
	}

	private Context mContext;

	private ArrayList<BasicNameValuePair> mParams;
	private HttpVerb mVerb;
	private Uri mAction;

	private HttpResponse mHttpResponse;
	private RestResponse mRestResponse;
	private HttpRequestBase mHttpRequest;
	private MultipartEntity mEntity;

	public RestService(Context context) {
		mContext = context;
	}

	public void setup(HttpVerb verb, Uri action, Bundle params) {
		mVerb = verb;
		mAction = action;
		extractParams(params);
	}

	public void setVerb(HttpVerb verb) {
		mVerb = verb;
	}

	public void setUri(String url) {
		mAction = Uri.parse(url);
	}

	public void setParams(ArrayList<BasicNameValuePair> params) {
		mParams = params;
	}

	public void setEntity(MultipartEntity entity) {
		mEntity = entity;
	}

	public RestResponse performRequest() {
		try {
			setupAdditionalParam();
			setRequest();
			executeRequest();
			parseResponse();
		} catch (Exception e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "Error while sending request " + verbToString(mVerb)
						+ ": " + mAction.toString(), e);
			}
		}

		return mRestResponse;
	}

	private void setupAdditionalParam() throws UnsupportedEncodingException {
		CurrentUser user = CurrentUser.load(mContext);
		if (user.api_key != null) {
			if (mEntity != null) {
				setupMultipartParams(user);
			} else {
				setupValuePairParams(user);
			}
		}
	}

	private void setupMultipartParams(CurrentUser user)
			throws UnsupportedEncodingException {
		StringBody apiKey = new StringBody(user.api_key);
		StringBody userId = new StringBody(Integer.toString(user.id));
		mEntity.addPart("api_key", apiKey);
		mEntity.addPart("user_id", userId);
	}

	private void setupValuePairParams(CurrentUser user) {
		if (mParams == null) {
			mParams = new ArrayList<BasicNameValuePair>();
		}
		mParams.add(new BasicNameValuePair("api_key", user.api_key));
		mParams.add(new BasicNameValuePair("user_id", Integer.toString(user.id)));
	}

	private void executeRequest() throws ParseException, IOException {
		HttpClient client = new DefaultHttpClient();
		mHttpResponse = client.execute(mHttpRequest);
	}

	private void parseResponse() throws ParseException, IOException {
		HttpEntity responseEntity = mHttpResponse.getEntity();
		StatusLine responseStatus = mHttpResponse.getStatusLine();
		int statusCode = responseStatus != null ? responseStatus
				.getStatusCode() : 0;
		String responseBody = responseEntity != null ? EntityUtils
				.toString(responseEntity) : null;

		mRestResponse = new RestResponse(responseBody, statusCode);
	}

	private void setRequest() throws UnsupportedEncodingException,
			URISyntaxException {
		switch (mVerb) {
		case GET:
			setGetRequest();
			break;

		case DELETE:
			setDeleteRequest();
			break;

		case POST:
			setPostRequest();
			break;

		case PUT:
			setPutRequest();
			break;
		}
	}

	private void setGetRequest() {
		mHttpRequest = new HttpGet();
		attachUriWithQuery();
	}

	private void setDeleteRequest() {
		mHttpRequest = new HttpDelete();
		attachUriWithQuery();
	}

	private void setPostRequest() throws UnsupportedEncodingException,
			URISyntaxException {
		mHttpRequest = new HttpPost();
		mHttpRequest.setURI(new URI(mAction.toString()));

		if (mParams != null) {
			HttpPost postRequest = (HttpPost) mHttpRequest;
			setEntity(postRequest);
		}
	}

	private void setPutRequest() throws URISyntaxException,
			UnsupportedEncodingException {
		mHttpRequest = new HttpPut();
		mHttpRequest.setURI(new URI(mAction.toString()));

		if (mParams != null || mEntity != null) {
			HttpPut putRequest = (HttpPut) mHttpRequest;
			setEntity(putRequest);
		}
	}

	private void setEntity(HttpEntityEnclosingRequestBase request)
			throws UnsupportedEncodingException {
		if (mEntity == null) {
			HttpEntity entity = new UrlEncodedFormEntity(mParams);
			request.setEntity(entity);
		} else {
			request.setEntity(mEntity);
		}
	}

	private void attachUriWithQuery() {
		try {
			if (mParams == null) {
				mHttpRequest.setURI(new URI(mAction.toString()));
			} else {
				Uri.Builder uriBuilder = mAction.buildUpon();

				for (BasicNameValuePair param : mParams) {
					uriBuilder.appendQueryParameter(param.getName(),
							param.getValue());
				}

				mAction = uriBuilder.build();
				mHttpRequest.setURI(new URI(mAction.toString()));
			}
		} catch (URISyntaxException e) {
			Log.e(TAG, "URI syntax was incorrect: " + mAction.toString());
		}
	}

	private static String verbToString(HttpVerb verb) {
		switch (verb) {
		case GET:
			return "GET";

		case POST:
			return "POST";

		case PUT:
			return "PUT";

		case DELETE:
			return "DELETE";
		}

		return "";
	}

	private void extractParams(Bundle args) {
		ArrayList<BasicNameValuePair> formList = new ArrayList<BasicNameValuePair>(
				args.size());

		for (String key : args.keySet()) {
			Object value = args.get(key);

			if (value != null)
				formList.add(new BasicNameValuePair(key, value.toString()));
		}

		mParams = formList;
	}

	public interface RestListener {
		public abstract void onResult(RestResponse response);
	}
}