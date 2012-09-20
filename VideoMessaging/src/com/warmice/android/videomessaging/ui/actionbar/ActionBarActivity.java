/*
 * Copyright 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.warmice.android.videomessaging.ui.actionbar;

import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.tools.networktasks.SignOutTask;
import com.warmice.android.videomessaging.ui.SignInActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

/**
 * A base activity that defers common functionality across app activities to an
 * {@link ActionBarHelper}.
 * 
 * NOTE: dynamically marking menu items as invisible/visible is not currently
 * supported.
 * 
 * NOTE: this may used with the Android Compatibility Package by extending
 * android.support.v4.app.FragmentActivity instead of {@link Activity}.
 */
public abstract class ActionBarActivity extends FragmentActivity {
	final ActionBarHelper mActionBarHelper = ActionBarHelper
			.createInstance(this);

	/**
	 * Returns the {@link ActionBarHelper} for this activity.
	 */
	protected ActionBarHelper getActionBarHelper() {
		return mActionBarHelper;
	}

	/** {@inheritDoc} */
	@Override
	public MenuInflater getMenuInflater() {
		return mActionBarHelper.getMenuInflater(super.getMenuInflater());
	}

	/** {@inheritDoc} */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActionBarHelper.onCreate(savedInstanceState);
	}

	/** {@inheritDoc} */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mActionBarHelper.onPostCreate(savedInstanceState);
	}

	/**
	 * Base action bar-aware implementation for
	 * {@link Activity#onCreateOptionsMenu(android.view.Menu)}.
	 * 
	 * Note: marking menu items as invisible/visible is not currently supported.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean retValue = false;
		retValue |= mActionBarHelper.onCreateOptionsMenu(menu);
		retValue |= super.onCreateOptionsMenu(menu);
		return retValue;
	}

	/** {@inheritDoc} */
	@Override
	protected void onTitleChanged(CharSequence title, int color) {
		mActionBarHelper.onTitleChanged(title, color);
		super.onTitleChanged(title, color);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.sign_out:
			SignOut();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void SignOut() {
		new SignOutTask(this).execute();
		Intent intent = new Intent(this, SignInActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	protected boolean fieldIsEmpty(EditText editText) {
		if (getTrimmedText(editText).length() > 0)
			return false;
		else
			return true;
	}

	public String getTrimmedText(EditText editText) {
		return editText.getText().toString().trim();
	}
}
