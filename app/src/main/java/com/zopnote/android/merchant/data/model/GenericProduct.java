package com.zopnote.android.merchant.data.model;

import java.io.Serializable;

public class GenericProduct implements Serializable{
    private String id;
    private String name;
    private String language;
    private String frequency;
    private String type;
    private Double price;
    private String shortCode;
    private String logoUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }


    @Override
    public int hashCode() {
        return Integer.parseInt(this.getId());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Product){
            return this.getId().equals(((Product)obj).getId());
        }
        return this.getId().equals(((GenericProduct)obj).getId());
    }
}
