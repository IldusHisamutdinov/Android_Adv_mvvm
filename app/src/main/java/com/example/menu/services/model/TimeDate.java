package com.example.menu.services.model;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@RequiresApi(api = Build.VERSION_CODES.O)
public class TimeDate {

    // установим дату
    public static String dateNow() {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd.MM, H:mm");
        return sdf.format(calendar.getTime());
    }


    static final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("H:mm", Locale.ENGLISH)
            .withZone(ZoneId.systemDefault());

    public static String formatTime(Instant time) {
        return formatter.format(time);
    }

    public static String timeStamp(int timeStamp) {

        Date date = new Date(timeStamp * 1000L);
        SimpleDateFormat jdf = new SimpleDateFormat("HH:mm");
        jdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        String java_date = jdf.format(date);
        return java_date;

    }

}
