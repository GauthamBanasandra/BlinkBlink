package com.care.eye.blinkblink;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import Exceptions.InvalidTimeException;
import UserTime.Time;
import UserTime.TwentyFourHourClock;

public class SettingsActivity extends AppCompatActivity {
    public static final String SETTINGS_PREF = "SETTINGS_PREF";
    public static final String START_TIME = "START_TIME";
    public static final String STOP_TIME = "STOP_TIME";
    public static final String Activity = "SettingsActivity";

    private Button startButton;
    private Button stopButton;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = getSharedPreferences(SETTINGS_PREF, 0);

        startButton = (Button) findViewById(R.id.button_set_start_time);
        stopButton = (Button) findViewById(R.id.button_set_stop_time);

        initButtonText();
        initButtonClickListener();
    }

    private void initButtonClickListener() {
        initButtonClickListener(startButton, START_TIME);
        initButtonClickListener(stopButton, STOP_TIME);
    }

    private void initButtonClickListener(final Button button, final String mode) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        try {
                            TwentyFourHourClock time = new TwentyFourHourClock(hourOfDay, minute);
                            saveSharedPreference(mode, time);
                            Log.d(Activity, "mode:\t" + mode + "\thour:" + hourOfDay + "\tminute:" + minute);
                            button.setText(time.convertToTwelveHourClock().toString());
                        } catch (InvalidTimeException e) {
                            e.printStackTrace();
                            Toast.makeText(SettingsActivity.this, "Invalid " + mode + " retrieved", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, hour, minute, false);
                timePickerDialog.setTitle("Set start time");
                timePickerDialog.show();
            }
        });
    }

    private void saveSharedPreference(String mode, TwentyFourHourClock time) {
        SharedPreferences.Editor settingsEditor = settings.edit();
        settingsEditor.putString(mode, time.toString());
        settingsEditor.apply();
    }

    private void initButtonText() {
        try {
            initButtonText(settings.getString(START_TIME, ""), startButton);
        } catch (InvalidTimeException e) {
            startButton.setText("Invalid start time");
        }
        try {
            initButtonText(settings.getString(STOP_TIME, ""), stopButton);
        } catch (InvalidTimeException e) {
            stopButton.setText("Invalid stop time");
        }
    }

    private void initButtonText(String timeStr, Button button) throws InvalidTimeException {
        if (timeStr.equals("")) {
            button.setText("Not set");
        } else {
            TwentyFourHourClock time = new TwentyFourHourClock(timeStr);
            button.setText(time.convertToTwelveHourClock().toString());
        }
    }
}
