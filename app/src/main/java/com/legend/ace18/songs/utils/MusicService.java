package com.legend.ace18.songs.utils;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.legend.ace18.songs.model.Songs;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by rohan on 7/19/15.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {

    private MediaPlayer player;
    private List<Songs> songsList;
    private int songIndex;

    private Boolean isShuffle = false;
    private Boolean isRepreat = false;
    private String title, artist;

    private final IBinder musicBind = new MusicBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        initMusicPlayer();
    }

    private void initMusicPlayer() {
        //set listeners
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(List<Songs> songsList) {
        this.songsList = songsList;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void playSong(int songIndex) {
        this.songIndex = songIndex;
        Songs songs = songsList.get(songIndex);
        title = songs.getTitle();
        artist = songs.getArtist();
        player.reset();
        try {
            player.setDataSource(songs.getPath());
            player.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pauseSong() {
        if (player.isPlaying())
            player.pause();
    }

    public void resumeSong() {
        if (!player.isPlaying())
            player.start();
    }

    public void playNext() {
        if (isShuffle) {
            int newSong = songIndex;
            while (newSong == songIndex) {
                newSong = new Random().nextInt(songsList.size());
            }
            songIndex = newSong;
        } else {
            songIndex++;
            if (songIndex >= songsList.size()) songIndex = 0;
        }
        playSong(songIndex);
    }

    public void playPrev() {
        songIndex--;
        if (songIndex < 0) songIndex = songsList.size() - 1;
        playSong(songIndex);
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public Songs getSongDetails() {
        Songs songs = new Songs();
        songs.setTitle(title);
        songs.setArtist(artist);
        return songs;
    }

    public int getDuration() {
        if (player != null) return player.getDuration();
        return 0;
    }

    public void seekTo(int time) {
        player.seekTo(time);
    }

    public int getCurrentPosition() {
        if (player != null)
            return player.getCurrentPosition();
        return 0;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer player) {
        if (player.getCurrentPosition() > 0) {
            player.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer player, int i, int i1) {
        Log.d("MUSIC PLAYER", "Playback Error");
        player.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    //binder
    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

}
