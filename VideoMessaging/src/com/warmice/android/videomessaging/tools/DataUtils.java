package com.warmice.android.videomessaging.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtils {

	public static String createCurrentDate() {
		final SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		final Date date = new Date();
		final String formattedDate = dateFormat.format(date);
		return formattedDate;
	}
	
}
