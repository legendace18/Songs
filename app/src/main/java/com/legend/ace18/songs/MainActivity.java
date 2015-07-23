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
import android.widget.SeekBar;
import android.widget.TextView;

import com.legend.ace18.songs.model.Songs;
import com.legend.ace18.songs.utils.MusicService;
import com.legend.ace18.songs.utils.SongUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        DrawerToggleListener,
        SongClickListener,
        SeekBar.OnSeekBarChangeListener {

    private FragmentManager fm;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    //service
    private MusicService musicSrv;
    private Intent serviceIntent;
    //binding
    private boolean musicBound = false;

    private TextView tv_title, tv_artist;
    private ImageButton btn_play, btn_next, btn_prev;
    private SlidingUpPanelLayout slidingLayout;
    private SeekBar songProgressbar;
    private Handler mHandler = new Handler();
    private SongUtils utils;

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
        btn_play = (ImageButton) findViewById(R.id.btn_play);
        btn_next = (ImageButton) findViewById(R.id.btn_next);
        btn_prev = (ImageButton) findViewById(R.id.btn_prev);
        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        songProgressbar = (SeekBar) findViewById(R.id.songProgressBar);
        songProgressbar.setOnSeekBarChangeListener(this);

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicBound) {
                    if (musicSrv.isPlaying()) {
                        musicSrv.pauseSong();
                        btn_play.setImageResource(R.drawable.ic_play_circle_fill_black_48dp);
                    } else {
                        musicSrv.resumeSong();
                        btn_play.setImageResource(R.drawable.ic_pause_circle_fill_black_48dp);
                    }
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(musicBound) {
                    musicSrv.playNext();
                    Songs songs = musicSrv.getSongDetails();
                    setSongDetail(songs);
                    setSongProgressbar();
                }
            }
        });

        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(musicBound) {
                    musicSrv.playPrev();
                    Songs songs = musicSrv.getSongDetails();
                    setSongDetail(songs);
                    setSongProgressbar();
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
            musicBound = true;
            if(musicSrv.isPlaying()){
                Songs songs = musicSrv.getSongDetails();
                setSongDetail(songs);
                setSongProgressbar();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        if (serviceIntent == null) {
            serviceIntent = new Intent(this, MusicService.class);
            startService(serviceIntent);
            bindService(serviceIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onSongClick(int position, List<Songs> songsList) {
        Songs songs = songsList.get(position);
        musicSrv.setList(songsList);
        musicSrv.playSong(position);
        setSongDetail(songs);
        setSongProgressbar();
        btn_play.setImageResource(R.drawable.ic_pause_circle_fill_black_48dp);
    }

    private void setSongProgressbar() {
        songProgressbar.setProgress(0);
        songProgressbar.setMax(100);
        updateProgressbar();
    }

    private void updateProgressbar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            int totalDuration = 0, currentDuration = 0;
            if (musicSrv != null && musicSrv.isPlaying()) {
                totalDuration = musicSrv.getDuration();
                currentDuration = musicSrv.getCurrentPosition();
            }

            // Displaying Total Duration time
            //songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            //songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = utils.getProgressPercentage(currentDuration, totalDuration);
            //Log.d("Progress", ""+progress);
            songProgressbar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    private void setSongDetail(Songs songs) {
        tv_title.setText(songs.getTitle());
        tv_artist.setText(songs.getArtist());
    }

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
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (slidingLayout != null &&
                (slidingLayout.getPanelState() == PanelState.EXPANDED || slidingLayout.getPanelState() == PanelState.ANCHORED)) {
            slidingLayout.setPanelState(PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
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

}
