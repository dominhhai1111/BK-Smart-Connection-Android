package com.example.administrator.bk_smart_connection_android;

import java.io.Serializable;

/**
 * Created by DOANBK on 12/15/2017.
 */

public class ItemSong implements Serializable{
    private String name;
    private String artist;
    private String link;

    public ItemSong(String name, String artist, String link) {
        this.name = name;
        this.artist = artist;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getLink() {
        return link;
    }
}

