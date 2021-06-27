package com.example.barbershop.models;

public class Appointment {
    private String user;
    private String clientName;
    private String date;
    private String hour;
    private String status;
    private String photo_link;
    private String video_link;
    boolean isExpanded;

    public Appointment() {
    }

    public Appointment(String user, String clientName, String date, String hour, String status, String photo_link, String video_link) {
        this.user = user;
        this.clientName = clientName;
        this.date = date;
        this.hour = hour;
        this.status = status;
        this.photo_link = photo_link;
        this.video_link = video_link;
        this.isExpanded = false;

    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhoto_link() {
        return photo_link;
    }

    public void setPhoto_link(String photo_link) {
        this.photo_link = photo_link;
    }

    public String getVideo_link() {
        return video_link;
    }

    public void setVideo_link(String video_link) {
        this.video_link = video_link;
    }

    public boolean getIsExpanded() {
        return isExpanded;
    }

    public void setIsExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }
}
