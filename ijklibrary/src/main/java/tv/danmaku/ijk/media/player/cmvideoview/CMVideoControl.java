package tv.danmaku.ijk.media.player.cmvideoview;

import android.net.Uri;

/**
 * ****************************************************************
 * Author: LCM
 * Date: 2017/8/3 下午5:17
 * Desc:
 * *****************************************************************
 */

public interface CMVideoControl {

    void initPath(String url);

    void initPath(Uri uri);

    void start();

    void pause();

    void stop();

    void relase();


}
