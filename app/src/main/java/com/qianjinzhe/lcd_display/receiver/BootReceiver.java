package com.qianjinzhe.lcd_display.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.qianjinzhe.lcd_display.ui.activity.WelcomeActivity;
import com.qianjinzhe.lcd_display.util.AppUtils;
import com.qianjinzhe.lcd_display.util.LogUtils;

/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              BootReceiver.java
 * Description：
 *              开机启动广播
 * Author:
 *              youngHu
 * Finished：
 *              2018年05月31日
 ********************************************************/

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.d("state", "开机了");
        try {
            //屏幕解锁
            AppUtils.screenUnlock(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //MainActivity就是开机显示的界面
                Intent mBootIntent = new Intent(context, WelcomeActivity.class);
                //下面这句话必须加上才能开机自动运行app的界面
                mBootIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mBootIntent);
            } else {
                //启动应用，参数为需要自动启动的应用的包名
                Intent intent2 = context.getPackageManager().getLaunchIntentForPackage("com.qianjinzhe.lcd_display");
                context.startActivity(intent2);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
