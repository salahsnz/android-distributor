package com.zopnote.android.merchant.data.model;

public class DailyIndentSubscription {

    private String productId;
    private Integer subscriptionQuantity;
    private String productShortCode;
    private Integer productPrice;
    private Integer total;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getSubscriptionQuantity() {
        return subscriptionQuantity;
    }

    public void setSubscriptionQuantity(Integer subscriptionQuantity) {
        this.subscriptionQuantity = subscriptionQuantity;
    }

    public String getProductShortCode() {
        return productShortCode;
    }

    public void setProductShortCode(String productShortCode) {
        this.productShortCode = productShortCode;
    }

    public Integer getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Integer productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
