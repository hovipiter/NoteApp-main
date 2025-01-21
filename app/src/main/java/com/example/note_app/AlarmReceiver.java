package com.example.note_app;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private NotificationManager notificationManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        Toast.makeText(context, "Báo thức: " + title, Toast.LENGTH_LONG).show();
// Hiển thị thông báo tại thời điểm xác định
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        NotificationChannel channel = new NotificationChannel("default", "Channel name", NotificationManager.IMPORTANCE_DEFAULT);
//        notificationManager.createNotificationChannel(channel);
//    }
//    Notification notification = new Notification.Builder(context, "default")
//            .setContentTitle("Nhắc nhở: " + title)
////            .setContentText(date+" "+time)
//            .setSmallIcon(R.drawable.alarm)
//            .setAutoCancel(true)
//            .build();
//
//    notificationManager.notify(0, notification);

        // Phát âm thanh từ raw resource (ví dụ: sound.mp3 trong thư mục raw)
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.sound);
        mediaPlayer.start();

        // Rung thiết bị
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(2000); // Rung trong 2 giây
        }
    }
}
