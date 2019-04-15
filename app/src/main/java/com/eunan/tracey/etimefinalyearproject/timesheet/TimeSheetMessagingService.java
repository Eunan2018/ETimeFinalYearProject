package com.eunan.tracey.etimefinalyearproject.timesheet;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.eunan.tracey.etimefinalyearproject.R;
import com.google.firebase.messaging.RemoteMessage;


public class TimeSheetMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "TSMessagingService";
    Context context;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String messageTitle = remoteMessage.getNotification().getTitle();
        String messageBody = remoteMessage.getNotification().getBody();

        String clickAction = remoteMessage.getNotification().getClickAction();
        String id = remoteMessage.getData().get("employeeId");

        Log.d(TAG, "onMessageReceived: in: " +  id);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.ts_notification_channel_id))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(messageTitle)
                .setContentText(messageBody);

        Intent resultIntent = new Intent(clickAction);
        resultIntent.putExtra("employeeId", id);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        int notification_id = (int) System.currentTimeMillis();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(notification_id, builder.build());
    }
}
