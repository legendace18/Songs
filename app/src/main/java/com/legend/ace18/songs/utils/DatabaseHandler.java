package com.legend.ace18.songs.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_LYRICS = "lyrics";
    private static final String KEY_ARTIST = "artist";
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

        db.execSQL(CREATE_SONGS_TABLE);
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
}
