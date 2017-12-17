package com.koretsky.crammer.Notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Михаил on 29.11.2017.
 */

public class setNotifyAlarmReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("myLogs", "RECIEVER_BOOT_HANDLED");
        Intent serviceIntent = new Intent(context, NotifyService.class);
        PendingIntent pIntent = PendingIntent.getService(context, 0, serviceIntent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Calendar cal = Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 8);
        am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTime().getTime() + 86400000, 10000, pIntent);//callService at 8pm
        Log.d("myLogs", String.valueOf(System.currentTimeMillis()));
        am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTime().getTime() + 4000, 10000, pIntent);//callService at 8am
    }
}
