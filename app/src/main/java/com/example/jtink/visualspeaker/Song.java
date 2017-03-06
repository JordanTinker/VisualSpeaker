package com.example.jtink.visualspeaker;

/**
 * Created by Devan on 3/5/17.
 */
import java.util.ArrayList;

public class Song {
    private String mSongName;
    private String mSongLocation;

    public Song()
    {
        mSongName = "Placeholder";
        mSongLocation = "Placeholder";
    }

    public Song(String name, String location)
    {
        mSongName = name;
        mSongLocation = location;
    }

    public String getName()
    {
        return mSongName;
    }

    public String getLocation()
    {
        return mSongLocation;
    }

    @Override
    public String toString()
    {
        return getName();
    }

    public static ArrayList<Song> test()
    {
        ArrayList<Song> songs = new ArrayList<Song>();
        //ArrayList<Song> songs;
        songs.add(new Song("Harder, Better, Faster, Stronger", "Home"));
        songs.add(new Song("Fireworks","Home"));
        songs.add(new Song("Titanium", "Home"));
        songs.add(new Song("Seven Nation Army", "Home"));

        return songs;
    }
}
