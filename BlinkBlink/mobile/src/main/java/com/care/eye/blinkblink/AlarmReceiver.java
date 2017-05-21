package com.care.eye.blinkblink;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by Gautham on 21-05-2017.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver {
    public static final String Activity = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Activity, "Alarm received");
    }
}
