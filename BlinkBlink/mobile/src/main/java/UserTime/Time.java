package UserTime;

import java.text.DecimalFormat;

import Exceptions.InvalidTimeException;

/**
 * Created by Gautham on 14-05-2017.
 */

public class Time {
    private int hour;
    private int minute;

    Time() {
        this.hour = 0;
        this.minute = 0;
    }

    public Time(int hour, int minute) throws InvalidTimeException {
        if (hour < 0) {
            throw new InvalidTimeException("hour can not be negative");
        }
        if (minute < 0 || minute > 59) {
            throw new InvalidTimeException("minute can not be negative");
        }

        this.hour = hour;
        this.minute = minute;
    }

    public Time(String timeStr) throws InvalidTimeException {
        String[] time = timeStr.split(":");
        this.hour = Integer.parseInt(time[0]);
        this.minute = Integer.parseInt(time[1]);

        if (this.hour < 0) {
            throw new InvalidTimeException("hour can not be negative");
        }
        if (this.minute < 0) {
            throw new InvalidTimeException("minute can not be negative");
        }
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) throws InvalidTimeException {
        if (hour < 0) {
            throw new InvalidTimeException("hour can not be negative");
        }

        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) throws InvalidTimeException {
        if (minute < 0) {
            throw new InvalidTimeException("minute can not be negative");
        }

        this.minute = minute;
    }

    @Override
    public String toString() {
        DecimalFormat format = new DecimalFormat("00");
        return format.format(hour)+ ":" + format.format(minute);
    }
}
