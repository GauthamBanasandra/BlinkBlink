package com.care.eye.blinkblink;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import Exceptions.InvalidTimeException;
import UserTime.TwentyFourHourClock;

public class SettingsActivity extends AppCompatActivity {
    public static final String SETTINGS_PREF = "SETTINGS_PREF";
    public static final String START_TIME = "START_TIME";
    public static final String STOP_TIME = "STOP_TIME";
    public static final String BUZZ_INTERVAL = "BUZZ_INTERVAL";
    public static final String Activity = "SettingsActivity";

    private Button startButton;
    private Button stopButton;
    private Button buzzIntervalButton;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = getSharedPreferences(SETTINGS_PREF, 0);

        startButton = (Button) findViewById(R.id.button_set_start_time);
        stopButton = (Button) findViewById(R.id.button_set_stop_time);
        buzzIntervalButton = (Button) findViewById(R.id.button_set_buzz_interval);

        initButtonText();
        initButtonClickListener();
    }

    private void initButtonClickListener() {
        initButtonClickListener(startButton, START_TIME);
        initButtonClickListener(stopButton, STOP_TIME);
        initButtonClickListener(buzzIntervalButton, BUZZ_INTERVAL);
    }

    private void initButtonClickListener(final Button button, final String mode) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mode) {
                    case START_TIME:
                    case STOP_TIME:
                        Calendar currentTime = Calendar.getInstance();
                        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = currentTime.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                try {
                                    TwentyFourHourClock time = new TwentyFourHourClock(hourOfDay, minute);
                                    // Save the time in SharedPreference.
                                    saveSharedPreference(mode, time);
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
                    case BUZZ_INTERVAL:
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
                                saveSharedPreference(mode, numberPicker.getValue());
                                Log.d(Activity, "buzz interval:\t" + numberPicker.getValue());
                                button.setText(numberPicker.getValue() + " minutes");
                            }
                        });

                        AlertDialog numberPickerDialog = builder.create();
                        numberPickerDialog.show();
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

        int buzzInterval = settings.getInt(BUZZ_INTERVAL, -1);
        if (buzzInterval == -1) {
            buzzIntervalButton.setText("Not set");
        } else {
            buzzIntervalButton.setText(String.valueOf(buzzInterval) + " minutes");
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
