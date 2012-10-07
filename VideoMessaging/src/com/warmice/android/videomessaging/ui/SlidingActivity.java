package com.warmice.android.videomessaging.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.tools.networktasks.SignOutTask;

public class SlidingActivity extends SlidingFragmentActivity {

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBehindContentView(R.layout.sliding_menu);
		setContentView(R.layout.activity_sliding);


		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.actionbar_home_width);

		// customize the ActionBar
		if (Build.VERSION.SDK_INT >= 11) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
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
	
}
