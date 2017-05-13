package com.example.weatherapp.weatherapp.utilities;

import android.content.Context;
import android.text.format.DateUtils;
import com.example.weatherapp.weatherapp.R;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public final class DateTimeUtils {

    private static final int SECOND_IN_MILLIS = 1000;
    private static final int MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60;
    private static final int HOURS_IN_MILLIS = MINUTE_IN_MILLIS * 60;
    public static final int DAY_IN_MILLIS = HOURS_IN_MILLIS * 24;

    private static long getDayNumber(long date) {
        TimeZone tz = TimeZone.getDefault();
        int gmtOffset = tz.getOffset(date);
        return (date + gmtOffset) / DAY_IN_MILLIS;
    }

    public static long normalizeDate(long date) {
        return date / DAY_IN_MILLIS * DAY_IN_MILLIS;
    }

    private static long getLocalDateFromUTC(long utcDate) {
        TimeZone tz = TimeZone.getDefault();
        int gmtOffset = tz.getOffset(utcDate);
        return utcDate - gmtOffset;
    }

    public static long getUTCDateFromLocal(long localDate) {
        TimeZone tz = TimeZone.getDefault();
        int gmtOffset = tz.getOffset(localDate);
        return localDate - gmtOffset;
    }

    private static String getDayName(Context context, long dateInMillis) {
        long dayNumber = getDayNumber(dateInMillis);
        long currentDayNumber = getDayNumber(System.currentTimeMillis());
        if (dayNumber == currentDayNumber) {
            return context.getString(R.string.today);
        } else if (dayNumber == currentDayNumber + 1) {
            return context.getString(R.string.tomorrow);
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            return dateFormat.format(dateInMillis);
        }
    }

    private static String getReadableDateString(Context context, long timeInMillis) {
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY;
        return DateUtils.formatDateTime(context, timeInMillis, flags);
    }

    public static String getFriendlyDateString(Context context, long dateInMillis, boolean showFullDate) {

        long localDate = getLocalDateFromUTC(dateInMillis);
        long dayNumber = getDayNumber(localDate);
        long currentDayNumber = getDayNumber(System.currentTimeMillis());

        if (dayNumber == currentDayNumber || showFullDate) {
            String dayName = getDayName(context, localDate);
            String readableDate = getReadableDateString(context, localDate);
            if (dayNumber - currentDayNumber < 2) {
                String localizedDayName = new SimpleDateFormat("EEEE", Locale.getDefault()).format(localDate);
                return readableDate.replace(localizedDayName, dayName);
            } else {
                return readableDate;
            }
        } else if (dayNumber < currentDayNumber + 7) {
            return getDayName(context, localDate);
        } else {
            int flags = DateUtils.FORMAT_SHOW_DATE
                    | DateUtils.FORMAT_NO_YEAR
                    | DateUtils.FORMAT_ABBREV_ALL
                    | DateUtils.FORMAT_SHOW_WEEKDAY;
            return DateUtils.formatDateTime(context, localDate, flags);
        }

    }

}
