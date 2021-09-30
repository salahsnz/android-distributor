package com.zopnote.android.merchant.data.model;

public class DraftInvoiceReportItem {
    private String customerId;
    private String route;
    private String addressLine1;
    private String addressLine2;
    private String doorNumber;
    private String notes;
    private DraftInvoice invoice;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
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

    public String getDoorNumber() {
        return doorNumber;
    }

    public void setDoorNumber(String doorNumber) {
        this.doorNumber = doorNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public DraftInvoice getInvoice() {
        return invoice;
    }

    public void setInvoice(DraftInvoice invoice) {
        this.invoice = invoice;
    }
}
