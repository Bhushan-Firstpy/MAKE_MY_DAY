package com.makemyday.app;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum NotificationTimePeriod {
    MORNING("Morning"),
    BEFORE_NOON("Before noon"),
    AFTERNOON("Afternoon"),
    EVENING("Evening"),
    NIGHT("Night");

    private final String label;

    NotificationTimePeriod(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static NotificationTimePeriod fromLabel(String label) {
        if (label == null) {
            return MORNING;
        }
        for (NotificationTimePeriod period : values()) {
            if (period.label.equalsIgnoreCase(label)) {
                return period;
            }
        }
        return MORNING;
    }

    public static List<String> defaultLabels() {
        return Arrays.asList(
            MORNING.getLabel(),
            BEFORE_NOON.getLabel(),
            AFTERNOON.getLabel(),
            EVENING.getLabel(),
            NIGHT.getLabel()
        );
    }

    public static List<String> defaultPreferredCategoriesForPeriod(String periodLabel) {
        NotificationTimePeriod period = fromLabel(periodLabel);
        switch (period) {
            case MORNING:
                return Arrays.asList(
                    "Health",
                    "Self-discipline",
                    "Motivation",
                    "Personal growth"
                );
            case BEFORE_NOON:
                return Arrays.asList(
                    "Motivation",
                    "Productivity",
                    "Career",
                    "Study"
                );
            case AFTERNOON:
                return Arrays.asList(
                    "Productivity",
                    "Career",
                    "Business",
                    "Confidence"
                );
            case EVENING:
                return Arrays.asList(
                    "Relationships",
                    "Peace and mindfulness",
                    "Hope",
                    "Personal growth"
                );
            case NIGHT:
                return Arrays.asList(
                    "Peace and mindfulness",
                    "Hope",
                    "Personal growth",
                    "Relationships"
                );
            default:
                return Collections.emptyList();
        }
    }
}
