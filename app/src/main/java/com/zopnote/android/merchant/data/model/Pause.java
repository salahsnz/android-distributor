package com.zopnote.android.merchant.data.model;

import java.io.Serializable;
import java.util.Date;

public class Pause implements Serializable {
    private String id;
    private String subscriptionId;
    private Date pauseStartDate;
    private Date pauseEndDate;
    private boolean startDateLocked;
    private StatusEnum pauseStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
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

    public boolean isStartDateLocked() {
        return startDateLocked;
    }

    public void setStartDateLocked(boolean startDateLocked) {
        this.startDateLocked = startDateLocked;
    }

    public StatusEnum getPauseStatus() {
        return pauseStatus;
    }

    public void setPauseStatus(StatusEnum pauseStatus) {
        this.pauseStatus = pauseStatus;
    }
}
