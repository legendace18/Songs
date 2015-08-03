package com.legend.ace18.songs;


import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.legend.ace18.songs.adapters.PlayListAdapter;
import com.legend.ace18.songs.model.PlayList;
import com.legend.ace18.songs.utils.DatabaseHandler;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayListFragment extends Fragment implements LibraryFragment.FragmentRefreshListener,
PlayListAdapter.TouchListener{

    private RecyclerView recyclerView;
    private DatabaseHandler db;
    private PlayListAdapter adapter;
    private List<PlayList> playLists;

    public PlayListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_play_list, container, false);
        db = new DatabaseHandler(getActivity());

        recyclerView = (RecyclerView) layout.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                PlayList playList = playLists.get(position);
                Intent intent = new Intent(getActivity(), PlayListActivity.class);
                intent.putExtra("PLAYLIST_ID", playList.getId());
                intent.putExtra("PLAYLIST_TITLE", playList.getTitle());
                intent.putExtra("PLAYLIST_DESC", playList.getDescription());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateViews();

    }

    private void updateViews() {
        playLists = db.getPlayList();
        adapter = new PlayListAdapter(getActivity(), R.layout.playlist_row, playLists);
        adapter.setTouchListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void refreshFragment() {
        updateViews();
    }

    @Override
    public void itemTouched(View v, final int position) {
        final PlayList playList = playLists.get(position);
        PopupMenu popup = new PopupMenu(getActivity(), v);
        popup.getMenuInflater().inflate(R.menu.playlist_popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch(id){
                    case R.id.action_open:

                        Intent intent = new Intent(getActivity(), PlayListActivity.class);
                        intent.putExtra("PLAYLIST_ID", playList.getId());
                        intent.putExtra("PLAYLIST_TITLE", playList.getTitle());
                        intent.putExtra("PLAYLIST_DESC", playList.getDescription());
                        startActivity(intent);
                        break;
                    case R.id.action_remove:
                        if(db.removePlayList(playList.getId())){
                            playLists.remove(position);
                            adapter.notifyItemRemoved(position);
                        }
                        break;
                    case R.id.action_edit:
                        break;
                }
                return true;
            }
        });
        popup.show();
    }
}
