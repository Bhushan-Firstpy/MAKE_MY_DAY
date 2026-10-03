package com.makemyday.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.List;

public class NotificationHelper {
    public static final String CHANNEL_ID = "daily_inspiration";
    public static final String EXTRA_QUOTE_TEXT = "mmd_quote_text";
    public static final String EXTRA_QUOTE_ID = "mmd_quote_id";

    public static void sendNotification(Context context, NotificationQuote quote, boolean isTest) {
        if (context == null || quote == null) {
            return;
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Daily Inspiration",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("MAKE MY DAY daily inspiration notifications");
            manager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(EXTRA_QUOTE_TEXT, quote.getText());
        intent.putExtra(EXTRA_QUOTE_ID, quote.getId());
        intent.putExtra("mmd_is_test_notification", isTest);

        PendingIntent pendingIntent = PendingIntent.getActivity(
            context,
            quote.getId().hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        String bodyText = quote.getText();
        if (isTest) {
            bodyText = "Test: " + bodyText;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentTitle("MAKE MY DAY")
            .setContentText(bodyText)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(bodyText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent);

        manager.notify(1001 + quote.getId().hashCode(), builder.build());
    }

    public static void scheduleLocalReminder(Context context) {
        NotificationPreferencesModel preferences = NotificationPreferenceStore.load(context);
        if (!preferences.isNotificationsEnabled()) {
            return;
        }
        NotificationScheduler.schedule(context, preferences);
    }

    public static void updateRecentQuoteHistory(Context context, NotificationQuote quote) {
        if (context == null || quote == null) {
            return;
        }

        NotificationPreferencesModel preferences = NotificationPreferenceStore.load(context);
        List<String> recent = preferences.getRecentlyDeliveredQuoteIds();
        if (recent == null) {
            recent = new java.util.ArrayList<>();
        }
        if (!recent.contains(quote.getId())) {
            recent.add(quote.getId());
        }
        while (recent.size() > 30) {
            recent.remove(0);
        }
        preferences.setRecentlyDeliveredQuoteIds(recent);
        preferences.setLastNotificationTimestamp(System.currentTimeMillis());
        NotificationPreferenceStore.save(context, preferences);
    }
}
