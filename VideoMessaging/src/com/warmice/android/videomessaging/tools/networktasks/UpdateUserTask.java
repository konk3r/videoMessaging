package com.warmice.android.videomessaging.tools.networktasks;

import ch.boye.httpclientandroidlib.entity.mime.HttpMultipartMode;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntity;
import ch.boye.httpclientandroidlib.entity.mime.content.FileBody;
import ch.boye.httpclientandroidlib.entity.mime.content.StringBody;

import com.warmice.android.videomessaging.BuildConfig;
import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.file.image.CurrentUserImage;

import android.content.Context;
import android.util.Log;

import com.warmice.android.videomessaging.tools.networktasks.RestService.*;

public class UpdateUserTask extends RestTask {
	private static final String TAG = "rest task";

	Context mContext;
	MultipartEntity mEntity;
	boolean hasNewPicture;

	public UpdateUserTask(Context context) {
		super(context, HttpVerb.PUT);
		mContext = context;
		mEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		hasNewPicture = false;
	}

	public void setFirstName(String name) {
		try {
			StringBody body = new StringBody(name);
			mEntity.addPart("first_name", body);
		} catch (Exception e) {
		}
	}

	public void setLastName(String name) {
		try {
			StringBody body = new StringBody(name);
			mEntity.addPart("last_name", body);
		} catch (Exception e) {
		}
	}

	public void setNewPicture() {
		hasNewPicture = true;
	}

	@Override
	protected void onPreExecute() {
		String createAccountUrl = formUrl();
		setUri(createAccountUrl);

		super.onPreExecute();
	}

	private String formUrl() {
		String url = mContext.getString(R.string.url_update_user);
		return url;
	}

	@Override
	protected RestResponse doInBackground(Void... params) {
		loadFileEntity();
		setEntity(mEntity);
		return super.doInBackground(params);
	}

	private void loadFileEntity() {
		if (hasNewPicture) {
			try {
				CurrentUserImage image = new CurrentUserImage(mContext);
				FileBody body = image.getFileBody();
				mEntity.addPart("image", body);
			} catch (Exception e) {
				if (BuildConfig.DEBUG) {
					Log.e(TAG, e.getMessage());
				}
			}
		}
	}

	@Override
	protected void onPostExecute(RestResponse result) {
		String json = result.getData();
	}

}
