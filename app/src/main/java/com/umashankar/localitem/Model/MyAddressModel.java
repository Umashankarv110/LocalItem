package com.umashankar.localitem.Model;

public class MyAddressModel {

    private String name,address,city,pincode,state,landmark,mobile,aid;

    public MyAddressModel() {
    }

    public MyAddressModel(String aid,String name, String address, String city, String pincode, String state, String landmark, String mobile) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.pincode = pincode;
        this.state = state;
        this.landmark = landmark;
        this.mobile = mobile;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
