package com.plumcreektechnology.proximityalertv2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

public class ProxReceiver extends BroadcastReceiver {

	/**
	 * when it receives a proximity transition update it creates
	 * a notification that links to a site-specific URL
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false)) {

			// extract name and uri from intent
			String name = intent.getStringExtra("POI");
			String uri = intent.getStringExtra("URI");

			// create a pending intent to be activated from notification
			Intent pending = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			pending.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
					pending, 0);

			// create a notification of the proximity alert
			NotificationCompat.Builder builder = new NotificationCompat.Builder(
					context)
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle("We did it!")
					.setContentText("You're near " + name)
					.addAction(R.drawable.ic_launcher, "sneak peek",
							pendingIntent);
			((NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE)).notify(
					name.hashCode(), builder.build());

			// call our activity that creates the dialog
			Intent intend = new Intent(context, InvisibleActivity.class);
			intend.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intend.putExtra("POI", intent.getStringExtra("POI"));
			intend.putExtra("URI", intent.getStringExtra("URI"));
			context.startActivity(intend);

		}
		
	}

}
