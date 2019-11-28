package com.qianjinzhe.lcd_display.ijkplayer;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.qianjinzhe.lcd_display.util.ScreenUtils;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              CustomMediaPlayer.java
 * Description：
 *              自定义视频播放器
 * Author:
 *              youngHu
 * Finished：
 *              2018年05月21日
 ********************************************************/

public class CustomMediaPlayer extends FrameLayout {
    /**播放路径*/
    private String mPath = "";
    /**SurfaceView*/
    private SurfaceView surfaceView;
    /**播放监听*/
    private VideoPlayerListener listener;
    /**播放控制类*/
    private IjkMediaPlayer ijkMediaPlayer = null;
    /**播放控制接口*/
    private IMediaPlayer mMediaPlayer = null;
    /**上下文*/
    private Context mContext;
    /**屏幕宽*/
    private int mWith = 0;
    /**屏幕高*/
    private int mHeight = 0;
    /**是否静音,true为静音*/
    private boolean isMute = false;

    public CustomMediaPlayer(Context context) {
        super(context);
        initVideoView(context);
    }

    public CustomMediaPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVideoView(context);
    }

    public CustomMediaPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoView(context);
    }

    private void initVideoView(final Context context) {
        //获取焦点，不知道有没有必要~。~
        mContext = context;
        //获取屏幕宽度
        mWith = ScreenUtils.getScreenWidth(mContext);
        //获取屏幕高度
        mHeight = ScreenUtils.getScreenHeight(mContext);
        //设置获取焦点
        setFocusable(true);
    }

    /**
     * 开启声音
     */
    public void openVolume() {
        if (mMediaPlayer != null) {
            isMute = false;
            AudioManager audioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_SYSTEM);
            mMediaPlayer.setVolume(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM), audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
            mMediaPlayer.start();
        }
    }

    /**
     * 设置静音
     */
    public void closeVolume() {
        if (mMediaPlayer != null) {
            isMute = true;
            mMediaPlayer.setVolume(0, 0);
        }
    }

    /**
     * 获取声音状态
     * @return true静音, false有声音
     */
    public boolean getVolumeState() {
        return isMute;
    }

    /**
     * 设置视频地址。
     * 根据是否第一次播放视频，做不同的操作。
     * @param path the path of the video.
     */
    public void startPlayVideo(String path) {
        if (TextUtils.equals("", mPath)) {
            //如果是第一次播放视频，那就创建一个新的surfaceView
            mPath = path;
            //创建SurfaceView
            createSurfaceView();
        } else {
            //否则就直接load
            mPath = path;
            //加载视频
            load();
        }
    }


    /**
     * 新建一个surfaceview
     */
    private void createSurfaceView() {
        //生成一个新的surface view
        surfaceView = new SurfaceView(mContext);
        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.getHolder().addCallback(new CustomSurfaceCallback());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER);
        surfaceView.setLayoutParams(layoutParams);
        //设置长按事件
        surfaceView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onMyLongClickListenrer != null) {
                    onMyLongClickListenrer.onLongClick();
                }
                return true;
            }
        });
        this.addView(surfaceView);
    }

    /**
     * Surface监听
     */
    private class CustomSurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            createPlayer();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int videoWidth, int videoHeight) {
            //surfaceview创建成功后，加载视频
            if (videoWidth > mWith || videoHeight > mHeight) {
                // 如果video的宽或者高超出了当前屏幕的大小，则要进行缩放
                float wRatio = (float) videoWidth / (float) mWith;
                float hRatio = (float) videoHeight / (float) mHeight;

                // 选择大的一个进行缩放
                float ratio = Math.max(wRatio, hRatio);
                videoWidth = (int) Math.ceil((float) videoWidth / ratio);
                videoHeight = (int) Math.ceil((float) videoHeight / ratio);
            }
            surfaceView.getHolder().setFixedSize(videoWidth, videoHeight);
            //加载视频
            load();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mMediaPlayer != null) {
                mMediaPlayer.setDisplay(null);
            }
        }
    }


    /**
     * 加载视频
     */
    private void load() {
        //每次都要重新创建IMediaPlayer
        createPlayer();
        try {
            mMediaPlayer.setDataSource(mPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //给mediaPlayer设置视图
        mMediaPlayer.setDisplay(surfaceView.getHolder());
        mMediaPlayer.prepareAsync();
        surfaceView.setVisibility(VISIBLE);
    }


    /**
     * 暂停后，播放
     */
    public void onStart() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }


    /**
     * 暂停
     */
    public void onPause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }


    /**
     * 重播
     */
    public void reStart() {
        if (mMediaPlayer != null) {
            load();
        }
    }

    /**
     * 停止播放
     */
    public void onStop() {
        if (mMediaPlayer != null) {
            surfaceView.setVisibility(GONE);
            mMediaPlayer.stop();
        }
    }

    /**
     * 销毁
     */
    public void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            surfaceView = null;
        }
    }

    public void reset() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
        }
    }


    public long getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    public void createPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.setDisplay(null);
            mMediaPlayer.release();
        }
        ijkMediaPlayer = new IjkMediaPlayer();
        ijkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);

        //开启硬解码
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
        //IjkMediaPlayer.SDL_FCC_RV32
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
        //跳帧处理,放CPU处理较慢时，进行跳帧处理，保证播放流程，画面和声音同步
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
        //设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec_mpeg4", 1);

        mMediaPlayer = ijkMediaPlayer;
        mMediaPlayer.setOnPreparedListener(mPreparedListener);

        if (listener != null) {
            mMediaPlayer.setOnPreparedListener(listener);
            mMediaPlayer.setOnInfoListener(listener);
            mMediaPlayer.setOnSeekCompleteListener(listener);
            mMediaPlayer.setOnBufferingUpdateListener(listener);
            mMediaPlayer.setOnErrorListener(listener);
            mMediaPlayer.setOnVideoSizeChangedListener(listener);
            mMediaPlayer.setOnCompletionListener(listener);
        }
        //播放完成监听
        mMediaPlayer.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                if (onVideoPlayListener != null) {
                    onVideoPlayListener.onCompletion();
                }

            }
        });
        //播放错误监听
        mMediaPlayer.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                if (onVideoPlayListener != null) {
                    surfaceView.setVisibility(GONE);
                    onVideoPlayListener.onPlayError();
                }
                return false;
            }
        });
        //设置开启声音或静音,如果是静音状态,设置静音,否则开启声音
        if (isMute) {
            //设置静音
            closeVolume();
        } else {
            //开启声音
            openVolume();
        }

    }

    IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener() {
        public void onPrepared(IMediaPlayer mp) {
            //设置播放速度
//            mMediaPlayer.seekTo(1000);
//            ((IjkMediaPlayer) mMediaPlayer).setSpeed(1);

            onStart();
        }
    };

    /**
     * 设置播放监听
     * @param listener
     */
    public void setListener(VideoPlayerListener listener) {
        this.listener = listener;
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnPreparedListener(listener);
        }
    }


    private OnVideoPlayListener onVideoPlayListener;


    //设置播放监听
    public void setOnVideoPlayListener(OnVideoPlayListener onVideoPlayListener) {
        this.onVideoPlayListener = onVideoPlayListener;
    }


    public interface OnVideoPlayListener {
        //播放完成
        public void onCompletion();

        //播放发生错误
        public void onPlayError();
    }


    private OnMyLongClickListenrer onMyLongClickListenrer;

    /**
     * 长按事件
     * @param onMyLongClickListenrer
     */
    public void setOnMyLongClickListenrer(OnMyLongClickListenrer onMyLongClickListenrer) {
        this.onMyLongClickListenrer = onMyLongClickListenrer;
    }

    public interface OnMyLongClickListenrer {
        public void onLongClick();
    }

}

