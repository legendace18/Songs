package com.legend.ace18.songs.utils;

import android.media.MediaPlayer;

import com.legend.ace18.songs.model.Songs;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by rohan on 7/19/15.
 */
public class MusicPlayer {

    private MediaPlayer mp;
    private SongsManager songsManager;
    private SongUtils timer;
    private ArrayList<Songs> songsList;
    private static MusicPlayer instance = null;

    public MusicPlayer() {
        mp = new MediaPlayer();
        songsManager = new SongsManager();
        timer = new SongUtils();
        this.songsList = songsManager.getPlayList();
    }

    public static synchronized MusicPlayer getInstance() {
        if (instance == null) {
            instance = new MusicPlayer();
        }

        return instance;
    }

    public void playSong(int songIndex) {
        Songs songs = songsList.get(songIndex);
        try {
            mp.reset();
            mp.setDataSource(songs.getPath());
            mp.prepare();
            mp.start();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        mp.start();
    }

    public void pause(){
        mp.pause();
    }

    public boolean isPlaying(){
        return mp.isPlaying();
    }

}
