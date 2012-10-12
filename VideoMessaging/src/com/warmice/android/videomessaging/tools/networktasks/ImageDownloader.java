package com.warmice.android.videomessaging.tools.networktasks;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class ImageDownloader {

	private File mFile;
	private String mUrl;
	private InputStream mInputStream;
	private OutputStream mOutputStream;
	private boolean mSucceeded;

	public ImageDownloader setFile(File file) {
		mFile = file;
		return this;
	}

	public ImageDownloader setUrl(String url) {
		mUrl = url;
		return this;
	}

	public void download() {
		try {
			loadStreams();
			downloadFile();
			mSucceeded = true;
		} catch (Exception e) {
			e.printStackTrace();
			mSucceeded = false;
		}
	}

	private void loadStreams() throws IOException {
		URL url = new URL(mUrl);
		URLConnection ucon = url.openConnection();
		mInputStream = ucon.getInputStream();

		mOutputStream = new BufferedOutputStream(new FileOutputStream(mFile));
	}

	private void downloadFile() throws IOException {
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];
		int len = 0;
		while ((len = mInputStream.read(buffer)) != -1) {
			mOutputStream.write(buffer, 0, len);
		}
		if (mOutputStream != null) {
			mOutputStream.close();
		}
	}

	public boolean succeeded() {
		return mSucceeded;
	}
}
