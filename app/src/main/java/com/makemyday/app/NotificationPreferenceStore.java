package com.makemyday.app;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NotificationPreferenceStore {
    private static final String PREFS_NAME = "mmd_notification_preferences";
    private static final String KEY_SETTINGS = "settings";

    public static NotificationPreferencesModel load(Context context) {
        if (context == null) {
            return NotificationPreferencesModel.defaultPreferences();
        }

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String value = prefs.getString(KEY_SETTINGS, "");
        if (value == null || value.trim().isEmpty()) {
            NotificationPreferencesModel defaults = NotificationPreferencesModel.defaultPreferences();
            save(context, defaults);
            return defaults;
        }

        try {
            JSONObject json = new JSONObject(value);
            NotificationPreferencesModel model = new NotificationPreferencesModel();
            model.setNotificationsEnabled(json.optBoolean("notificationsEnabled", false));
            model.setFrequency(json.optString("frequency", NotificationFrequency.OFF.getKey()));
            model.setSelectedCategories(readStringSet(json, "selectedCategories"));
            model.setSelectedTimePeriods(readStringSet(json, "selectedTimePeriods"));
            model.setSelectedLanguages(readStringSet(json, "selectedLanguages"));
            model.setQuietHoursEnabled(json.optBoolean("quietHoursEnabled", true));
            model.setQuietHoursStart(json.optString("quietHoursStart", "22:00"));
            model.setQuietHoursEnd(json.optString("quietHoursEnd", "07:00"));
            model.setRecentlyDeliveredQuoteIds(readStringList(json, "recentlyDeliveredQuoteIds"));
            model.setLastNotificationTimestamp(json.optLong("lastNotificationTimestamp", 0L));
            return model;
        } catch (Exception e) {
            NotificationPreferencesModel defaults = NotificationPreferencesModel.defaultPreferences();
            save(context, defaults);
            return defaults;
        }
    }

    public static void save(Context context, NotificationPreferencesModel model) {
        if (context == null || model == null) {
            return;
        }

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SETTINGS, toJson(model).toString());
        editor.apply();
    }

    private static JSONObject toJson(NotificationPreferencesModel model) {
        JSONObject json = new JSONObject();
        try {
            json.put("notificationsEnabled", model.isNotificationsEnabled());
            json.put("frequency", model.getFrequency());
            json.put("selectedCategories", new JSONArray(model.getSelectedCategories()));
            json.put("selectedTimePeriods", new JSONArray(model.getSelectedTimePeriods()));
            json.put("selectedLanguages", new JSONArray(model.getSelectedLanguages()));
            json.put("quietHoursEnabled", model.isQuietHoursEnabled());
            json.put("quietHoursStart", model.getQuietHoursStart());
            json.put("quietHoursEnd", model.getQuietHoursEnd());
            json.put("recentlyDeliveredQuoteIds", new JSONArray(model.getRecentlyDeliveredQuoteIds()));
            json.put("lastNotificationTimestamp", model.getLastNotificationTimestamp());
        } catch (Exception e) {
            return new JSONObject();
        }
        return json;
    }

    private static Set<String> readStringSet(JSONObject json, String key) {
        Set<String> values = new HashSet<>();
        if (json == null || !json.has(key)) {
            return values;
        }
        try {
            JSONArray array = json.getJSONArray(key);
            for (int i = 0; i < array.length(); i++) {
                values.add(array.getString(i));
            }
        } catch (Exception ignored) {
        }
        return values;
    }

    private static List<String> readStringList(JSONObject json, String key) {
        List<String> values = new ArrayList<>();
        if (json == null || !json.has(key)) {
            return values;
        }
        try {
            JSONArray array = json.getJSONArray(key);
            for (int i = 0; i < array.length(); i++) {
                values.add(array.getString(i));
            }
        } catch (Exception ignored) {
        }
        return values;
    }
}
