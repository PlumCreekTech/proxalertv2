package com.plumcreektechnology.proximityalertv2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.plumcreektechnology.proximityalertv2.MyDialogFragment.ChangeList;
import com.plumcreektechnology.proximityalertv2.ProxAlertService.ProxAlertBinder;
import com.plumcreektechnology.proximityalertv2.SettingsFragment.SettingsCheck;
import com.plumcreektechnology.proximityalertv2.UserFragment.POISelect;

public class MainActivity extends FragmentActivity implements ProxConstants, POISelect, ChangeList, SettingsCheck {

	private TreeMap<String, MyGeofence> tree;
	
	// receiver is registered in manifest
	private ProxAlertService service;
	private int size;
	private boolean dialogAlert; // boolean keeps track of if we are displaying a dialogAlert on this resuming
//	private boolean bound; // keeps track of bound status to client
	private boolean runningInterface; // keeps track of if the activity is currently displaying a user interface
	// private ProxReceiver receiver;
	private UserFragment userFragment;
	//private Empty empty;
	private SettingsFragment settingsFragment;
	private FragmentManager fragMan;
	private ArrayList<String> treeList;
	private Vibrator vibrator;
	private final long[] VIBRATE_PATTERN = {0, 200, 200, 200, 200, 200};
	private AudioManager audioman;
	private PowerManager powerman;

	private MapFragment mapfrag;
	private GoogleMap map;
	
	/**
	 * service connection global variable for binding to ProxAlertService
	 */
	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder iBinder) {
			ProxAlertBinder binder = (ProxAlertBinder) iBinder;
			service = binder.getService();
//			bound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
//			bound = false;			
		}
		
	};

	//------------------------LIFECYCLE ORIENTED STUFF-------------------------//	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//create tree
		treeGrow();
		size = tree.size();
		
		// initialize alert effects
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		audioman = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		powerman = (PowerManager) getSystemService(Context.POWER_SERVICE);
		
		// fragments!
		fragMan = getFragmentManager();
		// settings
		settingsFragment = new SettingsFragment();
		fragAdder(settingsFragment);
		fragRemover(settingsFragment);
		// user
		userFragment = new UserFragment();
		userFragment.setListAdapter(treeAdapter());
		fragAdder(userFragment);
		runningInterface = false;
		// map
		mapfrag = MapFragment.newInstance(); //(MapFragment) fragMan.findFragmentById(R.id.map);
		map = mapfrag.getMap();

		if (map != null) {
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			map.setMyLocationEnabled(true);
		} else{
			Toast.makeText(this, "map is null", Toast.LENGTH_SHORT).show();
		}
		fragAdder(mapfrag);
	}

	/**
	 * initialize the options menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	protected void onStart() {
		super.onStart();
		onNewIntent(getIntent());
		
		//check if switch is on & service is running
		boolean proxSwitch = !settingsFragment.getIsChecked();
		Toast.makeText(this, "proxSwitch = "+proxSwitch, Toast.LENGTH_SHORT).show();
		if(proxSwitch && isServiceConnected()){
			//all set
			Toast.makeText(this, "all set - on!", Toast.LENGTH_SHORT).show();
			
		}else if(proxSwitch && !isServiceConnected()){
			// bind activity to ProxAlertService
			Intent intent = new Intent(this, ProxAlertService.class);
			bindService(intent, connection, Context.BIND_AUTO_CREATE);			
		}else {
			//service shouldn't be connected, switch off
			Toast.makeText(this, "all set - off!", Toast.LENGTH_SHORT).show();
		}
	}
	
	protected void onNewIntent(Intent intent) {
		dialogAlert = intent.getBooleanExtra("dialog", false);
		if(dialogAlert && !runningInterface) { // if we are creating a dialog and the user interface was not being displayed (hide the user interface)
			//remove preexisting fragments
			if(userFragment.isAdded()){ //do we need tags...? why did we do that before?
				fragRemover(userFragment);
			} else if(settingsFragment.isAdded()){
				fragRemover(settingsFragment);
			}
			getActionBar().hide();
		}
		if(dialogAlert) { // if we are creating a dialog regardless
			//show dialog
			MyDialogFragment dfrag = new MyDialogFragment();
			Bundle fragBundle = new Bundle();
			fragBundle.putString("POI", intent.getStringExtra("POI"));
			fragBundle.putString("URI", intent.getStringExtra("URI"));
			fragBundle.putInt("ICON", intent.getIntExtra("ICON", INVALID_INT_VALUE));
			if(runningInterface) {
				fragBundle.putBoolean("hideAfter", false);
			} else fragBundle.putBoolean("hideAfter", true);
			dfrag.setArguments(fragBundle);
			FragmentManager fragman = getFragmentManager();
			dfrag.show(fragman, null);
			
			// vibrate, make sound, and light up 
			vibrator.vibrate( VIBRATE_PATTERN, -1);
			if(audioman.getRingerMode()==AudioManager.RINGER_MODE_NORMAL) {
				audioman.loadSoundEffects();
				audioman.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_UP);
				audioman.unloadSoundEffects();
			}
			PowerManager.WakeLock wl = powerman.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "dialog light-up");
			wl.acquire(1000);
			wl.release();
		}
	}
	
	protected void onResume() {
		super.onResume();
		if(dialogAlert) { // if a dialog is happening, then onNewIntent handled it
			dialogAlert = false;
		} else { // otherwise, if nothing but the dialog is visible, pull up the user fragment
			if(!userFragment.isVisible() && !settingsFragment.isVisible()){ //do we need tags...? why did we do that before?
				fragReplacer(userFragment);
			}
			getActionBar().show();
			runningInterface = true;
		}
	}
	
	protected void onStop() {
		super.onStop();
		runningInterface = false;
	}
	
	/**
	 * unbind from service at the end of activity
	 */
	protected void onDestroy() {
		super.onDestroy();
		if (isServiceConnected()) {
			unbindService(connection);
		}
	}

	//------------------------------OTHER STUFF-------------------------------//
	
	/**
	 * automatically populates the tree with premade MyGeofences (which will
	 * then populate a list in the user fragment)
	 */
	private void treeGrow() {
		tree = new TreeMap<String, MyGeofence>();
		tree.put("Public Library", new MyGeofence("Public Library", 41.289818, -82.216895, RADIUS, EXPIRATION, "http://www.oberlinpl.lib.oh.us/", ICON));
		tree.put("South", new MyGeofence("South", 41.289508, -82.221159, RADIUS, EXPIRATION, "http://new.oberlin.edu/office/housing/housing-options/traditional-housing/south.dot", ICON));
		tree.put("Hales", new MyGeofence("Hales", 41.294764, -82.223917, RADIUS, EXPIRATION, "http://new.oberlin.edu/student-life/facilities/detail.dot?id=352252&buildingId=30220", ICON));
		tree.put("Science Center", new MyGeofence("Science Center", 41.294692, -82.221782, RADIUS, EXPIRATION, "http://www.oberlin.edu/science/", ICON));
		tree.put("Laundromat", new MyGeofence("Laundromat", 41.294426, -82.212115, RADIUS, EXPIRATION, "http://en.wiktionary.org/wiki/laundromat", ICON));
		tree.put("Tank", new MyGeofence("Tank", 41.292088, -82.213263, RADIUS, EXPIRATION, "http://new.oberlin.edu/office/housing/housing-options/co-ops/tank.dot", ICON));
		tree.put("Arboretum", new MyGeofence("Arboretum", 41.285558, -82.226127, RADIUS, EXPIRATION, "http://new.oberlin.edu/student-life/facilities/detail.dot?id=2111210&buildingId=175090", ICON));
		tree.put("Giraffe", new MyGeofence("Giraffe", 41.294889, -82.216921, 20, EXPIRATION, "http://animals.nationalgeographic.com/animals/mammals/giraffe/", ICON));
		tree.put("Keep", new MyGeofence("Keep", 41.295898, -82.217575, RADIUS, EXPIRATION, "http://oberwiki.net/Keep_Cottage", ICON));
	}

	/**
	 * private utility for creating an ArrayAdapter that contains all the ids of
	 * MyGeofences in the tree
	 * @return
	 */
	private ArrayAdapter<String> treeAdapter() {
		treeList = new ArrayList<String>();
		for(Map.Entry<String, MyGeofence> entry: tree.entrySet()) {
			treeList.add(entry.getKey());
		}
		return new ArrayAdapter<String>(this, R.layout.checked_text, treeList);
	}
	
	/**
	 * when a menu item is selected starts its corresponding fragment
	 */
	public boolean onOptionsItemSelected(MenuItem item){
		int itemId = item.getItemId();
		switch(itemId){
		case (R.id.action_settings):
			fragReplacer(settingsFragment);
			break;
		case (R.id.user_frag):
			fragReplacer(userFragment);
			break;
		case(R.id.map_frag):
			fragReplacer(mapfrag);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * private utility for adding fragments
	 * @param frag
	 */
	private void fragAdder(Fragment frag) {
		FragmentTransaction trans = fragMan.beginTransaction();
		trans.add(R.id.main_root, frag);
		trans.addToBackStack(null);
		trans.commit();
	}
	
	/**
	 * private utility for removing fragments
	 * @param frag
	 */
	private void fragRemover(Fragment frag) {
		FragmentTransaction trans = fragMan.beginTransaction();
		trans.remove(frag);
		trans.addToBackStack(null);
		trans.commit();
	}
	
	/**
	 * private utility for replacing fragments
	 * @param frag
	 */
	private void fragReplacer(Fragment frag) {
		FragmentTransaction trans = fragMan.beginTransaction();
		trans.replace(R.id.main_root, frag);
		trans.addToBackStack(null);
		trans.commit();
	}
	
	/**
	 * receive changes from user interface and ask service to add and remove
	 * proximity alerts correspondingly
	 */
	@Override
	public void onPointSelect(String name, boolean flag) {
		if (isServiceConnected()) {
			if (flag) {
				service.addProximityAlert(tree.get(name));
			} else {
				service.removeProximityAlert(tree.get(name));
			}
		} else {
			Log.d("CONNECTION ERROR", "service is not connected!");
		}
	}

	/**
	 * reacts to changing switch
	 */
	@Override
	public void onSwitchChanged() {
		boolean proxSwitch = !settingsFragment.getIsChecked(); //why the hell is it backwards?
		if(proxSwitch){
			Toast.makeText(this, "Switch is on", Toast.LENGTH_SHORT).show();
			
			//TODO deal with service
			if(isServiceConnected()){
				Toast.makeText(this, "service is connected. winning.", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(this, "service not connected, let's fix it", Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(this, "Switch is off", Toast.LENGTH_SHORT).show();
			
			//TODO deal with service
			if(!isServiceConnected()){
				Toast.makeText(this, "service not connected. great.", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(this, "service connected, let's disconnect", Toast.LENGTH_SHORT).show();
			}
		}
		
	}

	public int getSize() {
		return size;
	}

	/**
	 * checks if service is running, returns appropriate boolean
	 * @param serviceClassName
	 * @return
	 */
	private boolean isServiceConnected() {
	    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if ("com.example.MyService".equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	@Override
	public void update(String name, boolean flag) { // a slightly eccentric work-around to updating GUI
		for (int i = 0; i < size; i++) {			// depending on how the user responds to the alert
			if ( name.equals(treeList.get(i)) ) {	// if the list is in view, change it in the GUI
				if(userFragment.isVisible()) {
					ListView lview = userFragment.getListView();
					lview.setItemChecked( i, flag);
				}
				else {	// if the list fragment is hidden, change shared preferences so the next time it loads it will be updated
					SharedPreferences.Editor ed = getSharedPreferences(UserFragment.KEY_THIS_PREFERENCE, Context.MODE_PRIVATE).edit();
					ed.putBoolean(UserFragment.getItemPreferenceKey(i), flag);
					ed.commit();
				}
				break;
			}
		}
		onPointSelect(name, flag);
	}

}
