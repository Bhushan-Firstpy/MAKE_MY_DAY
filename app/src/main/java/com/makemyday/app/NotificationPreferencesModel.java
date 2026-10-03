package com.makemyday.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NotificationPreferencesModel {
    private boolean notificationsEnabled;
    private String frequency = NotificationFrequency.OFF.getKey();
    private Set<String> selectedCategories = new HashSet<>();
    private Set<String> selectedTimePeriods = new HashSet<>();
    private Set<String> selectedLanguages = new HashSet<>();
    private boolean quietHoursEnabled = true;
    private String quietHoursStart = "22:00";
    private String quietHoursEnd = "07:00";
    private List<String> recentlyDeliveredQuoteIds = new ArrayList<>();
    private long lastNotificationTimestamp;

    public static NotificationPreferencesModel defaultPreferences() {
        NotificationPreferencesModel preferences = new NotificationPreferencesModel();
        preferences.setNotificationsEnabled(false);
        preferences.setFrequency(NotificationFrequency.OFF.getKey());

        List<String> defaultCategories = new ArrayList<>();
        defaultCategories.add("Motivation");
        defaultCategories.add("Confidence");
        defaultCategories.add("Productivity");
        defaultCategories.add("Study");
        defaultCategories.add("Career");
        defaultCategories.add("Business");
        defaultCategories.add("Health");
        defaultCategories.add("Relationships");
        defaultCategories.add("Self-discipline");
        defaultCategories.add("Peace and mindfulness");
        defaultCategories.add("Success");
        defaultCategories.add("Hope");
        defaultCategories.add("Personal growth");
        preferences.setSelectedCategories(new HashSet<>(defaultCategories));

        preferences.setSelectedTimePeriods(new HashSet<>(NotificationTimePeriod.defaultLabels()));
        preferences.setSelectedLanguages(new HashSet<>(new ArrayList<>(Collections.singletonList("English"))));
        preferences.setQuietHoursEnabled(true);
        preferences.setQuietHoursStart("22:00");
        preferences.setQuietHoursEnd("07:00");
        preferences.setRecentlyDeliveredQuoteIds(new ArrayList<>());
        preferences.setLastNotificationTimestamp(0L);
        return preferences;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Set<String> getSelectedCategories() {
        return selectedCategories;
    }

    public void setSelectedCategories(Set<String> selectedCategories) {
        this.selectedCategories = selectedCategories == null ? new HashSet<>() : selectedCategories;
    }

    public Set<String> getSelectedTimePeriods() {
        return selectedTimePeriods;
    }

    public void setSelectedTimePeriods(Set<String> selectedTimePeriods) {
        this.selectedTimePeriods = selectedTimePeriods == null ? new HashSet<>() : selectedTimePeriods;
    }

    public Set<String> getSelectedLanguages() {
        return selectedLanguages;
    }

    public void setSelectedLanguages(Set<String> selectedLanguages) {
        this.selectedLanguages = selectedLanguages == null ? new HashSet<>() : selectedLanguages;
    }

    public boolean isQuietHoursEnabled() {
        return quietHoursEnabled;
    }

    public void setQuietHoursEnabled(boolean quietHoursEnabled) {
        this.quietHoursEnabled = quietHoursEnabled;
    }

    public String getQuietHoursStart() {
        return quietHoursStart;
    }

    public void setQuietHoursStart(String quietHoursStart) {
        this.quietHoursStart = quietHoursStart;
    }

    public String getQuietHoursEnd() {
        return quietHoursEnd;
    }

    public void setQuietHoursEnd(String quietHoursEnd) {
        this.quietHoursEnd = quietHoursEnd;
    }

    public List<String> getRecentlyDeliveredQuoteIds() {
        return recentlyDeliveredQuoteIds;
    }

    public void setRecentlyDeliveredQuoteIds(List<String> recentlyDeliveredQuoteIds) {
        this.recentlyDeliveredQuoteIds = recentlyDeliveredQuoteIds == null ? new ArrayList<>() : recentlyDeliveredQuoteIds;
    }

    public long getLastNotificationTimestamp() {
        return lastNotificationTimestamp;
    }

    public void setLastNotificationTimestamp(long lastNotificationTimestamp) {
        this.lastNotificationTimestamp = lastNotificationTimestamp;
    }
}
