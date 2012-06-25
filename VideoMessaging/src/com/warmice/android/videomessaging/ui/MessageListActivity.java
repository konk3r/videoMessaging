/*
 * Copyright 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.warmice.android.videomessaging.ui;

import org.apache.http.message.BasicNameValuePair;

import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.VideoApplication;
import com.warmice.android.videomessaging.provider.MessagingContract.UserColumns;
import com.warmice.android.videomessaging.provider.MessagingContract.Users;
import com.warmice.android.videomessaging.ui.actionbar.ActionBarActivity;
import com.warmice.android.videomessaging.ui.adapter.MessageListAdapter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

public class MessageListActivity extends ActionBarActivity {
	private static final String TAG = "MessagingListActivity";
	private ListView mList;
	private Cursor mCursor;
	private MessageListAdapter mAdapter;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_message_list);
        
        refreshCursor();
        initializeList();
    }

	private void initializeList() {
		mList = (ListView) findViewById(R.id.message_list);
		mAdapter = new MessageListAdapter(this, mCursor);
		
		mList.setAdapter(mAdapter);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.message_list, menu);
        
        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.menu_new_message:
        	addNewMessage();
        	refreshCursor();
        	updateListAdapter();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

	private void updateListAdapter() {
		mAdapter.changeCursor(mCursor);
//		mAdapter.notifyDataSetChanged();
	}

	private void refreshCursor() {
		final ContentResolver resolver = getContentResolver();
		final Uri uri = Users.CONTENT_URI;
		mCursor = resolver.query(uri, null, null, null, null);
	}

	private void addNewMessage() {
		ContentResolver resolver = getContentResolver();
		Uri uri = Users.CONTENT_URI;
		ContentValues values = new ContentValues();
		values.put(UserColumns.USER_ID, "other sharon");
		values.put(UserColumns.USER_NAME, "Poli Love");
		
		Uri userUri = resolver.insert(uri, values);
		
		if (VideoApplication.IS_DEBUGGABLE) Log.d(TAG, userUri.toString());
		
	}
    
}
