package com.zopnote.android.merchant.reports.subscription;

import java.util.List;

public class SubscriptionReportItem {
    private String id;
    private String doorNumber;
    private String addressLine1;
    private String addressLine2;
    private String route;
    private List<SubscriptionInfo> subscriptions;
    private ViewTypeEnum type;

    public SubscriptionReportItem() {
        this.type = ViewTypeEnum.CONTENT;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDoorNumber() {
        return doorNumber;
    }

    public void setDoorNumber(String doorNumber) {
        this.doorNumber = doorNumber;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public List<SubscriptionInfo> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<SubscriptionInfo> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public ViewTypeEnum getType() {
        return type;
    }
}
