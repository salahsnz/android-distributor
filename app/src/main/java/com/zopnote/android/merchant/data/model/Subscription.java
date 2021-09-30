package com.zopnote.android.merchant.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Subscription implements Serializable{

    private String id;
    private String customerId;
    private Product product;
    private Date endDate;
    private Date startDate;
    private String tag;
    private List<Pause> pauses;
    private StatusEnum subscriptionStatus;
    private boolean annualSubscription;
    private Map<String, Double> price;
    private PricingModeEnum pricingMode;
    private List<Integer> weekDays;
    private Double serviceCharge;
    private Integer quantity;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<Pause> getPauses() {
        return pauses;
    }

    public void setPauses(List<Pause> pauses) {
        this.pauses = pauses;
    }

    public StatusEnum getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(StatusEnum subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public boolean isAnnualSubscription() {
        return annualSubscription;
    }

    public void setAnnualSubscription(boolean annualSubscription) {
        this.annualSubscription = annualSubscription;
    }

    public Map<String, Double> getPrice() {
        return price;
    }

    public void setPrice(Map<String, Double> price) {
        this.price = price;
    }

    public PricingModeEnum getPricingMode() {
        return pricingMode;
    }

    public void setPricingMode(PricingModeEnum pricingMode) {
        this.pricingMode = pricingMode;
    }

    public List<Integer> getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(List<Integer> weekDays) {
        this.weekDays = weekDays;
    }

    public Double getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(Double serviceCharge) {
        this.serviceCharge = serviceCharge;
    }
}
