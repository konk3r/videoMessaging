package com.warmice.android.videomessaging.ui.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ProgressFragment extends DialogFragment implements
		DialogInterface.OnClickListener {

	public static ProgressFragment newInstance(Bundle args) {
		ProgressFragment fragment = new ProgressFragment();
		fragment.setArguments(args);
		return fragment;
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setCancelable(false);
		return dialog;
	}


	@Override
	public void onClick(DialogInterface dialog, int which) {
	}

}
