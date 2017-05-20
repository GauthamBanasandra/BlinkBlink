package UserTime;

import Exceptions.InvalidTimeException;

/**
 * Created by Gautham on 20-05-2017.
 */

public class TwentyFourHourClock extends Time {
    public TwentyFourHourClock(int hour, int minute) throws InvalidTimeException {
        super(hour, minute);
        if (hour > 23) {
            throw new InvalidTimeException("Hours can not exceed 23");
        }
    }

    public TwentyFourHourClock(String time) throws InvalidTimeException {
        super(time);
    }

    public TwelveHourClock convertToTwelveHourClock() throws InvalidTimeException {
        int hour = super.getHour();
        hour = hour > 12 ? hour - 12 : hour;
        hour = hour == 0 ? 12 : hour;
        String meridiem = super.getHour() >= 12 ? TwelveHourClock.P_M : TwelveHourClock.A_M;
        return new TwelveHourClock(hour, super.getMinute(), meridiem);
    }
}
