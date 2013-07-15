package com.plumcreektechnology.proximityalertv2;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ListView;
// is available in both normal and support libs

public class UserFragment extends ListFragment implements ProxConstants {
	
	private POISelect onSelect;
	private String KEY_THIS_PREFERENCE = "list_prefs";
	private String KEY_ITEM_POSITION = "position";
	private SharedPreferences prefs;
	
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
	
	/**
	 * generate consistent item keys for saving the lists state in SharedPreferences
	 * @param pos
	 * @return
	 */
	private String getItemPreferenceKey(int pos) {
		return PACKAGE + "_" + KEY_ITEM_POSITION + "_" + pos;
	}
	
	/**
	 * iterate through shared preferences and set lists's values to what they were on last stop
	 */
	public void onStart() {
		super.onStart();
		ListView listview = getListView();
		prefs = getActivity().getSharedPreferences(KEY_THIS_PREFERENCE, Context.MODE_PRIVATE); // get the list preferences file
		int size = ((MainActivity) getActivity()).getSize();
		for(int i=0; i<size; i++) {
			listview.setItemChecked(i, prefs.getBoolean(getItemPreferenceKey(i), false));
			
		}
	}
	
	/**
	 * iterate through array of item's checked values and commit them to SharedPreferences
	 */
	public void onStop() {
		super.onStop();
		SparseBooleanArray checkedItems = getListView().getCheckedItemPositions();
		SharedPreferences.Editor ed = prefs.edit();
		int size = ((MainActivity) getActivity()).getSize();
		for(int i=0; i<size; i++) {
			ed.putBoolean(getItemPreferenceKey(i), checkedItems.get(i));
		}
		ed.commit();
	}

}
