package com.care.eye.blinkblink;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import Exceptions.InvalidTimeException;
import UserTime.Time;
import UserTime.TwentyFourHourClock;

public class SettingsActivity extends AppCompatActivity {
    private enum ButtonState {SAVE, CANCEL}

    public static final String Activity = "SettingsActivity";
    public AlarmManager alarmManager;
    public PendingIntent pendingIntent;

    ButtonState saveCancelState;
    private int buzzInterval = -1;
    private Button startButton;
    private Button stopButton;
    private Button buzzIntervalButton;
    private Button saveCancelButton;
    private SharedPreferences settings;

    private TwentyFourHourClock startTime = null;
    private TwentyFourHourClock stopTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = getSharedPreferences(Time.SETTINGS_PREF, 0);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        startButton = (Button) findViewById(R.id.button_set_start_time);
        stopButton = (Button) findViewById(R.id.button_set_stop_time);
        buzzIntervalButton = (Button) findViewById(R.id.button_set_buzz_interval);
        saveCancelButton = (Button) findViewById(R.id.button_save_cancel);

        initButtonText();
        initButtonClickListener();
    }

    private void initButtonClickListener() {
        initButtonClickListener(startButton, Time.START_TIME);
        initButtonClickListener(stopButton, Time.STOP_TIME);
        initButtonClickListener(buzzIntervalButton, Time.BUZZ_INTERVAL);
        initButtonClickListener(saveCancelButton, Time.NEXT_BUZZ);
    }

    private void initButtonClickListener(final Button button, final String mode) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mode) {
                    case Time.START_TIME:
                    case Time.STOP_TIME:
                        Calendar currentTime = Calendar.getInstance();
                        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = currentTime.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                try {
                                    TwentyFourHourClock time = new TwentyFourHourClock(hourOfDay, minute);
                                    if (mode.equals(Time.START_TIME)) {
                                        startTime = time;
                                    } else if (mode.equals(Time.STOP_TIME)) {
                                        stopTime = time;
                                    } else {
                                        // Impossible case.
                                        assert true;
                                    }
                                    Log.d(Activity, "mode:\t" + mode + "\thour:" + hourOfDay + "\tminute:" + minute);
                                    // Set the new time on the button.
                                    button.setText(time.convertToTwelveHourClock().toString());
                                } catch (InvalidTimeException e) {
                                    e.printStackTrace();
                                    Toast.makeText(SettingsActivity.this, "Invalid " + mode + " retrieved", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, hour, minute, false);
                        timePickerDialog.setTitle("Set start time");
                        timePickerDialog.show();
                        break;
                    case Time.BUZZ_INTERVAL:
                        LinearLayout linearLayout = new LinearLayout(SettingsActivity.this);
                        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        layoutInflater.inflate(R.layout.buzz_interval_dialog, linearLayout);

                        final NumberPicker numberPicker = (NumberPicker) linearLayout.findViewById(R.id.numberPicker_set_buzz_interval);
                        numberPicker.setMinValue(1);
                        numberPicker.setMaxValue(60);

                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                        builder.setTitle("Buzz interval");
                        builder.setView(linearLayout);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO :   A keyboard pops up when numberPickerDialog is shown. However, the number that is keyed in isn't captured. Check this.
                                buzzInterval = numberPicker.getValue();
                                Log.d(Activity, "buzz interval:\t" + numberPicker.getValue());
                                button.setText(Time.getMinutesString(numberPicker.getValue()));
                            }
                        });

                        AlertDialog numberPickerDialog = builder.create();
                        numberPickerDialog.show();
                        break;
                    case Time.NEXT_BUZZ:
                        switch (saveCancelState) {
                            case SAVE:
                                boolean save = true;
                                if (startTime != null) {
                                    saveSharedPreference(Time.START_TIME, startTime);
                                } else {
                                    Toast.makeText(SettingsActivity.this, "Start time not set", Toast.LENGTH_SHORT).show();
                                    save = false;
                                }

                                if (stopTime != null) {
                                    saveSharedPreference(Time.STOP_TIME, stopTime);
                                } else {
                                    Toast.makeText(SettingsActivity.this, "Stop time not set", Toast.LENGTH_SHORT).show();
                                    save = false;
                                }

                                if (buzzInterval != -1) {
                                    saveSharedPreference(Time.BUZZ_INTERVAL, buzzInterval);
                                } else {
                                    Toast.makeText(SettingsActivity.this, "Buzz interval not set", Toast.LENGTH_SHORT).show();
                                    save = false;
                                }

                                if (save) {
                                    long nextBuzz = SystemClock.elapsedRealtime() + buzzInterval * 60 * 1000;
                                    saveSharedPreference(Time.NEXT_BUZZ, nextBuzz);
                                    Log.d(Activity, "Next alarm in " + nextBuzz);
                                    saveCancelButton.setText("Cancel");
                                    saveCancelState = ButtonState.CANCEL;
                                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, nextBuzz, pendingIntent);
                                }
                                break;
                            case CANCEL:
                                long _nextBuzz = settings.getLong(Time.NEXT_BUZZ, -1L);
                                Log.d(Activity, "Cancelled buzz. Next buzz was supposed to be " + _nextBuzz);
                                alarmManager.cancel(pendingIntent);
                                saveSharedPreference(Time.NEXT_BUZZ, -1L);
                                saveCancelButton.setText("Save");
                                saveCancelState = ButtonState.SAVE;
                                break;
                            default:
                                assert true;
                        }
                        break;
                    default:
                        assert true;
                }
            }
        });
    }

    private void saveSharedPreference(String mode, TwentyFourHourClock time) {
        SharedPreferences.Editor settingsEditor = settings.edit();
        settingsEditor.putString(mode, time.toString());
        settingsEditor.apply();
    }

    private void saveSharedPreference(String mode, int data) {
        SharedPreferences.Editor settingsEditor = settings.edit();
        settingsEditor.putInt(mode, data);
        settingsEditor.apply();
    }

    private void saveSharedPreference(String mode, long data) {
        SharedPreferences.Editor settingsEditor = settings.edit();
        settingsEditor.putLong(mode, data);
        settingsEditor.apply();
    }

    private void initButtonText() {
        try {
            String timeStr = settings.getString(Time.START_TIME, "");
            if (timeStr.equals("")) {
                startButton.setText("Not set");
            } else {
                startTime = new TwentyFourHourClock(timeStr);
                startButton.setText(startTime.convertToTwelveHourClock().toString());
            }
        } catch (InvalidTimeException e) {
            startButton.setText("Invalid start time");
        }
        try {
            String timeStr = settings.getString(Time.STOP_TIME, "");
            if (timeStr.equals("")) {
                stopButton.setText("Not set");
            } else {
                stopTime = new TwentyFourHourClock(timeStr);
                stopButton.setText(stopTime.convertToTwelveHourClock().toString());
            }
        } catch (InvalidTimeException e) {
            stopButton.setText("Invalid stop time");
        }

        int buzzInterval = settings.getInt(Time.BUZZ_INTERVAL, -1);
        if (buzzInterval == -1) {
            buzzIntervalButton.setText("Not set");
        } else {
            buzzIntervalButton.setText(Time.getMinutesString(buzzInterval));
            this.buzzInterval = buzzInterval;
        }

        long nextBuzz = settings.getLong(Time.NEXT_BUZZ, -1);
        if (nextBuzz == -1) {
            saveCancelButton.setText("Save");
            saveCancelState = ButtonState.SAVE;
        } else {
            saveCancelButton.setText("Cancel");
            saveCancelState = ButtonState.CANCEL;
        }
    }
}
