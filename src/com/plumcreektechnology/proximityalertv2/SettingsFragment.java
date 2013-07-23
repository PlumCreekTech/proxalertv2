package com.plumcreektechnology.proximityalertv2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// from only standard lib

public class SettingsFragment extends PreferenceFragment {
	private SwitchPreference s;
	private SettingsCheck listen;
	
	/**
	 * interface this fragment requires for an activity to instantiate it
	 *
	 */
	public interface SettingsCheck {
		public void onSwitchChanged();
	}
	
	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings);
		
		s = (SwitchPreference) findPreference("onoff");
		s.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

	        @Override
	        public boolean onPreferenceChange(Preference preference, Object newValue) {
	            Log.d(getClass().getSimpleName(),"onPreferenceChange:" + newValue);
	            listen.onSwitchChanged();
	            return true;
	        }
	    });
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
			listen = (SettingsCheck) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement SettingsCheck");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		view.setBackgroundColor(getResources().getColor(android.R.color.white));
		view.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		return view;
	}
	
	@SuppressLint("NewApi")
	public boolean getIsChecked(){
		return s.isChecked();
	}
}
