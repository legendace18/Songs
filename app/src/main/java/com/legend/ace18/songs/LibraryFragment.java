package com.legend.ace18.songs;


import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.legend.ace18.songs.utils.MusicPlayer;


/**
 * A simple {@link Fragment} subclass.
 */
public class LibraryFragment extends Fragment {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DrawerToggleListener drawerToggleListener;

    public LibraryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_library, container, false);
        setToolbar(layout);
        setViewPager(layout);
        return layout;
    }

    private void setToolbar(View layout) {
        toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_songs));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        drawerToggleListener.setNavigationView(toolbar);
    }

    private void setViewPager(View layout) {
        viewPager = (ViewPager) layout.findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), viewPager));
        tabLayout = (TabLayout) layout.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

    public class MyFragmentPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

        private String tabTitles[] = new String[]{"PlayLists", "All Songs"};
        private ViewPager mViewPager;

        public MyFragmentPagerAdapter(FragmentManager fm, ViewPager pager) {
            super(fm);
            this.mViewPager = pager;
            mViewPager.addOnPageChangeListener(this);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag = null;
            switch (position) {
                case 0:
                    frag = new PlayListFragment();
                    break;
                case 1:
                    frag = new MusicFragment();
                    break;
            }
            return frag;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            FragmentRefreshListener frag = (FragmentRefreshListener) this.instantiateItem(mViewPager, position);
            frag.refreshFragment();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    public interface FragmentRefreshListener {
        void refreshFragment();
    }
}
