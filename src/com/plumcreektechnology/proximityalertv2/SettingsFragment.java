package com.plumcreektechnology.proximityalertv2;

import android.os.Bundle;
import android.preference.PreferenceFragment; // from only standard lib

public class SettingsFragment extends PreferenceFragment {
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings);
	}
}
