package com.zopnote.android.merchant.data.model;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DailyIndent implements Comparable<DailyIndent> {


    private String customerId;
    private String route;
    private Long indentDate;
    private String merchantId;
    private List<DailyIndentSubscription> subscriptions;
    private String customerName;


    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Long getIndentDate() {
        return indentDate;
    }

    public void setIndentDate(Long indentDate) {
        this.indentDate = indentDate;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public List<DailyIndentSubscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<DailyIndentSubscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    @Override
    public int compareTo(DailyIndent o) {
        return this.getCustomerName().compareTo(o.getCustomerName());
    }



    Comparator<DailyIndent> compareById = new Comparator<DailyIndent>() {
        @Override
        public int compare(DailyIndent o1, DailyIndent o2) {
            return o1.getCustomerName().compareTo(o2.getCustomerName());
        }
    };
}
