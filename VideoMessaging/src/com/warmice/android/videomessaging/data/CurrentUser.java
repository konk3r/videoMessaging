package com.warmice.android.videomessaging.data;

import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.util.BCrypt;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;

public class CurrentUser extends User {
	private static CurrentUser mUser;

	private static String mPrefKey;
	private static String mApiKey;
	private static String mUserKey;
	private static String mPasswordKey;
	private static String mNameKey;
	private static String mIdKey;
	private static String mLastUpdateKey;

	public String api_key;
	public String last_update;

	private String mPassword;

	public CurrentUser() {
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
		mUser.name = prefs.getString(mNameKey, null);
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
		if (mPrefKey == null) {
			loadKeys(context);
		}
		SharedPreferences prefs = context.getSharedPreferences(mPrefKey,
				Context.MODE_PRIVATE);
		return prefs;
	}

	private static void loadKeys(Context context) {
		mPrefKey = context.getString(R.string.preferences);
		mApiKey = context.getString(R.string.preference_api_key);
		mUserKey = context.getString(R.string.preference_username);
		mPasswordKey = context.getString(R.string.preference_password);
		mNameKey = context.getString(R.string.preference_full_name);
		mIdKey = context.getString(R.string.preference_id);
		mLastUpdateKey = context.getString(R.string.preference_last_update);
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
		name = null;
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
			encryptPassword();
			SharedPreferences.Editor editor = getEditor(params[0]);
			editor.putString(mUserKey, username);
			editor.putString(mPasswordKey, mPassword);
			editor.putString(mApiKey, api_key);
			editor.putString(mNameKey, name);
			editor.putInt(mIdKey, id);
			editor.putString(mLastUpdateKey, last_update);
			editor.commit();

			return null;
		}
	}
}
