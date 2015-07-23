package com.legend.ace18.songs;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import com.legend.ace18.songs.utils.NetworkHandler;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class MySongsFragment extends Fragment {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private DrawerToggleListener drawerToggleListener;
    private NetworkHandler networkHandler;
    private ProgressBar progressBar;
    private CoordinatorLayout rootLayout;

    public MySongsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout =  inflater.inflate(R.layout.fragment_my_songs, container, false);
        setHasOptionsMenu(true);
        setToolbar(layout);
        setFAB(layout);
        networkHandler = new NetworkHandler(getActivity());
        recyclerView = (RecyclerView) layout.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        rootLayout = (CoordinatorLayout) layout.findViewById(R.id.rootLayout);
        progressBar = (ProgressBar) layout.findViewById(R.id.progressBar);
        return layout;
    }

    private void setFAB(View layout) {
        FloatingActionButton btn_fab = (FloatingActionButton) layout.findViewById(R.id.fabBtn);
        btn_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(rootLayout, "This is Snack Bar.", Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }

    ;
    private void setToolbar(View layout) {
        toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_songs));
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        drawerToggleListener.setNavigationView(toolbar);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (networkHandler.isOnline()) {
            requestDataFromServer();
        } else {
            Snackbar.make(rootLayout, "Not connected to network.", Snackbar.LENGTH_LONG)
                    .setAction("Try Again", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestDataFromServer();
                        }
                    })
                    .show();
        }
    }

    private void requestDataFromServer() {
        progressBar.setVisibility(View.VISIBLE);
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(Conf.ENDPOINT)
                .build();
        WebService api = adapter.create(WebService.class);
        api.getSongs(new Callback<List<Songs>>() {
            @Override
            public void success(List<Songs> songsList, Response response) {
                progressBar.setVisibility(View.GONE);
                updateViews(songsList);
            }

            @Override
            public void failure(RetrofitError error) {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(rootLayout, "" + error, Snackbar.LENGTH_LONG)
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

    private void updateViews(List<Songs> songsList) {
        CustomAdapter adapter = new CustomAdapter(getActivity(), songsList);
        recyclerView.setAdapter(adapter);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_search:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
