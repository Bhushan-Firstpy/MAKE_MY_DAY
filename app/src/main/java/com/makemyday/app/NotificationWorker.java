package com.makemyday.app;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;
import java.util.List;

public class NotificationWorker extends Worker {
    private static final String TAG = "MmdNotificationWorker";

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        NotificationPreferencesModel preferences = NotificationPreferenceStore.load(context);

        if (!preferences.isNotificationsEnabled()) {
            return Result.success();
        }

        if (shouldSkipQuietHours(preferences)) {
            return Result.success();
        }

        NotificationFrequency frequency = NotificationFrequency.fromKey(preferences.getFrequency());
        if (frequency == NotificationFrequency.OFF) {
            return Result.success();
        }

        NotificationQuoteSelector selector = new NotificationQuoteSelector();
        NotificationQuote quote = selector.selectQuote(preferences);
        if (quote == null) {
            return Result.failure();
        }

        NotificationHelper.sendNotification(context, quote, false);
        NotificationHelper.updateRecentQuoteHistory(context, quote);
        Log.d(TAG, "Notification dispatched: " + quote.getText());
        return Result.success();
    }

    private boolean shouldSkipQuietHours(NotificationPreferencesModel preferences) {
        if (preferences == null || !preferences.isQuietHoursEnabled()) {
            return false;
        }

        try {
            String start = preferences.getQuietHoursStart();
            String end = preferences.getQuietHoursEnd();
            if (start == null || end == null) {
                return false;
            }

            java.util.Calendar calendar = java.util.Calendar.getInstance();
            int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
            int minute = calendar.get(java.util.Calendar.MINUTE);
            int currentMinutes = (hour * 60) + minute;

            int startMinutes = parseTimeToMinutes(start);
            int endMinutes = parseTimeToMinutes(end);

            if (startMinutes < endMinutes) {
                return currentMinutes >= startMinutes && currentMinutes < endMinutes;
            }

            return currentMinutes >= startMinutes || currentMinutes < endMinutes;
        } catch (Exception e) {
            return false;
        }
    }

    private int parseTimeToMinutes(String timeValue) {
        if (timeValue == null || !timeValue.contains(":")) {
            return 22 * 60;
        }

        String[] parts = timeValue.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        return (hour * 60) + minute;
    }
}
