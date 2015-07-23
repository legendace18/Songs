package com.legend.ace18.songs.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.legend.ace18.songs.model.Songs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohan on 7/20/15.
 */
public class MusicRetriever {

    private Context context;
    private List<Songs> songsList = new ArrayList<>();

    public MusicRetriever(Context context) {
        this.context = context;
    }

    public List<Songs> prepare() {
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cur = context.getContentResolver().query(uri, null,
                MediaStore.Audio.Media.IS_MUSIC + " = 1", null, null);

        int artistColumn = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int titleColumn = cur.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int albumColumn = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        int durationColumn = cur.getColumnIndex(MediaStore.Audio.Media.DURATION);
        int dataColumn = cur.getColumnIndex(MediaStore.Audio.Media.DATA);
        int idColumn = cur.getColumnIndex(MediaStore.Audio.Media._ID);

        if (cur == null) {
            Log.d("legend.ace18", "NO data");
        } else {
            if (cur.moveToFirst()) {
                do {
                    Songs songs = new Songs();
                    songs.setTitle(cur.getString(titleColumn));
                    songs.setArtist(cur.getString(artistColumn));
                    songs.setAlbum(cur.getString(albumColumn));
                    songs.setDuration(cur.getLong(durationColumn));
                    songs.setPath(cur.getString(dataColumn));
                    songsList.add(songs);
                } while (cur.moveToNext());
            }
        }
        return songsList;
    }

}
