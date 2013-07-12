package com.plumcreektechnology.proximityalertv2;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class ProxReceiver extends BroadcastReceiver {

	/**
	 * when it receives a proximity transition update it creates
	 * a notification that links to a site-specific URL
	 */
	@Override
	public void onReceive(Context context, Intent intent) {

		// extract name and uri from intent
		String name = intent.getStringExtra("POI");
		String uri = intent.getStringExtra("URI");

		// create a pending intent to be activated from notification
		Intent pending = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		pending.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, pending, 0);

		// create a notification of the proximity alert
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("We did it!")
				.setContentText("You're near " + name)
				.addAction(R.drawable.ic_launcher, "sneak peek", pendingIntent);
		((NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE)).notify(
				name.hashCode(), builder.build());
		
		// assemble dialog from the receiver
		MyDialogFragment dfrag = new MyDialogFragment();
		// put the POI and URI arguments in the bundle
		Bundle fragBundle = new Bundle();
		fragBundle.putString("POI", intent.getStringExtra("POI"));
		fragBundle.putString("URI", intent.getStringExtra("URI"));
		dfrag.setArguments(fragBundle);
		FragmentManager fragman = ((Activity) context).getFragmentManager();
		dfrag.show(fragman, null);
		
	}

}
