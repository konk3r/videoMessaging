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
import com.warmice.android.videomessaging.ui.actionbar.ActionBarActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends ActionBarActivity {
	public final static int ACTION_TAKE_VIDEO = 1;
	
	public final static String EXTRA_VIDEO_URI = "extra_video_uri";
	public final static String EXTRA_DATE = "extra_video_date";
	public final static String EXTRA_POSITION = "extra_position";

	private VideoView mVideoView;
	private Uri mVideoUri;
	private String mDate;
	private int mVideoPosition;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			restoreInstanceState(savedInstanceState);
		} else {
			extractDataFromExtras();
		}

		if (hasNativeActionBar()) {
			getActionBar().setHomeButtonEnabled(true);
		}
		setTitle(mDate);
		setContentView(R.layout.activity_show_video);
		storeViews();
		setupVideoView();
	}

	private void extractDataFromExtras() {
		final Intent intent = getIntent();
		final Bundle extras = intent.getExtras();
		
		mVideoUri = (Uri) extras.get(EXTRA_VIDEO_URI);
		mDate = (String) extras.get(EXTRA_DATE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveVideoProgress();
	}

	@Override
	protected void onResume() {
		super.onResume();
		restoreVideoProgress();
	}

	private void saveVideoProgress() {
		mVideoPosition = mVideoView.getCurrentPosition();
	}

	private void restoreVideoProgress() {
		mVideoView.seekTo(mVideoPosition);
		mVideoView.resume();
	}

	private boolean hasNativeActionBar() {
		return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB);
	}

	private void setupVideoView() {
		mVideoView.setMediaController(new MediaController(this));
		if (mVideoUri != null) {
			mVideoView.setVideoURI(mVideoUri);
		}
	}

	private void restoreInstanceState(Bundle savedInstanceState) {
		mVideoUri = (Uri) savedInstanceState.getParcelable(EXTRA_VIDEO_URI);
		mVideoPosition = savedInstanceState.getInt(EXTRA_POSITION);
		mDate = savedInstanceState.getString(EXTRA_DATE);
	}

	private void storeViews() {
		mVideoView = (VideoView) findViewById(R.id.video_view);
	}
	
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater menuInflater = getMenuInflater();
//
//		// Calling super after populating the menu is necessary here to ensure
//		// that the
//		// action bar helpers have a chance to handle this event.
//		return super.onCreateOptionsMenu(menu);
//	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(EXTRA_VIDEO_URI, mVideoUri);
		outState.putInt(EXTRA_POSITION, mVideoPosition);
		outState.putString(EXTRA_DATE, mDate);
	}

}
