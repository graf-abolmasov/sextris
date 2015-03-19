package com.dreamjteam.android.sextries;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Collections;

public class Sextris extends Activity {

    private TetrisTileView mTetrisView;

    private Button mStartButton;
    private Button mPauseButton;
    private Button mResumeButton;
    private Button mResetButton;
    private TextView mScore;
    private TextView mLevel;

    private boolean mIsBound = false;
    private BackgroundMusicService mServ;
    private ServiceConnection mScon = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mServ = ((BackgroundMusicService.ServiceBinder) binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };
    private Intent musicServiceName;

    void doBindService() {
        if (!mIsBound) {
            mIsBound = bindService(musicServiceName, mScon, Context.BIND_AUTO_CREATE);
        }
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(mScon);
            mIsBound = false;
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tetris_layout);

        mTetrisView = (TetrisTileView) findViewById(R.id.tetris_tile_view);

        mScore = (TextView) findViewById(R.id.score);
        mLevel = (TextView) findViewById(R.id.level);
        final CharSequence scoreText = getResources().getText(R.string.score);
        final CharSequence levelText = getResources().getText(R.string.level);
        mScore.setText(String.format("%1$s %2$d", scoreText, 0));
        mLevel.setText(String.format("%1$s %2$d", levelText, 1));

        mStartButton = (Button) findViewById(R.id.start_button);
        mStartButton.setFocusable(false);
        mPauseButton = (Button) findViewById(R.id.pause_button);
        mPauseButton.setFocusable(false);
        mPauseButton.setVisibility(View.INVISIBLE);
        mResumeButton = (Button) findViewById(R.id.resume_button);
        mResumeButton.setFocusable(false);
        mResumeButton.setVisibility(View.INVISIBLE);
        mResetButton = (Button) findViewById(R.id.reset_button);
        mResetButton.setFocusable(false);
        mResetButton.setVisibility(View.INVISIBLE);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mStartButton.setVisibility(View.INVISIBLE);
                mPauseButton.setVisibility(View.VISIBLE);
                mResetButton.setVisibility(View.VISIBLE);

                mTetrisView.setMode(Constants.RUNNING);
            }
        });
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mPauseButton.setVisibility(View.INVISIBLE);
                mResumeButton.setVisibility(View.VISIBLE);
                mResetButton.setVisibility(View.VISIBLE);

                mTetrisView.setMode(Constants.PAUSE);
            }
        });
        mResumeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mResumeButton.setVisibility(View.INVISIBLE);
                mPauseButton.setVisibility(View.VISIBLE);

                mTetrisView.setMode(Constants.RUNNING);
            }
        });
        mResetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mResetButton.setVisibility(View.INVISIBLE);
                mStartButton.setVisibility(View.VISIBLE);
                mResumeButton.setVisibility(View.INVISIBLE);
                mPauseButton.setVisibility(View.INVISIBLE);

                mTetrisView.setMode(Constants.READY);
            }
        });

        mTetrisView.setMode(Constants.READY);
        mTetrisView.setControlMethod(Constants.CONTROL_TOUCH);
        mTetrisView.setOnGameEventListener(new TetrisTileView.OnGameEventListener() {
            @Override
            public void onGameOver(int score, int level) {

            }

            @Override
            public void onScoreChange(int newScore) {
                mScore.setText(String.format("%1$s %2$d", scoreText, newScore));
            }

            @Override
            public void onLevelChange(int newLevel) {
                mLevel.setText(String.format("%1$s %2$d", levelText, newLevel));
            }
        });

        musicServiceName = new Intent(this, BackgroundMusicService.class);
        doBindService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(musicServiceName);
    }

    @Override
    protected void onPause() {
        if (mServ != null)
            mServ.stopMusic();
        mPauseButton.setVisibility(View.INVISIBLE);
        mResumeButton.setVisibility(mTetrisView.getMode() == Constants.LOSE ? View.INVISIBLE : View.VISIBLE);
        mResetButton.setVisibility(View.VISIBLE);

        mTetrisView.setMode(Constants.PAUSE);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        doUnbindService();
        stopService(musicServiceName);
        super.onDestroy();
    }
}