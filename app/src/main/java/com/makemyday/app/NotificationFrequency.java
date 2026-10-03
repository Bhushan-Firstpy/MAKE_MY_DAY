package com.makemyday.app;

public enum NotificationFrequency {
    OFF("OFF", 0),
    ONE_PER_DAY("1/day", 24 * 60),
    TWO_PER_DAY("2/day", 12 * 60),
    FOUR_PER_DAY("4/day", 6 * 60),
    EVERY_2_HOURS("2h", 120),
    EVERY_1_HOUR("1h", 60);

    private final String key;
    private final int intervalMinutes;

    NotificationFrequency(String key, int intervalMinutes) {
        this.key = key;
        this.intervalMinutes = intervalMinutes;
    }

    public String getKey() {
        return key;
    }

    public int getIntervalMinutes() {
        return intervalMinutes;
    }

    public boolean isEnabled() {
        return this != OFF;
    }

    public static NotificationFrequency fromKey(String value) {
        if (value == null) {
            return OFF;
        }
        for (NotificationFrequency frequency : values()) {
            if (frequency.key.equalsIgnoreCase(value)) {
                return frequency;
            }
        }
        return OFF;
    }

    public static String[] optionKeys() {
        return new String[] {
            OFF.key,
            ONE_PER_DAY.key,
            TWO_PER_DAY.key,
            FOUR_PER_DAY.key,
            EVERY_2_HOURS.key,
            EVERY_1_HOUR.key
        };
    }
}
