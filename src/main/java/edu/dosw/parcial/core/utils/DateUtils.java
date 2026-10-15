package edu.dosw.parcial.core.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public final class DateUtils {

    private DateUtils() {
    }

    public static DayOfWeek getDayOfWeek(LocalDate date) {
        return date != null ? date.getDayOfWeek() : null;
    }

    public static boolean isTopOfTheHour(LocalTime time) {
        return time != null && time.getMinute() == 0 && time.getSecond() == 0;
    }
}
