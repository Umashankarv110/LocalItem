package com.umashankar.localitem.Model;

public class OrderDetail {
    private String orderid,date,product_name;
    private String time,order_date_time,product_price,product_qty,pTotalPrice,delivery,finalTotal,name,address,state,pincode;

    public OrderDetail(){}

    public OrderDetail(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }
}