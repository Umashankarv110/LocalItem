package com.umashankar.localitem.Model;

public class Notification {
    private String date_time,details,notificationID,title;

    public Notification(){
    }

    public Notification(String date_time, String details, String notificationID, String title) {
        this.date_time = date_time;
        this.details = details;
        this.notificationID = notificationID;
        this.title = title;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
