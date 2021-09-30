package com.zopnote.android.merchant.data.model;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class InvoiceItem implements Serializable, Comparable<InvoiceItem>{
    private String item;
    private double amount;
    private String billImg;
    private long date;
    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getBillImg() {
        return billImg;
    }

    public void setBillImg(String billImg) {
        this.billImg = billImg;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public int compareTo(@NonNull InvoiceItem o) {
        return this.item.compareTo(o.item);
    }
}
