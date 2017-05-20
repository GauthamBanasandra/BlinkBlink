package UserTime;

import Exceptions.InvalidTimeException;

/**
 * Created by Gautham on 20-05-2017.
 */

public class TwelveHourClock extends Time {
    public static final String A_M = "a.m";
    public static final String P_M = "p.m";
    private String meridiem;

    public TwelveHourClock(int hour, int minute, String meridiem) throws InvalidTimeException {
        super(hour, minute);
        if (hour > 12 || hour < 1) {
            throw new InvalidTimeException("Hour should be between 1 and 12");
        }

        if (!meridiem.equals(A_M) && !meridiem.equals(P_M)) {
            throw new InvalidTimeException("Invalid meridiem: " + meridiem);
        }

        this.meridiem = meridiem;
    }

    public TwentyFourHourClock convertToTwelveHourClock() throws InvalidTimeException {
        int hour = super.getHour();
        hour = meridiem.equals(P_M) && hour < 12 ? hour + 12 : hour;
        hour = meridiem.equals(A_M) && hour == 12 ? 0 : hour;

        return new TwentyFourHourClock(hour, super.getMinute());
    }

    @Override
    public String toString() {
        return super.toString() + ' ' + meridiem;
    }
}
