package com.makemyday.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.webkit.JavascriptInterface;

import androidx.core.content.ContextCompat;

import java.lang.ref.WeakReference;

public class NotificationBridge {
    private final WeakReference<MainActivity> activityRef;

    public NotificationBridge(MainActivity activity) {
        this.activityRef = new WeakReference<>(activity);
    }

    @JavascriptInterface
    public String getNotificationPreferences() {
        MainActivity activity = activityRef.get();
        if (activity == null) {
            return "{}";
        }
        return NotificationPreferenceStore.load(activity).toString();
    }

    @JavascriptInterface
    public void saveNotificationPreferences(String payload) {
        MainActivity activity = activityRef.get();
        if (activity == null || payload == null) {
            return;
        }
        try {
            NotificationPreferencesModel preferences = NotificationPreferenceStore.load(activity);
            org.json.JSONObject json = new org.json.JSONObject(payload);
            preferences.setNotificationsEnabled(json.optBoolean("notificationsEnabled", false));
            preferences.setFrequency(json.optString("frequency", NotificationFrequency.OFF.getKey()));
            preferences.setSelectedCategories(toStringSet(json.optJSONArray("selectedCategories")));
            preferences.setSelectedTimePeriods(toStringSet(json.optJSONArray("selectedTimePeriods")));
            preferences.setSelectedLanguages(toStringSet(json.optJSONArray("selectedLanguages")));
            preferences.setQuietHoursEnabled(json.optBoolean("quietHoursEnabled", true));
            preferences.setQuietHoursStart(json.optString("quietHoursStart", "22:00"));
            preferences.setQuietHoursEnd(json.optString("quietHoursEnd", "07:00"));
            preferences.setRecentlyDeliveredQuoteIds(toStringList(json.optJSONArray("recentlyDeliveredQuoteIds")));
            preferences.setLastNotificationTimestamp(json.optLong("lastNotificationTimestamp", 0L));
            NotificationPreferenceStore.save(activity, preferences);
            NotificationScheduler.schedule(activity, preferences);
        } catch (Exception ignored) {
        }
    }

    @JavascriptInterface
    public void requestNotificationPermission() {
        MainActivity activity = activityRef.get();
        if (activity == null) {
            return;
        }
        activity.ensureNotificationPermission();
    }

    @JavascriptInterface
    public void triggerTestNotification() {
        MainActivity activity = activityRef.get();
        if (activity == null) {
            return;
        }
        activity.triggerTestNotification();
    }

    private java.util.Set<String> toStringSet(org.json.JSONArray array) {
        java.util.Set<String> result = new java.util.HashSet<>();
        if (array == null) {
            return result;
        }
        for (int i = 0; i < array.length(); i++) {
            try {
                result.add(array.getString(i));
            } catch (Exception ignored) {
            }
        }
        return result;
    }

    private java.util.List<String> toStringList(org.json.JSONArray array) {
        java.util.List<String> result = new java.util.ArrayList<>();
        if (array == null) {
            return result;
        }
        for (int i = 0; i < array.length(); i++) {
            try {
                result.add(array.getString(i));
            } catch (Exception ignored) {
            }
        }
        return result;
    }
}
