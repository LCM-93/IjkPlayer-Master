package com.lcm.ijkplayerdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.view.AndroidMediaController;
import tv.danmaku.ijk.media.player.view.IRenderView;
import tv.danmaku.ijk.media.player.view.IjkVideoView;
import tv.danmaku.ijk.media.player.view.SurfaceRenderView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnInfoListener {
    private IjkVideoView ijkVideoView;
    private Button btnPlay, btnPause, btnStop;
    private static final String TAG = "IJKPlayerDemo";
    private String url = "http://2449.vod.myqcloud.com/2449_22ca37a6ea9011e5acaaf51d105342e3.f20.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        ijkVideoView = (IjkVideoView) findViewById(R.id.ijkPlayer);
        btnPlay = (Button) findViewById(R.id.btn_play);
        btnPause = (Button) findViewById(R.id.btn_pause);
        btnStop = (Button) findViewById(R.id.btn_stop);

        btnPlay.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        ijkVideoView.setOnCompletionListener(this);
        ijkVideoView.setOnPreparedListener(this);
        ijkVideoView.setOnErrorListener(this);
        ijkVideoView.setOnInfoListener(this);

        ijkVideoView.setAspectRatio(IRenderView.AR_ASPECT_FILL_PARENT);
        AndroidMediaController androidMediaController = new AndroidMediaController(this);

        ijkVideoView.setMediaController(androidMediaController);

        ijkVideoView.setVideoPath(url);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
                ijkVideoView.start();
                break;

            case R.id.btn_pause:
                ijkVideoView.pause();
                break;


            case R.id.btn_stop:
                if (ijkVideoView.isPlaying()) {
                    ijkVideoView.stopPlayback();
                }
                break;
        }
    }

    @Override
    public void onCompletion(IMediaPlayer mp) {
        Log.i(TAG, "onCompletion ------ 播放完成！！！");
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        Log.i(TAG, "onPrepared  ----  预加载完成！！！");
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        Log.e(TAG, "onError  ----  播放出错！！！" + what + "  extra::" + extra);

        return false;
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        Log.i(TAG, "onInfo --- 播放信息!!!" + what + "   extra::" + extra);
        return false;
    }
}
