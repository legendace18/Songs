package com.legend.ace18.songs;

import com.legend.ace18.songs.model.Songs;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by rohan on 7/7/15.
 */
public interface WebService {

    @GET("/songs.php")
    public void getSongs(Callback<List<Songs>> response);

}
