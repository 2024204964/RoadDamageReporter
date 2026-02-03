package com.example.roaddamagereporter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessageReceiver extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "report_push_channel";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        android.util.Log.d("FCM_TOKEN", "Token: " + token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = "New Road Damage Report";
        String message = "A new report has been submitted.";

        if (remoteMessage.getNotification() != null) {
            if (remoteMessage.getNotification().getTitle() != null)
                title = remoteMessage.getNotification().getTitle();
            if (remoteMessage.getNotification().getBody() != null)
                message = remoteMessage.getNotification().getBody();
        }

        if (remoteMessage.getData().size() > 0) {
            if (remoteMessage.getData().get("title") != null)
                title = remoteMessage.getData().get("title");
            if (remoteMessage.getData().get("body") != null)
                message = remoteMessage.getData().get("body");
        }

        sendNotification(title, message);
    }

    private void sendNotification(String title, String message) {
        Context context = getApplicationContext();
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Report Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for push notifications for new reports");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 250, 500});
            channel.setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI,
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
            );
            manager.createNotificationChannel(channel);
        }

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) vibrator.vibrate(500);

        // Open ReportListActivity when tapped
        Intent intent = new Intent(context, ReportListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("fromNotification", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo1)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
