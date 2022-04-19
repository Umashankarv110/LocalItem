package com.umashankar.localitem.Model;

public class Cart {
    private String pid,itemID,pname,price,quantity,discount,image,rateFor,ship_charges,oTrackComp;

    public Cart(){
    }

    public Cart(String itemID,String pid, String pname, String price, String quantity, String discount, String image,
                String rateFor, String ship_charges, String oTrackComp) {
        this.pid = pid;
        this.itemID = itemID;
        this.pname = pname;
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
        this.image = image;
        this.rateFor = rateFor;
        this.ship_charges = ship_charges;
        this.oTrackComp = oTrackComp;
    }

    public String getoTrackComp() {
        return oTrackComp;
    }

    public void setoTrackComp(String oTrackComp) {
        this.oTrackComp = oTrackComp;
    }

    public String getShip_charges() {
        return ship_charges;
    }

    public void setShip_charges(String ship_charges) {
        this.ship_charges = ship_charges;
    }

    public String getRateFor() {
        return rateFor;
    }

    public void setRateFor(String rateFor) {
        this.rateFor = rateFor;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
