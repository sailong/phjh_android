package com.qianjinzhe.lcd_display.ui.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/******************************************************
 * Copyrights @ 2018，Shenzhen Qianjinzhe Technologies Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              HomeListen.java
 * Description：
 *              Home键监听
 * Author:
 *              youngHu
 * Finished：
 *              2018年07月13日
 ********************************************************/

public class HomeListen {
    public HomeListen(Context context) {
        mContext = context;
        mHomeBtnReceiver = new HomeBtnReceiver();
        mHomeBtnIntentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    }

    public void setOnHomeBtnPressListener(OnHomeBtnPressLitener onHomeBtnPressListener) {
        mOnHomeBtnPressListener = onHomeBtnPressListener;
    }

    public void start() {
        mContext.registerReceiver(mHomeBtnReceiver, mHomeBtnIntentFilter);
    }

    public void stop() {
        mContext.unregisterReceiver(mHomeBtnReceiver);
    }

    class HomeBtnReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            receive(context, intent);
        }
    }

    private void receive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            String reason = intent.getStringExtra("reason");
            if (reason != null) {
                if (null != mOnHomeBtnPressListener) {
                    if (reason.equals("homekey")) {
                        // 按Home按键
                        mOnHomeBtnPressListener.onHomeBtnPress();
                    } else if (reason.equals("recentapps")) {
                        // 长按Home按键
                        mOnHomeBtnPressListener.onHomeBtnLongPress();
                    }
                }
            }
        }
    }

    public interface OnHomeBtnPressLitener {
        public void onHomeBtnPress();

        public void onHomeBtnLongPress();
    }

    private Context mContext = null;
    private IntentFilter mHomeBtnIntentFilter = null;
    private OnHomeBtnPressLitener mOnHomeBtnPressListener = null;
    private HomeBtnReceiver mHomeBtnReceiver = null;
}
