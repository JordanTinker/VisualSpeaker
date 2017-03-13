package com.example.jtink.visualspeaker;

/**
 * Created by Devan on 3/5/17.
 */
import java.util.ArrayList;

public class Song {
    private String mSongName;

    public Song()
    {
        mSongName = "Placeholder";
    }

    public Song(String name)
    {
        mSongName = name;
    }

    public String getName(int position)
    {
        String toReturn="";
        if(position == 0)
        {
            toReturn = "harder_better_faster_stronger";
        }

        else if(position==1)
        {
            toReturn =  "fireworks";
        }

        else if(position==2)
        {
            toReturn = "titanium";
        }

        else if(position==3)
        {
            toReturn = "seven_nation_army";
        }
        return toReturn;
    }

    public String getName()
    {
        return mSongName;
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
        songs.add(new Song("Harder, Better, Faster, Stronger"));
        songs.add(new Song("Fireworks"));
        songs.add(new Song("Titanium"));
        songs.add(new Song("Seven Nation Army"));
        return songs;
    }
}
