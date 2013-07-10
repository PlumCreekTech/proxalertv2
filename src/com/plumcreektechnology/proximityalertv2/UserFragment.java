package com.plumcreektechnology.proximityalertv2;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ListView;

public class UserFragment extends ListFragment {
	
	private POISelect onSelect;
	
	/**
	 * interface this fragment requires for an activity to instantiate it
	 * must receive a string (location name) and a boolean (checked or unchecked)
	 * @author devinfrenze
	 *
	 */
	public interface POISelect {
		public void onPointSelect(String name, boolean flag);
	}

	/**
	 * makes sure that an instantiating activity can not attach this fragment
	 * unless it implements POISelect and saves the activity in onSelect for
	 * future reference
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onSelect = (POISelect) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement POISelect");
		}
	}

	/**
	 * inflates a multiChoice enabled layout with a listview from user_frag
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.user_frag,  container, false);
	}

	/**
	 * when an item is check or unchecked, the fragment calls its instantiating
	 * method (which must implement POISelect) with the location name and a flag
	 * for selected (true) unselected (false), so it can add the POI to
	 * proximity alerts
	 */
	public void onListItemClick(ListView lv, View v, int position, long id) {
		CheckedTextView check = (CheckedTextView) v;
		onSelect.onPointSelect( check.getText().toString(), check.isChecked());
	}
}
