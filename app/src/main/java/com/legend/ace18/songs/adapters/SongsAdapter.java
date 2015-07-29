package com.legend.ace18.songs.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.legend.ace18.songs.R;
import com.legend.ace18.songs.model.Songs;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

/**
 * Created by Legend.ace18 on 7/25/2015.
 */
public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    private List<Songs> songsList = Collections.emptyList();
    private TouchListener touchListener;

    public SongsAdapter(Context context, List<Songs> songList) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.songsList = songList;
    }

    @Override
    public SongsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.songs_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(SongsAdapter.MyViewHolder holder, int position) {
        Songs songs = songsList.get(position);
        holder.tv_title.setText(songs.getTitle());
        holder.tv_artist.setText(songs.getArtist());
        if (songs.getAlbumArtUri() != null)
            Picasso.with(context).load(songs.getAlbumArtUri()).fit().into(holder.iv_cardImage);
        else
            holder.iv_cardImage.setImageResource(R.drawable.music_bg);
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public void setTouchListener(TouchListener touchListener){
        this.touchListener = touchListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{

        private ImageView iv_cardImage;
        private ImageButton btn_overflow;
        private TextView tv_title, tv_artist;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_cardImage = (ImageView) itemView.findViewById(R.id.iv_cardImage);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_artist = (TextView) itemView.findViewById(R.id.tv_artist);
            btn_overflow = (ImageButton) itemView.findViewById(R.id.btn_overflow);
            btn_overflow.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            view.getParent().requestDisallowInterceptTouchEvent(true);
            switch(motionEvent.getAction()){
                case MotionEvent.ACTION_UP:
                    //Toast.makeText(context, "from adapter", Toast.LENGTH_LONG).show();
                    touchListener.itemTouched(view, getAdapterPosition());
                    return true;

            }
            return false;
        }
    }

    public interface TouchListener{
        void itemTouched(View v, int position);
    }
}
