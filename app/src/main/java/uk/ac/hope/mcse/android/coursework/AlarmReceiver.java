package uk.ac.hope.mcse.android.coursework;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "alarm_channel";
    private static final String CHANNEL_NAME = "Alarm Notifications";
    public static final String ACTION_SHOW_ALARM    = "uk.ac.hope.mcse.android.coursework.SHOW_ALARM";
    public static final String ACTION_DISMISS_ALARM = "uk.ac.hope.mcse.android.coursework.DISMISS_ALARM";
    private static final int NOTIF_ID = 1001;
    private static Ringtone ringtone;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_DISMISS_ALARM.equals(action)) {
            // 1) Stop the looping ringtone
            if (ringtone != null && ringtone.isPlaying()) {
                ringtone.stop();
                ringtone = null;
            }
            // 2) Dismiss the notification
            NotificationManager nm = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(NOTIF_ID);
            return;
        }

        createNotificationChannel(context);


        String contentText;
        int hour = intent.getIntExtra("hour", -1);
        int minute = intent.getIntExtra("minute", -1);
        if (hour >= 0 && minute >= 0) {
            contentText = String.format("Alarm for %02d:%02d", hour, minute);
        } else {
            contentText = "Alarm";
        }

        Intent dismissIntent = new Intent(context, AlarmReceiver.class)
                .setAction(ACTION_DISMISS_ALARM);
        PendingIntent dismissPI = PendingIntent.getBroadcast(
                context, 0, dismissIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("Alarm")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setOngoing(false)
                .setAutoCancel(true)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel,
                        "Dismiss Alarm", dismissPI);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify((int) System.currentTimeMillis(), builder.build());

        Uri alarmSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(context, alarmSoundUri);
        if (ringtone != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ringtone.setLooping(true);
            }
            ringtone.play();
        }
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

