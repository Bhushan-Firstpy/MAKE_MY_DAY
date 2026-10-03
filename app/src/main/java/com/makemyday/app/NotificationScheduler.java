package com.makemyday.app;

import android.content.Context;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class NotificationScheduler {
    private static final String UNIQUE_WORK_NAME = "mmd_notification_worker";

    public static void schedule(Context context, NotificationPreferencesModel preferences) {
        if (context == null || preferences == null) {
            return;
        }

        WorkManager workManager = WorkManager.getInstance(context.getApplicationContext());
        workManager.cancelUniqueWork(UNIQUE_WORK_NAME);

        if (!preferences.isNotificationsEnabled()) {
            return;
        }

        NotificationFrequency frequency = NotificationFrequency.fromKey(preferences.getFrequency());
        if (frequency == NotificationFrequency.OFF) {
            return;
        }

        Constraints constraints = new Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
            NotificationWorker.class,
            frequency.getIntervalMinutes(),
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 15, TimeUnit.MINUTES)
            .build();

        workManager.enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        );
    }

    public static void triggerImmediateTest(Context context) {
        if (context == null) {
            return;
        }

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(NotificationWorker.class)
            .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.NOT_REQUIRED).build())
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
            .addTag("mmd_test_notification")
            .build();

        WorkManager.getInstance(context.getApplicationContext())
            .enqueue(request);
    }
}
