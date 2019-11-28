package com.qianjinzhe.lcd_display.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.qianjinzhe.lcd_display.R;
import com.qianjinzhe.lcd_display.event.ShutdownEvent;
import com.qianjinzhe.lcd_display.manager.AppManager;
import com.qianjinzhe.lcd_display.ui.activity.base.BaseActivity;
import com.qianjinzhe.lcd_display.util.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.DataOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.OnClick;

/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              ShutdownCountdownActivity.java
 * Description：
 *              关机倒计时弹窗
 * Author:
 *              youngHu
 * Finished：
 *              2018年06月20日
 ********************************************************/

public class ShutdownCountdownActivity extends BaseActivity {
    /**时间*/
    @Bind(R.id.tv_time)
    TextView tv_time;
    /**取消按钮*/
    @Bind(R.id.btn_cancel)
    TextView btn_cancel;
    /**Handler*/
    private Handler handler;
    /**倒计时60秒*/
    private int time = 59;
    /**Runnable*/
    private Runnable mRunnable;
    /**是否取消关机*/
    private boolean isCancelShutdown = false;

    /**
     * 进入倒计时弹窗页面
     * @param context
     */
    public static void startShutdownCountdownActivity(Context context) {
        Intent intent = new Intent(context, ShutdownCountdownActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_shutdown_countdown;
    }

    @Override
    public void initView() {
        //发送Event,关闭更新弹窗
        EventBus.getDefault().post(new ShutdownEvent(1));
        //关闭其他所有页面，除主页和本页面
        AppManager.getInstance().closeAllActivityExceptTwo("ShutdownCountdownActivity", "MainActivity");
        //执行倒计时
        countdownTime();
        //设置按钮选中
        btn_cancel.setSelected(true);
    }


    /**
     * 取消按钮
     */
    @OnClick(R.id.btn_cancel)
    public void onClick() {
        isCancelShutdown = true;
        //发送Event,显示更新弹窗
        EventBus.getDefault().post(new ShutdownEvent(2));
        //关闭弹窗
        finish();
    }


    /**
     * 倒计时
     *
     */
    private void countdownTime() {
        try {
            handler = new Handler();
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    if (time == 0) {
                        if (isCancelShutdown) {
                            return;
                        }
                        //显示时间
                        tv_time.setText(String.valueOf(time) + "s");
                        //执行关机
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //执行关机
                                    Process process = Runtime.getRuntime().exec("su");
                                    DataOutputStream out = new DataOutputStream(
                                            process.getOutputStream());
                                    out.writeBytes("reboot -p\n");
                                    out.writeBytes("exit\n");
                                    out.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 1000);
                        //关闭弹窗
                        finish();
                        return;
                    }
                    //设置倒计时间
                    tv_time.setText(String.valueOf(time) + "s");
                    time--;
                    //设置1秒执行一次
                    handler.postDelayed(mRunnable, 1000);
                }
            };
            handler.post(mRunnable);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {

            case KeyEvent.KEYCODE_BACK:    //返回键
                LogUtils.d("onKeyDown", "back--->");

                return true;

            case KeyEvent.KEYCODE_HOME://
                LogUtils.d("onKeyDown", "keycode_home--->");

                return true;
        }

        return super.onKeyDown(keyCode, event);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRunnable != null) {
            handler.removeCallbacks(mRunnable);
        }
    }


}
