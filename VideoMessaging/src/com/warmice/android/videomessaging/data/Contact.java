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
import android.os.AsyncTask;
import android.os.RemoteException;

public class Contact {
	public String approved;
	public String contact_id;
	public String username;
	public String name;

	public static void storeContactFromJson(Context context, String json){
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final Contact contact = mapper.readValue(json,
					Contact.class);
			
			ArrayList<Contact> contacts = new ArrayList<Contact>();
			contacts.add(contact);
			storeContacts(context, contacts);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void storeContactsFromJson(Context context, String json){
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

		private void storeContacts(Contact[] contacts) throws RemoteException, OperationApplicationException {
			final ContentResolver resolver = mContext.getContentResolver();
			final ArrayList<ContentProviderOperation> operations = new 
					ArrayList<ContentProviderOperation>();

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
			
			 values.put(ContactColumns.CONTACT_APPROVAL_STATUS, contact.approved);
			 values.put(ContactColumns.CONTACT_ID, contact.contact_id);
			 values.put(ContactColumns.CONTACT_NAME, contact.name);
			 values.put(ContactColumns.CONTACT_USERNAME, contact.username);
			
			return values;
		}

	}
}
