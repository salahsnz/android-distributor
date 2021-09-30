package com.zopnote.android.merchant.data.model;

import java.util.Date;

public class OrderSummaryInfo {

    private double amount;
    private double charges;
    private Date date;
    private double transferredAmount;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getCharges() {
        return charges;
    }

    public void setCharges(double charges) {
        this.charges = charges;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getTransferredAmount() {
        return transferredAmount;
    }

    public void setTransferredAmount(double transferredAmount) {
        this.transferredAmount = transferredAmount;
    }

}


