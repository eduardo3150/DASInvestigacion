package com.chavez.eduardo.dasinvestigacion.adapters;


import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chavez.eduardo.dasinvestigacion.R;
import com.chavez.eduardo.dasinvestigacion.network.GameObject;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Eduardo on 27/8/2017.
 */

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder> {
    List<GameObject> recyclerViewList;
    Context context;
    RecyclerViewItemListener listener;

    public MainRecyclerViewAdapter(List<GameObject> recyclerFiller, Context context, RecyclerViewItemListener listener) {
        this.recyclerViewList = recyclerFiller;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MainRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(final MainRecyclerViewAdapter.ViewHolder holder, int position) {
        final GameObject object = recyclerViewList.get(position);

        holder.gameName.setText(object.getGame_name());
        holder.platforms.setText(object.getPlatform());

        ViewCompat.setTransitionName(holder.imageView, object.getGame_name());
        Picasso.with(context).load(object.getGame_image()).into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onReciclerViewItemClick(holder.getAdapterPosition(), object, holder.imageView);
            }
        });


    }

    @Override
    public int getItemCount() {
        return recyclerViewList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView gameName, platforms;
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageRecycler);
            gameName = (TextView) itemView.findViewById(R.id.gameTitle);
            platforms = (TextView) itemView.findViewById(R.id.gamePlatforms);
        }
    }
}
