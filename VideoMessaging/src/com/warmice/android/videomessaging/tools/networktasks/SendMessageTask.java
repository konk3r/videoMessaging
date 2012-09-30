package com.warmice.android.videomessaging.tools.networktasks;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import com.warmice.android.videomessaging.R;

import android.content.Context;

import com.warmice.android.videomessaging.data.Message;
import com.warmice.android.videomessaging.tools.networktasks.RestService.*;

public class SendMessageTask extends RestTask {

	Context mContext;
	Message mMessage;
	
	public SendMessageTask(Context context) {
		super(context, HttpVerb.POST);
		
		mContext = context;
		String url = mContext.getString(R.string.url_send_message);
		setUri(url);
	}
	
	public void setMessage(Message message){
		mMessage = message;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		setupAdditionalParameters();
	}

	private void setupAdditionalParameters() {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("sent_at", mMessage.sent_at));
		params.add(new BasicNameValuePair("contact_id", Integer.toString(mMessage.receiver_id)));
		params.add(new BasicNameValuePair("text", mMessage.text));
		params.add(new BasicNameValuePair("message_type", mMessage.message_type));
		
		setParams(params);
	}

	@Override
	protected void onPostExecute(RestResponse result) {
		String json = result.getData();
		Message.updateMessage(mContext, json);
	}

}
