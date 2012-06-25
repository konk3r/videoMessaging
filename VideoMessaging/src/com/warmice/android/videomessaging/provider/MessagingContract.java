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

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract class for interacting with {@link MessagingProvider}. Unless
 * otherwise noted, all time-based fields are milliseconds since epoch and can
 * be compared against {@link System#currentTimeMillis()}.
 * <p>
 * The backing {@link android.content.ContentProvider} assumes that {@link Uri} are generated
 * using stronger {@link String} identifiers, instead of {@code int}
 * {@link BaseColumns#_ID} values, which are prone to shuffle during sync.
 */
public class MessagingContract {

    /**
     * Special value for {@link SyncColumns#UPDATED} indicating that an entry
     * has never been updated, or doesn't exist yet.
     */
    public static final long UPDATED_NEVER = -2;

    /**
     * Special value for {@link SyncColumns#UPDATED} indicating that the last
     * update time is unknown, usually when inserted from a local file source.
     */
    public static final long UPDATED_UNKNOWN = -1;

    public interface SyncColumns {
        /** Last time this entry was updated or synchronized. */
        String UPDATED = "updated";
    }

    public interface VideoColumns {
        String VIDEO_FILE_PATH = "video_file_path";
        String VIDEO_DATE = "video_date";
        String VIDEO_NOTE = "video_note";
        
        String USER_ID = "fk_user";
    }

    public interface UserColumns {
        String USER_ID = "user_id";
        String USER_NAME = "user_name";
        String USER_LAST_POST_DATE = "user_last_post_date";
    }

    public static final String CONTENT_AUTHORITY = "com.warmice.android.videomessaging";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_VIDEOS = "videos";
    private static final String PATH_USERS = "users";

    /**
     * Blocks are generic timeslots that {@link Sessions} and other related
     * events fall into.
     */
    public static class Videos implements VideoColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEOS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.warmice.video";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.warmice.video";


        /** Default "ORDER BY" clause. */
        public static final String DEFAULT_SORT = VideoColumns.VIDEO_DATE + " ASC";

        /** Build {@link Uri} for requested {@link #BLOCK_ID}. */
        public static Uri buildVideoUri(String videoId) {
            return CONTENT_URI.buildUpon().appendPath(videoId).build();
        }

        /** Read {@link #BLOCK_ID} from {@link Blocks} {@link Uri}. */
        public static String getVideoId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    /**
     * Tracks are overall categories for {@link Sessions} and {@link Vendors},
     * such as "Android" or "Enterprise."
     */
    public static class Users implements UserColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USERS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.warmice.user";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.warmice.user";


        /** Default "ORDER BY" clause. */
        public static final String DEFAULT_SORT = UserColumns.USER_LAST_POST_DATE + " ASC";

        /** "All tracks" ID. */
        public static final String ALL_TRACK_ID = "all";

        /** Build {@link Uri} for requested {@link #TRACK_ID}. */
        public static Uri buildUserUri(String userId) {
            return CONTENT_URI.buildUpon().appendPath(userId).build();
        }

        /**
         * Build {@link Uri} that references any {@link Sessions} associated
         * with the requested {@link #TRACK_ID}.
         */
        public static Uri buildVideosUri(String userId) {
            return CONTENT_URI.buildUpon().appendPath(userId).appendPath(PATH_VIDEOS).build();
        }

        /** Read {@link #TRACK_ID} from {@link Tracks} {@link Uri}. */
        public static String getUserId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

}
