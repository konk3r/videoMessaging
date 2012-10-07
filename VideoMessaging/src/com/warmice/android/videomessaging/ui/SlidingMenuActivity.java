package com.warmice.android.videomessaging.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.data.CurrentUser;
import com.warmice.android.videomessaging.tools.networktasks.SignOutTask;

public class SlidingMenuActivity extends SlidingFragmentActivity {

	private ViewGroup mMenu;
	private TextView mName;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadMenu();

		if (Build.VERSION.SDK_INT >= 11) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	private void loadMenu() {
		loadViews();
		setBehindContentView(mMenu);
		setupMenuFields();
		buildMenuDropShadow();
	}

	private void loadViews() {
		LayoutInflater inflater = LayoutInflater.from(this);
		mMenu = (ViewGroup) inflater.inflate(R.layout.sliding_menu, null);
		mName = (TextView) mMenu.findViewById(R.id.name);
	}

	private void setupMenuFields() {
		CurrentUser user = CurrentUser.load(this);
		mName.setText(user.getName());
	}

	private void buildMenuDropShadow() {
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.actionbar_home_width);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_contacts, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onSideMenuItemSelected(View view) {
		switch (view.getId()) {
		case R.id.sign_out:
			SignOut();
			break;
		}
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
