package uk.ac.hope.mcse.android.coursework;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "alarm_channel";
    private static final String CHANNEL_NAME = "Alarm Notifications";

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context);

        String contentText;
        int hour = intent.getIntExtra("hour", -1);
        int minute = intent.getIntExtra("minute", -1);
        if (hour >= 0 && minute >= 0) {
            contentText = String.format("Alarm for %02d:%02d", hour, minute);
        } else {
            contentText = "Alarm triggered";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("Alarm")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = context.getSystemService(NotificationManager.class);
            if (nm.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

                channel.setDescription("Channel for alarm notifications");
                nm.createNotificationChannel(channel);
            }
        }
    }
}

