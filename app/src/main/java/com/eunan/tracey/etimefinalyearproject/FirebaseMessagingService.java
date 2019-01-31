package com.eunan.tracey.etimefinalyearproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = remoteMessage.getNotification().getTitle();
        String message = remoteMessage.getNotification().getBody();

        String clickAction = remoteMessage.getNotification().getClickAction();

        String fromUserId = remoteMessage.getData().get("from_user_id");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.timesheet)
                .setContentTitle(title)
                .setContentText(message);


        Intent resultIntent = new Intent(clickAction);
        resultIntent.putExtra("from_user_id",fromUserId);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        int notification_id = (int) System.currentTimeMillis();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(notification_id,builder.build());
    }
}
