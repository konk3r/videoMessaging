package com.warmice.android.videomessaging.data;

import com.warmice.android.videomessaging.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;

public class User {

	private static String mPrefKey;
	private static String mApiKey;
	private static String mUserKey;
	private static String mNameKey;
	private static String mIdKey;

	public String username;
	public String api_key;
	public int id;
	public String name;

	public User() {
	}

	public void save(Context context) {
		new SaveUserTask().execute(context);
	}

	public static User load(Context context) {
		SharedPreferences prefs = getPreferences(context);
		User user = new User();
		user.username = prefs.getString(mUserKey, null);
		user.api_key = prefs.getString(mApiKey, null);
		user.name = prefs.getString(mNameKey, null);
		user.id = prefs.getInt(mIdKey, -1);

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
	}

	public void signOut(Context context) {
		wipeValues();
		save(context);
	}

	private void wipeValues() {
		username = null;
		api_key = null;
		id = -1;
		name = null;
	}

	public boolean isSignedIn() {
		return username != null;
	}
	
	class SaveUserTask extends AsyncTask<Context, Void, Void>{

		@Override
		protected Void doInBackground(Context... params) {
			SharedPreferences.Editor editor = getEditor(params[0]);

			editor.putString(mUserKey, username);
			editor.putString(mApiKey, api_key);
			editor.putString(mNameKey, name);
			editor.putInt(mIdKey, id);
			editor.commit();
			
			return null;
		}
		
	}
}
