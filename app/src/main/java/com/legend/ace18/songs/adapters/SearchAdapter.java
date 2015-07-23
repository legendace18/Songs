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

import java.util.List;

/**
 * Created by rohan on 7/14/15.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    private Context context;
    private List<Songs> songsList;
    private LayoutInflater inflater;

    public SearchAdapter(Context context, List<Songs> songsList){
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.songsList = songsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.search_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Songs songs = songsList.get(position);
        holder.tv_title.setText(songs.getTitle());
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView iv_songsImage;
        private TextView tv_title;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_songsImage = (ImageView) itemView.findViewById(R.id.iv_songImage);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }
}
