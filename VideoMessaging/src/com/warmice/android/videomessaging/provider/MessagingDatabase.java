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

import com.warmice.android.videomessaging.provider.MessagingContract.ContactColumns;
import com.warmice.android.videomessaging.provider.MessagingContract.MessageColumns;

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

	private static final int VERSION = 7;

	private static final int DATABASE_VERSION = VERSION;

	interface Tables {
		String MESSAGES = "videos";
		String CONTACTS = "contacts";
	}

	public MessagingDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + Tables.MESSAGES + " (" + BaseColumns._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ MessageColumns.MESSAGE_SENT_DATE + " DATETIME NOT NULL,"
				+ MessageColumns.RECEIVER_ID + " TEXT NOT NULL,"
				+ MessageColumns.SENDER_ID + " TEXT NOT NULL,"
				+ MessageColumns.MESSAGE_TYPE + " TEXT NOT NULL,"
				+ MessageColumns.MESSAGE_RECEIVED_DATE + " DATETIME,"
				+ MessageColumns.MESSAGE_TEXT + " TEXT,"
				+ MessageColumns.MESSAGE_IMAGE_PATH + " TEXT,"
				+ MessageColumns.MESSAGE_VIDEO_URI + " TEXT,"
				+ MessageColumns.MESSAGE_ID + " INTEGER,"
				+ "UNIQUE (" + MessageColumns.MESSAGE_ID + ") ON CONFLICT REPLACE)");

		db.execSQL("CREATE TABLE " + Tables.CONTACTS + " (" + BaseColumns._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ ContactColumns.CONTACT_ID + " TEXT NOT NULL,"
				+ ContactColumns.CONTACT_APPROVAL_STATUS + " TEXT NOT NULL,"
				+ ContactColumns.CONTACT_USERNAME + " TEXT NOT NULL,"
				+ ContactColumns.CONTACT_NAME + " TEXT,"
				+ ContactColumns.CONTACT_IMAGE_URL + " TEXT,"
				+ ContactColumns.CONTACT_LAST_POST_DATE + " DATE," + "UNIQUE ("
				+ ContactColumns.CONTACT_ID + ") ON CONFLICT REPLACE)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);
		Log.w(TAG, "Destroying old data during upgrade");

		db.execSQL("DROP TABLE IF EXISTS " + Tables.MESSAGES);
		db.execSQL("DROP TABLE IF EXISTS " + Tables.CONTACTS);

		onCreate(db);
	}
}
