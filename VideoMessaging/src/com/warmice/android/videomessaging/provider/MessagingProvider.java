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

import com.warmice.android.videomessaging.provider.MessagingDatabase.Tables;
import com.warmice.android.videomessaging.provider.MessagingContract.Users;
import com.warmice.android.videomessaging.provider.MessagingContract.Videos;
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

    private static final int VIDEOS = 100;
    private static final int VIDEO_ID = 101;

    private static final int USERS = 200;
    private static final int USER_ID = 201;
    private static final int USER_ID_VIDEOS = 202;

    /**
     * Build and return a {@link UriMatcher} that catches all {@link Uri}
     * variations supported by this {@link ContentProvider}.
     */
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MessagingContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "videos", VIDEOS);
        matcher.addURI(authority, "videos/*", VIDEO_ID);

        matcher.addURI(authority, "users", USERS);
        matcher.addURI(authority, "users/*", USER_ID);
        matcher.addURI(authority, "users/*/videos", USER_ID_VIDEOS);

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
            case VIDEOS:
                return Videos.CONTENT_TYPE;
            case VIDEO_ID:
                return Videos.CONTENT_ITEM_TYPE;
            case USERS:
                return Users.CONTENT_TYPE;
            case USER_ID:
                return Users.CONTENT_ITEM_TYPE;
            case USER_ID_VIDEOS:
                return Videos.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        if (LOGV) Log.v(TAG, "query(uri=" + uri + ", proj=" + Arrays.toString(projection) + ")");
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            default: {
                // Most cases are handled with simple SelectionBuilder
                final SelectionBuilder builder = buildExpandedSelection(uri, match);
                return builder.where(selection, selectionArgs).query(db, projection, sortOrder);
            }
//            case SEARCH_SUGGEST: {
//                final SelectionBuilder builder = new SelectionBuilder();
//
//                // Adjust incoming query to become SQL text match
//                selectionArgs[0] = selectionArgs[0] + "%";
//                builder.table(Tables.SEARCH_SUGGEST);
//                builder.where(selection, selectionArgs);
//                builder.map(SearchManager.SUGGEST_COLUMN_QUERY,
//                        SearchManager.SUGGEST_COLUMN_TEXT_1);
//
//                projection = new String[] { BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1,
//                        SearchManager.SUGGEST_COLUMN_QUERY };
//
//                final String limit = uri.getQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT);
//                return builder.query(db, projection, null, null, SearchSuggest.DEFAULT_SORT, limit);
//            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (LOGV) Log.v(TAG, "insert(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case VIDEOS: {
                db.insertOrThrow(Tables.VIDEOS, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Videos.buildVideoUri(values.getAsString(Videos.VIDEO_FILE_PATH));
            }
            case USERS: {
                db.insertOrThrow(Tables.USERS, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Users.buildUserUri(values.getAsString(Users.USER_ID));
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (LOGV) Log.v(TAG, "update(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).update(db, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return retVal;
    }

    /** {@inheritDoc} */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (LOGV) Log.v(TAG, "delete(uri=" + uri + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).delete(db);
        getContext().getContentResolver().notifyChange(uri, null);
        return retVal;
    }

    /**
     * Apply the given set of {@link ContentProviderOperation}, executing inside
     * a {@link SQLiteDatabase} transaction. All changes will be rolled back if
     * any single one fails.
     */
    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
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
            case VIDEOS: {
                return builder.table(Tables.VIDEOS);
            }
            case VIDEO_ID: {
                final String videoId = Videos.getVideoId(uri);
                return builder.table(Tables.VIDEOS)
                        .where(Videos.VIDEO_FILE_PATH + "=?", videoId);
            }
            case USERS: {
                return builder.table(Tables.USERS);
            }
            case USER_ID: {
                final String userId = Users.getUserId(uri);
                return builder.table(Tables.USERS)
                        .where(Users.USER_ID + "=?", userId);
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
            case VIDEOS: {
                return builder.table(Tables.USERS);
            }
            case VIDEO_ID: {
                final String videoId = Videos.getVideoId(uri);
                return builder.table(Tables.VIDEOS)
                        .where(Videos.VIDEO_FILE_PATH + "=?", videoId);
            }
            case USERS: {
                return builder.table(Tables.USERS);
            }
            case USER_ID: {
                final String userId = Users.getUserId(uri);
                return builder.table(Tables.USERS)
                        .where(Users.USER_ID + "=?", userId);
            }
            case USER_ID_VIDEOS: {
                final String userId = Users.getUserId(uri);
                return builder.table(Tables.VIDEOS)
                        .where(Videos.USER_ID + "=?", userId);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }
}
