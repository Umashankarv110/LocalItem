package com.umashankar.localitem.Model;

public class ShipCharges {
    String comp_name,state_name,charge_one,charge_half,shipping_status,deliver_in;

    public ShipCharges() {
    }

    public ShipCharges(String comp_name, String state_name, String charge_one, String charge_half, String shipping_status, String deliver_in) {
        this.comp_name = comp_name;
        this.state_name = state_name;
        this.charge_one = charge_one;
        this.charge_half = charge_half;
        this.shipping_status = shipping_status;
        this.deliver_in = deliver_in;
    }

    public String getComp_name() {
        return comp_name;
    }

    public void setComp_name(String comp_name) {
        this.comp_name = comp_name;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    public String getCharge_one() {
        return charge_one;
    }

    public void setCharge_one(String charge_one) {
        this.charge_one = charge_one;
    }

    public String getCharge_half() {
        return charge_half;
    }

    public void setCharge_half(String charge_half) {
        this.charge_half = charge_half;
    }

    public String getShipping_status() {
        return shipping_status;
    }

    public void setShipping_status(String shipping_status) {
        this.shipping_status = shipping_status;
    }

    public String getDeliver_in() {
        return deliver_in;
    }

    public void setDeliver_in(String deliver_in) {
        this.deliver_in = deliver_in;
    }

}