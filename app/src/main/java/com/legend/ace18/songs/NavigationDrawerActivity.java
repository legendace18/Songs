package com.legend.ace18.songs;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;


public class NavigationDrawerActivity extends ActionBarActivity implements NavigationView.OnNavigationItemSelectedListener{

    protected NavigationView navigationView;
    protected DrawerLayout drawerLayout;
    protected FrameLayout frameLayout;
    protected ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    public void setContentView(int layoutResID) {
        View layout = getLayoutInflater().inflate(R.layout.activity_navigation_drawer, null);
        frameLayout = (FrameLayout) layout.findViewById(R.id.activity_frame);
        getLayoutInflater().inflate(layoutResID, frameLayout, true);
        super.setContentView(layout);

        drawerLayout = (DrawerLayout) layout.findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) layout.findViewById(R.id.navigation);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }

        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch(id){
            case R.id.action_main:
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                finish();
                drawerLayout.closeDrawers();
                break;

            case R.id.action_mySongs:
                drawerLayout.closeDrawers();
                break;
        }
        return true;
    }
}
