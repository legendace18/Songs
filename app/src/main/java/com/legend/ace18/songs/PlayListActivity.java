package com.legend.ace18.songs;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.legend.ace18.songs.adapters.SongsAdapter;
import com.legend.ace18.songs.model.Songs;
import com.legend.ace18.songs.utils.DatabaseHandler;
import com.legend.ace18.songs.utils.MusicService;
import com.legend.ace18.songs.utils.SongUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;


public class PlayListActivity extends AppCompatActivity implements MusicService.MusicServiceListener,
SlidingUpPanelLayout.PanelSlideListener,
SeekBar.OnSeekBarChangeListener{

    private int playListId;
    private DatabaseHandler db;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    //service
    private MusicService musicSrv;
    private Intent serviceIntent;
    //binding
    private boolean musicBound = false;

    private TextView tv_title, tv_artist, tv_currentTime, tv_totalDuration;
    private ImageButton btn_play, btn_next, btn_prev, btn_repeat, btn_shuffle, btn_like, btn_play_short, btn_overflow;
    private ImageView playerImage, songImage;
    private SlidingUpPanelLayout slidingLayout;
    private SeekBar songProgressbar;

    private SongUtils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        Intent intent = getIntent();
        playListId = intent.getIntExtra("PLAYLIST_ID", 0);
        db = new DatabaseHandler(this);
        utils = new SongUtils();
        setToolbar();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getSongs(playListId);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_artist = (TextView) findViewById(R.id.tv_artist);
        tv_currentTime = (TextView) findViewById(R.id.tv_currentTime);
        tv_totalDuration = (TextView) findViewById(R.id.tv_totalDuration);
        btn_play = (ImageButton) findViewById(R.id.btn_play);
        btn_next = (ImageButton) findViewById(R.id.btn_next);
        btn_prev = (ImageButton) findViewById(R.id.btn_prev);
        btn_repeat = (ImageButton) findViewById(R.id.btn_repeat);
        btn_shuffle = (ImageButton) findViewById(R.id.btn_shuffle);
        btn_play_short = (ImageButton) findViewById(R.id.btn_play_short);
        btn_overflow = (ImageButton) findViewById(R.id.btn_overflow);
        playerImage = (ImageView) findViewById(R.id.iv_playerImage);
        songImage = (ImageView) findViewById(R.id.iv_songImage);
        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingLayout.setPanelSlideListener(this);
        songProgressbar = (SeekBar) findViewById(R.id.songProgressBar);
        songProgressbar.setOnSeekBarChangeListener(this);
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            musicSrv.setMusicServiceListener(PlayListActivity.this);
            if(musicSrv.isPlaying()){
                setSongsDetail(musicSrv.getSongDetails());
            }
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    private void setSongsDetail(Songs songs) {
        tv_title.setText(songs.getTitle());
        tv_artist.setText(songs.getArtist());
        String totalDuration = utils.milliSecondsToTimer(songs.getDuration());
        tv_totalDuration.setText(totalDuration);
        tv_currentTime.setText("0:00");
        if (songs.getAlbumArtUri() != null) {
            Picasso.with(this).load(songs.getAlbumArtUri()).fit().into(playerImage);
            Picasso.with(this).load(songs.getAlbumArtUri()).fit().into(songImage);
        } else {
            playerImage.setImageResource(R.drawable.music_bg);
            songImage.setImageResource(R.drawable.music_bg);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Start and Bind service to activity
        if (serviceIntent == null) {
            serviceIntent = new Intent(this, MusicService.class);
            startService(serviceIntent);
            bindService(serviceIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void getSongs(int playListId) {
        List<Songs> songsList = db.getPlayListSongs(playListId);
        SongsAdapter adapter = new SongsAdapter(this, songsList);
        recyclerView.setAdapter(adapter);
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPlayMusic(Songs songs) {

    }

    @Override
    public void onPanelSlide(View view, float v) {

    }

    @Override
    public void onPanelCollapsed(View view) {

    }

    @Override
    public void onPanelExpanded(View view) {

    }

    @Override
    public void onPanelAnchored(View view) {

    }

    @Override
    public void onPanelHidden(View view) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
