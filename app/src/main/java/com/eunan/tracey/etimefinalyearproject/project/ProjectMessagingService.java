package com.eunan.tracey.etimefinalyearproject.project;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.eunan.tracey.etimefinalyearproject.R;
import com.google.firebase.messaging.RemoteMessage;

public class ProjectMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "PrMessagingService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String messageTitle = remoteMessage.getNotification().getTitle();
        String messageBody = remoteMessage.getNotification().getBody();

        String clickAction = remoteMessage.getNotification().getClickAction();

        String fromUserId = remoteMessage.getData().get("from_user_id");
        Log.d(TAG, "onMessageReceived: in" + fromUserId);
        NotificationCompat.Builder builder =  new NotificationCompat.Builder(this,getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(messageTitle)
                .setContentText(messageBody);


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
