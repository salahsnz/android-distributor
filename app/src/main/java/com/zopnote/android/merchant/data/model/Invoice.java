package com.zopnote.android.merchant.data.model;

import com.google.firebase.firestore.IgnoreExtraProperties;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@IgnoreExtraProperties
public class Invoice implements Serializable {
    private String id;
    private String invoiceNumber;
    private Date invoiceDate;
    private Date dueDate;
    private Double invoiceAmount;
    private String invoicePeriod;
    private InvoiceStatusEnum status;
    private PaymentModeEnum paymentMode;
    private Customer customer;
    private Date invoicePaidDate;
    private Map<String, InvoiceItem> invoiceItems;

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

    public PaymentModeEnum getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(PaymentModeEnum paymentMode) {
        this.paymentMode = paymentMode;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Map<String, InvoiceItem> getInvoiceItems() {
        return invoiceItems;
    }

    public void setInvoiceItems(Map<String, InvoiceItem> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }

    public Date getInvoicePaidDate() {
        return invoicePaidDate;
    }

    public void setInvoicePaidDate(Date invoicePaidDate) {
        this.invoicePaidDate = invoicePaidDate;
    }
}
