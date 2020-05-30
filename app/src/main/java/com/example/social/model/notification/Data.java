package com.example.social.model.notification;

public class Data {
    private String user;
    private int icon;
    private String body;
    private String title;
    private String toSender;
    public Data(){}
    public Data(String user, int icon, String body, String title, String toSender) {
        this.user = user;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.toSender = toSender;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getToSender() {
        return toSender;
    }

    public void setToSender(String toSender) {
        this.toSender = toSender;
    }
}
