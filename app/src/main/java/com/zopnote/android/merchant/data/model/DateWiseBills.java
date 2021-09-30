package com.zopnote.android.merchant.data.model;

import java.util.List;

public class DateWiseBills {

    private Double dailyTotal;
    private Double advancePaid;
    private Long indentDate;
    private List<DailyIndentSubscription> indents;

    public Double getDailyTotal() {
        return dailyTotal;
    }

    public void setDailyTotal(Double dailyTotal) {
        this.dailyTotal = dailyTotal;
    }

    public Double getAdvancePaid() {
        return advancePaid;
    }

    public void setAdvancePaid(Double advancePaid) {
        this.advancePaid = advancePaid;
    }

    public Long getIndentDate() {
        return indentDate;
    }

    public void setIndentDate(Long indentDate) {
        this.indentDate = indentDate;
    }

    public List<DailyIndentSubscription> getIndents() {
        return indents;
    }

    public void setIndents(List<DailyIndentSubscription> indents) {
        this.indents = indents;
    }
}
