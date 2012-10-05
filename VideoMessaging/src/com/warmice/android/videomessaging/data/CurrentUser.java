package com.warmice.android.videomessaging.data;

import com.warmice.android.videomessaging.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;

public class CurrentUser extends User {

	private static String mPrefKey;
	private static String mApiKey;
	private static String mUserKey;
	private static String mNameKey;
	private static String mIdKey;
	private static String mLastUpdateKey;

	public String api_key;
	public String last_update;

	public CurrentUser() {
	}

	public boolean isSignedIn() {
		return username != null;
	}

	public static CurrentUser load(Context context) {
		SharedPreferences prefs = getPreferences(context);
		CurrentUser user = new CurrentUser();
		user.username = prefs.getString(mUserKey, null);
		user.api_key = prefs.getString(mApiKey, null);
		user.name = prefs.getString(mNameKey, null);
		user.id = prefs.getInt(mIdKey, -1);
		user.last_update = prefs.getString(mLastUpdateKey, null);
		return user;
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
		api_key = null;
		id = -1;
		name = null;
		last_update = null;
	}

	public void store(Context context) {
		new SaveUserTask().execute(context);
	}
	
	class SaveUserTask extends AsyncTask<Context, Void, Void>{

		@Override
		protected Void doInBackground(Context... params) {
			SharedPreferences.Editor editor = getEditor(params[0]);

			editor.putString(mUserKey, username);
			editor.putString(mApiKey, api_key);
			editor.putString(mNameKey, name);
			editor.putInt(mIdKey, id);
			editor.putString(mLastUpdateKey, last_update);
			editor.commit();
			
			return null;
		}
		
	}
}
