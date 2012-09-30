package com.warmice.android.videomessaging;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.StrictMode;

@ReportsCrashes(formKey = "dDB6WUxFamJvQVR6WlIzQ3FoLUZpa1E6MQ") 
public class VideoApplication extends Application {
	
    @SuppressLint("NewApi")
	@Override
    public void onCreate() {
        ACRA.init(this);
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build());
        }
        super.onCreate();
    }

}
