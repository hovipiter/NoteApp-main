package com.example.note_app;

public class Reminder {
    private String key;
    private String title;
    private String date;
    private String time;

    public Reminder() {
        // Empty constructor needed for Firebase
    }

    public Reminder(String title, String date, String time) {
        this.title = title;
        this.date = date;
        this.time = time;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String title) {
        this.time = time;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}

