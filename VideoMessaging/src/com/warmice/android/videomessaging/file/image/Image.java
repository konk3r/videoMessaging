package com.warmice.android.videomessaging.file.image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.warmice.android.videomessaging.BuildConfig;

public abstract class Image {
	private static final String TAG = "File Manager";
	private static final int JPEG_QUALITY = 75;

	protected Context mContext;
	protected BitmapFactory.Options mOptions;
	protected int mWidth;
	protected int mHeight;
	protected Bitmap mBitmap;
	protected File mFile;

	public Image(Context context) {
		mContext = context;
	}

	public void setDimens(int width, int height) {
		mWidth = width;
		mHeight = height;
	}

	public final void load() {
		onPreLoad();
		if (resourceExists()) {
			setFileImageOptions();
			mBitmap = decodeImage();
		}
	}

	protected boolean resourceExists() {
		return true;
	}

	public Bitmap getBitmap() {
		return mBitmap;
	}

	public void setFileImageOptions() {
		createOptions();
		decodeImage();
		setImageScale();
		setupOptionsForDecoding();
	}

	private void setupOptionsForDecoding() {
		mOptions.inJustDecodeBounds = false;
	}

	private void createOptions() {
		mOptions = new BitmapFactory.Options();
		mOptions.inJustDecodeBounds = true;
	}

	private void setImageScale() {
		int imageHeight = mOptions.outHeight;
		int imageWidth = mOptions.outWidth;
		float imageScaleRatio = 1;

		if (imageHeight > mHeight || imageWidth > mWidth) {
			if (imageWidth > imageHeight) {
				imageScaleRatio = (float) imageHeight / (float) mHeight;
			} else {
				imageScaleRatio = (float) imageWidth / (float) mWidth;
			}
		}

		mOptions.inSampleSize = Math.round(imageScaleRatio);
	}

	public void store(Bitmap bitmap) {
		mBitmap = bitmap;
		store();
	}

	protected void buildImageFile() {
		String path = buildFilePath();
		String fileName = buildFileName();
		mFile = new File(path, fileName);
	}

	private String buildFilePath() {
		String path = Environment.getExternalStorageDirectory().toString();
		path += "/sMessage";
		return path;
	}

	public void store() {
		try {
			buildImageFile();
			createFileAndWriteData();
		} catch (Exception e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, e.getMessage());
			}
		}
	}

	private void createFileAndWriteData() throws IOException {
		mFile.getParentFile().mkdirs();
		OutputStream fOut = null;
		fOut = new FileOutputStream(mFile);
		mBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, fOut);
		fOut.flush();
		fOut.close();
	}

	protected void onPreLoad() {
	}

	protected abstract String buildFileName();

	protected abstract Bitmap decodeImage();

}
