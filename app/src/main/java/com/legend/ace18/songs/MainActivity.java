package com.legend.ace18.songs;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;

import com.legend.ace18.songs.adapters.PlayListAdapter;
import com.legend.ace18.songs.model.PlayList;
import com.legend.ace18.songs.model.Songs;
import com.legend.ace18.songs.utils.Controllers;
import com.legend.ace18.songs.utils.DatabaseHandler;
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
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNavigationView(null);

        fm = getSupportFragmentManager();
        Fragment frag;
        if (savedInstanceState != null) {
            frag = fm.getFragment(savedInstanceState, "MAINFRAGMENT");
            fm.beginTransaction().replace(R.id.activity_container, frag).commit();
        } else {

            frag = new LibraryFragment();
            fm.beginTransaction().add(R.id.activity_container, frag).commit();
        }

        utils = new SongUtils();
        db = new DatabaseHandler(this);

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

        btn_overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(MainActivity.this, view);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.action_addPlayList:
                                showPlayListPopup(musicSrv.getSongDetails());
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
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
            if (musicSrv.isMusicSet) {
                setSongDetail(musicSrv.getSongDetails());
                slidingLayout.setPanelState(PanelState.COLLAPSED);
                updateProgressBar();
                Controllers.setControllers(musicSrv, btn_play, btn_play_short, btn_shuffle, btn_repeat);
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
            if (musicBound)
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
    protected void onResume() {
        super.onResume();
        serviceIntent = new Intent(this, MusicService.class);
        startService(serviceIntent);
        bindService(serviceIntent, musicConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (musicBound) {
            unbindService(musicConnection);
            musicBound = false;
        }
    }

    @Override
    public void onDestroy() {
        //unbindService(musicConnection);
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
    public void onStopMusic() {
        mHandler.removeCallbacks(mUpdateTimeTask);
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

    private void showPlayListPopup(final Songs songs) {
        final List<PlayList> playLists = db.getPlayList();
        PlayListAdapter adapter;

        final Dialog d = new Dialog(this);
        d.setContentView(R.layout.popup_content);
        d.setTitle("PlayLists");
        d.setCancelable(true);
        RecyclerView pRecyclerView = (RecyclerView) d.findViewById(R.id.playList_recyclerView);
        pRecyclerView.setHasFixedSize(true);
        pRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(this, pRecyclerView, new ClickListener() {
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
        adapter = new PlayListAdapter(this, R.layout.search_row, playLists);
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
        final Dialog d = new Dialog(this);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        fm.putFragment(outState, "MAINFRAGMENT", fm.findFragmentById(R.id.activity_container));
    }

}
