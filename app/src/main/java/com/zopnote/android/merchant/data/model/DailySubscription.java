package com.zopnote.android.merchant.data.model;

import android.support.annotation.NonNull;

public class DailySubscription implements Comparable<DailySubscription> {
    private String name;
    private int activeCount;
    private int pauseCount;
    private int procureCount;
    private String route;
    private String addressLine2;
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(int activeCount) {
        this.activeCount = activeCount;
    }

    public int getPauseCount() {
        return pauseCount;
    }

    public void setPauseCount(int pauseCount) {
        this.pauseCount = pauseCount;
    }

    public int getProcureCount() {
        return procureCount;
    }

    public void setProcureCount(int procureCount) {
        this.procureCount = procureCount;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int compareTo(@NonNull DailySubscription d) {
        return this.name.compareTo(d.name);
    }
}
