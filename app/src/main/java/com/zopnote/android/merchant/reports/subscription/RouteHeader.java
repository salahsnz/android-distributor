package com.zopnote.android.merchant.reports.subscription;

public class RouteHeader {
    private String name;
    private ViewTypeEnum type;

    public RouteHeader() {
        this.type = ViewTypeEnum.HEADER;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ViewTypeEnum getType() {
        return type;
    }
}
