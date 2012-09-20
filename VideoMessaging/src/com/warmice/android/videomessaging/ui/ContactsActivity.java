package com.warmice.android.videomessaging.ui;

import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.provider.MessagingContract.Users;
import com.warmice.android.videomessaging.tools.networktasks.AddContactTask;
import com.warmice.android.videomessaging.tools.networktasks.GetContactsTask;
import com.warmice.android.videomessaging.ui.actionbar.ActionBarActivity;
import com.warmice.android.videomessaging.ui.adapter.ContactAdapter;

import android.os.Bundle;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;

public class ContactsActivity extends ActionBarActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {
	
	CursorAdapter mAdapter;
	ListView mList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		
		findViews();
		initializeList();
		new GetContactsTask(this).execute();
	}

	private void initializeList() {
		mAdapter = new ContactAdapter(this, null);
		mList.setAdapter(mAdapter);
		getSupportLoaderManager().initLoader(0, null, this);
	}

	private void findViews() {
		mList = (ListView) findViewById(R.id.contact_list);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_contacts, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()){
		case R.id.add_contact:
			displayAddContactDialog();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	private void displayAddContactDialog() {
		DialogFragment newFragment = AddContactFragment.newInstance(null);
	    newFragment.show(getSupportFragmentManager(), "dialog");
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader loader = new CursorLoader(this, Users.CONTENT_URI, null,
				null, null, null);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

	public void addContact(String contactName) {
		new AddContactTask(this, contactName).execute();
	}
}
