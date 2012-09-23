package com.warmice.android.videomessaging;

import com.google.android.gcm.GCMBaseIntentService;
import com.warmice.android.videomessaging.data.PushMessage;
import com.warmice.android.videomessaging.tools.MessageFactory;
import com.warmice.android.videomessaging.tools.NotificationCenter;
import com.warmice.android.videomessaging.tools.networktasks.AddDeviceTask;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GCMIntentService extends GCMBaseIntentService {
	public static String TAG = "GCM Intent Service";

	@Override
	protected void onError(Context context, String errorId) {
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		PushMessage message = new MessageFactory().buildMessage(intent);
		Context applicationContext = getApplicationContext();
		NotificationCenter center = new NotificationCenter(applicationContext);
		center.buildNotification(message);
	}

	@Override
	protected void onRegistered(Context context, String regId) {
		AddDeviceTask deviceTask = new AddDeviceTask(context);
		deviceTask.setDeviceId(regId);
		deviceTask.execute();
		if (BuildConfig.DEBUG) {
			Log.e(TAG, "registered");
		}
	}

	@Override
	protected void onUnregistered(Context context, String regId) {
		if (BuildConfig.DEBUG) {
			Log.e(TAG, "unregistered");
		}
	}

}
