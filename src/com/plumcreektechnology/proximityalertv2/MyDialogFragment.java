package com.plumcreektechnology.proximityalertv2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MyDialogFragment extends DialogFragment implements ProxConstants {

	private String POI;
	private String URI;
	boolean hideAfter; // do we hide activity after dismissing dialog?

	/** The system calls this only when creating the layout in a dialog. */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		// get data from arguments
		POI = getArguments().getString("POI");
		URI = getArguments().getString("URI");
		hideAfter = getArguments().getBoolean("hideAfter");
		
		// format and build and return the dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("you're near " + POI).setTitle("PROXIMITY UPDATE");
		builder.setPositiveButton("visit",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						buttonSelected(true);
					}
				});
		builder.setNeutralButton("not now",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						buttonSelected(false);
					}
				});
		builder.setNegativeButton("ignore",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						buttonSelected(false);
					}
				});
		return builder.create();
	}

	/**
	 * callback method for when the user selects dialog buttons if true, it
	 * opens a web page for relevant comment in all cases it dismisses the
	 * dialog at the end
	 */
	public void buttonSelected(Boolean action) {
		if (action) {
			Intent websurfing = new Intent(Intent.ACTION_VIEW, Uri.parse(URI));
			startActivity(websurfing);
		}
		if (hideAfter)
			((MainActivity) getActivity()).onBackPressed(); // so that screen
															// returns to its
															// previous state
		dismiss();
	}

//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//	Bundle bundle) {

// // inflate the layout that will be returned
// View layout = inflater.inflate(R.layout.dialog_fragment, container,
// false);
//
// // dynamically set the view objects in the layout to reflect this
// point of interest
// //((TextView)layout.findViewById(R.id.textview)).setText(POI); THIS
// LOOKS REALLY BAD
// // make sure the buttons call back to the main method
// ((Button)layout.findViewById(R.id.positive)).setOnClickListener(new
// Button.OnClickListener() {
// public void onClick(View v) {
// //responder.positiveSelected(MyDialogFragment.this);
// buttonSelected(true);
// }
// });
// ((Button)layout.findViewById(R.id.negative)).setOnClickListener(new
// Button.OnClickListener() {
// public void onClick(View v) {
// //responder.negativeSelected(MyDialogFragment.this);
// buttonSelected(false);
// }
// });
// return layout;
//}
}
