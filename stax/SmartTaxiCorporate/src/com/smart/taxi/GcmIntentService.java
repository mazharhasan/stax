package com.smart.taxi;
/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import java.util.Random;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.smart.taxi.utils.Utils;
import com.smarttaxi.client.R;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
	public static final String DISPLAY_MESSAGE_ACTION = "com.smart.taxi.gcm.DISPLAY_MESSAGE";
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    String TAG = "Intent service";
	private Random r;

    public GcmIntentService() {
        super("GcmIntentService");
        r = new Random();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
        	String msg = extras.toString();
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                /*for (int i=0; i<0; i++) {
                    Log.i(TAG, "Working... " + (i+1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                }*/
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                sendNotification(extras);
                try{
                	
                	PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                	 PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
                	 wl.acquire();                	   
                	 wl.release();

                }catch(Exception ex)
                {
                	Log.e("Wake lock acquire error:", ex.toString());
                }
               // Log.i(TAG, "Received: " + extras.toString());
                
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(Object msg) {
    	Intent intent;
    	String message;
    	int rid = r.nextInt(1800 - 650) + 650;
    	try{
    		Bundle extras = (Bundle) msg;
        	String notifType = extras.getString("type");
        	String journeyId = extras.getString("journey_id");
        	message = extras.getString("message");
			
			intent = new Intent(DISPLAY_MESSAGE_ACTION);
			
			intent.putExtra("rid", rid);
			intent.putExtra("journeyId", journeyId);
    		intent.putExtra("type", notifType);
    		intent.putExtra("message", (Utils.isEmptyOrNull(message))?"":message);
    		getApplicationContext().sendBroadcast(intent);
        	
        }catch(Exception ex)
        {
        	return;
        }
    	
    	
    	Intent notifIntent = new Intent("com.smart.taxi.beep");
    	notifIntent.putExtra("msg", (Utils.isEmptyOrNull(message))?"Beep recevied":message);
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        
        
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notifIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        
      //Define sound URI
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        
        
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher)
			        .setContentTitle(getString(R.string.app_name))
			        .setStyle(new NotificationCompat.InboxStyle().addLine(msg.toString()))
			        .setContentText(message)
			        .setSound(soundUri).setDefaults(Notification.DEFAULT_VIBRATE);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(rid, mBuilder.build());
    }
}