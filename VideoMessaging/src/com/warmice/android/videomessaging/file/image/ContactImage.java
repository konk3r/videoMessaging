package com.warmice.android.videomessaging.file.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import ch.boye.httpclientandroidlib.entity.mime.content.FileBody;

import com.warmice.android.videomessaging.data.Contact;
import com.warmice.android.videomessaging.tools.networktasks.ImageDownloader;

public class ContactImage extends Image {
	private int mContactId;

	public ContactImage(Context context, int contactId) {
		super(context);
		mContactId = contactId;
	}

	@Override
	protected void onLoadFailed() {
		downloadImage();
	}

	private void downloadImage() {
		Contact contact = Contact.load(mContext, mContactId);
		if (contact.image_url != null) {
			ImageDownloader downloader = new ImageDownloader();
			downloader.setFile(mFile).setUrl(contact.image_url).download();
			if (downloader.succeeded()) {
				super.onLoad();
			}
		}
	}

	@Override
	protected String buildFileName() {
		String fileName = String.format("/avt%s.jpg", mContactId);
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
	protected Bitmap onDecodeImage() {
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

	public void delete() {
		buildImageFile();
		mFile.delete();
	}
}
