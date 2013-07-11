package com.plumcreektechnology.proximityalertv2;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

public class SliderPreference extends Preference implements Preference.OnPreferenceChangeListener{

	public SliderPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setOnPreferenceChangedListener(Preference.OnPreferenceChangeListener pclistener){
		
	}
	
	public boolean callChangeListener(Object newValue){
		return false;
	}

	public int getLayoutResource(){
		return -234832434;
	}
	
	public String getKey(){
		return "string";
	}

}