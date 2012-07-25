package com.warmice.android.videomessaging.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

public class DataUtils {
	private final static String DIRECTORY = "/videomessaging";

	public static String createCurrentDate() {
		final SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		final Date date = new Date();
		final String formattedDate = dateFormat.format(date);
		return formattedDate;
	}

	public static String getBaseDirectory() {
		String path = Environment.getExternalStorageDirectory().toString();
		StringBuilder builder = new StringBuilder(path).append(DIRECTORY);
		return builder.toString();
	}

	public static String getFilePath(String fileName) {
		StringBuilder builder = new StringBuilder(getBaseDirectory()).append(
				"/").append(fileName);
		return builder.toString();
	}
}
