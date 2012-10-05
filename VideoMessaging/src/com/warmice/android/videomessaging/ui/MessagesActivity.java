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
import com.warmice.android.videomessaging.data.Message;
import com.warmice.android.videomessaging.data.CurrentUser;
import com.warmice.android.videomessaging.provider.MessagingContract.Contacts;
import com.warmice.android.videomessaging.tools.networktasks.SendMessageTask;
import com.warmice.android.videomessaging.ui.actionbar.ActionBarActivity;
import com.warmice.android.videomessaging.ui.adapter.MessageAdapter;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

public class MessagesActivity extends ActionBarActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {
	public final static String EXTRA_CONTACT_ID = "extra_user_id";
	public final static String EXTRA_USERNAME = "extra_user_name";

	private ListView mList;
	private MessageAdapter mAdapter;
	private EditText mInput;

	private Integer mContactId;
	private String mUserName;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		extractContentFromBundle();
		initializeViews();
		initializeList();
	}

	private void initializeViews() {
		setContentView(R.layout.activity_messages);
		setTitle(mUserName);
		mList = (ListView) findViewById(R.id.message_list);
		mInput = (EditText) findViewById(R.id.input_text);
	}

	private void initializeList() {
		mAdapter = new MessageAdapter(this, null);
		mList.setAdapter(mAdapter);
		getSupportLoaderManager().initLoader(0, null, this);
	}

	private void extractContentFromBundle() {
		final Intent intent = getIntent();

		mContactId = intent.getIntExtra(EXTRA_CONTACT_ID, 0);
		mUserName = intent.getStringExtra(EXTRA_USERNAME);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.messages, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri uri = Contacts.buildMessagesUri(mContactId.toString());
		CursorLoader loader = new CursorLoader(this, uri, null, null, null,
				null);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.send:
			if (!fieldIsEmpty(mInput)) {
				sendMessage();
				mInput.setText("");
			}
			break;
		}
	}

	private void sendMessage() {
		Message message = pullNewMessageFromInput();
		message.store(this);
		
		SendMessageTask task = new SendMessageTask(this);
		task.setMessage(message);
		task.execute();
	}

	private Message pullNewMessageFromInput() {
		Message message = new Message();
		message.text = getTrimmedText(mInput);
		message.sender_id = CurrentUser.load(this).id;
		message.receiver_id = mContactId;
		message.message_type = Message.TYPE_TEXT;
		return message;
	}
}
