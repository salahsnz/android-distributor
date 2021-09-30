package com.zopnote.android.merchant.data.model;

import java.util.List;

public class DailyIndentInvoice {



    private String invoicePeriod;
    private String invoiceDate;
    private Double invoiceAmount;
    private String invoiceNumber;
    private Double previousAmount;
    private String invoiceDueDate;
    private List<DateWiseBills> datewiseBills;

    public String getInvoicePeriod() {
        return invoicePeriod;
    }

    public void setInvoicePeriod(String invoicePeriod) {
        this.invoicePeriod = invoicePeriod;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Double getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(Double invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Double getPreviousAmount() {
        return previousAmount;
    }

    public void setPreviousAmount(Double previousAmount) {
        this.previousAmount = previousAmount;
    }

    public String getInvoiceDueDate() {
        return invoiceDueDate;
    }

    public void setInvoiceDueDate(String invoiceDueDate) {
        this.invoiceDueDate = invoiceDueDate;
    }

    public List<DateWiseBills> getDatewiseBills() {
        return datewiseBills;
    }

    public void setDatewiseBills(List<DateWiseBills> datewiseBills) {
        this.datewiseBills = datewiseBills;
    }
}
