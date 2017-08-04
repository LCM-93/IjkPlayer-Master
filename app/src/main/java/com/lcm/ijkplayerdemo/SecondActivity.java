package com.lcm.ijkplayerdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import tv.danmaku.ijk.media.player.cmvideoview.CMVideoView;

/**
 * ****************************************************************
 * Author: LCM
 * Date: 2017/8/3 下午7:56
 * Desc:
 * *****************************************************************
 */

public class SecondActivity extends AppCompatActivity {
    private CMVideoView cmVideoView;
    private String url = "http://2449.vod.myqcloud.com/2449_22ca37a6ea9011e5acaaf51d105342e3.f20.mp4";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        cmVideoView = (CMVideoView) findViewById(R.id.cmVideoView);

        cmVideoView.initPath(url);
    }

    @Override
    protected void onDestroy() {
        cmVideoView.release();
        super.onDestroy();
    }
}
