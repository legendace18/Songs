package com.legend.ace18.songs;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;

import com.legend.ace18.songs.adapters.PlayListAdapter;
import com.legend.ace18.songs.adapters.SongsAdapter;
import com.legend.ace18.songs.model.PlayList;
import com.legend.ace18.songs.model.Songs;
import com.legend.ace18.songs.utils.DatabaseHandler;
import com.legend.ace18.songs.utils.MusicRetriever;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicFragment extends Fragment implements SongsAdapter.TouchListener, LibraryFragment.FragmentRefreshListener {

    private List<Songs> songsList;
    private RecyclerView recyclerView;
    private SongClickListener songClickListener;
    private DatabaseHandler db;

    public MusicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_music, container, false);
        db = new DatabaseHandler(getActivity());

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

        SongsAdapter adapter = new SongsAdapter(getActivity(), songsList);
        adapter.setTouchListener(this);
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

    @Override
    public void itemTouched(View v, final int position) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.action_play:
                        songClickListener.onSongClick(position, songsList);
                        break;
                    case R.id.action_addPlayList:
                        showPlayListPopup(songsList.get(position));
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    private void showPlayListPopup(final Songs songs) {
        final List<PlayList> playLists = db.getPlayList();
        PlayListAdapter adapter;

        final Dialog d = new Dialog(getActivity());
        d.setContentView(R.layout.popup_content);
        d.setTitle("PlayLists");
        d.setCancelable(true);
        RecyclerView pRecyclerView = (RecyclerView) d.findViewById(R.id.playList_recyclerView);
        pRecyclerView.setHasFixedSize(true);
        pRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        pRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), pRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                PlayList playList = playLists.get(position);
                db.addPlayListSongs(songs, playList.getId());
                d.dismiss();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        Button btn_createPlaylist = (Button) d.findViewById(R.id.btn_createPlaylist);
        adapter = new PlayListAdapter(getActivity(), R.layout.search_row, playLists);
        pRecyclerView.setAdapter(adapter);
        btn_createPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreatePlaylistDialog(songs);
                d.dismiss();
            }
        });
        d.show();
    }

    private void showCreatePlaylistDialog(final Songs songs) {
        final Dialog d = new Dialog(getActivity());
        d.setContentView(R.layout.dialog_add_playlist);
        d.setTitle("Create PlayList");
        d.setCancelable(false);
        final EditText et_title = (EditText) d.findViewById(R.id.et_title);
        final EditText et_description = (EditText) d.findViewById(R.id.et_description);
        Button btn_create = (Button) d.findViewById(R.id.btn_create);
        Button btn_cancel = (Button) d.findViewById(R.id.btn_cancel);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = et_title.getText().toString();
                String description = et_description.getText().toString();
                db.addPlayList(new PlayList(title, description), songs);
                d.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });
        d.show();
    }

    @Override
    public void refreshFragment() {

    }
}
