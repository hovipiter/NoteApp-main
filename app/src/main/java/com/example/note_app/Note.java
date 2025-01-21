package com.example.note_app;

//import java.sql.Timestamp;
import com.google.firebase.Timestamp;

public class Note {
    private String note_id;
    private String note_title;
    private String note_day;
    private String note_label;
    private boolean isFavorite;

    public Note(String note_title, String note_day) {
        this.note_title = note_title;
        this.note_day = note_day;
    }

    public Note() {
        this.isFavorite = false;
    }

    public Note(String note_id, String note_title, String note_day, String note_content, boolean isFavorite) {
        this.note_id = note_id;
        this.note_title = note_title;
        this.note_day = note_day;
        this.note_content = note_content;
        this.isFavorite = isFavorite;
    }

    private String note_content;
    public String getNote_id() {
        return note_id;
    }

    public void setNote_id(String note_id) {
        this.note_id = note_id;
    }

    public String getNote_title() {
        return note_title;
    }

    public void setNote_title(String note_title) {
        this.note_title = note_title;
    }

    public String getNote_day() {
        return note_day;
    }

    public void setNote_day(String note_day) {
        this.note_day = note_day;
    }

    public String getNote_content() {
        return note_content;
    }

    public void setNote_content(String note_content) {
        this.note_content = note_content;
    }
    public boolean isFavorite() {
        return isFavorite;
    }
    public void setFavorite(boolean favorite) {
        this.isFavorite = favorite;
    }
    public Note(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Timestamp getTimestamp(){
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    Timestamp timestamp;

}
