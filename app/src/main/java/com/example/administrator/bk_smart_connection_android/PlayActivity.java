package com.example.administrator.bk_smart_connection_android;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by DOANBK on 12/15/2017.
 */

public class PlayActivity extends AppCompatActivity implements MediaService.OnControlMusic, MediaService.OnCompleteMusic, SongAdapter.IMusicAdapter {

    private ServiceConnection serviceConnection;
    private MediaService mediaService;
    @BindView(R.id.small_avatar)
    CircleImageView smallAvatar;
    @BindView(R.id.name_song)
    TextView txtNameSong;
    @BindView(R.id.artist_song)
    TextView txtArtist;
    @BindView(R.id.button_small_play)
    ImageView btnSmallPlay;
    private ItemSong itemSong;
    private SongAdapter adapter;
    private List<ItemSong> itemSongs;
    private RecyclerView rcSong;
    private int state = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_music);
        ButterKnife.bind(this);
        inits();
        onStartService();
        connectServiceMedia();

    }

    public void inits() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        initData();
        initView();
    }

    private void initView() {
        rcSong = findViewById(R.id.list_music);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rcSong.setLayoutManager(manager);
        adapter = new SongAdapter(this);
        rcSong.setAdapter(adapter);
    }

    private void initData() {

        itemSongs = new ArrayList<>();
        ItemSong itemSongo = new ItemSong(
                "Em của ngày hôm qua",
                "Sơn tùng MTP",
                "https://mp3.zing.vn/xhr/media/download-source?type=audio&code=LmcHtLHsXzdlndLtLTFmZHtZpLVzLsnlN&sig=d3bf4d6fa9b0150a62d9503369960a90");

        itemSongs.add(itemSongo);
    }

    public void openPlay(ItemSong itemSong) {
//        mediaService.newSong(itemSong);
        txtNameSong.setText(itemSong.getName());
        txtArtist.setText(itemSong.getArtist());
        btnSmallPlay.setImageResource(R.drawable.ic_pause_24dp);
        mediaService.newSong(itemSong);
        smallAvatar.setImageResource(R.drawable.girl);


        smallAvatar.startRotateAnimation();
        this.itemSong = itemSong;


    }

    private void connectServiceMedia() {
        //1 tao ra cau de phat tin hieu den service...
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                //4. nhan ket qua cua service tra ve
                //lay service ra
                MediaService.MyBinder myBinder = (MediaService.MyBinder) iBinder;
                mediaService = myBinder.getMediaService();
                mediaService.setOnCompleteMusic(PlayActivity.this);

                mediaService.setControlMusic(PlayActivity.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        //2phat tin hieu den servic yeu cau ket noi
        Intent intent = new Intent();
        intent.setClass(this, MediaService.class);
        this.bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    private void onStartService() {
        mediaService = new MediaService();
        Intent intent = new Intent();
        intent.setClass(this, MediaService.class);
        startService(intent);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @OnClick(R.id.button_small_play)
    void onClick() {
        if (mediaService.getPlayMusic() != null) {
            if (state == 0) {
                btnSmallPlay.setImageResource(R.drawable.ic_play_24dp);
                mediaService.getPlayMusic().pause();
                mediaService.showNotification(itemSong, true);
                smallAvatar.pauseRotateAnimation();
                state = 1;
            } else if (state == 1) {
                btnSmallPlay.setImageResource(R.drawable.ic_pause_24dp);
                mediaService.getPlayMusic().play();
                mediaService.showNotification(itemSong, false);
                smallAvatar.resumeRotateAnimation();
                state = 0;
            } else if (state == 2) {
                //phat lai bai hat
                playAgain();
                mediaService.showNotification(itemSong, false);
                btnSmallPlay.setImageResource(R.drawable.ic_pause_24dp);
                state = 0;
            }
        }

    }

//    public void pause(){
//        if (mediaService.isPlaying()){
//            btnSmallPlay.setImageResource(R.drawable.ic_play_24dp);
//            mediaService.getPlayMusic().pause();
//            mediaService.showNotification(itemSong, true);
//            smallAvatar.pauseRotateAnimation();
//            state = 1;
//        }
//    }


    public void playAgain() {
        mediaService.newSong(itemSong);
        smallAvatar.startRotateAnimation();
    }

    @Override
    public void onPauseMusic() {
        smallAvatar.pauseRotateAnimation();
        btnSmallPlay.setImageResource(R.drawable.ic_play_24dp);
        state = 1;

    }

    @Override
    public void onPlayMusic() {
        smallAvatar.startRotateAnimation();
        btnSmallPlay.setImageResource(R.drawable.ic_pause_24dp);
        state = 0;

    }

    @Override
    public void onCompleteMusic() {
        if (mediaService.isPlaying() == false) {
            if (smallAvatar.mRotateAnimator.isRunning()) {
                smallAvatar.cancelRotateAnimation();
            }
            btnSmallPlay.setImageResource(R.drawable.ic_replay);
            state = 2;

        }

    }

    @Override
    public int getCount() {
        return itemSongs.size();
    }

    @Override
    public ItemSong getData(int position) {
        return itemSongs.get(position);
    }

    @Override
    public void onClickItemExternal(int position) {
        openPlay(itemSongs.get(position));
    }
}
