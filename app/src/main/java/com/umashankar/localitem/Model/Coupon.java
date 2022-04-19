package com.umashankar.localitem.Model;

public class Coupon {
    private String activateState, couponCode,couponId,couponName,couponValue,createDate,description,endDate,startDate,couponTheme;

    public Coupon() {
    }

    public Coupon(String activateState, String couponCode, String couponId, String couponName, String couponValue, String createDate, String description, String endDate, String startDate, String couponTheme) {
        this.activateState = activateState;
        this.couponCode = couponCode;
        this.couponId = couponId;
        this.couponName = couponName;
        this.couponValue = couponValue;
        this.createDate = createDate;
        this.description = description;
        this.endDate = endDate;
        this.startDate = startDate;
        this.couponTheme = couponTheme;
    }


    public String getActivateState() {
        return activateState;
    }

    public void setActivateState(String activateState) {
        this.activateState = activateState;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public String getCouponValue() {
        return couponValue;
    }

    public void setCouponValue(String couponValue) {
        this.couponValue = couponValue;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getCouponTheme() {
        return couponTheme;
    }

    public void setCouponTheme(String couponTheme) {
        this.couponTheme = couponTheme;
    }
}