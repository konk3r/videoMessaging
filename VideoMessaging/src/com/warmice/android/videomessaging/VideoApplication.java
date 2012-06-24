package com.warmice.android.videomessaging;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "dDB6WUxFamJvQVR6WlIzQ3FoLUZpa1E6MQ") 
public class VideoApplication extends Application {
    @Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        super.onCreate();
    }

}
