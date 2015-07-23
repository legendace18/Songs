package com.legend.ace18.songs;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.legend.ace18.songs.adapters.CustomAdapter;
import com.legend.ace18.songs.model.Songs;
import com.legend.ace18.songs.utils.Conf;
import com.legend.ace18.songs.utils.DatabaseHandler;
import com.legend.ace18.songs.utils.NetworkHandler;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class SongsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NetworkHandler networkHandler;
    private DrawerToggleListener drawerToggleListener;
    private ProgressBar progressBar;
    private List<Songs> songsList;
    private DatabaseHandler db;

    public SongsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_songs, container, false);
        setHasOptionsMenu(true);
        setToolbar(layout);
        networkHandler = new NetworkHandler(getActivity());
        db = new DatabaseHandler(getActivity());

        swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(R.color.color_primary);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        progressBar = (ProgressBar) layout.findViewById(R.id.progressBar);


        return layout;
    }

    private void setToolbar(View layout) {
        toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        drawerToggleListener.setNavigationView(toolbar);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                showSongsDetail(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        if (networkHandler.isOnline()) {
            requestDataFromServer();
        } else {
            songsList = db.getSongs();
            updateViews();
        }
    }

    private void requestDataFromServer() {
        if (!swipeRefreshLayout.isRefreshing())
            progressBar.setVisibility(View.VISIBLE);

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(Conf.ENDPOINT)
                .build();
        WebService api = adapter.create(WebService.class);
        api.getSongs(new Callback<List<Songs>>() {
            @Override
            public void success(List<Songs> songs, Response response) {
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                songsList = songs;
                addSongsToDb();
                updateViews();
            }

            @Override
            public void failure(RetrofitError error) {
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                Snackbar.make(recyclerView, "" + error, Snackbar.LENGTH_LONG)
                        .setAction("Try Again", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                requestDataFromServer();
                            }
                        })
                        .show();
            }
        });
    }

    private void addSongsToDb() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                db.addSongs(songsList);
            }
        });
        thread.start();
    }

    private void updateViews() {
        CustomAdapter adapter = new CustomAdapter(getActivity(), songsList);
        recyclerView.setAdapter(adapter);
    }

    private void showSongsDetail(int position) {
        Songs songs = songsList.get(position);
        Intent intent = new Intent(getActivity(), SongsDetailActivity.class);
        intent.putExtra("TITLE", songs.getTitle());
        intent.putExtra("ARTIST", songs.getArtist());
        intent.putExtra("LYRICS", songs.getLyrics());

        startActivity(intent);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            drawerToggleListener = (DrawerToggleListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DrawerToggleListener");
        }
    }

    @Override
    public void onRefresh() {
        requestDataFromServer();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
