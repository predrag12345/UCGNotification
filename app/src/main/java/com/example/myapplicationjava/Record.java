package com.example.myapplicationjava;

public class Record {

    private String title;
    private String description;

    public Record(String title, String description){

        this.title = title;
        this.description = description;

    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
