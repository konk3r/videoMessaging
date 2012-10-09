package com.warmice.android.videomessaging.data;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.RemoteException;

import com.warmice.android.videomessaging.provider.MessagingContract;
import com.warmice.android.videomessaging.provider.MessagingContract.MessageColumns;
import com.warmice.android.videomessaging.provider.MessagingContract.Messages;
import com.warmice.android.videomessaging.tools.DateUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
	public static final String TYPE_TEXT = "text";

	private Context mContext;

	public Integer id;
	public Integer sender_id;
	public Integer receiver_id;
	public String text;
	public String sent_at;
	public String message_type;

	private ContentValues mValues;

	public Message() {
		sent_at = DateUtils.createCurrentDate();
	}

	public boolean update(Context context) {
		final ContentResolver resolver = context.getContentResolver();
		final ContentValues values = createValues();
		final Uri uri = Messages.CONTENT_URI;
		String selection = createSelection();
		String[] selectionArgs = createSelectionArgs();
		int updateCount = resolver
				.update(uri, values, selection, selectionArgs);

		if (updateCount > 0) {
			return true;
		}
		return false;
	}

	private String createSelection() {
		String selection = MessageColumns.MESSAGE_SENT_DATE + " =? AND "
				+ MessageColumns.SENDER_ID + " =? AND "
				+ MessageColumns.RECEIVER_ID + " =?";
		return selection;
	}

	private String[] createSelectionArgs() {
		String[] selectionArgs = new String[] { sent_at, sender_id.toString(),
				receiver_id.toString() };
		return selectionArgs;
	}

	public void store(Context context) {
		mContext = context;
		mValues = createValues();
		final ContentResolver resolver = mContext.getContentResolver();
		final Uri uri = Messages.CONTENT_URI;
		resolver.insert(uri, mValues);
	}

	public ContentValues createValues() {
		ContentValues values = new ContentValues();

		if (id != null) {
			values.put(MessageColumns.MESSAGE_ID, id);
		}

		values.put(MessageColumns.SENDER_ID, sender_id);
		values.put(MessageColumns.MESSAGE_SENT_DATE, sent_at);
		values.put(MessageColumns.MESSAGE_TEXT, text);
		values.put(MessageColumns.RECEIVER_ID, receiver_id);
		values.put(MessageColumns.MESSAGE_TYPE, message_type);

		return values;
	}

	public static void storeMessages(Context context,
			ArrayList<Message> messages) {
		Message[] formattedMessages = messages.toArray(new Message[messages
				.size()]);
		if (messages.size() > 0) {
			new StoreMessagesTask(context).execute(formattedMessages);
		}
	}

	private static class StoreMessagesTask extends
			AsyncTask<Message, Void, Void> {
		Context mContext;

		public StoreMessagesTask(Context context) {
			mContext = context;
		}

		@Override
		protected Void doInBackground(Message... params) {
			try {
				storeMessages(params);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		private void storeMessages(Message[] messages) throws RemoteException,
				OperationApplicationException {
			final ContentResolver resolver = mContext.getContentResolver();
			final ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

			for (int i = 0; i < messages.length; i++) {
				final Message message = messages[i];
				final ContentValues values = message.createValues();
				ContentProviderOperation operation = ContentProviderOperation
						.newInsert(Messages.CONTENT_URI).withValues(values)
						.build();
				operations.add(operation);
			}

			ContentProviderResult[] result = resolver.applyBatch(
					MessagingContract.CONTENT_AUTHORITY, operations);
			result.toString();
		}
	}

	public static void updateMessage(Context context, String json) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final Message message = mapper.readValue(json, Message.class);
			message.update(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getContactId(Context context) {
		int id = CurrentUser.load(context).id;
		
		if(sender_id == id) {
			return receiver_id;
		} else {
			return sender_id;
		}
	}
}
