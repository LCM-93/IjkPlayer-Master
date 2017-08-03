package tv.danmaku.ijk.media.player.cmvideoview;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.R;
import tv.danmaku.ijk.media.player.view.IjkVideoView;

/**
 * ****************************************************************
 * Author: LCM
 * Date: 2017/8/3 下午5:12
 * Desc:
 * *****************************************************************
 */

public class CMVideoView extends RelativeLayout implements CMVideoControl, View.OnClickListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnInfoListener {
    private static final String TAG = "CMVideoView";

    private IjkVideoView ijkVideoView;
    private RelativeLayout layoutControl;
    private ProgressBar bottomProgress, loading;
    private SeekBar bottomSeekBar;
    private LinearLayout layoutTop, layoutBottom, layoutStart;
    private ImageView ivBack, ivStart, ivFullScreen;
    private TextView tvTitle, tvCurrent, tvTotal;


    private boolean isPrepared = false;
    private boolean isCompleted = false;

    private int videoState = CMVideoViewState.VIDEO_STOP;

    public CMVideoView(Context context) {
        this(context, null);
    }

    public CMVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CMVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        ijkVideoView = new IjkVideoView(getContext());
        ijkVideoView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(ijkVideoView);

        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutControl = (RelativeLayout) layoutInflater.inflate(R.layout.cm_videoview_control, null);
        addView(layoutControl);

        bottomProgress = (ProgressBar) layoutControl.findViewById(R.id.bottom_progress);
        loading = (ProgressBar) layoutControl.findViewById(R.id.loading);
        bottomSeekBar = (SeekBar) layoutControl.findViewById(R.id.bottom_seek_progress);
        layoutTop = (LinearLayout) layoutControl.findViewById(R.id.layout_top);
        layoutBottom = (LinearLayout) layoutControl.findViewById(R.id.layout_bottom);
        layoutStart = (LinearLayout) layoutControl.findViewById(R.id.layout_start);
        ivBack = (ImageView) layoutControl.findViewById(R.id.iv_back);
        ivStart = (ImageView) layoutControl.findViewById(R.id.iv_start);
        ivFullScreen = (ImageView) layoutControl.findViewById(R.id.iv_fullscreen);
        tvTitle = (TextView) layoutControl.findViewById(R.id.tv_title);
        tvCurrent = (TextView) layoutControl.findViewById(R.id.tv_current);
        tvTotal = (TextView) layoutControl.findViewById(R.id.tv_total);

        ivBack.setOnClickListener(this);
        ivStart.setOnClickListener(this);
        ivFullScreen.setOnClickListener(this);

        ijkVideoView.setOnErrorListener(this);
        ijkVideoView.setOnCompletionListener(this);
        ijkVideoView.setOnPreparedListener(this);
        ijkVideoView.setOnInfoListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {

        } else if (v.getId() == R.id.iv_fullscreen) {


        } else if (v.getId() == R.id.iv_start) {
            if (videoState == CMVideoViewState.VIDEO_STOP) {
                Log.e(TAG, "need init");
            } else if (videoState == CMVideoViewState.VIDEO_PREPARED || videoState == CMVideoViewState.VIDEO_PAUSE || videoState == CMVideoViewState.VIDEO_COMPLETE) {
                start();
            } else if (videoState == CMVideoViewState.VIDEO_PLAYING) {
                pause();
            } else if (videoState == CMVideoViewState.VIDEO_ERROR) {
                Log.e(TAG, "video error");
            }
        }
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        videoState = CMVideoViewState.VIDEO_ERROR;
        ivStart.setImageResource(R.drawable.cm_slt_click_error);
        return false;

    }

    @Override
    public void onCompletion(IMediaPlayer mp) {
        videoState = CMVideoViewState.VIDEO_COMPLETE;
        Log.i(TAG, "onCompletion");
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        videoState = CMVideoViewState.VIDEO_PREPARED;
        Log.i(TAG, "onPrepared");
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        Log.i(TAG, "onInfo");
        switch (what) {
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                loading.setVisibility(VISIBLE);
                break;

            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                loading.setVisibility(GONE);
                break;

            case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                loading.setVisibility(GONE);
                break;
        }
        return false;
    }


    @Override
    public void initPath(String url) {
        ijkVideoView.setVideoPath(url);
    }

    @Override
    public void initPath(Uri uri) {
        ijkVideoView.setVideoURI(uri);
    }

    @Override
    public void start() {
        ijkVideoView.start();
        videoState = CMVideoViewState.VIDEO_PLAYING;
        ivStart.setImageResource(R.drawable.cm_slt_click_pause);
    }

    @Override
    public void pause() {
        ijkVideoView.pause();
        videoState = CMVideoViewState.VIDEO_PAUSE;
        ivStart.setImageResource(R.drawable.cm_slt_click_play);
    }

    @Override
    public void stop() {
        ijkVideoView.pause();

    }

    @Override
    public void relase() {
        ijkVideoView.pause();
        ijkVideoView.stopPlayback();
        ijkVideoView.release(true);
    }
}
