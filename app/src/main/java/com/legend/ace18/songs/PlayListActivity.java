package com.legend.ace18.songs;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.legend.ace18.songs.adapters.SongsAdapter;
import com.legend.ace18.songs.model.Songs;
import com.legend.ace18.songs.utils.Controllers;
import com.legend.ace18.songs.utils.DatabaseHandler;
import com.legend.ace18.songs.utils.MusicRetriever;
import com.legend.ace18.songs.utils.MusicService;
import com.legend.ace18.songs.utils.SongUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;


public class PlayListActivity extends AppCompatActivity implements MusicService.MusicServiceListener,
        SlidingUpPanelLayout.PanelSlideListener,
        SeekBar.OnSeekBarChangeListener,
        SongsAdapter.TouchListener {

    private int playListId;
    private String title;
    private String description;
    private DatabaseHandler db;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
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
    private RelativeLayout player_layout;

    private SongUtils utils;
    private Handler pHandler = new Handler();
    private List<Songs> songsList;
    private SongsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);

        Intent intent = getIntent();
        playListId = intent.getIntExtra("PLAYLIST_ID", 0);
        title = intent.getStringExtra("PLAYLIST_TITLE");
        description = intent.getStringExtra("PLAYLIST_DESC");
        db = new DatabaseHandler(this);
        utils = new SongUtils();
        setToolbar();
        initViews();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getSongs(playListId);
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (musicBound) {
                    onSongClick(songsList, position);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicBound) {
                    if (musicSrv.isMusicSet) {
                        if (musicSrv.isPlaying()) {
                            musicSrv.pauseSong();
                            btn_play.setImageResource(R.drawable.ic_play_circle_fill_blue_48dp);
                            btn_play_short.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                        } else {
                            musicSrv.resumeSong();
                            btn_play.setImageResource(R.drawable.ic_pause_circle_fill_blue_48dp);
                            btn_play_short.setImageResource(R.drawable.ic_pause_black_48dp);
                        }
                    } else {
                        List<Songs> songsList = new MusicRetriever(PlayListActivity.this).prepare();
                        onSongClick(songsList, 0);
                    }
                }
            }
        });

        btn_play_short.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicBound) {
                    if (musicSrv.isMusicSet) {
                        if (musicSrv.isPlaying()) {
                            musicSrv.pauseSong();
                            btn_play_short.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                            btn_play.setImageResource(R.drawable.ic_play_circle_fill_blue_48dp);
                        } else {
                            musicSrv.resumeSong();
                            btn_play_short.setImageResource(R.drawable.ic_pause_black_48dp);
                            btn_play.setImageResource(R.drawable.ic_pause_circle_fill_blue_48dp);
                        }
                    }
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicBound) {
                    if (musicSrv.isMusicSet) {
                        musicSrv.playNext();
                    }
                }
            }
        });

        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicBound) {
                    if (musicSrv.isMusicSet) {
                        musicSrv.playPrev();
                    }
                }
            }
        });

        btn_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicBound) {
                    int status = musicSrv.isRepeat;
                    switch (status) {
                        case 0:
                            musicSrv.isRepeat = 1;
                            btn_repeat.setImageResource(R.drawable.ic_repeat_blue_24dp);
                            break;
                        case 1:
                            musicSrv.isRepeat = 2;
                            btn_repeat.setImageResource(R.drawable.ic_repeat_one_blue_24dp);
                            break;
                        case 2:
                            musicSrv.isRepeat = 0;
                            btn_repeat.setImageResource(R.drawable.ic_repeat_black_24dp);
                            break;
                    }
                }
            }
        });

        btn_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicBound) {
                    if (musicSrv.isShuffle) {
                        musicSrv.isShuffle = false;
                        btn_shuffle.setImageResource(R.drawable.ic_shuffle_black_24dp);
                    } else {
                        musicSrv.isShuffle = true;
                        btn_shuffle.setImageResource(R.drawable.ic_shuffle_blue_24dp);
                    }
                }
            }
        });
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            musicSrv.setMusicServiceListener(PlayListActivity.this);
            if (musicSrv.isMusicSet) {
                setSongDetail(musicSrv.getSongDetails());
                updateProgressBar();
                Controllers.setControllers(musicSrv, btn_play, btn_play_short, btn_shuffle, btn_repeat);
            }else{
                player_layout.setVisibility(View.GONE);
            }
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        // Start and Bind service to activity

        if (serviceIntent == null) {
            Log.d("legend.ace18", "ololo");
            serviceIntent = new Intent(this, MusicService.class);
            startService(serviceIntent);
            bindService(serviceIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        collapsingToolbarLayout.setTitle(title);
    }

    private void initViews() {
        player_layout = (RelativeLayout) findViewById(R.id.player);
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

    private void getSongs(int playListId) {
        songsList = db.getPlayListSongs(playListId);
        adapter = new SongsAdapter(this, songsList);
        adapter.setTouchListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void onSongClick(List<Songs> songsList, int position) {
        musicSrv.setList(songsList);
        musicSrv.playSong(position);
        btn_play.setImageResource(R.drawable.ic_pause_circle_fill_blue_48dp);
        btn_play_short.setImageResource(R.drawable.ic_pause_black_48dp);
        slidingLayout.setPanelState(PanelState.COLLAPSED);
        updateProgressBar();
    }


    private void setSongDetail(Songs songs) {
        tv_title.setText(songs.getTitle());
        tv_artist.setText(songs.getArtist());
        String totalDuration = utils.milliSecondsToTimer(songs.getDuration());
        tv_totalDuration.setText(totalDuration);
        tv_currentTime.setText("0:00");
        if (songs.getAlbumArtUri() != null) {
            Picasso.with(this).load(songs.getAlbumArtUri()).into(playerImage);
            Picasso.with(this).load(songs.getAlbumArtUri()).fit().into(songImage);
        }
    }


    /**
     * Update timer on seekbar
     */
    public void updateProgressBar() {
        songProgressbar.setProgress(0);
        songProgressbar.setMax(100);
        pHandler.postDelayed(pUpdateTimeTask, 200);
    }

    /**
     * Background Runnable thread
     */
    private Runnable pUpdateTimeTask = new Runnable() {
        public void run() {
            int totalDuration = musicSrv.getDuration();
            int currentDuration = musicSrv.getCurrentPosition();

            tv_currentTime.setText(utils.milliSecondsToTimer(currentDuration));
            // Updating progress bar
            int progress = utils.getProgressPercentage(currentDuration, totalDuration);
            //Log.d("Progress", ""+progress);
            songProgressbar.setProgress(progress);

            // Running this thread after 100 milliseconds
            if (musicBound)
                pHandler.postDelayed(this, 200);
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if (musicBound) {
            unbindService(musicConnection);
            musicBound = false;
        }
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
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPlayMusic(Songs songs) {
        setSongDetail(songs);
    }

    @Override
    public void onStopMusic() {
        pHandler.removeCallbacks(pUpdateTimeTask);
    }

    @Override
    public void onPanelSlide(View view, float v) {

    }

    @Override
    public void onPanelCollapsed(View view) {
        btn_play_short.setVisibility(View.VISIBLE);
        btn_overflow.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPanelExpanded(View view) {
        btn_play_short.setVisibility(View.INVISIBLE);
        btn_overflow.setVisibility(View.VISIBLE);
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
        int progress = seekBar.getProgress();
        int time = utils.progressToTimer(progress, musicSrv.getDuration());
        musicSrv.seekTo(time);
        seekBar.setProgress(progress);
    }

    private void setControllers() {
        if (musicSrv.isPlaying()) {
            btn_play.setImageResource(R.drawable.ic_pause_circle_fill_blue_48dp);
            btn_play_short.setImageResource(R.drawable.ic_pause_black_48dp);
        } else {
            btn_play.setImageResource(R.drawable.ic_play_circle_fill_blue_48dp);
            btn_play_short.setImageResource(R.drawable.ic_play_arrow_black_48dp);
        }
        if (musicSrv.isRepeat == 0) {
            btn_repeat.setImageResource(R.drawable.ic_repeat_black_24dp);
        } else if (musicSrv.isRepeat == 1) {
            btn_repeat.setImageResource(R.drawable.ic_repeat_blue_24dp);
        } else {
            btn_repeat.setImageResource(R.drawable.ic_repeat_one_blue_24dp);
        }
        if (musicSrv.isShuffle) {
            btn_shuffle.setImageResource(R.drawable.ic_shuffle_blue_24dp);
        } else {
            btn_shuffle.setImageResource(R.drawable.ic_shuffle_black_24dp);
        }
    }

    @Override
    public void itemTouched(View v, final int position) {
        final Songs songs = songsList.get(position);
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.playlist_songs_popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.action_play:
                        onSongClick(songsList, position);
                        break;
                    case R.id.action_remove:
                        int count = db.removePlayListSong(playListId, songs.getSongs_id());
                        if (count == 1) {
                            songsList.remove(position);
                            adapter.notifyItemRemoved(position);
                        }
                        break;
                }
                return true;
            }
        });
        popup.show();
    }
}
