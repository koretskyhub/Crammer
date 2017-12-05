package com.koretsky.crammer;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Михаил on 29.11.2017.
 */

public class NotifiService extends IntentService {

    final String LOG_TAG = "myLogs";

    public NotifiService() {
        super("notifyService");
    }


    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "NotifyService_onCreate");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "NotifyService_onHandleIntent");
        DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        for (String s : Arrays.asList(getFilesDir().list())
                ) {
            try {
                Date d = format.parse(s.substring(0, s.indexOf("_") - 1));
                if (d.after(new Date())) {
                    createNotification();
                    break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    private void createNotification() {
        Context context = getApplicationContext();

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_create_package)
                .setTicker("Time to learn!")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Crammer")
                .setContentText("You have to repeat material"); // Текст уведомления
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "NotifyServiceon_Destroy");
    }
}
