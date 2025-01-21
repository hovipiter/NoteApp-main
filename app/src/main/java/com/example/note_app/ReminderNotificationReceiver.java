package com.example.note_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ReminderNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");

        // Hiển thị thông báo
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Channel name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new Notification.Builder(context, "default")
                .setContentTitle("Reminder: " + title)
                .setContentText("Nội dung thông báo")
                .setSmallIcon(R.drawable.alarm)
                .setAutoCancel(true)
                .build();

        if (notificationManager != null) {
            notificationManager.notify(0, notification);
        }
    }
}
