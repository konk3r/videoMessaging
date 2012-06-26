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
import java.text.SimpleDateFormat;
import java.util.Date;

import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.VideoApplication;
import com.warmice.android.videomessaging.provider.MessagingContract.Users;
import com.warmice.android.videomessaging.provider.MessagingContract.VideoColumns;
import com.warmice.android.videomessaging.provider.MessagingContract.Videos;
import com.warmice.android.videomessaging.ui.actionbar.ActionBarActivity;
import com.warmice.android.videomessaging.ui.adapter.MessageListAdapter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MessagesActivity extends ActionBarActivity implements OnItemClickListener {
	private static final String TAG = "MessagingListActivity";

	public final static String EXTRA_USER_URI = "extra_video_uri";
	private final static int REQUEST_RECORD_VIDEO = 0;
	
	private ListView mList;
	private Cursor mCursor;
	private MessageListAdapter mAdapter;
	
	private Uri mUserUri;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        pullUserUri();
        refreshCursor();
    	initializeList();
    }

	private void pullUserUri() {
        final Intent intent = getIntent();
        final Bundle extras = intent.getExtras();
        
        mUserUri = (Uri) extras.get(EXTRA_USER_URI);
		
	}

	private void initializeList() {
		mList = (ListView) findViewById(R.id.message_list);
		mAdapter = new MessageListAdapter(this, mCursor);
		
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
		final String userId = Users.getUserId(mUserUri);
		final Uri videoUri = Users.buildVideosUri(userId);
		final ContentResolver resolver = getContentResolver();
		
		mCursor = resolver.query(videoUri, null, null, null, null);
	}

	private void updateListAdapter() {
		mAdapter.changeCursor(mCursor);
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Uri videoUri = mAdapter.getVideoUri(position);
		startVideoActivity(videoUri);
	}

	private void startVideoActivity(Uri videoUri) {
		Intent intent = new Intent(this, VideoActivity.class);
		intent.putExtra(VideoActivity.EXTRA_VIDEO_URI, videoUri);
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

	private String createCurrentDate() {
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		final Date date = new Date();
		final String formattedDate = dateFormat.format(date);
		return formattedDate;
	}

	private void storeUri(Intent intent) {
		final Uri returnedUri = intent.getData();
		if (returnedUri != null) {
			final ContentResolver resolver = getContentResolver();
			final ContentValues values = createOutboundVideoValues(returnedUri);
			final Uri userUri = resolver.insert(Videos.CONTENT_URI, values);
			
			if (VideoApplication.IS_DEBUGGABLE) Log.d(TAG, userUri.toString());
		}
		
	}

	private ContentValues createOutboundVideoValues(Uri uri) {
		final ContentValues values = new ContentValues();
		values.put(VideoColumns.VIDEO_DATE, createCurrentDate());
		values.put(VideoColumns.VIDEO_FILE_PATH, uri.toString());
		values.put(VideoColumns.USER_ID, Users.getUserId(mUserUri));
		
		return values;
	}
	
    
}
