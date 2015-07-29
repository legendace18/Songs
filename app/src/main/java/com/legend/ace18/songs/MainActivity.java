package com.legend.ace18.songs;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.legend.ace18.songs.model.Songs;
import com.legend.ace18.songs.utils.MusicService;
import com.legend.ace18.songs.utils.SongUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        DrawerToggleListener,
        SongClickListener,
        SeekBar.OnSeekBarChangeListener,
        MusicService.MusicServiceListener,
        SlidingUpPanelLayout.PanelSlideListener {

    private FragmentManager fm;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

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
    private Handler mHandler = new Handler();
    private SongUtils utils;
    private List<Songs> songsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNavigationView(null);

        fm = getSupportFragmentManager();
        LibraryFragment frag = new LibraryFragment();
        fm.beginTransaction().add(R.id.activity_container, frag).commit();

        utils = new SongUtils();

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

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicBound) {
                    if (musicSrv.isPlaying()) {
                        musicSrv.pauseSong();
                        btn_play.setImageResource(R.drawable.ic_play_circle_fill_blue_48dp);
                        btn_play_short.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                    } else {
                        musicSrv.resumeSong();
                        btn_play.setImageResource(R.drawable.ic_pause_circle_fill_blue_48dp);
                        btn_play_short.setImageResource(R.drawable.ic_pause_black_48dp);
                    }
                }
            }
        });

        btn_play_short.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicBound) {
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
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicBound) {
                    musicSrv.playNext();
                }
            }
        });

        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicBound) {
                    musicSrv.playPrev();
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
            musicSrv.setMusicServiceListener(MainActivity.this);
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
            serviceIntent = new Intent(this, MusicService.class);
            startService(serviceIntent);
            bindService(serviceIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }
    }


    @Override
    public void onSongClick(int position, List<Songs> songsList) {
        musicSrv.setList(songsList);
        musicSrv.playSong(position);
        btn_play.setImageResource(R.drawable.ic_pause_circle_fill_blue_48dp);
        btn_play_short.setImageResource(R.drawable.ic_pause_black_48dp);
        updateProgressBar();
    }

    private void setSongDetail(Songs songs) {
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

    /**
     * Update timer on seekbar
     */
    public void updateProgressBar() {
        songProgressbar.setProgress(0);
        songProgressbar.setMax(100);
        mHandler.postDelayed(mUpdateTimeTask, 200);
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            int totalDuration = musicSrv.getDuration();
            int currentDuration = musicSrv.getCurrentPosition();

            tv_currentTime.setText(utils.milliSecondsToTimer(currentDuration));
            // Updating progress bar
            int progress = utils.getProgressPercentage(currentDuration, totalDuration);
            //Log.d("Progress", ""+progress);
            songProgressbar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 200);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                break;
            case R.id.action_search:
                return false;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setNavigationView(Toolbar toolbar) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }

        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.action_playList:
                break;
            case R.id.action_library:
                LibraryFragment libFrag = new LibraryFragment();
                fm.beginTransaction().replace(R.id.activity_container, libFrag).commit();
                break;
            case R.id.action_main:
                SongsFragment songsFrag = new SongsFragment();
                fm.beginTransaction().replace(R.id.activity_container, songsFrag).commit();
                break;
            case R.id.action_mySongs:
                MySongsFragment mySongsFrag = new MySongsFragment();
                fm.beginTransaction().replace(R.id.activity_container, mySongsFrag).commit();
                break;
        }
        drawerLayout.closeDrawers();
        return true;
    }


    @Override
    public void onDestroy() {
        unbindService(musicConnection);
        stopService(serviceIntent);
        musicBound = false;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (slidingLayout != null &&
                (slidingLayout.getPanelState() == PanelState.EXPANDED || slidingLayout.getPanelState() == PanelState.ANCHORED)) {
            slidingLayout.setPanelState(PanelState.COLLAPSED);
        } else {
            moveTaskToBack(true);
        }
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

    @Override
    public void onPlayMusic(Songs songs) {
        setSongDetail(songs);
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
}
