package com.legend.ace18.songs.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.legend.ace18.songs.model.PlayList;
import com.legend.ace18.songs.model.Songs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohan on 7/12/15.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "songs";

    // table name
    private static final String TABLE_NAME = "songs";
    private static final String TABLE_PLAYLIST = "playlist";
    private static final String TABLE_PLAYLIST_SONGS = "playlist_songs";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_LYRICS = "lyrics";
    private static final String KEY_ARTIST = "artist";
    private static final String KEY_ALBUM = "album";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_PATH = "path";
    private static final String KEY_ALBUMARTURI = "albumArturi";
    private static final String KEY_PLAYLISTID = "playlist_id";
    private static final String KEY_SONGS_ID = "songs_id";
    private static final String KEY_USER_ID = "user_id";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SONGS_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + KEY_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_SONGS_ID + " INTEGER,"
                + KEY_USER_ID + " INTEGER," + KEY_TITLE + " VARCHAR," + KEY_ARTIST + " VARCHAR,"
                + KEY_LYRICS + " VARCHAR " + ")";

        String CREATE_PLAYLIST_TABLE = "CREATE TABLE " + TABLE_PLAYLIST + "(" + KEY_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TITLE + " VARCHAR," + KEY_DESCRIPTION + " VARCHAR " + ")";

        String CREATE_PLAYLIST_SONGS_TABLE = "CREATE TABLE " + TABLE_PLAYLIST_SONGS + "(" + KEY_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TITLE + " VARCHAR," + KEY_ARTIST + " VARCHAR, "
                + KEY_ALBUM + " VARCHAR, " + KEY_DURATION + " INTEGER, " + KEY_PATH + " VARCHAR, "
                + KEY_ALBUMARTURI + " VARCHAR, " + KEY_PLAYLISTID + " INTEGER, "
                + " FOREIGN KEY (" + KEY_PLAYLISTID + ") REFERENCES " + TABLE_PLAYLIST + "(" + KEY_ID + ")" + ")";

        db.execSQL(CREATE_SONGS_TABLE);
        db.execSQL(CREATE_PLAYLIST_TABLE);
        db.execSQL(CREATE_PLAYLIST_SONGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    public void addSongs(List<Songs> songsList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);

        for (Songs songs : songsList) {
            ContentValues values = new ContentValues();
            values.put(KEY_SONGS_ID, songs.getSongs_id());
            values.put(KEY_USER_ID, songs.getUser_id());
            values.put(KEY_TITLE, songs.getTitle());
            values.put(KEY_ARTIST, songs.getArtist());
            values.put(KEY_LYRICS, songs.getLyrics());

            db.insert(TABLE_NAME, null, values);
        }
        db.close();
    }

    public List<Songs> getSongs() {
        List<Songs> songsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Songs songs = new Songs();
                songs.setSongs_id(cursor.getInt(1));
                songs.setUser_id(cursor.getInt(2));
                songs.setTitle(cursor.getString(3));
                songs.setArtist(cursor.getString(4));
                songs.setLyrics(cursor.getString(5));

                songsList.add(songs);
            } while (cursor.moveToNext());
        }
        return songsList;
    }

    public List<Songs> searchSongs(String newText) {
        List<Songs> songsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null,
                KEY_TITLE + " LIKE ?", new String[]{"%" + newText + "*"}, null, null, null);
        Log.d("Cursor", "" + cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                Songs songs = new Songs();
                songs.setSongs_id(cursor.getInt(1));
                songs.setUser_id(cursor.getInt(2));
                songs.setTitle(cursor.getString(3));
                songs.setArtist(cursor.getString(4));
                songs.setLyrics(cursor.getString(5));

                songsList.add(songs);
            } while (cursor.moveToNext());
        }

        return songsList;
    }

    public boolean addPlayList(PlayList playList, Songs songs) {
        Log.d("legend.ace18", "Add" + songs.getTitle());
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PLAYLIST + " WHERE " + KEY_TITLE + "=?";
        Cursor cur = db.rawQuery(query, new String[]{playList.getTitle()});
        if (cur.getCount() >= 1) {
            return false;
        } else {
            ContentValues values = new ContentValues();
            values.put(KEY_TITLE, playList.getTitle());
            values.put(KEY_DESCRIPTION, playList.description);
            db.insert(TABLE_PLAYLIST, null, values);
            getPlayListId(playList.getTitle(), songs);
        }
        db.close();
        return true;
    }

    private void getPlayListId(String title, Songs songs) {
        int id = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + KEY_ID + " FROM " + TABLE_PLAYLIST + " WHERE " + KEY_TITLE + "=?";
        Cursor cur = db.rawQuery(query, new String[]{title});
        if (cur.moveToFirst()) {
            id = cur.getInt(0);
        }
        addPlayListSongs(songs, id);
    }

    public List<PlayList> getPlayList() {
        List<PlayList> playLists = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PLAYLIST;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                PlayList playList = new PlayList();
                playList.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                playList.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                playList.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));

                playLists.add(playList);
            } while (cursor.moveToNext());
        }
        db.close();
        return playLists;
    }

    public void addPlayListSongs(Songs songs, int playlistId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, songs.getTitle());
        values.put(KEY_ARTIST, songs.getArtist());
        values.put(KEY_ALBUM, songs.getAlbum());
        values.put(KEY_PATH, songs.getPath());
        values.put(KEY_DURATION, songs.getDuration());
        values.put(KEY_ALBUMARTURI, songs.getAlbumArtUri().toString());
        values.put(KEY_PLAYLISTID, playlistId);
        db.insert(TABLE_PLAYLIST_SONGS, null, values);
        db.close();
    }

    public List<Songs> getPlayListSongs(int playListId) {
        List<Songs> songsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PLAYLIST_SONGS + " WHERE " + KEY_PLAYLISTID + "=?";
        Cursor cur = db.rawQuery(query, new String[]{String.valueOf(playListId)});
        if (cur.moveToFirst()) {
            do {
                Songs songs = new Songs();
                songs.setTitle(cur.getString(cur.getColumnIndex(KEY_TITLE)));
                songs.setArtist(cur.getString(cur.getColumnIndex(KEY_ARTIST)));
                songs.setAlbum(cur.getString(cur.getColumnIndex(KEY_ALBUM)));
                songs.setPath(cur.getString(cur.getColumnIndex(KEY_PATH)));
                songs.setDuration(cur.getInt(cur.getColumnIndex(KEY_DURATION)));
                songs.setAlbumArtUri(Uri.parse(cur.getString(cur.getColumnIndex(KEY_ALBUMARTURI))));
                songsList.add(songs);
            } while (cur.moveToNext());
        }
        return songsList;
    }

    public boolean removePlayList(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] whereArgs = new String[]{String.valueOf(id)};
        int count = db.delete(TABLE_PLAYLIST_SONGS, KEY_PLAYLISTID + "=?", whereArgs);
        if (count == 1)
            return db.delete(TABLE_PLAYLIST, KEY_ID + "=?", whereArgs) > 0;
        else
            return false;

    }
}
