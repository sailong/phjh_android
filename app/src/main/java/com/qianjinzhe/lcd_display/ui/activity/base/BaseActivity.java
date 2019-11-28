package com.qianjinzhe.lcd_display.ui.activity.base;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.qianjinzhe.lcd_display.R;
import com.qianjinzhe.lcd_display.manager.AppManager;
import com.qianjinzhe.lcd_display.ui.activity.MainActivity;
import com.qianjinzhe.lcd_display.ui.listener.HomeListen;
import com.qianjinzhe.lcd_display.ui.widget.CommonProgressDialog;
import com.qianjinzhe.lcd_display.util.AppUtils;
import com.qianjinzhe.lcd_display.util.Constants;
import com.qianjinzhe.lcd_display.util.LogUtils;
import com.qianjinzhe.lcd_display.util.SPUtils;
import com.qianjinzhe.lcd_display.util.TextUtils;
import com.qianjinzhe.lcd_display.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;

/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              BaseActivity.java
 * Description：
 *              activity基类
 * Author:
 *              youngHu
 * Finished：
 *              2018年05月31日
 ********************************************************/

public abstract class BaseActivity extends FragmentActivity {
    /**上下文*/
    protected Context mContext;
    /**activity名称*/
    protected String TAG;
    /**EventBus*/
    private EventBus eventBus;
    /**是否显示输入ip弹窗*/
    public static boolean isShowSettingIp = false;
    /**当前进度*/
    private int curr_progress = 0;
    /**之前的进度*/
    private int progress_1 = 0;
    /**第二次查看的进度*/
    private int progress_2 = 0;
    /**关闭进度定时器*/
    private Timer closeProgress_timer;
    /**Handler*/
    private Handler mHandle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //上下文赋值
        mContext = this;
        mHandle = new Handler();
        //设置窗体全屏,隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置显示播放不受屏幕限制
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        //设置窗体始终点亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //自动解锁无密码的锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        //点亮屏幕
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        /*隐藏底部Home导航栏*/
        AppUtils.hideBottomUIMenu(this);
        //设置横竖屏
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        if (width > height) {
            //设置横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            //设置竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
//        //设置横竖屏
//        if (AppUtils.isPad(mContext)) {
//            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//                //设置横屏
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            }
//        } else {
//            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                //设置竖屏
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            }
//        }
        //配置语言
        AppUtils.setLanguage(mContext, SPUtils.get(mContext, SPUtils.LANGUAGE_CODE, Constants.LANGUAGE_CN).toString());
        //获取activity名称
        TAG = this.getClass().getSimpleName();
        //将activity名称添加到管理类
        if (getIsPutActivity()) {
            AppManager.getInstance().putActivity(TAG, this);
        }
        //设置contentView
        setContentView(getLayoutId());
        //初始化注解
        ButterKnife.bind(this);
        //初始化数据
        initData();
        //初始化UI
        initView();
        //初始化Home键监听
        initHomeListen();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mHomeListen.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHomeListen.stop();
    }

    private HomeListen mHomeListen = null;

    /**
     * 监听home键
     */
    private void initHomeListen() {
        mHomeListen = new HomeListen(this);
        mHomeListen.setOnHomeBtnPressListener(new HomeListen.OnHomeBtnPressLitener() {
            @Override
            public void onHomeBtnPress() {
                LogUtils.e("-----home键出现了----", "-----home键出现了----");
                startSelfFromPendingIntent();
            }

            @Override
            public void onHomeBtnLongPress() {
                LogUtils.e("-----home键出现了----", "-----home键出现了----");
                startSelfFromPendingIntent();
            }
        });
    }

    /**
     * 进入主页
     */
    private void startSelfFromPendingIntent() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(mContext, MainActivity.class));
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        try {
            pendingIntent.send();
        } catch (Exception e) {
            LogUtils.e("baseAct", "stayTop fail");
        }
    }

    /**
     * 初始化数据
     */
    public void initData() {
    }

    /**
     * 设置ContentView
     * @return
     */
    public abstract int getLayoutId();

    /**
     * 初始化UI
     */
    public abstract void initView();


    protected boolean getIsPutActivity() {
        return true;
    }


    /**进度条dialog*/
    public CommonProgressDialog mDialog;
    public boolean isUpdateFile = false;

    /**
     * 初始化加载进度条
     */
    public void initDialog() {
        if (mDialog == null) {
            dismissProgressDialog();
            mDialog = null;
            mDialog = new CommonProgressDialog(mContext, R.style.progress_dialog);
            mDialog.setCanceledOnTouchOutside(false);
        }
        if (!mDialog.isShowing()) {
            mDialog.show();
            if (closeProgress_timer != null) {
                closeProgress_timer.cancel();
                closeProgress_timer = null;
            }
            closeProgress_timer = new Timer();
            closeProgress_timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    progress_1 = curr_progress;

                    mHandle.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progress_2 = curr_progress;
                            LogUtils.d("检查进度", "progress_1=" + progress_1 + ",progress_2==" + progress_2);
                            //如果两个值不变
                            if (progress_1 == progress_2) {
                                dismissProgressDialog();
                            }
                        }
                    }, 10 * 1000);


                }
            }, 2 * 1000, 15 * 1000);
        }
    }


    /**
     * 显示进度条
     */
    public void showProgressDialog() {
        isUpdateFile = true;
        initDialog();
    }

    /**
     * 关闭加载进度条
     */
    public void dismissProgressDialog() {
        isUpdateFile = false;
        if (mDialog == null) {
            return;
        }
        mDialog.dismiss();
        if (closeProgress_timer != null) {
            closeProgress_timer.cancel();
            closeProgress_timer = null;
        }
    }

    /**
     * 设置进度
     * @param progress
     * @param max
     */
    public void setProgress(CharSequence fileName, int progress, int max) {
        try {
            if (mDialog != null) {
                if (TextUtils.isEmpty((String) fileName) || null == fileName) {
                    ToastUtil.showShort(mContext, "更新出错，可能数据包丢失！");
                    dismissProgressDialog();
                    return;
                }
                mDialog.setProgress(fileName, progress, max);
                //记录当前进度值
                curr_progress = progress;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }


    /**
     * 初始化EventBus
     */
    public void setEvent() {
        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventBus != null) {
            eventBus.unregister(this);
        }
        if (closeProgress_timer != null) {
            closeProgress_timer.cancel();
            closeProgress_timer = null;
        }
    }

}
