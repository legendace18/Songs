package com.legend.ace18.songs;

import android.view.View;

/**
 * Created by rohan on 7/19/15.
 */
public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
