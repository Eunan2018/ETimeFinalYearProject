package com.eunan.tracey.etimefinalyearproject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

public class TimeSheetMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "TSMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String messageTitle = remoteMessage.getNotification().getTitle();
        String messageBody = remoteMessage.getNotification().getBody();

        String clickAction = remoteMessage.getNotification().getClickAction();

//        String comments = remoteMessage.getData().get("ts_comments");
//        String days = remoteMessage.getData().get("ts_days");
//        String hours = remoteMessage.getData().get("ts_hour");
//        String projects = remoteMessage.getData().get("ts_projects");
//        String userName = remoteMessage.getData().get("userName");
//        String id = remoteMessage.getData().get("ts_id");
//
//        Log.d(TAG, "onMessageReceived: " + comments + " "  + days + " " + hours + " " + projects );

        NotificationCompat.Builder builder =  new NotificationCompat.Builder(this,getString(R.string.ts_notification_channel_id))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(messageTitle)
                .setContentText(messageBody);

        Intent resultIntent = new Intent(clickAction);
//        resultIntent.putExtra("ts_comments",comments);
//        resultIntent.putExtra("ts_days",days);
//        resultIntent.putExtra("ts_hours",hours);
//        resultIntent.putExtra("ts_projects",projects);
//        resultIntent.putExtra("ts_id",id);
//        resultIntent.putExtra("userName",userName);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        int notification_id = (int) System.currentTimeMillis();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(notification_id,builder.build());
    }
}
