/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.warmice.android.videomessaging.provider;

import com.warmice.android.videomessaging.provider.MessagingContract.UserColumns;
import com.warmice.android.videomessaging.provider.MessagingContract.VideoColumns;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Helper for managing {@link SQLiteDatabase} that stores data for
 * {@link MessagingProvider}.
 */
public class MessagingDatabase extends SQLiteOpenHelper {
    private static final String TAG = "MessagingDatabase";

    private static final String DATABASE_NAME = "messages.db";

    // NOTE: carefully update onUpgrade() when bumping database versions to make
    // sure user data is saved.

    private static final int VERSION = 1;

    private static final int DATABASE_VERSION = VERSION;

    interface Tables {
        String VIDEOS = "videos";
        String USERS = "users";
    }

    public MessagingDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.VIDEOS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + VideoColumns.VIDEO_DATE + " DATE NOT NULL,"
                + VideoColumns.VIDEO_NOTE + " TEXT,"
                + VideoColumns.VIDEO_FILE_PATH + " TEXT NOT NULL,"
                + VideoColumns.USER_ID + " TEXT NOT NULL,"
                + "UNIQUE (" + VideoColumns.VIDEO_FILE_PATH + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.USERS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + UserColumns.USER_ID + " TEXT NOT NULL,"
                + UserColumns.USER_NAME + " TEXT NOT NULL,"
                + UserColumns.USER_LAST_POST_DATE + " DATE,"
                + "UNIQUE (" + UserColumns.USER_ID + ") ON CONFLICT REPLACE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);
        Log.w(TAG, "Destroying old data during upgrade");

        db.execSQL("DROP TABLE IF EXISTS " + Tables.VIDEOS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.USERS);

        onCreate(db);
    }
}
