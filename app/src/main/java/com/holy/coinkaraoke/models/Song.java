package com.holy.coinkaraoke.models;

public class Song {

    private final int id;
    private final String title;
    private final String artist;
    private final String album;
    private final int preference;

    public Song(int id, String title, String artist, String album, int preference) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.preference = preference;
    }

    public Song(String title, String artist, String album, int preference) {
        this.id = -1;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.preference = preference;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public int getPreference() {
        return preference;
    }
}
