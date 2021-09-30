package com.zopnote.android.merchant.data.model;

import java.util.List;

public class SettlementReport {
    private double billed;
    private double cashCollection;
    private double unPaid;
    private double onlineCollection;
    private double previousBalance;
    private double charges;
    private List<SettlementInfo> settlements;
    private double transferred;
    private double settled;
    private double cgst;
    private double sgst;
    private double pending;
    private double advanceTransfer;
    private double availableAmount;
    private int totalNoOfInvoicesProcessed;
    private int totalNoOfOrdersProcessed;
    private double paidBankCharges;
    private String merchantType;

    public double getBilled() {
        return billed;
    }

    public void setBilled(double billed) {
        this.billed = billed;
    }

    public double getOnlineCollection() {
        return onlineCollection;
    }

    public void setOnlineCollection(double onlineCollection) {
        this.onlineCollection = onlineCollection;
    }

    public double getCharges() {
        return charges;
    }

    public void setCharges(double charges) {
        this.charges = charges;
    }

    public List<SettlementInfo> getSettlements() {
        return settlements;
    }

    public void setSettlements(List<SettlementInfo> settlements) {
        this.settlements = settlements;
    }

    public double getTransferred() {
        return transferred;
    }

    public void setTransferred(double transferred) {
        this.transferred = transferred;
    }

    public double getSettled() {
        return settled;
    }

    public void setSettled(double settled) {
        this.settled = settled;
    }

    public double getCgst() {
        return cgst;
    }

    public void setCgst(double cgst) {
        this.cgst = cgst;
    }

    public double getSgst() {
        return sgst;
    }

    public void setSgst(double sgst) {
        this.sgst = sgst;
    }

    public double getPending() {
        return pending;
    }

    public void setPending(double pending) {
        this.pending = pending;
    }

    public double getAdvanceTransfer() {
        return advanceTransfer;
    }

    public void setAdvanceTransfer(double advanceTransfer) {
        this.advanceTransfer = advanceTransfer;
    }

    public double getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(double availableAmount) {
        this.availableAmount = availableAmount;
    }

    public double getCashCollection() {
        return cashCollection;
    }

    public void setCashCollection(double cashCollection) {
        this.cashCollection = cashCollection;
    }

    public double getUnPaid() {
        return unPaid;
    }

    public void setUnPaid(double unPaid) {
        this.unPaid = unPaid;
    }

    public double getPreviousBalance() {
        return previousBalance;
    }

    public void setPreviousBalance(double previousBalance) {
        this.previousBalance = previousBalance;
    }

    public int getTotalNoOfInvoicesProcessed() {
        return totalNoOfInvoicesProcessed;
    }

    public void setTotalNoOfInvoicesProcessed(int totalNoOfInvoicesProcessed) {
        this.totalNoOfInvoicesProcessed = totalNoOfInvoicesProcessed;
    }

    public double getPaidBankCharges() {
        return paidBankCharges;
    }
    public void setPaidBankCharges(double paidBankCharges) {
        this.paidBankCharges = paidBankCharges;
    }

    public String getMerchantType() {
        return merchantType;
    }
    public void setMerchantType(String merchantType) {
        this.merchantType = merchantType;
    }

    public int getTotalNoOfOrdersProcessed() {
        return totalNoOfOrdersProcessed;
    }

    public void setTotalNoOfOrdersProcessed(int totalNoOfInvoicesProcessed) {
        this.totalNoOfOrdersProcessed = totalNoOfOrdersProcessed;
    }
}
