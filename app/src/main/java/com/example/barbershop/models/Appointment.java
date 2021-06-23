package com.example.barbershop.models;

public class Appointment {
    private User user;
    private String clientName;
    private String date;
    private String hour;
    private String status;

    public Appointment() {
    }

    public Appointment(User user, String clientName, String date, String hour, String status) {
        this.user = user;
        this.clientName = clientName;
        this.date = date;
        this.hour = hour;
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
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
}
