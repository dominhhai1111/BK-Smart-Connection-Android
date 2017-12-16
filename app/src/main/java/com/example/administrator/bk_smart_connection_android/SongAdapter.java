package com.example.administrator.bk_smart_connection_android;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;

/**
 * Created by DOANBK on 12/16/2017.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolderMusic> {
    private IMusicAdapter mInterf;

    public SongAdapter(IMusicAdapter interf) {
        mInterf = interf;
    }
    @Override
    public ViewHolderMusic onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_offline,parent, false);
        return new ViewHolderMusic(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderMusic holder, final int position) {
        ItemSong data = mInterf.getData(position);
        holder.tvArtist.setText(data.getArtist());
        holder.tvName.setText(data.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInterf.onClickItemExternal(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mInterf.getCount();
    }
    public interface IMusicAdapter {
        int getCount();

        ItemSong getData(int position);

        void onClickItemExternal(int position);
    }
    static class ViewHolderMusic extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvArtist;

        public ViewHolderMusic(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvArtist = (TextView) itemView.findViewById(R.id.tv_artist);

        }
    }
}
