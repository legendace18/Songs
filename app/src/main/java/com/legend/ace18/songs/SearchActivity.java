package com.legend.ace18.songs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.legend.ace18.songs.adapters.SearchAdapter;
import com.legend.ace18.songs.model.Songs;
import com.legend.ace18.songs.utils.DatabaseHandler;
import com.legend.ace18.songs.utils.MusicRetriever;

import java.util.List;


public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private Toolbar toolbar;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private DatabaseHandler db;
    private List<Songs> songsList;
    private String context;
    private SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setToolbar();
        db = new DatabaseHandler(this);
        Intent intent = getIntent();
        context = intent.getStringExtra("CONTEXT");
        Log.d("legend.ace18", "" + context);
        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.onActionViewExpanded();
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (context.equalsIgnoreCase("LibraryFragment"))
            songsList = new MusicRetriever(this).filter(query);
        else if (context.equalsIgnoreCase("SongsFragment"))
            songsList = db.searchSongs(query);
        updateViews();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (context.equalsIgnoreCase("LibraryFragment")) {
            songsList = new MusicRetriever(this).filter(newText);
        } else if (context.equalsIgnoreCase("SongsFragment"))
            songsList = db.searchSongs(newText);
        updateViews();
        return false;
    }

    private void updateViews() {
        adapter = new SearchAdapter(this, songsList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onClose() {
        return false;
    }
}
