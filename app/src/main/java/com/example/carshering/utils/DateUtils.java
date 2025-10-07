package com.example.carshering.utils;

import java.util.Calendar;
import java.util.regex.Pattern;

public class DateUtils {
    private static final String DATE_PATTERN = "\\d{2}/\\d{2}/\\d{4}";

    public static boolean isValidDate(String date) {
        if (date == null || !Pattern.matches(DATE_PATTERN, date)) {
            return false;
        }
        String[] parts = date.split("/");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        if (month < 1 || month > 12) {
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);

        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        return day >= 1 && day <= maxDay;
    }
}