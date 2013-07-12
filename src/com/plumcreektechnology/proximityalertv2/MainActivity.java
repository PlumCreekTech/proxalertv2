package com.plumcreektechnology.proximityalertv2;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.plumcreektechnology.proximityalertv2.ProxAlertService.ProxAlertBinder;
import com.plumcreektechnology.proximityalertv2.UserFragment.POISelect;

public class MainActivity extends FragmentActivity implements ProxConstants, POISelect {

	private TreeMap<String, MyGeofence> tree;
	
	private ProxAlertService service;
	private boolean bound;
//	private ProxReceiver receiver;
	private UserFragment userFragment;
	//private Empty empty;
	private SettingsFragment settingsFragment;
	private FragmentManager fragMan;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// bind activity to ProxAlertService
		Intent intent = new Intent(this, ProxAlertService.class);
		bindService(intent, connection, Context.BIND_AUTO_CREATE);
		treeGrow();
		
		//register receiver
//		receiver = new ProxReceiver();
//		IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
//		registerReceiver(receiver, filter);
		
		// fragments!
		fragMan = getFragmentManager();
		// empty
//		empty = new Empty();
//		fragAdder(empty);
//		fragRemover(empty);
		// settings
		settingsFragment = new SettingsFragment();
		fragAdder(settingsFragment);
		fragRemover(settingsFragment);
		// user
		userFragment = new UserFragment();
		userFragment.setListAdapter(treeAdapter());
		fragAdder(userFragment);
	}
	
	/**
	 * automatically populates the tree with premade MyGeofences (which will
	 * then populate a list in the user fragment)
	 */
	private void treeGrow() {
		tree = new TreeMap<String, MyGeofence>();
		tree.put("Public Library", new MyGeofence("Public Library", 41.289818, -82.216895, RADIUS, EXPIRATION, URL));
		tree.put("South", new MyGeofence("South", 41.289508, -82.221159, RADIUS, EXPIRATION, URL));
		tree.put("Hales", new MyGeofence("Hales", 41.294764, -82.223917, RADIUS, EXPIRATION, URL));
		tree.put("Science Center", new MyGeofence("Science Center", 41.294692, -82.221782, RADIUS, EXPIRATION, URL));
		tree.put("Laundromat", new MyGeofence("Laundromat", 41.294426, -82.212115, RADIUS, EXPIRATION, URL));
		tree.put("Tank", new MyGeofence("Tank", 41.292088, -82.213263, RADIUS, EXPIRATION, URL));
		tree.put("Arboretum", new MyGeofence("Arboretum", 41.285558, -82.226127, RADIUS, EXPIRATION, URL));
	}

	/**
	 * private utility for creating an ArrayAdapter that contains all the ids of
	 * MyGeofences in the tree
	 * @return
	 */
	private ArrayAdapter<String> treeAdapter() {
		ArrayList<String> treeList = new ArrayList<String>();
		for(Map.Entry<String, MyGeofence> entry: tree.entrySet()) {
			treeList.add(entry.getKey());
		}
		return new ArrayAdapter<String>(this, R.layout.checked_text, treeList);
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
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * private utility for adding fragments
	 * @param frag
	 */
	private void fragAdder(Fragment frag) {
		FragmentTransaction trans = fragMan.beginTransaction();
		trans.add(R.id.container, frag);
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
		trans.replace(R.id.container, frag);
		trans.addToBackStack(null);
		trans.commit();
	}
	
	/**
	 * service connection utility for binding to ProxAlertService
	 */
	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder iBinder) {
			ProxAlertBinder binder = (ProxAlertBinder) iBinder;
			service = binder.getService();
			bound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			bound = false;			
		}
		
	};
	
	/**
	 * receive changes from user interface and ask service to add and remove
	 * proximity alerts correspondingly
	 */
	@Override
	public void onPointSelect(String name, boolean flag) {
		if (bound) {
			Toast.makeText(this, "bound and processing in main actiivty", Toast.LENGTH_SHORT).show();
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
	 * unbind from service at the end of activity
	 */
	protected void onDestroy() {
		super.onDestroy();
//		unregisterReceiver(receiver);
		if (bound) {
			unbindService(connection);
		}
	}

}
