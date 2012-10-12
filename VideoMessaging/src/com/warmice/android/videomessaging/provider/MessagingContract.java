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

public class MessagingContract {

	public static final long UPDATED_NEVER = -2;
	public static final long UPDATED_UNKNOWN = -1;

	public interface SyncColumns {
		/** Last time this entry was updated or synchronized. */
		String UPDATED = "updated";
	}

	public interface MessageColumns {
		String MESSAGE_ID = "message_id";
		String MESSAGE_TYPE = "message_type";
		String MESSAGE_SENT_DATE = "message_sent_date";
		String MESSAGE_RECEIVED_DATE = "message_received_date";
		String MESSAGE_TEXT = "message_note";
		String MESSAGE_IMAGE_PATH = "message_image_uri";
		String MESSAGE_VIDEO_URI = "message_video_uri";

		String RECEIVER_ID = "fk_receiver";
		String SENDER_ID = "fk_sender";
	}

	public interface ContactColumns {
		String CONTACT_ID = "contact_id";
		String CONTACT_USERNAME = "contact_username";
		String CONTACT_NAME = "contact_name";
		String CONTACT_IMAGE_URL = "contact_image_url";
		String CONTACT_APPROVAL_STATUS = "contact_approved";
		String CONTACT_LAST_POST_DATE = "contact_last_post_date";
	}

	public static final String CONTENT_AUTHORITY = "com.warmice.android.videomessaging";

	private static final Uri BASE_CONTENT_URI = Uri.parse("content://"
			+ CONTENT_AUTHORITY);

	private static final String PATH_MESSAGES = "messages";
	private static final String PATH_USERS = "users";

	public static class AuthenticatedTables {
		private final static String CLEAR_ALL = "clear_all";

		public static Uri getClearAllUri() {
			return BASE_CONTENT_URI.buildUpon().appendPath(CLEAR_ALL).build();
		}
	}

	public static class Messages implements MessageColumns, BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_MESSAGES).build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.warmice.message";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.warmice.message";

		public static final String DEFAULT_SORT = MessageColumns.MESSAGE_SENT_DATE
				+ " ASC";

		public static Uri buildMessageUri(String videoId) {
			return CONTENT_URI.buildUpon().appendPath(videoId).build();
		}

		public static String getMessageId(Uri uri) {
			return uri.getPathSegments().get(1);
		}

	}

	public static class Contacts implements ContactColumns, BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_USERS).build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.warmice.user";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.warmice.user";

		public static final String DEFAULT_SORT = ContactColumns.CONTACT_LAST_POST_DATE
				+ " ASC";

		public static final String ALL_TRACK_ID = "all";

		public static Uri buildUserUri(String userId) {
			return CONTENT_URI.buildUpon().appendPath(userId).build();
		}

		public static Uri buildMessagesUri(String contactId) {
			return CONTENT_URI.buildUpon().appendPath(contactId)
					.appendPath(PATH_MESSAGES).build();
		}

		public static String getUserId(Uri uri) {
			return uri.getPathSegments().get(1);
		}
	}
}
