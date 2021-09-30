package com.zopnote.android.merchant.data.model;

import java.util.Date;
import java.util.List;

public class DraftInvoice {
    private String id;
    private String invoiceNumber;
    private Date invoiceDate;
    private Date dueDate;
    private Double invoiceAmount;
    private String invoicePeriod;
    private InvoiceStatusEnum status;
    private List<DraftInvoiceItem> invoiceItems;
    private String notes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getInvoicePeriod() {
        return invoicePeriod;
    }

    public void setInvoicePeriod(String invoicePeriod) {
        this.invoicePeriod = invoicePeriod;
    }

    public Double getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(Double invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public InvoiceStatusEnum getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatusEnum status) {
        this.status = status;
    }

    public List<DraftInvoiceItem> getInvoiceItems() {
        return invoiceItems;
    }

    public void setInvoiceItems(List<DraftInvoiceItem> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
