package com.example.administrator.bk_smart_connection_android;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by DOANBK on 12/16/2017.
 */

public class PlayMusic implements MediaPlayer.OnPreparedListener {
    private String path;
    private MediaPlayer mediaPlayer;


    public boolean init(Context context, String path) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            mediaPlayer = null;
            return false;
        }
    }

    public boolean prepare(MediaPlayer.OnCompletionListener listener) {
        if (mediaPlayer == null) {
            return false;
        }
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnCompletionListener(listener);
        mediaPlayer.setOnPreparedListener(this);
        return true;
    }


    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        play();
    }

    public boolean play() {
        if (mediaPlayer == null) {
            return false;
        }
        mediaPlayer.start();
        return true;
    }

    public boolean pause() {
        if (mediaPlayer == null){
            return false;
        }
        mediaPlayer.pause();
        return true;
    }
    public boolean stop(){
        if (mediaPlayer == null){
            return false;
        }
        mediaPlayer.stop();
        return true;
    }
    public void release(){
        if (mediaPlayer == null){
            return;
        }
        mediaPlayer.release();
        mediaPlayer = null;
    }


    public boolean isPlaying(){
        if (mediaPlayer == null){
            return false;
        }
        return mediaPlayer.isPlaying();
    }


    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}
