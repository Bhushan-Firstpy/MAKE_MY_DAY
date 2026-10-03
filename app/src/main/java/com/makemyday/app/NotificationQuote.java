package com.makemyday.app;

public class NotificationQuote {
    private final String id;
    private final String text;
    private final String language;
    private final String category;
    private final boolean status;
    private final boolean published;
    private final boolean notificationEligible;

    public NotificationQuote(
        String id,
        String text,
        String language,
        String category,
        boolean status,
        boolean published,
        boolean notificationEligible
    ) {
        this.id = id;
        this.text = text;
        this.language = language;
        this.category = category;
        this.status = status;
        this.published = published;
        this.notificationEligible = notificationEligible;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getLanguage() {
        return language;
    }

    public String getCategory() {
        return category;
    }

    public boolean isStatus() {
        return status;
    }

    public boolean isPublished() {
        return published;
    }

    public boolean isNotificationEligible() {
        return notificationEligible;
    }
}
