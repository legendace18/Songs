package com.legend.ace18.songs;

import com.legend.ace18.songs.model.Songs;

import java.util.List;

/**
 * Created by Legend.ace18 on 7/22/2015.
 */
public interface SongClickListener {
    //void onSongsPrepared(List<Songs> songsList);
    void onSongClick(int position, List<Songs> songsList);
}
