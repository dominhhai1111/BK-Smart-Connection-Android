package com.example.administrator.bk_smart_connection_android;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by DOANBK on 12/15/2017.
 */

public class ItemSong implements Serializable{
    @SerializedName("song_name")
    private String name;
    @SerializedName("singer_name")
    private String artist;
    @SerializedName("url")
    private String link;
    @SerializedName("view_name")
    private String view;

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getLink() {
        return link;
    }

    public String getView() {
        return view;
    }

    public ItemSong(String name, String artist, String link, String view) {

        this.name = name;
        this.artist = artist;
        this.link = link;
        this.view = view;
    }
}

