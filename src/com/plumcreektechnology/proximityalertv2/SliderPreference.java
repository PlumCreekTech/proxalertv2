package com.plumcreektechnology.proximityalertv2;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

public class SliderPreference extends Preference implements Preference.OnPreferenceChangeListener{
	
	private String KEY = "slider";
	private long slideValue;
	private long DEFAULT_VALUE = 60000;
	private Context context;
	private SeekBar seekBar;
	
	public SliderPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		setLayoutResource(R.layout.seek_bar_layout);
		setWidgetLayoutResource(R.layout.seek_bar_layout);
		setDefaultValue(DEFAULT_VALUE);
		setKey(KEY);
	}
	
	protected void setOnPreferenceChangedListener(Preference.OnPreferenceChangeListener pclistener){
		
	}
	
	protected void onBindView(View view) {
		super.onBindView(view);
		seekBar = (SeekBar) view.findViewById(R.id.seek_bar);
	}
	
	protected void onClick() {
		
	}
	
	protected View onCreateView(ViewGroup parent) {
		return super.onCreateView(parent);
//		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		return inflater.inflate(getLayoutResource(), parent);
	}
	
	protected void onPrepareForRemoval() {
		super.onPrepareForRemoval();
		persistLong(slideValue);
	}
	
	protected void onSetInitialValue(Boolean restorePersistedValue, Object defaultValue) {
		if(restorePersistedValue) {
			slideValue = this.getPersistedLong(DEFAULT_VALUE);
		} else {
			slideValue = (Long) defaultValue;
			persistFloat(slideValue);
		}
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if(preference.getClass().equals(SliderPreference.class)) {
			slideValue = (Long) newValue;
		}
		return false;
	}

	protected void onRestoreInstanceState(Parcelable state) {
		 // Check whether we saved the state in onSaveInstanceState
	    if (state == null || !state.getClass().equals(SavedState.class)) {
	        // Didn't save the state, so call superclass
	        super.onRestoreInstanceState(state);
	        return;
	    }

	    // Cast state to custom BaseSavedState and pass to superclass
	    SavedState myState = (SavedState) state;
	    super.onRestoreInstanceState(myState.getSuperState());
	    
	    // Set this Preference's widget to reflect the restored state
	    //seekBar.setProgress((int)myState.value);
	}
	
	protected Parcelable onSaveIntanceState() {
		final Parcelable superState = super.onSaveInstanceState();
		// Check whether this Preference is persistent (continually saved)
		if (isPersistent()) {
			// No need to save instance state since it's persistent, use
			// superclass state
			return superState;
		}

		// Create instance of custom BaseSavedState
		final SavedState myState = new SavedState(superState);
		// Set the state's value with the class member that holds current
		// setting value
		myState.value = slideValue;
		return myState;
	}
	
	private static class SavedState extends BaseSavedState {
	    // Member that holds the setting's value
	    // Change this data type to match the type saved by your Preference
	    long value;

	    public SavedState(Parcelable superState) {
	        super(superState);
	    }

	    public SavedState(Parcel source) {
	        super(source);
	        // Get the current preference's value
	        value = source.readLong();  // Change this to read the appropriate data type
	    }

	    @Override
	    public void writeToParcel(Parcel dest, int flags) {
	        super.writeToParcel(dest, flags);
	        // Write the preference's value
	        dest.writeLong(value);  // Change this to write the appropriate data type
	    }

	    // Standard creator object using an instance of this class
	    public static final Parcelable.Creator<SavedState> CREATOR =
	            new Parcelable.Creator<SavedState>() {

	        public SavedState createFromParcel(Parcel in) {
	            return new SavedState(in);
	        }

	        public SavedState[] newArray(int size) {
	            return new SavedState[size];
	        }
	    };
	}

}