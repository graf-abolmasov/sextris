package com.dreamjteam.android.sextries;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class BackgroundMusicService extends Service implements MediaPlayer.OnErrorListener{
    private static final String TAG = "BackgroundMusicService";
    private final IBinder mBinder = new ServiceBinder();
    MediaPlayer mPlayer;

    public BackgroundMusicService() {
    }

    public class ServiceBinder extends Binder {
        BackgroundMusicService getService() {
            return BackgroundMusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            final AssetManager assetManager = getAssets();
            final AssetFileDescriptor afd = assetManager.openFd("b1.ogg");
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mPlayer.prepare();
        } catch (Exception e) {
            mPlayer = MediaPlayer.create(this, R.raw.b3);
        }
        mPlayer.setOnErrorListener(this);
        mPlayer.setLooping(true);
        mPlayer.setVolume(0.1f, 0.1f);
        mPlayer.start();
        return START_NOT_STICKY;
    }

    public void stopMusic() {
        releasePlayer();
    }

    private void releasePlayer() {
        if (mPlayer == null)
            return;
        try {
            mPlayer.stop();
            mPlayer.release();
        } finally {
            mPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(this, "music player failed", Toast.LENGTH_SHORT).show();
        releasePlayer();
        return false;
    }
}
