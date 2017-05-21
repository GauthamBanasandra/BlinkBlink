package com.care.eye.blinkblink;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import Exceptions.InvalidTimeException;
import UserTime.Time;
import UserTime.TwentyFourHourClock;

public class AlarmDisplayActivity extends AppCompatActivity {
    public static final String Activity = "AlarmDisplayActivity";

    private SharedPreferences settings;
    private TwentyFourHourClock startTime;
    private TwentyFourHourClock stopTime;
    private int buzzInterval;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_display);

        settings = getSharedPreferences(Time.SETTINGS_PREF, 0);

        readSavedTimes();
        Log.d(Activity, "start time:" + startTime.toString());
        Log.d(Activity, "stop time:" + stopTime.toString());

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 10 * 1000, pendingIntent);
    }

    private void readSavedTimes() {
        String startTime = settings.getString(Time.START_TIME, "");
        String stopTime = settings.getString(Time.STOP_TIME, "");
        this.buzzInterval = settings.getInt(Time.BUZZ_INTERVAL, -1);

        // Some parameter is missing. Immediately goto SettingsActivity.
        if (startTime.equals("") || stopTime.equals("") || buzzInterval == -1) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        try {
            this.startTime = new TwentyFourHourClock(startTime);
            this.stopTime = new TwentyFourHourClock(stopTime);
        } catch (InvalidTimeException e) {
            e.printStackTrace();
        }
    }
}
