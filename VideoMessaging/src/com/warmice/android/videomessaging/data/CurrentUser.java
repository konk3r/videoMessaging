package com.warmice.android.videomessaging.data;

import org.codehaus.jackson.map.ObjectMapper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;

import com.warmice.android.videomessaging.file.image.CurrentUserImage;
import com.warmice.android.videomessaging.util.BCrypt;

public class CurrentUser extends User {
	private static CurrentUser mUser;

	private static final String mPrefKey = "my_preferences";
	private static final String mApiKey = "api_key";
	private static final String mUserKey = "username";
	private static final String mPasswordKey = "password";
	private static final String mFirstNameKey = "first_name";
	private static final String mLastNameKey = "last_name";
	private static final String mImageUrlKey = "image_url";
	private static final String mIdKey = "id";
	private static final String mLastUpdateKey = "last_update";

	public String api_key;
	public String last_update;
	public String first_name;
	public String last_name;

	private String mPassword;

	public CurrentUser() {
	}
	
	@Override
	public String getName() {
		return first_name + " " + last_name;
	}

	public static CurrentUser load(Context context) {
		if (mUser == null) {
			loadUser(context);
		}
		return mUser;
	}

	private static CurrentUser loadUser(Context context) {
		SharedPreferences prefs = getPreferences(context);
		mUser = new CurrentUser();
		mUser.username = prefs.getString(mUserKey, null);
		mUser.mPassword = prefs.getString(mPasswordKey, null);
		mUser.api_key = prefs.getString(mApiKey, null);
		mUser.first_name = prefs.getString(mFirstNameKey, null);
		mUser.last_name = prefs.getString(mLastNameKey, null);
		mUser.image_url = prefs.getString(mImageUrlKey, null);
		mUser.id = prefs.getInt(mIdKey, -1);
		mUser.last_update = prefs.getString(mLastUpdateKey, null);
		return mUser;
	}

	public boolean isSignedIn() {
		return username != null;
	}

	public void setPassword(String password) {
		mPassword = password;
	}

	private void encryptPassword() {
		if (mPassword != null) {
			mPassword = BCrypt.hashpw(mPassword, BCrypt.gensalt());
		}
	}

	public boolean checkPassword(String password) {
		return BCrypt.checkpw(password, mPassword);
	}

	private static Editor getEditor(Context context) {
		SharedPreferences prefs = getPreferences(context);
		return prefs.edit();
	}

	private static SharedPreferences getPreferences(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(mPrefKey,
				Context.MODE_PRIVATE);
		return prefs;
	}

	public void signOut(Context context) {
		wipeValues();
		store(context);
	}

	private void wipeValues() {
		username = null;
		mPassword = null;
		api_key = null;
		id = -1;
		first_name = null;
		last_name = null;
		image_url = null;
		last_update = null;
	}

	public void store(Context context) {
		new SaveUserTask().execute(context);
		if (isSignedIn()) {
			mUser = this;
		}
	}

	class SaveUserTask extends AsyncTask<Context, Void, Void> {

		@Override
		protected Void doInBackground(Context... params) {
			Context context = params[0];
			encryptPassword();
			SharedPreferences.Editor editor = getEditor(context);
			editor.putString(mUserKey, username);
			editor.putString(mPasswordKey, mPassword);
			editor.putString(mApiKey, api_key);
			editor.putString(mFirstNameKey, first_name);
			editor.putString(mLastNameKey, last_name);
			editor.putString(mImageUrlKey, image_url);
			editor.putInt(mIdKey, id);
			editor.putString(mLastUpdateKey, last_update);
			editor.commit();
			
			CurrentUserImage image = new CurrentUserImage(context);
			image.delete();

			return null;
		}
	}

	public void storeUpdate(Context context, String json) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final CurrentUser updatedUser = mapper.readValue(json, CurrentUser.class);

			image_url = updatedUser.image_url;
			first_name = updatedUser.first_name;
			last_name = updatedUser.last_name;
			store(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void storeNewUser(Context context, String json, String password) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final CurrentUser user = mapper.readValue(json, CurrentUser.class);
			user.setPassword(password);
			user.store(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
