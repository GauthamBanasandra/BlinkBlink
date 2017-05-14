package UserTime;

import Exceptions.InvalidTimeException;

/**
 * Created by Gautham on 14-05-2017.
 */

public class Time {
    private int hour;
    private int minute;
    private String meridiem;

    public static final String A_M = "a.m";
    public static final String P_M = "p.m";

    public Time(int hour, int minute) throws InvalidTimeException {
        if (hour < 0) {
            throw new InvalidTimeException("hour can not be negative");
        }
        if (minute < 0) {
            throw new InvalidTimeException("minute can not be negative");
        }

        this.hour = hour;
        this.minute = minute;
    }

    public Time(int hour, int minute, String meridiem) throws InvalidTimeException {
        if (hour < 0) {
            throw new InvalidTimeException("hour can not be negative");
        }
        if (minute < 0) {
            throw new InvalidTimeException("minute can not be negative");
        }

        this.hour = hour;
        this.minute = minute;
        this.meridiem = meridiem;
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

    public String getMeridiem() {
        return meridiem;
    }

    public void setMeridiem(String meridiem) {
        this.meridiem = meridiem;
    }

    public String convertTo12Hour() throws Exception {
        if (this.meridiem.equals("")) {
            throw new Exception("meridium field is empty");
        }

        String hour = String.valueOf(this.hour > 12 ? this.hour - 12 : this.hour);
        String minute = String.valueOf(this.minute);
        String meridiem = this.hour >= 12 ? Time.P_M : Time.A_M;
        return hour + ":" + minute + " " + meridiem;
    }

    @Override
    public String toString() {
        return hour + ":" + minute;
    }
}
