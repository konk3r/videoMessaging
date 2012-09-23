package com.warmice.android.videomessaging.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;

import com.warmice.android.videomessaging.VideoApplication;
import com.warmice.android.videomessaging.provider.MessagingContract.VideoColumns;
import com.warmice.android.videomessaging.provider.MessagingContract.Videos;
import com.warmice.android.videomessaging.tools.DataUtils;

public class Video {

	private static final String TAG = "Video";

	private String mUserId;
	private Uri mVideoUri;
	private String mDate;

	private String mThumbnailFileName;

	private Activity mActivity;

	public Video(Uri videoUri, String userId) {
		mUserId = userId;
		mVideoUri = videoUri;
		mDate = DataUtils.createCurrentDate();
	}

	public void store(Activity activity) {
		mActivity = activity;
		setupThumbnail();

		final ContentResolver resolver = activity.getContentResolver();
		final ContentValues values = createValues();
		final Uri userUri = resolver.insert(Videos.CONTENT_URI, values);

		if (VideoApplication.IS_DEBUGGABLE)
			Log.d(TAG, userUri.toString());

	}

	private void setupThumbnail() {
		setupThumbnailPath();
		Bitmap thumbnail = createThumbnail();
		storeThumbnail(thumbnail);
	}

	private void setupThumbnailPath() {
		final String formattedDate = mDate.replaceAll(" ", "_").replaceAll(":",
				"_");
		StringBuilder builder = new StringBuilder(mUserId)
				.append(formattedDate).append(".jpg");
		mThumbnailFileName = builder.toString();
	}

	private void storeThumbnail(Bitmap thumbnail) {
		try {
			OutputStream fOut = null;
			File file = new File(DataUtils.getBaseDirectory(),
					mThumbnailFileName);
			createDirectory(file);

			fOut = new FileOutputStream(file);
			thumbnail.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createDirectory(File file) {
		if ((!file.exists())) {
			if (!file.mkdirs()) {
				Log.e(TAG, "FUUUUCK");
			}
		}
		file.delete();
	}

	private Bitmap createThumbnail() {
		String videoPath = getVideoFilePath();
		return ThumbnailUtils.createVideoThumbnail(videoPath,
				Thumbnails.MICRO_KIND);
	}

	private ContentValues createValues() {
		final ContentValues values = new ContentValues();
		final String thumbnailFilePath = DataUtils
				.getFilePath(mThumbnailFileName);

		values.put(VideoColumns.VIDEO_DATE, mDate);
		values.put(VideoColumns.VIDEO_URI, mVideoUri.toString());
		values.put(VideoColumns.THUMBNAIL_FILE_PATH, thumbnailFilePath);
		values.put(VideoColumns.USER_ID, mUserId);

		return values;
	}

	@SuppressWarnings("deprecation")
	private String getVideoFilePath() {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = mActivity.managedQuery(mVideoUri, proj, null, null,
				null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
}
