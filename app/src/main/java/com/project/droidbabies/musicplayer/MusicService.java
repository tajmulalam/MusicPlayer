package com.project.droidbabies.musicplayer;

import android.app.Notification;
import android.app.PendingIntent;
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
import java.util.Random;

/**
 * Created by Sumon on 4/6/2016.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, View.OnClickListener {

    private MediaPlayer mediaPlayer;
    private ArrayList<Song> songArrayList;
    private int songPosition;
    private final IBinder musicBinder = new MusicBinder();
    private String songTitle = "";
    private static final int NOTIFY_ID = 1;
    private boolean shuffle = false;
    private Random rand;

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
        rand = new Random();
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
        songTitle = playSong.getTitle();
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

    public void setShuffle() {
        if (shuffle)
            shuffle = false;
        else
            shuffle = true;
    }

    public class MusicBinder extends Binder {
        MusicService getMusicService() {
            return MusicService.this;
        }
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mediaPlayer.getCurrentPosition() > 0) {
            mediaPlayer.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mediaPlayer.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        Intent notiIntent = new Intent(this, MainActivity.class);
        notiIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")

                .setContentText(songTitle);

        Notification notification = builder.build();
        startForeground(NOTIFY_ID, notification);
//        PendingIntent pendingIntentForService = PendingIntent.getActivity(this, 0, notiIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    @Override
    public void onClick(View v) {

    }

    public int getSongPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void pausePlayer() {
        mediaPlayer.pause();
    }

    public void seek(int pos) {
        mediaPlayer.seekTo(pos);
    }

    public void go() {
        mediaPlayer.start();
    }

    public void playPrev() {
        songPosition--;
        if (songPosition <= 0)
            songPosition = songArrayList.size() - 1;
        playSong();
    }

    public void playNext() {
        if (shuffle) {
            int newSong = songPosition;
            while (newSong == songPosition) {
                newSong = rand.nextInt(songArrayList.size());
            }
            songPosition = newSong;
        } else {
            songPosition++;
            if (songPosition >= songArrayList.size())
                songPosition = 0;

        }
        playSong();
    }

    public void stop() {
        mediaPlayer.stop();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }
}
