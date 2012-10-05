package com.warmice.android.videomessaging.ui;

import com.warmice.android.videomessaging.R;

import com.warmice.android.videomessaging.data.CurrentUser;
import com.warmice.android.videomessaging.tools.networktasks.RestService.RestResponse;
import com.warmice.android.videomessaging.tools.networktasks.SignInLoader;
import com.warmice.android.videomessaging.ui.actionbar.ActionBarActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

public class SignInActivity extends ActionBarActivity implements
		LoaderManager.LoaderCallbacks<RestResponse> {

	private final static int REQUEST_CREATE = 0;
	private final static int REQUEST_SIGNED_IN = 1;
	
	EditText mUsernameField;
	EditText mPasswordField;
	ProgressDialog mProgressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (CurrentUser.load(this).isSignedIn()){
			startMainActivity();
		} else {
			setContentView(R.layout.activity_sign_in);
			setTitle(R.string.title_activity_sign_in);
			loadViews();
		}
	}

	private void loadViews() {
		mUsernameField = (EditText) findViewById(R.id.username);
		mPasswordField = (EditText) findViewById(R.id.password);
		createProgressDialog();
	}

	private void createProgressDialog() {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage(getString(R.string.logging_in));
		mProgressDialog.setCancelable(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_sign_in, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		boolean accountCreated = requestCode == REQUEST_CREATE
				&& resultCode == RESULT_OK;
		if (accountCreated) {
			signInFromBundle(data);
		}
		
		CurrentUser user = CurrentUser.load(this);
		if (user.isSignedIn()) {
			finish();
		}
	}

	private void signInFromBundle(Intent data) {
		String userKey = getString(R.string.username);
		String passKey = getString(R.string.password);
		String username = data.getStringExtra(userKey);
		String password = data.getStringExtra(passKey);

		mUsernameField.setText(username);
		mPasswordField.setText(password);

		verifyFieldsAndSignIn();
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.sign_in:
			verifyFieldsAndSignIn();
			break;

		case R.id.create_account:
			create_account();
			break;
		}
	}

	private void verifyFieldsAndSignIn() {
		if (inputFieldsAreValid()) {
			mProgressDialog.show();
		    getSupportLoaderManager().initLoader(0, null, this);
		}
	}

	private boolean inputFieldsAreValid() {
		boolean usernameNotBlank = !fieldIsEmpty(mUsernameField);
		boolean passwordNotBlank = !fieldIsEmpty(mPasswordField);
		return usernameNotBlank && passwordNotBlank;
	}

	private void create_account() {
		Intent intent = new Intent(this, CreateAccountActivity.class);
		startActivityForResult(intent, 0);
	}

	@Override
	public Loader<RestResponse> onCreateLoader(int id, Bundle args) {
		SignInLoader loader = new SignInLoader(this);
		String username = getTrimmedText(mUsernameField);
		String password = getTrimmedText(mPasswordField);
		loader.setUsername(username);
		loader.setPassword(password);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<RestResponse> loader, RestResponse response) {
		mProgressDialog.hide();
		if (response.actionSucceeded()) {
			startMainActivity();
		} else {
			displayLoginFailedDialog();
		}
	}

	public void startMainActivity() {
		Intent intent = new Intent(this, ContactsActivity.class);
		startActivityForResult(intent, REQUEST_SIGNED_IN);
	}

	private void displayLoginFailedDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.title_sign_in_failed))
				.setMessage(R.string.message_sign_in_failed)
				.setCancelable(true)
				.setPositiveButton(R.string.okay,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public void onLoaderReset(Loader<RestResponse> loader) {
	}

}
