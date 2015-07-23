package com.legend.ace18.songs.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.legend.ace18.songs.R;
import com.legend.ace18.songs.model.Songs;

import java.util.Collections;
import java.util.List;

/**
 * Created by rohan on 7/7/15.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    private List<Songs> songsList = Collections.emptyList();

    public CustomAdapter(Context context, List<Songs> songList) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.songsList = songList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Songs songs = songsList.get(position);
        holder.tv_title.setText(songs.getTitle());
        holder.tv_lyrics.setText(songs.getLyrics());
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_cardImage;
        private TextView tv_title, tv_lyrics;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_cardImage = (ImageView) itemView.findViewById(R.id.iv_cardImage);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_lyrics = (TextView) itemView.findViewById(R.id.tv_lyrics);
        }
    }
}
