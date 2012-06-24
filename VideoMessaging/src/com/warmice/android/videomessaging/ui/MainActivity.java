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
import com.warmice.android.videomessaging.ui.actionbar.ActionBarActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

public class MainActivity extends ActionBarActivity {
	final static int ACTION_TAKE_VIDEO = 1;
	final static String EXTRA_URI = "extra_uri";
	final static String EXTRA_POSITION = "extra_position";

	private VideoView mVideoView;
	private Uri mVideoUri;
	private int mVideoPosition;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null)
			restoreInstanceState(savedInstanceState);

		if (hasNativeActionBar()) {
			getActionBar().setHomeButtonEnabled(true);
		}

		setContentView(R.layout.activity_show_video);
		storeViews();
		setupVideoView();
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
	}

	private boolean hasNativeActionBar() {
		return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB);
	}

	private void setupVideoView() {
		mVideoView.setMediaController(new MediaController(this));
		mVideoView.requestFocus();
		if (mVideoUri != null)
			mVideoView.setVideoURI(mVideoUri);
	}

	private void restoreInstanceState(Bundle savedInstanceState) {
		mVideoUri = (Uri) savedInstanceState.getParcelable(EXTRA_URI);
		mVideoPosition = savedInstanceState.getInt(EXTRA_POSITION);
	}

	private void storeViews() {
		mVideoView = (VideoView) findViewById(R.id.video_view);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.camera_menu, menu);

		// Calling super after populating the menu is necessary here to ensure
		// that the
		// action bar helpers have a chance to handle this event.
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.switch_cam:
			dispatchTakeVideoIntent();
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	private void dispatchTakeVideoIntent() {
		Intent videoCaptureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		Uri fileUri = buildFileUri();
		
		videoCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		startActivityForResult(videoCaptureIntent, ACTION_TAKE_VIDEO);
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

		if (requestCode == ACTION_TAKE_VIDEO) {
			handleVideoResult(data);
		}
	}

	private void handleVideoResult(Intent data) {
		if (data != null) {
			handleCameraVideo(data);
			mVideoView.seekTo(0);
		}
	}

	private void handleCameraVideo(Intent intent) {
		if (intent != null) {
			final Uri returnedUri = intent.getData();
			if (returnedUri != null) {
				mVideoPosition = 0;
				mVideoUri = returnedUri;
			}
			mVideoView.setVideoURI(mVideoUri);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(EXTRA_URI, mVideoUri);
		outState.putInt(EXTRA_POSITION, mVideoPosition);
	}

}
