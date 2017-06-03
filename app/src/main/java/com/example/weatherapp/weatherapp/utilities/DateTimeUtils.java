package com.example.weatherapp.weatherapp.utilities;

import android.content.Context;

import com.example.weatherapp.weatherapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class DateTimeUtils {

    private static final int SECOND_IN_MILLIS = 1000;
    private static final int MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60;
    private static final int HOURS_IN_MILLIS = MINUTE_IN_MILLIS * 60;
    public static final int DAY_IN_MILLIS = HOURS_IN_MILLIS * 24;

    private static final String TODAY_AND_TOMORROW_DATE_FORMAT = "k:mm";
    private static final String REST_DAYS_DATE_FORMAT = "EEEE MMMM, dd k:mm";

    private static String convertMillisToFormattedDate(long dateInMillis, String dateFormat) {
        Date date = new Date(dateInMillis * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT-4"));
        return simpleDateFormat.format(date);
    }

    private static boolean isToday(SimpleDateFormat formatter, long dateInMillis) {
        return formatter.format(dateInMillis * 1000L).equals(formatter.format(System.currentTimeMillis()));
    }

    private static boolean isTomorrow(SimpleDateFormat formatter, long dateInMillis) {
        long tomorrowDayInMillis = System.currentTimeMillis() + DAY_IN_MILLIS;
        return formatter.format(dateInMillis * 1000L).equals(formatter.format(tomorrowDayInMillis));
    }

    public static String getReadableDateString(Context context, long dateInMillis) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("GMT-4"));

        if (isToday(formatter, dateInMillis)) {
            String formattedDate = convertMillisToFormattedDate(dateInMillis, TODAY_AND_TOMORROW_DATE_FORMAT);
            return context.getString(R.string.today) + ", " + formattedDate;
        } else if (isTomorrow(formatter, dateInMillis)) {
            String formattedDate = convertMillisToFormattedDate(dateInMillis, TODAY_AND_TOMORROW_DATE_FORMAT);
            return context.getString(R.string.tomorrow) + ", " + formattedDate;
        } else {
            return convertMillisToFormattedDate(dateInMillis, REST_DAYS_DATE_FORMAT);
        }

    }

}
