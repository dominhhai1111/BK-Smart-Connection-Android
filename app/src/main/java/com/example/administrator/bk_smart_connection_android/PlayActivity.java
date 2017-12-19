package com.example.administrator.bk_smart_connection_android;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by DOANBK on 12/15/2017.
 */

public class PlayActivity extends AppCompatActivity
        implements MediaService.OnControlMusic,
        MediaService.OnCompleteMusic,
        SongAdapter.IMusicAdapter {

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
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    ImageView backView;
    private ItemSong itemSong;
    private SongAdapter adapter;
    private List<ItemSong> itemSongs;
    private RecyclerView rcSong;
    private Disposable disposable;
    private String reObject;
    private String analyzedObject;
    private int state = 0;
    private IApi api;
    private MyBroadCastMain myBroadCastMain;
    public static final String BASE_URL= "http://dominhhhaiapps.com";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_music);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        analyzedObject = intent.getStringExtra("ANALYZEDOBJECT");
        reObject = intent.getStringExtra("REOBJECT");
        api = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build().create(IApi.class);
        toolbar.findViewById(R.id.danhsachnhac).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        inits();
        onStartService();
        connectServiceMedia();
        registerBroadcast();

    }

    public void inits() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        initView();
        initData();

    }

    private void initView() {
        rcSong = findViewById(R.id.list_music);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rcSong.setLayoutManager(manager);
        adapter = new SongAdapter(this);
        rcSong.setAdapter(adapter);

    }

    private void registerBroadcast() {
        myBroadCastMain = new MyBroadCastMain();
        IntentFilter filter = new IntentFilter();
        filter.addAction("CANCEL");
        registerReceiver(myBroadCastMain, filter);
    }
    private boolean initData() {

        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        disposable = api.getMusic( analyzedObject, reObject)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ItemSong>>() {
                               @Override
                               public void accept(List<ItemSong> itemSongOnlines) throws Exception {
                                   itemSongs = itemSongOnlines;
                                   adapter.notifyDataSetChanged();
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                throwable.printStackTrace();
                            }
                        });
        return false;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadCastMain);
        unbindService(serviceConnection);

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
        if (itemSongs == null) {
            return 0;
        }
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

    private class MyBroadCastMain extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case "CANCEL":
                    if (state == 0) {
                        smallAvatar.cancelRotateAnimation();
                        btnSmallPlay.setImageResource(R.drawable.ic_play_24dp);
                        state = 1;
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
