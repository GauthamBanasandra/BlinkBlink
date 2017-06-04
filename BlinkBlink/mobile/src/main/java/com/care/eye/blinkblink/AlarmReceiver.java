package com.care.eye.blinkblink;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import UserTime.Time;

/**
 * Created by Gautham on 21-05-2017.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver {
    public static final String Activity = "AlarmReceiver";

    private SharedPreferences settings;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Activity, "Alarm received");
        vibrate(context);
        settings = context.getSharedPreferences(Time.SETTINGS_PREF, 0);
        long nextBuzz = settings.getLong(Time.NEXT_BUZZ, -1L);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent _intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, _intent, 0);

        if (nextBuzz != -1) {
            int buzzInterval = settings.getInt(Time.BUZZ_INTERVAL, 0);
            nextBuzz += buzzInterval * 60 * 1000;
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, nextBuzz, pendingIntent);
            saveSharedPreference(Time.NEXT_BUZZ, nextBuzz);
        } else {
            alarmManager.cancel(pendingIntent);
        }
    }

    private void vibrate(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        boolean hasVibrator = vibrator.hasVibrator();
        Log.d(Activity, "Vibrator support: " + hasVibrator);
        if (hasVibrator) {
            vibrator.vibrate(1000);
        }
    }

    private void saveSharedPreference(String mode, long data) {
        SharedPreferences.Editor settingsEditor = settings.edit();
        settingsEditor.putLong(mode, data);
        settingsEditor.apply();
    }
}
