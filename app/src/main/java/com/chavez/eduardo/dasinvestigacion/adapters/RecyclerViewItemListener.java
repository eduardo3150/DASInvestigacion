package com.chavez.eduardo.dasinvestigacion.adapters;

import android.widget.ImageView;

import com.chavez.eduardo.dasinvestigacion.network.GameObject;

/**
 * Created by Eduardo on 28/8/2017.
 */

public interface RecyclerViewItemListener {
    void onReciclerViewItemClick(int pos, GameObject object, ImageView sharedImage);
}
