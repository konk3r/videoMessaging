package com.warmice.android.videomessaging.file.image;

import java.io.InputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.warmice.android.videomessaging.data.CurrentUser;

public class ResourceImage extends Image {
	private Uri mUri;

	public ResourceImage(Context context) {
		super(context);
	}

	public void setUri(Uri uri) {
		mUri = uri;
	}

	public void load(Uri uri) {
		setUri(uri);
		super.load();
	}

	@Override
	protected String buildFileName() {
		CurrentUser user = CurrentUser.load(mContext);
		String fileName = String.format("/avt%s.jpg", user.id);
		return fileName;
	}

	@Override
	protected Bitmap decodeImage() {
		try {
			ContentResolver cr = mContext.getContentResolver();
			InputStream is;
			is = cr.openInputStream(mUri);
			return BitmapFactory.decodeStream(is, null, mOptions);
		} catch (Exception e) {
			return null;
		}
	}
}
