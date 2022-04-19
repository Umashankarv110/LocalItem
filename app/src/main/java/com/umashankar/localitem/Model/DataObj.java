package com.umashankar.localitem.Model;

public class DataObj {

    public long invoiceNo;
    public String customerName;
    public long date;
    public String fuelType;
    public double fuleQty;
    public double amount;

    public DataObj() {
    }

    public DataObj(long invoiceNo, String customerName, long date, String fuelType, double fuleQty, double amount) {
        this.invoiceNo = invoiceNo;
        this.customerName = customerName;
        this.date = date;
        this.fuelType = fuelType;
        this.fuleQty = fuleQty;
        this.amount = amount;
    }

    public long getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(long invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public long getData() {
        return date;
    }

    public void setData(long data) {
        this.date = data;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public double getFuleQty() {
        return fuleQty;
    }

    public void setFuleQty(double fuleQty) {
        this.fuleQty = fuleQty;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
