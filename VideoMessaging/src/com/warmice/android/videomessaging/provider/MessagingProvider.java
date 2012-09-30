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

import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import com.warmice.android.videomessaging.data.User;
import com.warmice.android.videomessaging.provider.MessagingContract.MessageColumns;
import com.warmice.android.videomessaging.provider.MessagingDatabase.Tables;
import com.warmice.android.videomessaging.provider.MessagingContract.Contacts;
import com.warmice.android.videomessaging.provider.MessagingContract.Messages;
import com.warmice.android.videomessaging.util.SelectionBuilder;

/**
 * Provider that stores {@link MessagingContract} data. Data is usually inserted
 * by {@link SyncService}, and queried by various {@link Activity} instances.
 */
public class MessagingProvider extends ContentProvider {
	private static final String TAG = "ScheduleProvider";
	private static final boolean LOGV = Log.isLoggable(TAG, Log.VERBOSE);

	private MessagingDatabase mOpenHelper;

	private static final UriMatcher sUriMatcher = buildUriMatcher();

	private static final int MESSAGES = 100;
	private static final int MESSAGE_ID = 101;

	private static final int CONTACT = 200;
	private static final int CONTACT_ID = 201;
	private static final int CONTACT_ID_MESSAGES = 202;

	private static final int CLEAR_ALL = 500;

	/**
	 * Build and return a {@link UriMatcher} that catches all {@link Uri}
	 * variations supported by this {@link ContentProvider}.
	 */
	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = MessagingContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, "messages", MESSAGES);
		matcher.addURI(authority, "messages/*", MESSAGE_ID);

		matcher.addURI(authority, "users", CONTACT);
		matcher.addURI(authority, "users/*", CONTACT_ID);
		matcher.addURI(authority, "users/*/messages", CONTACT_ID_MESSAGES);

		matcher.addURI(authority, "clear_all", CLEAR_ALL);

		return matcher;
	}

	@Override
	public boolean onCreate() {
		final Context context = getContext();
		mOpenHelper = new MessagingDatabase(context);
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public String getType(Uri uri) {
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case MESSAGES:
			return Messages.CONTENT_TYPE;
		case MESSAGE_ID:
			return Messages.CONTENT_ITEM_TYPE;
		case CONTACT:
			return Contacts.CONTENT_TYPE;
		case CONTACT_ID:
			return Contacts.CONTENT_ITEM_TYPE;
		case CONTACT_ID_MESSAGES:
			return Messages.CONTENT_TYPE;
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	/** {@inheritDoc} */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		if (LOGV) {
			Log.v(TAG,
					"query(uri=" + uri + ", proj="
							+ Arrays.toString(projection) + ")");
		}

		final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

		final int match = sUriMatcher.match(uri);
		final SelectionBuilder builder = buildExpandedSelection(uri, match);
		Cursor cursor = builder.where(selection, selectionArgs).query(db,
				projection, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	/** {@inheritDoc} */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (LOGV)
			Log.v(TAG, "insert(uri=" + uri + ", values=" + values.toString()
					+ ")");
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case MESSAGES: {
			db.insertOrThrow(Tables.MESSAGES, null, values);
			String contactId = getContactId(values);
			Uri updatedUri = Contacts.buildMessagesUri(contactId);
			getContext().getContentResolver().notifyChange(updatedUri, null);
			return Messages.buildMessageUri(values
					.getAsString(Messages.MESSAGE_VIDEO_URI));
		}
		case CONTACT: {
			db.insertOrThrow(Tables.CONTACTS, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Contacts.buildUserUri(values
					.getAsString(Contacts.CONTACT_ID));
		}
		default: {
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		}
	}

	private String getContactId(ContentValues values) {
		String userId = Integer.toString(User.load(getContext()).id);
		String senderId = values.getAsString(MessageColumns.SENDER_ID);
		String receiverId = values.getAsString(MessageColumns.RECEIVER_ID);
		if (senderId.equals(userId)) {
			return receiverId;
		}
		return senderId;
	}

	/** {@inheritDoc} */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		if (LOGV)
			Log.v(TAG, "update(uri=" + uri + ", values=" + values.toString()
					+ ")");
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final SelectionBuilder builder = buildSimpleSelection(uri);
		int retVal = builder.where(selection, selectionArgs).update(db, values);
		getContext().getContentResolver().notifyChange(uri, null);
		return retVal;
	}

	/** {@inheritDoc} */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		if (LOGV)
			Log.v(TAG, "delete(uri=" + uri + ")");
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		int retVal = 0;

		switch (match) {
		case CLEAR_ALL:
			retVal += db.delete(Tables.CONTACTS, null, null);
			retVal += db.delete(Tables.MESSAGES, null, null);
			break;

		default:
			final SelectionBuilder builder = buildSimpleSelection(uri);
			retVal = builder.where(selection, selectionArgs).delete(db);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return retVal;
	}

	/**
	 * Apply the given set of {@link ContentProviderOperation}, executing inside
	 * a {@link SQLiteDatabase} transaction. All changes will be rolled back if
	 * any single one fails.
	 */
	@Override
	public ContentProviderResult[] applyBatch(
			ArrayList<ContentProviderOperation> operations)
			throws OperationApplicationException {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			final int numOperations = operations.size();
			final ContentProviderResult[] results = new ContentProviderResult[numOperations];
			for (int i = 0; i < numOperations; i++) {
				results[i] = operations.get(i).apply(this, results, i);
			}
			db.setTransactionSuccessful();
			return results;
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * Build a simple {@link SelectionBuilder} to match the requested
	 * {@link Uri}. This is usually enough to support {@link #insert},
	 * {@link #update}, and {@link #delete} operations.
	 */
	private SelectionBuilder buildSimpleSelection(Uri uri) {
		final SelectionBuilder builder = new SelectionBuilder();
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case MESSAGES: {
			return builder.table(Tables.MESSAGES);
		}
		case MESSAGE_ID: {
			final String videoId = Messages.getMessageId(uri);
			return builder.table(Tables.MESSAGES).where(
					Messages.MESSAGE_VIDEO_URI + "=?", videoId);
		}
		case CONTACT: {
			return builder.table(Tables.CONTACTS);
		}
		case CONTACT_ID: {
			final String userId = Contacts.getUserId(uri);
			return builder.table(Tables.CONTACTS).where(
					Contacts.CONTACT_ID + "=?", userId);
		}
		case CONTACT_ID_MESSAGES: {
			final String userId = Contacts.getUserId(uri);
			return builder.table(Tables.MESSAGES)
					.where(Messages.RECEIVER_ID + "=? OR " + Messages.SENDER_ID
							+ "=?", userId, userId);
		}
		default: {
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		}
	}

	/**
	 * Build an advanced {@link SelectionBuilder} to match the requested
	 * {@link Uri}. This is usually only used by {@link #query}, since it
	 * performs table joins useful for {@link Cursor} data.
	 */
	private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
		final SelectionBuilder builder = new SelectionBuilder();
		switch (match) {
		case MESSAGES: {
			return builder.table(Tables.CONTACTS);
		}
		case MESSAGE_ID: {
			final String videoId = Messages.getMessageId(uri);
			return builder.table(Tables.MESSAGES).where(
					Messages.MESSAGE_VIDEO_URI + "=?", videoId);
		}
		case CONTACT: {
			return builder.table(Tables.CONTACTS);
		}
		case CONTACT_ID: {
			final String userId = Contacts.getUserId(uri);
			return builder.table(Tables.CONTACTS).where(
					Contacts.CONTACT_ID + "=?", userId);
		}
		case CONTACT_ID_MESSAGES: {
			final String userId = Contacts.getUserId(uri);
			return builder.table(Tables.MESSAGES)
					.where(Messages.RECEIVER_ID + "=? OR " + Messages.SENDER_ID
							+ "=?", userId, userId);
		}
		default: {
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		}
	}
}
