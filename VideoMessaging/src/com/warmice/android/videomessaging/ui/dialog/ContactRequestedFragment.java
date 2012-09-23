package com.warmice.android.videomessaging.ui.dialog;

import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.tools.networktasks.ApproveContactTask;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ContactRequestedFragment extends DialogFragment implements
		DialogInterface.OnClickListener {
	
	public static final String CONTACT_ID_KEY = "contact_id_key";
	
	int mContactId;

	public static ContactRequestedFragment newInstance(Bundle args) {
		ContactRequestedFragment fragment = new ContactRequestedFragment();
		fragment.setArguments(args);
		return fragment;
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args;
		if (savedInstanceState != null){
			args = savedInstanceState;
		} else {
			args = getArguments();
		}
		mContactId = args.getInt(CONTACT_ID_KEY);
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.respond_to_contact_request)
				.setPositiveButton(R.string.approve, this)
				.setNegativeButton(R.string.cancel, null).create();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(CONTACT_ID_KEY, mContactId);
		super.onSaveInstanceState(outState);
	}


	@Override
	public void onClick(DialogInterface dialog, int which) {
		new ApproveContactTask(getActivity(), mContactId).execute();
	}

}
