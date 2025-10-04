package com.example.carshering.utils;

public class PrefManager {
    private static final String PREF_NAME = "app_preferences";
    private static final String KEY_FIRST_TIME = "first_time_launch";

    private final android.content.Context context;
    private final android.content.SharedPreferences prefs;

    public PrefManager(android.content.Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, android.content.Context.MODE_PRIVATE);
    }

    public boolean isFirstTimeLaunch() {
        return prefs.getBoolean(KEY_FIRST_TIME, true);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        prefs.edit().putBoolean(KEY_FIRST_TIME, isFirstTime).apply();
    }
}
