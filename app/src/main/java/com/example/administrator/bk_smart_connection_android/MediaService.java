package com.example.administrator.bk_smart_connection_android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

/**
 * Created by DOANBK on 12/16/2017.
 */

public class MediaService extends Service implements MediaPlayer.OnCompletionListener {
public static final String LINK_MUSIC="http://dominhhhaiapps.com/public/uploads/music/";
    private PlayMusic playMusic;
    private OnControlMusic controlMusic;
    private OnCompleteMusic onCompleteMusic;
    private MyBroadCast myBroadCast;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder(this);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        myBroadCast = new MyBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("CANCEL");
        registerReceiver(myBroadCast, filter);
    }

    public void setOnCompleteMusic(OnCompleteMusic onCompleteMusic) {
        this.onCompleteMusic = onCompleteMusic;
    }
    public void setControlMusic(OnControlMusic controlMusic){
        this.controlMusic = controlMusic;
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (playMusic != null) {
            playMusic.release();
        }
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        stopForeground(true);
        notificationManager.cancel(1);
        if ( playMusic.isPlaying() ) {
            playMusic.pause();
        }
        onCompleteMusic.onCompleteMusic();

    }

    public PlayMusic getPlayMusic() {
        return playMusic;
    }

    public static class MyBinder extends Binder {
        private MediaService mediaService;

        public MyBinder(MediaService mediaService) {
            this.mediaService = mediaService;
        }

        public MediaService getMediaService() {
            return mediaService;
        }
    }

    public void newSong(ItemSong itemSong) {
        if (playMusic != null) {
            playMusic.release();
        } else {
            playMusic = new PlayMusic();
        }

        playMusic.init(this, LINK_MUSIC+itemSong.getLink());
        if (playMusic.getMediaPlayer() == null) {
            return;
        }

        playMusic.prepare(this);

        if (playMusic.getMediaPlayer() == null) {
            return;
        }
//        playMusic.play();
        showNotification(itemSong, false);
    }

    public void showNotification(ItemSong itemSong, boolean isPause) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this);
        builder.setContentTitle("Music");
        builder.setContentText(itemSong.getName());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setVisibility(Notification.VISIBILITY_PUBLIC);
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        manager.notify(1,builder.build());

        //custom notification

        RemoteViews views = new RemoteViews(getPackageName(), R.layout.layout_notification);
        //set content notification
        views.setTextViewText(R.id.name_song,
                itemSong.getName());
        views.setTextViewText(R.id.ca_si,
                itemSong.getArtist());


        views.setImageViewResource(R.id.image_notifi, R.mipmap.ic_launcher);
        if (isPause) {
            views.setImageViewResource(R.id.btn_pause, android.R.drawable.ic_media_play);
        } else {
            views.setImageViewResource(R.id.btn_pause, android.R.drawable.ic_media_pause);
        }

        Intent intent
                = new Intent();
        intent.setClass(this, PlayActivity.class);

        intent.putExtra("ITEMSONG", itemSong);
        intent.putExtra("ID", 1);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingPlay = PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingPlay);


        //pause, play nhac
        Intent intentPlay = new Intent();
        intentPlay.setClass(this, MediaService.class);
        if (playMusic.getMediaPlayer().isPlaying()) {
            intentPlay.setAction("PLAY");
        } else {
            intentPlay.setAction("PAUSE");
        }
        intentPlay.putExtra("ITEMSONG", itemSong);
        PendingIntent pendingIntent = PendingIntent.getService(this, 10, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.btn_pause, pendingIntent);


        Intent intentCalcel = new Intent();
//        intentCalcel.setClass(this, MediaService.class);
        intentCalcel.setAction("CANCEL");
        intentCalcel.putExtra("ITEMSONG", itemSong);
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(this, 10, intentCalcel, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.btn_cancle, pendingIntentCancel);


        //end

        builder.setCustomContentView(views);
        builder.setContent(views);

        stopForeground(false);
        startForeground(1, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadCast);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action != null && !action.equals("")) {
            switch (action) {
                case "PLAY":
                    playMusic.pause();
                    ItemSong itemSong = (ItemSong) intent.getSerializableExtra("ITEMSONG");
                    showNotification(itemSong, true);
                    controlMusic.onPauseMusic();
                    break;
                case "PAUSE":
                    playMusic.play();
                    itemSong = (ItemSong) intent.getSerializableExtra("ITEMSONG");
                    showNotification(itemSong, false);
                    controlMusic.onPlayMusic();
                    break;

            }
        }

        return START_NOT_STICKY;
    }


    public int getDuration() {
        if (playMusic == null) {
            return 0;
        }
        return playMusic.getMediaPlayer().getDuration();
    }
    public boolean isPlaying() {
        if (playMusic == null) {
            return false;
        }
        return playMusic.isPlaying();
    }
    public interface OnCompleteMusic {
        void onCompleteMusic();
    }

    public interface OnControlMusic {
        void onPauseMusic();

        void onPlayMusic();
    }
    private class MyBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case "CANCEL":
                    NotificationManager notificationManager = (NotificationManager)
                            getSystemService(Context.NOTIFICATION_SERVICE);
                    stopForeground(true);
                    notificationManager.cancel(1);
                    if ( playMusic.isPlaying() ) {
                        playMusic.pause();
                    }
                    break;
                default:
                    break;

            }
        }
    }
}

