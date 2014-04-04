package com.magic.energize.service;

import com.magic.energize.ui.MainActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

//import com.blundell.tut.ui.phone.SecondActivity;

/**
 * This service is started when an Alarm has been raised
 * 
 * We pop a notification into the status bar for the user to click on
 * When the user clicks the notification a new activity is opened
 * 
 */
public class NotifyService extends Service {

	/**
	 * Class for clients to access
	 */
	public class ServiceBinder extends Binder {
		NotifyService getService() {
			return NotifyService.this;
		}
	}

	// Unique id to identify the notification.
	private static final int NOTIFICATION = 123;
	// Name of an intent extra we can use to identify if this service was started to create a notification	
	public static final String INTENT_NOTIFY = "com.magic.energize.service.INTENT_NOTIFY";
	// The system notification manager
	private NotificationManager mNM;

	@Override
	public void onCreate() {
		Log.i("NotifyService", "onCreate()");
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
		
		// If this service was started by out AlarmTask intent then we want to show our notification
		if(intent.getBooleanExtra(INTENT_NOTIFY, false))
			showNotification();
		
		// We don't care if this service is stopped as we have already delivered our notification
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// This is the object that receives interactions from clients
	private final IBinder mBinder = new ServiceBinder();

	/**
	 * Creates a notification and shows it in the OS drag-down status bar
	 */
	private void showNotification() {
		// This is the 'title' of the notification
		CharSequence title = "Energize Meter Alert";
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		// This is the icon to use on the notification
		int icon = android.R.drawable.ic_dialog_alert;
		// This is the scrolling text of the notification
		CharSequence text = "Your notification time is upon us.";		
		// What time to show on the notification
		long when = System.currentTimeMillis();
		// The PendingIntent to launch our activity if the user selects this notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        
		Notification notification = new NotificationCompat.Builder(this)
    	.setTicker(title)
    	.setContentText(text)
    	.setSmallIcon(icon)
    	.setSound(alarmSound)
    	.setWhen(when)
    	.setOngoing(false)
    	.setLights(Color.parseColor("green"), 2000, 2000) // :) lights!!
    	.setContentIntent(contentIntent)
    	.build();
		mNM.notify("energize-service", 1, notification);

		// Send the notification to the system.
		mNM.notify(NOTIFICATION, notification);
		
		// Stop the service when we are finished
		stopSelf();
	}
}
