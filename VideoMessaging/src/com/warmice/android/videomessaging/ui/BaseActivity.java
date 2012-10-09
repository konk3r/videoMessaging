package com.warmice.android.videomessaging.ui;

import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class BaseActivity extends SherlockFragmentActivity {

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
