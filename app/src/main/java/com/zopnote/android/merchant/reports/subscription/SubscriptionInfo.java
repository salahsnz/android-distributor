package com.zopnote.android.merchant.reports.subscription;

import com.zopnote.android.merchant.data.model.Pause;
import com.zopnote.android.merchant.data.model.StatusEnum;

import java.util.Date;
import java.util.List;

public class SubscriptionInfo {
    private String id;
    private String productId;
    private String productName;
    private Date startDate;
    private Date endDate;
    private Date pauseStartDate;
    private Date pauseEndDate;
    private StatusEnum subscriptionStatus;
    private List<Pause> pauseList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getPauseStartDate() {
        return pauseStartDate;
    }

    public void setPauseStartDate(Date pauseStartDate) {
        this.pauseStartDate = pauseStartDate;
    }

    public Date getPauseEndDate() {
        return pauseEndDate;
    }

    public void setPauseEndDate(Date pauseEndDate) {
        this.pauseEndDate = pauseEndDate;
    }

    public StatusEnum getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(StatusEnum subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public List<Pause> getPauseList() {
        return pauseList;
    }

    public void setPauseList(List<Pause> pauseList) {
        this.pauseList = pauseList;
    }
}
