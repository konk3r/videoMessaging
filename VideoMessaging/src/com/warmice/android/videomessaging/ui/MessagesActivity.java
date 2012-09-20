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

import java.io.File;
import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.provider.MessagingContract.Users;
import com.warmice.android.videomessaging.tools.Video;
import com.warmice.android.videomessaging.ui.actionbar.ActionBarActivity;
import com.warmice.android.videomessaging.ui.adapter.MessageAdapter;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MessagesActivity extends ActionBarActivity implements OnItemClickListener {
	@SuppressWarnings("unused")
	private static final String TAG = "MessagingListActivity";

	public final static String EXTRA_USER_ID = "extra_user_id";
	public final static String EXTRA_USER_NAME = "extra_user_name";
	private final static int REQUEST_RECORD_VIDEO = 0;
	
	private ListView mList;
	private Cursor mCursor;
	private MessageAdapter mAdapter;
	
	private String mUserId;
	private String mUserName;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        extractContentFromBundle();
        
        setTitle(mUserName);
        
        refreshCursor();
    	initializeList();
    }

	private void extractContentFromBundle() {
        final Intent intent = getIntent();
        final Bundle extras = intent.getExtras();
        
        mUserId = (String) extras.get(EXTRA_USER_ID);
        mUserName = (String) extras.get(EXTRA_USER_NAME);
		
	}

	private void initializeList() {
		mList = (ListView) findViewById(R.id.message_list);
		mAdapter = new MessageAdapter(this, mCursor);
		
		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(this);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.messages, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.record:
        	dispatchRecordVideoIntent();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

	private void refreshCursor() {
		final Uri videoUri = Users.buildVideosUri(mUserId);
		final ContentResolver resolver = getContentResolver();
		
		mCursor = resolver.query(videoUri, null, null, null, null);
	}

	private void updateListAdapter() {
		mAdapter.changeCursor(mCursor);
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		startVideoActivity(position);
	}

	private void startVideoActivity(int messagePosition) {
		Intent intent = new Intent(this, VideoActivity.class);
		intent = mAdapter.setupStartVideoIntent(intent, messagePosition);
		startActivity(intent);
	}

	private void dispatchRecordVideoIntent() {
		Intent videoCaptureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		Uri fileUri = buildFileUri();
		
		videoCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		startActivityForResult(videoCaptureIntent, REQUEST_RECORD_VIDEO);
	}

	private Uri buildFileUri() {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		path += "/ourVideos/myVideo.mp4";
		
		File file = new File(path);
		return Uri.fromFile(file);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == REQUEST_RECORD_VIDEO) {
			handleVideoResult(data);
		}
	}


	private void handleVideoResult(Intent data) {
		if (data != null) {
			storeUri(data);
	        refreshCursor();
			updateListAdapter();
		}
	}

	private void storeUri(Intent intent) {
		final Uri returnedUri = intent.getData();
		if (returnedUri != null) {
			Video video = new Video(returnedUri, mUserId);
			video.store(this);
		}
		
	}
	
    
}
