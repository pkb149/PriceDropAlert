package com.pkb149.pricedropalert;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.pkb149.pricedropalert.R.id.notification;

/**
 * Created by CoderGuru on 06-10-2017.
 */

public class MyFirebaseMessagingService  extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        //Notification notification= new Notification();
        //Notification notification = new Notification(getResources().getDrawable(R.drawable.logo));

        Intent notificationIntent = new Intent(getApplicationContext(), NotificationsActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
                notificationIntent, 0);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
        notification.setSmallIcon(R.drawable.logo);
        notification.setContentIntent(intent);
        notification.addAction(R.drawable.logo,"PKB",intent);
        notification.build();
        //notification.setLatestEventInfo(getApplicationContext(), intent);
        //notification.flags |= Notification.FLAG_AUTO_CANCEL;


        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}
