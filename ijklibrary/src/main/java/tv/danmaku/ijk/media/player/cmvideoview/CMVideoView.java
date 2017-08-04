package tv.danmaku.ijk.media.player.cmvideoview;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Formatter;
import java.util.Locale;

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
    private boolean mShowing = false;
    private boolean mDragging = false; //是否正在拖动
    private boolean isFullScreen = false;
    private int sDefaultTimeout = 5000;

    StringBuilder mFormatBuilder;


    private int videoState = CMVideoViewState.VIDEO_STOP;
    private Formatter mFormatter;

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
        bottomSeekBar.setOnSeekBarChangeListener(mSeekListener);

        ijkVideoView.setOnErrorListener(this);
        ijkVideoView.setOnCompletionListener(this);
        ijkVideoView.setOnPreparedListener(this);
        ijkVideoView.setOnInfoListener(this);

        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
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
        resetViewByState();
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        videoState = CMVideoViewState.VIDEO_ERROR;
        resetViewByState();
        return false;

    }

    @Override
    public void onCompletion(IMediaPlayer mp) {
        videoState = CMVideoViewState.VIDEO_COMPLETE;
        resetViewByState();
        show(0);
        Log.i(TAG, "onCompletion");
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        videoState = CMVideoViewState.VIDEO_PREPARED;
        int duration = ijkVideoView.getDuration();
        if (tvTotal != null)
            tvTotal.setText(stringForTime(duration));
        resetViewByState();
        Log.i(TAG, "onPrepared");
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        Log.i(TAG, "onInfo");
        switch (what) {
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                loading.setVisibility(VISIBLE);
                ivStart.setVisibility(GONE);
                break;

            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                loading.setVisibility(GONE);
                ivStart.setVisibility(VISIBLE);
                break;

            case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                loading.setVisibility(GONE);
                ivStart.setVisibility(VISIBLE);
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
        show(sDefaultTimeout);

    }

    @Override
    public void pause() {
        ijkVideoView.pause();
        videoState = CMVideoViewState.VIDEO_PAUSE;
        show(0);

    }

    @Override
    public void stop() {
        ijkVideoView.pause();
    }

    @Override
    public void release() {
        removeCallbacks(mShowProgress);
        ijkVideoView.pause();
        ijkVideoView.stopPlayback();
        ijkVideoView.release(true);
    }

    private void resetViewByState() {
        switch (videoState) {
            case CMVideoViewState.VIDEO_PLAYING:
                ivStart.setImageResource(R.drawable.cm_slt_click_pause);
                break;

            case CMVideoViewState.VIDEO_PAUSE:
            case CMVideoViewState.VIDEO_PREPARED:
            case CMVideoViewState.VIDEO_COMPLETE:
                ivStart.setImageResource(R.drawable.cm_slt_click_play);
                break;

            case CMVideoViewState.VIDEO_ERROR:
                ivStart.setImageResource(R.drawable.cm_slt_click_error);
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                show(0); // show until hide is called
                break;
            case MotionEvent.ACTION_UP:
                show(sDefaultTimeout); // start timeout
                break;
            case MotionEvent.ACTION_CANCEL:
                hide();
                break;
            default:
                break;
        }
        return true;
    }

    private final Runnable mFadeOut = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Remove the controller from the screen.
     */
    public void hide() {
        if (mShowing) {
//                removeCallbacks(mShowProgress);
            mShowing = false;
            showHideControl(mShowing);
        }
    }

    public void show(int timeout) {
        if (!mShowing) {
            mShowing = true;
            showHideControl(mShowing);
        }

        post(mShowProgress);
        if (timeout != 0) {
            removeCallbacks(mFadeOut);
            postDelayed(mFadeOut, timeout);
        }
    }

    private void showHideControl(boolean isShow) {
        layoutTop.setVisibility(isShow ? VISIBLE : GONE);
        layoutBottom.setVisibility(isShow ? VISIBLE : GONE);
        layoutStart.setVisibility(isShow ? VISIBLE : GONE);
    }

    private boolean isPlaying() {
        return ijkVideoView.isPlaying();
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private int setProgress() {
        if (ijkVideoView == null || mDragging) {
            return 0;
        }
        int position = ijkVideoView.getCurrentPosition();
        int duration = ijkVideoView.getDuration();
        if (bottomProgress != null && bottomSeekBar != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                bottomProgress.setProgress((int) pos);
                bottomSeekBar.setProgress((int) pos);
            }
            int percent = ijkVideoView.getBufferPercentage();
            if (percent >= 95) percent = 100;
            bottomProgress.setSecondaryProgress(percent * 10);
            bottomSeekBar.setSecondaryProgress(percent * 10);
        }

        if (tvTotal != null)
            tvTotal.setText(stringForTime(duration));
        if (tvCurrent != null)
            tvCurrent.setText(stringForTime(position));

        return position;
    }


    private final Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            int pos = setProgress();
            if (!mDragging && ijkVideoView.isPlaying()) {
                postDelayed(mShowProgress, 1000 - (pos % 1000));
            }
        }
    };

    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar bar) {
            show(3600000);
            mDragging = true;
            removeCallbacks(mShowProgress);
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser) {
                return;
            }

            long duration = ijkVideoView.getDuration();
            long newposition = (duration * progress) / 1000L;
            ijkVideoView.seekTo((int) newposition);
            if (tvCurrent != null)
                tvCurrent.setText(stringForTime((int) newposition));
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            mDragging = false;
            setProgress();
//            updatePausePlay();
            show(sDefaultTimeout);

            post(mShowProgress);
        }
    };

}
