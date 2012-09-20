package com.warmice.android.videomessaging.ui;

import com.warmice.android.videomessaging.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class AddContactFragment extends DialogFragment implements
		DialogInterface.OnClickListener {
	EditText mContactField;

	public static AddContactFragment newInstance(Bundle args) {
		return new AddContactFragment();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View dialogBody = (ViewGroup) inflater.inflate(
				R.layout.fragment_add_contact_dialog, null);
		mContactField = (EditText) dialogBody.findViewById(R.id.contactInput);
		
		return new AlertDialog.Builder(getActivity())
				.setIcon(R.drawable.ic_action_add_contact)
				.setTitle(R.string.title_add_contact)
				.setPositiveButton(R.string.add, this)
				.setNegativeButton(R.string.cancel, null).setView(dialogBody)
				.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		ContactsActivity activity = ((ContactsActivity) getActivity());
		String contactName = activity.getTrimmedText(mContactField);
		activity.addContact(contactName);
	}

}
