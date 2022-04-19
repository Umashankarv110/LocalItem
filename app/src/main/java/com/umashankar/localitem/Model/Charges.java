package com.umashankar.localitem.Model;

public class Charges {
    String charge_half,charge_one,state_name;

    public Charges(){}

    public Charges(String charge_half, String charge_one, String state_name) {
        this.charge_half = charge_half;
        this.charge_one = charge_one;
        this.state_name = state_name;
    }

    public String getCharge_half() {
        return charge_half;
    }

    public void setCharge_half(String charge_half) {
        this.charge_half = charge_half;
    }

    public String getCharge_one() {
        return charge_one;
    }

    public void setCharge_one(String charge_one) {
        this.charge_one = charge_one;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }
}
