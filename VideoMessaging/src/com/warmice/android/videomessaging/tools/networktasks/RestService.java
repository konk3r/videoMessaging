package com.warmice.android.videomessaging.tools.networktasks;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.warmice.android.videomessaging.BuildConfig;
import com.warmice.android.videomessaging.data.User;

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
	
	public RestService(Context context){
		mContext = context;
	}

	public void setup(HttpVerb verb, Uri action, Bundle params){
		mVerb = verb;
		mAction = action;
		extractParams(params);
	}
	
	public void setVerb(HttpVerb verb){
		mVerb = verb;
	}

	public void setUri(String url) {
		mAction = Uri.parse(url);
	}

	public void setParams(ArrayList<BasicNameValuePair> params) {
		mParams = params;
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

	private void setupAdditionalParam() {
		
		User user = User.load(mContext);
		if (user.api_key != null) {

			if (mParams == null){
				mParams = new ArrayList<BasicNameValuePair>();
			}
			mParams.add(new BasicNameValuePair("api_key", user.api_key));
			mParams.add(new BasicNameValuePair("user_id", Integer.toString(user.id)));
		}
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

		HttpPost postRequest = (HttpPost) mHttpRequest;

		if (mParams != null) {
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(mParams);
			postRequest.setEntity(formEntity);
		}
	}

	private void setPutRequest() throws URISyntaxException,
			UnsupportedEncodingException {
		mHttpRequest = new HttpPut();
		mHttpRequest.setURI(new URI(mAction.toString()));

		HttpPut putRequest = (HttpPut) mHttpRequest;

		if (mParams != null) {
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(mParams);
			putRequest.setEntity(formEntity);
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

	public interface RestListener{
		public abstract void onResult(RestResponse response);
	}
}