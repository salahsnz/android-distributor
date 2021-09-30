package com.zopnote.android.merchant.reports.collection;

public enum PaymentFilterOption {
    BILLED("Billed", "billed"),
    PENDING("Pending", "pending"),
    PAID_ONLINE("Paid Online", "online"),
    PAID_CASH("Paid Cash", "cash"),
    PAID_CHEQUE("Paid Cheque", "cheque"),
    PAID_PAYTM("Paid PayTm", "paytm"),
    PAID_PHONEPE("Paid Phonepe", "phonepe"),
    PAID_UPI("Paid UPI", "upi"),
    PAID_OTHER("Paid Other", "other"),
    PAID_GPAY("Paid GPay", "gpay");

    private String displayName;
    private String shortName;

    PaymentFilterOption(String displayName, String shortName) {
        this.displayName = displayName;
        this.shortName = shortName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getShortName() {
        return shortName;
    }
}
