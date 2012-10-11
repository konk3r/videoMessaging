package com.warmice.android.videomessaging.file.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import ch.boye.httpclientandroidlib.entity.mime.content.FileBody;

import com.warmice.android.videomessaging.data.CurrentUser;

public class FileImage extends Image {

	public FileImage(Context context) {
		super(context);
	}

	@Override
	protected String buildFileName() {
		CurrentUser user = CurrentUser.load(mContext);
		String fileName = String.format("/avt%s.jpg", user.id);
		return fileName;
	}

	public FileBody getFileBody() {
		if (mFile == null) {
			buildImageFile();
		}
		FileBody body = new FileBody(mFile, "image/jpeg");
		
		return body;
	}

	@Override
	protected Bitmap decodeImage() {
		return BitmapFactory.decodeFile(mFile.getPath(), mOptions);
	}

	@Override
	protected void onPreLoad() {
		buildImageFile();
	}

	@Override
	protected boolean resourceExists() {
		if (mFile.exists()) {
			return true;
		} else {
			return false;
		}
	}
}
