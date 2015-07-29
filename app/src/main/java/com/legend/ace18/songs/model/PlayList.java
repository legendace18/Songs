package com.legend.ace18.songs.model;

/**
 * Created by Legend.ace18 on 7/27/2015.
 */
public class PlayList {
    public int id;
    public String title;
    public String description;

    public PlayList(){

    }

    public PlayList(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
