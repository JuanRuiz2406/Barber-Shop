package com.example.barbershop;

public class Appointment {
    private String clientName;
    private String date;
    private String state;

    public Appointment(String clientName, String date, String state) {
        this.clientName = clientName;
        this.date = date;
        this.state = state;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
