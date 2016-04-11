package com.project.droidbabies.musicplayer;

/**
 * Created by Sumon on 4/5/2016.
 */
public class Song {
    private  long songId;
    private  String artist;
    private String title;

    public Song(long songId, String title, String artist) {
        this.songId = songId;
        this.title = title;
        this.artist = artist;
    }

    public long getSongId() {
        return songId;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }
}
