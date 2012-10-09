package com.warmice.android.videomessaging.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.data.CurrentUser;
import com.warmice.android.videomessaging.file.image.FileImage;
import com.warmice.android.videomessaging.file.image.Image;
import com.warmice.android.videomessaging.tools.networktasks.SignOutTask;

public class SlidingMenuActivity extends SlidingFragmentActivity {

	private static final int LOAD_IMAGE = 0;
	private static final int CROP_IMAGE = 1;

	private ViewGroup mMenu;
	private TextView mName;
	private TextView mUsername;
	private TextView mFirstName;
	private TextView mLastName;
	private ImageButton mEditPhoto;
	private ImageView mPhoto;

	private boolean isEditting;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		displayAccountDetails();

		ActionBarSherlock sherlock = getSherlock();
		sherlock.getActionBar().setDisplayHomeAsUpEnabled(true);
		sherlock.getActionBar().setHomeButtonEnabled(true);
	}

	private void loadMenu() {
		loadViews();
		setBehindContentView(mMenu);
		setupMenuFields();
		buildMenuDropShadow();
	}

	private void loadViews() {
		LayoutInflater inflater = LayoutInflater.from(this);
		if (isEditting) {
			mMenu = (ViewGroup) inflater.inflate(R.layout.sliding_menu_edit,
					null);
			mFirstName = (TextView) mMenu.findViewById(R.id.first_name);
			mLastName = (TextView) mMenu.findViewById(R.id.last_name);
			mEditPhoto = (ImageButton) mMenu.findViewById(R.id.photo);
		} else {
			mMenu = (ViewGroup) inflater.inflate(R.layout.sliding_menu, null);
			mName = (TextView) mMenu.findViewById(R.id.name);
			mPhoto = (ImageView) mMenu.findViewById(R.id.photo);
		}

		mUsername = (TextView) mMenu.findViewById(R.id.username);
	}

	private void setupMenuFields() {
		CurrentUser user = CurrentUser.load(this);
		mUsername.setText(user.username);
		Bitmap image = loadImage();
		if (isEditting) {
			mFirstName.setText(user.name);
			mLastName.setText(user.name);
			if (image != null) {
				mEditPhoto.setImageBitmap(image);
			}
		} else {
			mName.setText(user.name);
			if (image != null) {
				mPhoto.setImageBitmap(image);
			}
		}
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
			signOut();
			break;
		case R.id.edit:
			displayEditView();
			break;
		case R.id.photo:
			captureNewPhoto();
			break;
		}
	}

	private void signOut() {
		new SignOutTask(this).execute();
		Intent intent = new Intent(this, SignInActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	private void displayEditView() {
		isEditting = true;
		loadMenu();
	}

	private void displayAccountDetails() {
		isEditting = false;
		loadMenu();
	}

	private void captureNewPhoto() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, LOAD_IMAGE);
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

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (isBehindShowing() && isEditting) {
			displayAccountDetails();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CANCELED) {
			return;
		}
		switch (requestCode) {
		case LOAD_IMAGE:
			cropImage(data);
			break;
		case CROP_IMAGE:
			mEditPhoto.setImageBitmap(loadImage());
			break;
		}

	}

	private void cropImage(Intent data) {
		data.toString();
		Uri contentUri = data.getData();
		if (contentUri != null) {
			Intent intent = new Intent(this, CropImageActivity.class);
			intent.setData(contentUri);
			startActivityForResult(intent, CROP_IMAGE);
		}
	}

	private Bitmap loadImage() {
		Image image = new FileImage(getApplicationContext());
		image.setDimens(256, 256);
		image.load();
		Bitmap bitmap = image.getBitmap();
		return bitmap;
	}

}
