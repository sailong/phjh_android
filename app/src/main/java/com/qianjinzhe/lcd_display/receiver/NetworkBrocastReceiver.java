package com.qianjinzhe.lcd_display.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.qianjinzhe.lcd_display.event.NetworkEvent;

import org.greenrobot.eventbus.EventBus;

/******************************************************
 * Copyrights @ 2018，Shenzhen Qianjinzhe Technologies Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              NetworkBrocastReceiver.java
 * Description：
 *              网络监听广播
 * Author:
 *              youngHu
 * Finished：
 *              2018年12月01日
 ********************************************************/

public class NetworkBrocastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()) {
            //网络连接成功
            EventBus.getDefault().post(new NetworkEvent(true));
        } else {
            //未连接网络
            EventBus.getDefault().post(new NetworkEvent(false));
        }
    }


}
