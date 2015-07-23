package com.legend.ace18.songs;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.legend.ace18.songs.adapters.CustomAdapter;
import com.legend.ace18.songs.model.Songs;
import com.legend.ace18.songs.utils.MusicPlayer;
import com.legend.ace18.songs.utils.MusicRetriever;
import com.legend.ace18.songs.utils.MusicService;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicFragment extends Fragment {

    private List<Songs> songsList;
    private RecyclerView recyclerView;
    private SongClickListener songClickListener;

    public MusicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_music, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return layout;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MusicRetriever musicRetriever = new MusicRetriever(getActivity());
        songsList = musicRetriever.prepare();
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.d("legend.ace", "" + position);
                songClickListener.onSongClick(position, songsList);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        CustomAdapter adapter = new CustomAdapter(getActivity(), songsList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            songClickListener = (SongClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SongClickListener");
        }
    }

}
