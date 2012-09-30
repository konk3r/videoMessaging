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

import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.provider.MessagingContract.Contacts;
import com.warmice.android.videomessaging.ui.actionbar.ActionBarActivity;
import com.warmice.android.videomessaging.ui.adapter.ConversationAdapter;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ConversationsActivity extends ActionBarActivity implements OnItemClickListener {
	private ListView mList;
	private Cursor mCursor;
	private ConversationAdapter mAdapter;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_conversations);
        
        refreshCursor();
        initializeList();
    }

	private void refreshCursor() {
		final ContentResolver resolver = getContentResolver();
		final Uri uri = Contacts.CONTENT_URI;
		mCursor = resolver.query(uri, null, null, null, null);
	}

	private void initializeList() {
		mList = (ListView) findViewById(R.id.message_list);
		mAdapter = new ConversationAdapter(this, mCursor);
		
		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(this);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.conversations, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
	}

	private void addNewMessage() {
		//TODO this will be changed to start a contact list activity
		promptForUserName();
	}

	private void promptForUserName() {
		Dialog newContactDialog = createDialog();
		newContactDialog.show();
	}

	private Dialog createDialog() {
		Context mContext = getApplicationContext();
		Dialog dialog = new Dialog(mContext);

		dialog.setContentView(R.layout.new_user_dialog);
		dialog.setTitle("Enter contact name");
		return dialog;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		startVideoActivity(position);
	}

	private void startVideoActivity(int conversationPosition) {
		Intent intent = new Intent(this, MessagesActivity.class);
		intent = mAdapter.setupMessagesIntent(intent, conversationPosition);
		
		startActivity(intent);
	}
    
}
