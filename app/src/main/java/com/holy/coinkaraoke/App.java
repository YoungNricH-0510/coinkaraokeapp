package com.holy.coinkaraoke;

import android.app.Application;

import com.holy.coinkaraoke.helpers.SQLiteHelper;
import com.holy.coinkaraoke.models.Song;

import java.util.Random;

public class App extends Application {

    // user id of currently logged in
    private String currentId = null;

    @Override
    public void onCreate() {
        super.onCreate();

        if (SQLiteHelper.getInstance(this).getAllSongs().isEmpty()) {
            // Insert songs into DB
            insertSongs();
        }
    }

    // Insert songs into DB

    private void insertSongs() {

        // Load song data array defined in resource
        String[] songDataArray = getResources().getStringArray(R.array.song_data_array);

        for (String songData : songDataArray) {

            // Parse each song data
            String[] elements = songData.split("//");
            String title = elements[0];
            String artist = elements[1];
            String album = elements[2];

            // Consist song object
            Song song = new Song(title, artist, album, new Random().nextInt(100));

            // Insert it into DB
            SQLiteHelper.getInstance(this).addSong(song);
        }

    }

    public void setCurrentId(String id) {
        currentId = id;
    }

    public String getCurrentId() {
        return currentId;
    }

}
