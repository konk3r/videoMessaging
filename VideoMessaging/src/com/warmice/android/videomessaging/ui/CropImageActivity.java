package com.warmice.android.videomessaging.ui;

import com.actionbarsherlock.app.SherlockActivity;
import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.file.image.FileImage;
import com.warmice.android.videomessaging.file.image.Image;
import com.warmice.android.videomessaging.ui.widget.CropImageView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class CropImageActivity extends SherlockActivity {

	Uri mImageUri;
	Bitmap mBitmap;
	CropImageView mBackground;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hideActionBar();
		loadData();
		loadViews();
	}

	private void loadData() {
		Intent intent = getIntent();
		mImageUri = intent.getData();
	}

	private void hideActionBar() {
		getSupportActionBar().hide();
	}

	private void loadViews() {
		setContentView(R.layout.activity_crop_image);
		mBackground = (CropImageView) findViewById(R.id.background);
		mBackground.setBitmap(this, mImageUri);
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.crop_image:
			cropImage();
			break;
		}
	}

	private void cropImage() {
		setResult(RESULT_OK);
		Bitmap bitmap = mBackground.getCroppedBitmap();
		saveImage(bitmap);
		finish();
	}

	private void saveImage(Bitmap bitmap) {
		Context applicationContext = getApplicationContext();
		Image image = new FileImage(applicationContext);
		image.store(bitmap);
	}
}
