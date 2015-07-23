package com.legend.ace18.songs.utils;

import android.os.Environment;

import com.legend.ace18.songs.model.Songs;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by rohan on 7/19/15.
 */
public class SongsManager {
    // SDCard Path
    private final String MEDIA_PATH = "" + Environment.getExternalStorageDirectory();
    private ArrayList<Songs> songsList = new ArrayList<>();

    // Constructor
    public SongsManager(){

    }

    /**
     * Function to read all mp3 files from sdcard
     * and store the details in ArrayList
     * */
    public ArrayList<Songs> getPlayList(){
        File home = new File(MEDIA_PATH);

        if (home.listFiles(new FileExtensionFilter()).length > 0) {
            for (File file : home.listFiles(new FileExtensionFilter())) {
                Songs songs = new Songs();
                songs.setTitle(file.getName().substring(0, (file.getName().length() - 4)));
                songs.setPath(file.getPath());

                songsList.add(songs);
            }
        }
        // return songs list array
        return songsList;
    }

    /**
     * Class to filter files which are having .mp3 extension
     * */
    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }
}


