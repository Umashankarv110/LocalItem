package com.umashankar.localitem.Model;

public class NotificationModel {
    public String image,body;
    public boolean readed;

    public NotificationModel(String image, String body, boolean readed) {
        this.image = image;
        this.body = body;
        this.readed = readed;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }
}
