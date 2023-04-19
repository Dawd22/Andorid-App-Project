package com.example.szallashelyfoglalas;

import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;


public class NotificationHandler {
    private static final String CHANNEL_ID = "room_notification_channel";
    private NotificationManager mManager;
    private Context mContext;
    private final int NOTIFICATION_ID = 0;

    public NotificationHandler(Context context) {
        this.mContext = context;
        this.mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        createChannel();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;

        NotificationChannel channel =
                new NotificationChannel(
                        CHANNEL_ID,
                        "Foglalás",
                        NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(Color.BLUE);
        channel.setDescription("Foglalás történt");
        this.mManager.createNotificationChannel(channel);
    }

    public void send(String message) {
        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(mContext, CHANNEL_ID)
                .setContentTitle("Foglalás")
                .setContentText(message)
                .setSmallIcon(R.drawable.baseline_hotel);

        this.mManager.notify(NOTIFICATION_ID, builder.build());
    }

}
