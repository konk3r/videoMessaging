package com.warmice.android.videomessaging.ui;

import org.apache.http.HttpStatus;

import com.actionbarsherlock.view.Menu;
import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.tools.networktasks.CreateAccountLoader;
import com.warmice.android.videomessaging.tools.networktasks.RestService.RestResponse;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.EditText;

public class CreateAccountActivity extends BaseActivity implements
		LoaderManager.LoaderCallbacks<RestResponse> {

	public static final String EXTRA_USERNAME = "extra_username";
	public static final String EXTRA_PASSWORD = "extra_password";
	
	private EditText mUsernameField;
	private EditText mFirstNameField;
	private EditText mLastNameField;
	private EditText mPasswordField;
	private EditText mPasswordConfirmationField;
	ProgressDialog mProgressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_account);
		loadViews();
		parseAndFillPresetValues();
	}

	private void parseAndFillPresetValues() {
		Intent intent = getIntent();
		String username = intent.getStringExtra(EXTRA_USERNAME);
		String password = intent.getStringExtra(EXTRA_PASSWORD);
		
		mUsernameField.setText(username);
		mPasswordField.setText(password);
	}

	private void loadViews() {
		mUsernameField = (EditText) findViewById(R.id.username_input);
		mFirstNameField = (EditText) findViewById(R.id.first_name_input);
		mLastNameField = (EditText) findViewById(R.id.last_name_input);
		mPasswordField = (EditText) findViewById(R.id.password_input);
		mPasswordConfirmationField = (EditText) findViewById(R.id.password_confirmation_input);
		createProgressDialog();
	}

	private void createProgressDialog() {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage(getString(R.string.creating_account));
		mProgressDialog.setCancelable(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_create_account, menu);
		return true;
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.create_account:
			verifyAndCreateAccount();
			break;
		}
	}

	private void verifyAndCreateAccount() {
		if (inputFieldsAreValid()) {
			mProgressDialog.show();
			getSupportLoaderManager().initLoader(0, null, this);
		}
	}

	private boolean inputFieldsAreValid() {
		boolean usernameNotBlank = !fieldIsEmpty(mUsernameField);
		return usernameNotBlank && passwordIsValid();
	}

	private boolean passwordIsValid() {
		boolean passwordIsBlank = fieldIsEmpty(mUsernameField);
		if (passwordIsBlank) {
			return false;
		}

		String password = getTrimmedText(mPasswordField);
		String passwordConfirmation = getTrimmedText(mPasswordConfirmationField);
		password = password.trim();
		passwordConfirmation = passwordConfirmation.trim();
		return password.equals(passwordConfirmation);
	}
	
	@Override
	public Loader<RestResponse> onCreateLoader(int arg0, Bundle arg1) {
		CreateAccountLoader loader = new CreateAccountLoader(this);
		loader.setUsername(getTrimmedText(mUsernameField));
		loader.setPassword(getTrimmedText(mPasswordField));

		if (!fieldIsEmpty(mFirstNameField)) {
			loader.setFirstName(getTrimmedText(mFirstNameField));
		}
		if (!fieldIsEmpty(mLastNameField)) {
			loader.setLastName(getTrimmedText(mLastNameField));
		}
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<RestResponse> loader, RestResponse response) {
		if (response.actionSucceeded()) {
			respondToNewAccount();
		} else {
			respondToFailedAccount(response.getCode());
		}
	}

	public void respondToNewAccount() {
		mProgressDialog.hide();
		Intent resultIntent = generateReturnIntent();
		setResult(RESULT_OK, resultIntent);
		finish();
	}

	private Intent generateReturnIntent() {
		Intent resultIntent = new Intent();

		String userKey = getString(R.string.username);
		String passKey = getString(R.string.password);
		String username = getTrimmedText(mUsernameField);
		String password = getTrimmedText(mPasswordField);

		resultIntent.putExtra(userKey, username);
		resultIntent.putExtra(passKey, password);

		return resultIntent;
	}

	private void respondToFailedAccount(int status) {
		mProgressDialog.hide();
		String failedMessage = constructFailedDialogMesage(status);
		displayCreationFailedDialog(failedMessage);
	}

	private String constructFailedDialogMesage(int status) {
		switch (status) {
		case HttpStatus.SC_CONFLICT:
			return getString(R.string.duplicate_account);
		}
		return null;
	}

	private void displayCreationFailedDialog(String failedMessage) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.title_create_account_failed))
				.setMessage(failedMessage)
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
	public void onLoaderReset(Loader<RestResponse> arg0) {
	}
}
