package com.zopnote.android.merchant.analytics;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nmohideen on 27/02/18.
 */

public class Analytics {

    private static final int NAME_MAX_LENGTH = 40;

    private static IAnalytics analyticsProvider;

    public static void init(IAnalytics analyticsProvider) {
        Analytics.analyticsProvider = analyticsProvider;
    }

    public static void logEvent(String eventName) {
        Analytics.logEvent(eventName, null);
    }

    private static void logEvent(String eventName, Map<String, Object> params) {
        if (analyticsProvider == null) {
            throw new IllegalStateException("Analytics is not initialized.");
        }

        if (eventName == null) {
            throw new IllegalArgumentException("Event name is not set");
        }

        if (eventName.length() > NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("Event name exceeds maximum length " + NAME_MAX_LENGTH);
        }

        if (params != null) {
            for (String key : params.keySet()) {
                if (key.length() > NAME_MAX_LENGTH) {
                    throw new IllegalArgumentException("Param name exceeds maximum length " + NAME_MAX_LENGTH);
                }
            }
        }

        analyticsProvider.logEvent(eventName, params);
    }

    public static class Builder {
        private String eventName;
        private Map<String, Object> params;

        public Builder() {
        }

        public Builder setEventName(String eventName) {
            this.eventName = eventName;
            return this;
        }

        public Builder addParam(String name, Object value) {
            if (params == null) {
                params = new HashMap<>();
            }
            params.put(name, value);
            return this;
        }

        public void logEvent() {
            Analytics.logEvent(eventName, params);
        }
    }
}
