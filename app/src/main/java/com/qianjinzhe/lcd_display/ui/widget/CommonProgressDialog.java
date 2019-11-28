package com.qianjinzhe.lcd_display.ui.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.StyleRes;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;

import com.qianjinzhe.lcd_display.R;
import com.qianjinzhe.lcd_display.util.DialogUtils;

import java.text.NumberFormat;

/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              CommonProgressDialog.java
 * Description：
 *              加载进度条
 * Author:
 *              youngHu
 * Finished：
 *              2018年06月05日
 ********************************************************/
public class CommonProgressDialog extends AlertDialog {
    private TextView mProgressPercent;
    private Handler mViewUpdateHandler;
    private int mMax = 100;
    private boolean mHasStarted;
    private NumberFormat mProgressPercentFormat;
    private Context mContext;
    private CharSequence fileName;
    private int progress = 0;
    /**文件名称*/
    private TextView tv_fileName;

    public CommonProgressDialog(Context context) {
        super(context);
        mContext = context;
        initFormats();
    }

    public CommonProgressDialog(Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        mContext = context;
        initFormats();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置窗体始终点亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        DialogUtils.hideNavigation(this);
        setContentView(R.layout.dialog_progress);
        mProgressPercent = (TextView) findViewById(R.id.progress_percent);
        tv_fileName = (TextView) findViewById(R.id.tv_fileName);
        mViewUpdateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (mProgressPercentFormat != null) {
                    //计算进度
                    if (progress > mMax) {
                        progress = mMax;
                    }
                    double percent = (double) progress / (double) mMax;
                    SpannableString tmp = new SpannableString(mProgressPercentFormat.format(percent));
                    tmp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, tmp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //设置文件名称
                    tv_fileName.setText(mContext.getResources().getString(R.string.loading_tip2) + " " + fileName);
                    mProgressPercent.setText(tmp);
                } else {
                    tv_fileName.setText(mContext.getResources().getString(R.string.loading_tip));
                    mProgressPercent.setText("0%");
                }
            }
        };
        onProgressChanged();
    }

    private void initFormats() {
        mProgressPercentFormat = NumberFormat.getPercentInstance();
        mProgressPercentFormat.setMaximumFractionDigits(0);
    }

    private void onProgressChanged() {
        mViewUpdateHandler.sendEmptyMessage(0);
    }

    public void setProgress(CharSequence fileName, int value, int max) {
        if (mHasStarted) {
            progress = value;
            mMax = max;
            this.fileName = fileName;
            onProgressChanged();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //拦截/屏蔽返回键、MENU键实现代码
        if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_MENU
                || keyCode == KeyEvent.KEYCODE_HOME) {
            
            dismiss();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void setMessage(CharSequence message) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mHasStarted = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHasStarted = false;
    }

}