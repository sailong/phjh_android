package com.qianjinzhe.lcd_display.ijkplayer;

import tv.danmaku.ijk.media.player.IMediaPlayer;


/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              VideoPlayerListener.java
 * Description：
 *              提供回调的接口
 * Author:
 *              youngHu
 * Finished：
 *              2018年05月31日
 ********************************************************/

public abstract class VideoPlayerListener implements IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnVideoSizeChangedListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnSeekCompleteListener {
}
