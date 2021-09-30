package com.zopnote.android.merchant.reports.onboarding;

public class OnboardInfo {
    private String route;
    private int totalCustomers;
    private int nameOrMobileAvailableCount;
    private int nameOrMobileNumberNotAddedCount;
    private int activeCount;
    private int inactiveCount;

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public int getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(int totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public int getNameOrMobileAvailableCount() {
        return nameOrMobileAvailableCount;
    }

    public void setNameOrMobileAvailableCount(int nameOrMobileAvailableCount) {
        this.nameOrMobileAvailableCount = nameOrMobileAvailableCount;
    }

    public int getNameOrMobileNumberNotAddedCount() {
        return nameOrMobileNumberNotAddedCount;
    }

    public void setNameOrMobileNumberNotAddedCount(int nameOrMobileNumberNotAddedCount) {
        this.nameOrMobileNumberNotAddedCount = nameOrMobileNumberNotAddedCount;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(int activeCount) {
        this.activeCount = activeCount;
    }

    public int getInactiveCount() {
        return inactiveCount;
    }

    public void setInactiveCount(int inactiveCount) {
        this.inactiveCount = inactiveCount;
    }
}
