package com.qianjinzhe.lcd_display.util;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              ToastUtil.java
 * Description：
 *              Toast工具类
 * Author:
 *              youngHu
 * Finished：
 *              2018年05月31日
 ********************************************************/
public class ToastUtil {


    private ToastUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isShow = true;

    private static Toast mToast;

    private static Handler mhandler = new Handler();

    private static Runnable r = new Runnable() {

        public void run() {
            mToast.cancel();
        }
    };

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, String message) {
        showToast(context, message, Toast.LENGTH_SHORT);
    }

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, int message) {
        showToast(context, message, Toast.LENGTH_SHORT);
    }


    public static void showToast(Context context, String text, int duration) {
        initToast(context, text, duration);
    }

    public static void showToast(Context context, int text, int duration) {
        initToast(context, context.getString(text), duration);
    }

    public static void initToast(Context context, String text, int duration){
        mhandler.removeCallbacks(r);
        if (null != mToast) {
            mToast.setText(text);
        } else {
            mToast = Toast.makeText(context, text, duration);
        }
        mhandler.postDelayed(r, 5000);
        mToast.show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, String message) {
        showToast(context, message, Toast.LENGTH_LONG);
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, int message) {
        showToast(context, message, Toast.LENGTH_LONG);
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, String message, int duration) {
        showToast(context, message, duration);
    }



}
