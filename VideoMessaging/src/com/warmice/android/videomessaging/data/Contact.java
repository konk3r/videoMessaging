package com.warmice.android.videomessaging.data;

import java.util.ArrayList;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.warmice.android.videomessaging.provider.MessagingContract;
import com.warmice.android.videomessaging.provider.MessagingContract.ContactColumns;
import com.warmice.android.videomessaging.provider.MessagingContract.Contacts;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.RemoteException;

public class Contact extends User {
	public String approved;
	public String name;

	@Override
	public String getName() {
		if (name.equals(" ")) {
			return username;
		}

		return name;
	}

	public static Contact load(Context context, int id) {
		ContentResolver resolver = context.getContentResolver();

		String selection = ContactColumns.CONTACT_ID + " =?";
		String[] selectionArgs = { Integer.toString(id) };
		Uri uri = Contacts.CONTENT_URI;
		Cursor c = resolver.query(uri, null, selection, selectionArgs, null);
		c.moveToFirst();

		return build(c);
	}

	public static Contact build(Cursor cursor) {
		Contact contact = new Contact();
		int nameIndex = cursor.getColumnIndex(ContactColumns.CONTACT_NAME);
		int approvedIndex = cursor
				.getColumnIndex(ContactColumns.CONTACT_APPROVAL_STATUS);
		int usernameIndex = cursor
				.getColumnIndex(ContactColumns.CONTACT_USERNAME);
		int idIndex = cursor.getColumnIndex(ContactColumns.CONTACT_ID);
		int imageIndex = cursor
				.getColumnIndex(ContactColumns.CONTACT_IMAGE_URL);

		contact.name = cursor.getString(nameIndex);
		contact.approved = cursor.getString(approvedIndex);
		contact.username = cursor.getString(usernameIndex);
		contact.image_url = cursor.getString(imageIndex);
		contact.id = cursor.getInt(idIndex);

		return contact;
	}

	@Override
	public void store(Context context) {
		new StoreContactsTask(context).execute(this);
	}

	public static void storeContactFromJson(Context context, String json) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final Contact contact = mapper.readValue(json, Contact.class);

			new StoreContactsTask(context).execute(contact);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void storeContactsFromJson(Context context, String json) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final ArrayList<Contact> contacts = mapper.readValue(json,
					new TypeReference<ArrayList<Contact>>() {
					});

			Contact.storeContacts(context, contacts);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void storeContacts(Context context,
			ArrayList<Contact> contacts) {
		Contact[] formattedContacts = contacts.toArray(new Contact[contacts
				.size()]);
		new StoreContactsTask(context).execute(formattedContacts);
	}

	private static class StoreContactsTask extends
			AsyncTask<Contact, Void, Void> {
		Context mContext;

		public StoreContactsTask(Context context) {
			mContext = context;
		}

		@Override
		protected Void doInBackground(Contact... params) {
			try {
				storeContacts(params);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		private void storeContacts(Contact[] contacts) throws RemoteException,
				OperationApplicationException {
			final ContentResolver resolver = mContext.getContentResolver();
			final ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

			for (int i = 0; i < contacts.length; i++) {
				final Contact contact = contacts[i];
				final ContentValues values = createValues(contact);
				ContentProviderOperation operation = ContentProviderOperation
						.newInsert(Contacts.CONTENT_URI).withValues(values)
						.build();
				operations.add(operation);
			}

			resolver.applyBatch(MessagingContract.CONTENT_AUTHORITY, operations);
		}

		private ContentValues createValues(Contact contact) {
			final ContentValues values = new ContentValues();
			if (contact.name.equals(" ")) {
				contact.name = contact.username;
			}

			values.put(ContactColumns.CONTACT_APPROVAL_STATUS, contact.approved);
			values.put(ContactColumns.CONTACT_ID, contact.id);
			values.put(ContactColumns.CONTACT_NAME, contact.name);
			values.put(ContactColumns.CONTACT_USERNAME, contact.username);
			values.put(ContactColumns.CONTACT_IMAGE_URL, contact.image_url);

			return values;
		}

	}
}
