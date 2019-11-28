package com.qianjinzhe.lcd_display;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.qianjinzhe.lcd_display.util.LogUtils;

/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              MyApplication.java
 * Description：
 *              Application
 * Author:
 *              youngHu
 * Finished：
 *              2018年05月31日
 ********************************************************/

public class MyApplication extends Application {

    public MyApplication() {
        mInstance = this;
    }

    public static MyApplication mInstance;

    public static MyApplication getInstance() {
        if (mInstance == null) {
            mInstance = new MyApplication();
        }
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //设置打印日志，发布时关闭,true打印，false不打印
        LogUtils.isDeBug = false;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            MultiDex.install(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
