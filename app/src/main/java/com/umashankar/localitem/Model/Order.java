package com.umashankar.localitem.Model;

public class Order {
    private String invoiceSr,orderid,date,product_name,contact,oplaced,opacked,oshipped,odelivered,placedDate,packedDate,shippedDate,deliverDate,uid;
    private String time,order_date_time,product_price,product_qty,pTotalPrice,delivery,finalTotal,name,address,state,pincode,orderStatus,couponValue,couponText,couponDetails,afterCouponValue;
    private long Invoice;
    String oTrackComp,otracking_id;

    public Order() {
    }

    public Order(String invoiceSr, String orderid, String date, String product_name, String contact, String oplaced, String opacked,
                 String oshipped, String odelivered, String placedDate, String packedDate, String shippedDate, String deliverDate,
                 String uid, String time, String order_date_time, String product_price, String product_qty, String pTotalPrice,
                 String delivery, String finalTotal, String name, String address, String state, String pincode, String orderStatus,
                 String couponValue, String couponText, String couponDetails, long invoice, String oTrackComp, String otracking_id) {
        this.orderid = orderid;
        this.date = date;
        this.product_name = product_name;
        this.contact = contact;
        this.oplaced = oplaced;
        this.opacked = opacked;
        this.oshipped = oshipped;
        this.odelivered = odelivered;
        this.placedDate = placedDate;
        this.packedDate = packedDate;
        this.shippedDate = shippedDate;
        this.deliverDate = deliverDate;
        this.uid = uid;
        this.time = time;
        this.order_date_time = order_date_time;
        this.product_price = product_price;
        this.product_qty = product_qty;
        this.pTotalPrice = pTotalPrice;
        this.delivery = delivery;
        this.finalTotal = finalTotal;
        this.name = name;
        this.address = address;
        this.state = state;
        this.pincode = pincode;
        this.orderStatus = orderStatus;
        this.couponValue = couponValue;
        this.couponText = couponText;
        this.couponDetails = couponDetails;
        this.otracking_id = otracking_id;
        this.oTrackComp = oTrackComp;
        Invoice = invoice;
    }

    public String getOtracking_id() {
        return otracking_id;
    }

    public void setOtracking_id(String otracking_id) {
        this.otracking_id = otracking_id;
    }

    public String getoTrackComp() {
        return oTrackComp;
    }

    public void setoTrackComp(String oTrackComp) {
        this.oTrackComp = oTrackComp;
    }

    public String getInvoiceSr() {
        return invoiceSr;
    }

    public void setInvoiceSr(String invoiceSr) {
        this.invoiceSr = invoiceSr;
    }

    public String getAfterCouponValue() {
        return afterCouponValue;
    }

    public void setAfterCouponValue(String afterCouponValue) {
        this.afterCouponValue = afterCouponValue;
    }

    public String getCouponValue() {
        return couponValue;
    }

    public void setCouponValue(String couponValue) {
        this.couponValue = couponValue;
    }

    public String getCouponText() {
        return couponText;
    }

    public void setCouponText(String couponText) {
        this.couponText = couponText;
    }

    public String getCouponDetails() {
        return couponDetails;
    }

    public void setCouponDetails(String couponDetails) {
        this.couponDetails = couponDetails;
    }

    public long getInvoice() {
        return Invoice;
    }

    public void setInvoice(long invoice) {
        Invoice = invoice;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPlacedDate() {
        return placedDate;
    }

    public void setPlacedDate(String placedDate) {
        this.placedDate = placedDate;
    }

    public String getPackedDate() {
        return packedDate;
    }

    public void setPackedDate(String packedDate) {
        this.packedDate = packedDate;
    }

    public String getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(String shippedDate) {
        this.shippedDate = shippedDate;
    }

    public String getDeliverDate() {
        return deliverDate;
    }

    public void setDeliverDate(String deliverDate) {
        this.deliverDate = deliverDate;
    }

    public String getOplaced() {
        return oplaced;
    }

    public void setOplaced(String oplaced) {
        this.oplaced = oplaced;
    }

    public String getOpacked() {
        return opacked;
    }

    public void setOpacked(String opacked) {
        this.opacked = opacked;
    }

    public String getOshipped() {
        return oshipped;
    }

    public void setOshipped(String oshipped) {
        this.oshipped = oshipped;
    }

    public String getOdelivered() {
        return odelivered;
    }

    public void setOdelivered(String odelivered) {
        this.odelivered = odelivered;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOrder_date_time() {
        return order_date_time;
    }

    public void setOrder_date_time(String order_date_time) {
        this.order_date_time = order_date_time;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getProduct_qty() {
        return product_qty;
    }

    public void setProduct_qty(String product_qty) {
        this.product_qty = product_qty;
    }

    public String getpTotalPrice() {
        return pTotalPrice;
    }

    public void setpTotalPrice(String pTotalPrice) {
        this.pTotalPrice = pTotalPrice;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(String finalTotal) {
        this.finalTotal = finalTotal;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
}