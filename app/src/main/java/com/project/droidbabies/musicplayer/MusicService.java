package com.project.droidbabies.musicplayer;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Sumon on 4/6/2016.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, View.OnClickListener {

    private MediaPlayer mediaPlayer;
    private ArrayList<Song> songArrayList;
    private int songPosition;
    private final IBinder musicBinder = new MusicBinder();


    @Override
    public IBinder onBind(Intent intent) {

        return musicBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.start();
        mediaPlayer.release();
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        songPosition = 0;
        mediaPlayer = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer() {
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    public void setSongList(ArrayList<Song> songArrayList) {
        this.songArrayList = songArrayList;
    }


    public void playSong() {
        mediaPlayer.reset();
        Song playSong = songArrayList.get(songPosition);
        long currentSong = playSong.getSongId();
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong);
        try {
            mediaPlayer.setDataSource(getApplicationContext(), trackUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
    }

    public void setSong(int songIndex) {
        this.songPosition = songIndex;
    }

    public class MusicBinder extends Binder {
        MusicService getMusicService() {
            return MusicService.this;
        }
    }


    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public void onClick(View v) {

    }


}
